
public class MudRun {
	
	static int total = 0;
	public static void count(int n, String prefix) {
		for (int i=0; i<=n; i++) {
			String pad = "";
			if (i<1000) pad += "0";
			if (i<100) pad += "0";
			if (i<10) pad += "0";
			System.out.println(prefix+pad+i+"&p=");
			total++;
			if (total % 500 == 0 ) System.out.println(" === SPLIT === ");
		}
	}
	public static void main(String[] args) {
		count(597, "http://www.photoreflect.com/web/bin/prpv.dll?cmd=photo&s=0&i=0J7K018F00");
		count(76,"http://www.photoreflect.com/web/bin/prpv.dll?cmd=photo&s=0&i=0J7K018C00");
		count(435,"http://www.photoreflect.com/web/bin/prpv.dll?cmd=photo&s=0&i=0J7K018D00");
		count(2285,"http://www.photoreflect.com/web/bin/prpv.dll?cmd=photo&s=0&i=0J7K018E00");
		count(566,"http://www.photoreflect.com/web/bin/prpv.dll?cmd=photo&s=0&i=0J7K018000");
		count(63,"http://www.photoreflect.com/web/bin/prpv.dll?cmd=photo&s=0&i=0J7K017T00");
		count(812,"http://www.photoreflect.com/web/bin/prpv.dll?cmd=photo&s=0&i=0J7K018200");
		count(934,"http://www.photoreflect.com/web/bin/prpv.dll?cmd=photo&s=0&i=0J7K017S00");
	}
}
