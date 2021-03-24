package com.zodiackillerciphers.web;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.zodiackillerciphers.io.FileUtil;

public class ScrapeZKS {
	static String prefix = "/Users/doranchak/projects/zodiac/zodiackillersite-mirror/";
	static DefaultHttpClient httpclient;
	static HttpGet httpget;
	static HttpResponse response;
	static HttpEntity entity;
	static String topicDir;
	static String postsDir;
	
	static StringBuffer currentBuffer;
	static int currentFileNumber;
	static String lastGet;
	
	public static void login() {
		try {
			httpclient = new DefaultHttpClient();
			httpget = new HttpGet(
					"http://zodiackillersite.forummotion.com/login");
			response = httpclient.execute(httpget);
			entity = response.getEntity();
			System.out.println("Login form get: " + response.getStatusLine());
			EntityUtils.consume(entity);
			System.out.println("Initial set of cookies:");
			List<Cookie> cookies = httpclient.getCookieStore().getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
			HttpPost httpost = new HttpPost(
					"http://zodiackillersite.forummotion.com/login?"
							+ "username=doranchak&" + "password=polyp85&"
							+ "login=Log+in&" + "autologin=on");

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("IDToken1", "username"));
			nvps.add(new BasicNameValuePair("IDToken2", "password"));

			httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			response = httpclient.execute(httpost);
			entity = response.getEntity();

			System.out.println("Login form get: " + response.getStatusLine());
			EntityUtils.consume(entity);

			System.out.println("Post logon cookies:");
			cookies = httpclient.getCookieStore().getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
			//Document doc = get("http://zodiackillersite.forummotion.com/post?p=12975&mode=editpost");
			//System.out.println(doc.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public static void scrape() throws Exception {
		
		try {
			login();
			// we are logged in.  get cranking.
			go();

		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}

	}
	
	static void go() {
		Document doc = get("http://zodiackillersite.forummotion.com");
		
		processForums(doc);
		
	}
	
	static void processForums(Document doc) {
		Elements forums = doc.select("a.forumlink");
		Iterator it = forums.iterator();
		System.out.println("Number of forums: " + forums.size());
		while (it.hasNext()) {
			Element link = (Element)it.next();
			Element row = link.parent().parent().parent().parent();
			Elements col1 = row.select("td:eq(1)");
			Elements col2 = row.select("td:eq(2)");
			Elements col3 = row.select("td:eq(3)");
			Elements col4 = row.select("td:eq(4)");
			System.out.println("Col1: [" + col1.text() + "] Col2: [" + col2.text() + "] Col3: [" + col3.text() + "] Col4: [" + col4.text() + "]");
			processForum(link);
			login(); // re-login after each forum, hopefully to avoid the maintenance error
		}
	}
	
	static void makeDirectory(String name) {
		try {
			File dir = new File(prefix + name);
			dir.mkdir();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void processForum(Element forum) {
		String h = forum.attr("href");
		String dir = h.substring(1);
		makeDirectory(dir);
		String linkHref = urlFrom(h);
		String linkText = forum.text();
		//if (!linkText.contains("Zodiac Cipher Discussion General Discussion")) return;
		System.out.println("Forum name: " + linkText);
		System.out.println("Forum URL: " + linkHref);

		List<Document> docs = pages(linkHref); // returns one doc if only one page, otherwise returns all pages.
		
		int i=0;
		for (Document doc : docs) {
			//if (i++ == 0) continue;
			processTopics(dir, linkText, doc); // dive into all topics and start extracting posts
		}
	}
	
	static void processTopics(String dir, String forumName, Document doc) {
		Elements topics = doc.select("a.topictitle");
		System.out.println("Number of topics for [" + forumName + "]: " + topics.size());
		if (topics.isEmpty()) return;
		Iterator<Element> it = topics.iterator();
		while (it.hasNext())
			processTopic(dir, forumName, it.next());
	}
	
	static void processTopic(String dir, String forumName, Element topic) {
		
		Element row = topic.parent().parent().parent().parent();
		Elements col2 = row.select("td:eq(2)");
		Elements col3 = row.select("td:eq(3)");
		Elements col4 = row.select("td:eq(4)");
		Elements col5 = row.select("td:eq(5)");
		Elements col6 = row.select("td:eq(6)");
		
		currentBuffer = new StringBuffer();
		currentFileNumber = 1;
		
		String h = topic.attr("href");
		String topicDir = h.substring(1);
		System.out.println("[" + forumName + "] topicDir [" + topicDir + "] col2 [" + col2.text() + "] col3 [" + col3.text() + "] col4 [" + col4.text() + "] col5 [" + col5.text() + "] col6 [" + col6.text() + "]");
		//if (!topicDir.endsWith("video-about-the-zodiac-ciphers-on-national-geographic")) return;
		postsDir = dir + "/" + topicDir;
		makeDirectory(postsDir);
		String linkHref = urlFrom(h);
		String linkText = topic.text();
		//System.out.println("Topic name: " + linkText);
		//System.out.println("Topic URL: " + linkHref);

		ScrapeZKS.topicDir = topicDir;
		List<Document> docs = pages(linkHref); // returns one doc if only one page, otherwise returns all pages.
		int pageNum = 0;
		for (Document doc : docs) {
			processPosts(pageNum++, postsDir, linkText, doc); // dive into all posts
		}
		
		writeBuffer();
		
	}
	
	static void writeBuffer() {
		System.out.println("Writing buffer...");
		
		if (currentBuffer.length() == 0) {
			System.out.println("ERROR: Buffer has length Zero!");
			System.out.println("ERROR: LAST GET: " + lastGet);
		}
		
		FileUtil.writeText(prefix + postsDir + "/" + (currentFileNumber++) + ".bbcode", currentBuffer.toString());
		currentBuffer = new StringBuffer();
	}
	
	static void processPosts(int pageNum, String postsDir, String topicName, Document doc) {
		Elements posts = doc.select("tr.post");
		Iterator<Element> it = posts.iterator();
		while (it.hasNext()) {
			processPost(it.next(), postsDir, pageNum);
		}
	}
	
	static void processPost(Element row, String postsDir, int pageName) {
		//System.out.println("ROW " + row.toString());
		String postId = row.attr("id").substring(1);
		String posterName = row.select("span.name").select("strong").select("a").text();
		String posterImgSrc = "";
		try {
			Elements e1 = row.select("span.poster-profile");
			Elements e2 = e1.select("a");
			Elements e3 = e2.select("img");
			Element e4 = e3.first();
			String src = e4.attr("src");
			posterImgSrc = src;
		} catch (Exception e) {}
		String postEditLink = urlFrom("/post?p=" + postId + "&mode=editpost");
		
		if (row.select("img[src=http://illiweb.com/fa/prosilver/icon_post_edit_en.png]").isEmpty()) {
			System.out.println("CANNOT EDIT POST: " + postId + " - " + postsDir);
			return;
		}
		Element postDetails = row.select("span.postdetails").get(1);
		String details = postDetails.text();
		
		System.out.println("    - processing post id " + postId + " by " + posterName + " details " + details);
		
		// get the bbcode by opening the edit link
		String bbcode = null;
		while (bbcode == null) {
			Document doc = get(postEditLink);
			//System.out.println(doc.toString());
			Element textarea = doc.select("textarea").first();
			try {
				bbcode = ((TextNode)textarea.childNode(0)).getWholeText();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("DOC: " + doc.html());
				System.out.println("Error, trying again...");
				try {
					Thread.sleep(5000); // take a break in case the server is complaining.
					login(); // new session
				}
				catch (Exception ex) {};
			}
		}
		
		// create a heading for use on the new forum
		StringBuffer sb = new StringBuffer();
		add(sb,"[img]http://zodiackillerciphers.com/images/p-nav-shadow2.png[/img]");
		add(sb,"[img]" + posterImgSrc + "[/img]");
		add(sb,"[b][u]" + escapeHtml(posterName) + "[/u][/b], [i]" + details + "[/i]");
		add(sb,"");
		add(sb, bbcode);
		add(sb, "");
		
		if (currentBuffer.length() + sb.length() > 58000) { 
			writeBuffer();
		}
		
		currentBuffer.append(sb);
		
		//FileUtil.writeText(prefix + postsDir + "/" + postId + ".bbcode", sb.toString());
	}
	
	static void add(StringBuffer sb, String str) {
		sb.append(str).append(System.getProperty("line.separator"));
	}
	
	static List<Document> pages(String url) {
		List<Document> docs = new ArrayList<Document>();
		System.out.println(" - Page 1");
		Document firstPage = get(url);
		docs.add(firstPage);
		if (isPaginated(firstPage)) {
			List<String> pageLinks = pageLinksFor(firstPage);
			for (int i=0; i<pageLinks.size(); i++) {
				System.out.println(" - Page " + (i+2));
				docs.add(get(urlFrom(pageLinks.get(i))));
			}
		}
		return docs;
	}
	
	static boolean isPaginated(Document doc) {
		return (!pageLinksFor(doc).isEmpty());
	}
	
	static List<String> pageLinksFor(Document doc) {
		List<String> list = new ArrayList<String>();
		Elements links = doc.select("tr").select("td.row1[colspan=7]").select("a");
		Iterator<Element> it = links.iterator();
		Element link;
		
		while (it.hasNext()) {
			link = it.next();
			Integer pageNum = toInt(link.text());
			if (pageNum != null) {
				list.add(link.attr("href"));
			}
		}
		if (!list.isEmpty()) return list;
		
		Integer max = null;;
		links = doc.select("tr").select("td.row1.pagination").select("a");
		it = links.iterator();
		while (it.hasNext()) {
			link = it.next();
			Integer pageNum = toInt(link.text());
			if (pageNum != null) 
				if (max == null) max = pageNum;
				else 
					max = pageNum;
		}
		if (max == null) return list;
		//t921-z340-and-the-sacred-nine
		String s1 = topicDir.substring(0,topicDir.indexOf('-'));
		String s2 = topicDir.substring(topicDir.indexOf('-'));
		for (int i=1; i<max; i++) {
			list.add("/" + s1 + "p" + (i*15) + s2);
		}
		return list;
	}
	
	static Integer toInt(String val) {
		Integer i = null;
		try {
			i = Integer.valueOf(val);
		} catch (Exception e) {}
		return i;
	}
	
	static String urlFrom(String href) {
		String s;
		if (href.startsWith("/")) s = "";
		else s = "/";
		return "http://zodiackillersite.forummotion.com" + s + href;
	}
	
	static Document get(String url) {
		System.out.println("Getting URL [" + url + "]");
		boolean go = true;
		StringBuffer result = null;
		while (go) {
			go = false;
			try {
				Thread.sleep(500); // avoid hammering the server
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				go = true;
			}
			result = new StringBuffer();
			try {
				httpget = new HttpGet(url);
				entity = httpclient.execute(httpget).getEntity();
				// Get the response
				BufferedReader rd = new BufferedReader
				  (new InputStreamReader(entity.getContent()));
				String line = "";
				while ((line = rd.readLine()) != null) {
					add(result, line);
				} 			
				
			} catch (Exception e) {
				e.printStackTrace();
				go = true;
				continue;
			}
			
			if (result.toString().contains("You can return to the index")) {
				System.out.println("ERROR: Bad URL: [" + url + "]");
				go = true;
			} else if (result.toString().contains("It appears that your computer has made too many requests on the same page recently")) {
				go = true;
				System.out.println("ERROR: Slowing down and trying again, because we got the 'too many requests' warning...");
				try {
					Thread.sleep(5000); // sleep 5s
				} catch (Exception e) {
					;
				}
			} else if (result.toString().contains("Your website will be back online soon")) {
				go = true;
				System.out.println("ERROR: Slowing down and trying again, because we got the 'maintenance' warning...");
				try {
					Thread.sleep(30000); // sleep 30s
					login();
				} catch (Exception e) {
					;
				}
				
			} else if (result.toString().contains("<b>General Error</b>")) {
				go = true;
				System.out.println("ERROR: Slowing down and trying again, because we got the 'General Error' (user profile) error...");
				try {
					Thread.sleep(30000); // sleep 30s
					login();
				} catch (Exception e) {
					;
				}
			} else go = false;
		}
		lastGet = result.toString();
		return Jsoup.parse(fix(result.toString()));

		
	}
	
	public static String fix(String str) {
		int[] sizes1 = new int[] {7,9,18,24};
		int[] sizes2 = new int[] {50,85,150,200};
		
		String newString = str;
		for (int i=0; i<sizes1.length; i++) {
			newString = newString.replaceAll("\\[size=" + sizes1[i] + "\\]", "[size=" + sizes2[i] + "]");
		}
		newString = newString.replaceAll("\\[b\\]\\[u\\]\\[/u\\]\\[/b\\]", "[b][u]Guest[/u][/b]");
		return newString;
	}
	
	public static void main(String[] args) throws Exception {
		try {
			scrape();
			//System.out.println("abc [b][u][/u][/b]".replaceAll("\\[b\\]\\[u\\]\\[/u\\]\\[/b\\]", "[b][u]Guest[/u][/b]"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
