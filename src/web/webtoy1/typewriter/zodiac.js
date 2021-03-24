/* many thanks to http://mspeight.blogspot.com/2007/05/how-to-disable-backspace-in-ie-and.html for this delete/backspace trapper */
if (typeof window.event != 'undefined') // IE
  document.onkeydown = function() // IE
    {
    var t=event.srcElement.type;
    var kc=event.keyCode;
    return ((kc != 8) || ( t == 'text') ||
             (t == 'textarea') || ( t == 'submit'))
    }
else
  document.onkeypress = function(e)  // FireFox/Others 
    {
    var t=e.target.type;
    var kc=e.keyCode;
    if ((kc != 8) || ( t == 'text') ||
        (t == 'textarea') || ( t == 'submit'))
        return true
    else {
        return false
    }
   }

	var doStats = true;
	var stats = new Array(2);
	var frequencies = new Array();
	var statsSortedKeys = new Array(2);

	var letterFrequencies = new Array(
	);
	letterFrequencies["A"] = 0.08167;
	letterFrequencies["B"] = 0.01492;	
	letterFrequencies["C"] = 0.02782;
	letterFrequencies["D"] = 0.04253; 	
	letterFrequencies["E"] = 0.12702; 	
	letterFrequencies["F"] = 0.02228; 	
	letterFrequencies["G"] = 0.02015; 	
	letterFrequencies["H"] = 0.06094; 	
	letterFrequencies["I"] = 0.06966; 	
	letterFrequencies["J"] = 0.00153; 	
	letterFrequencies["K"] = 0.00772; 	
	letterFrequencies["L"] = 0.04025; 	
	letterFrequencies["M"] = 0.02406; 	
	letterFrequencies["N"] = 0.06749;
	letterFrequencies["O"] = 0.07507;
	letterFrequencies["P"] = 0.01929;
	letterFrequencies["Q"] = 0.00095;
	letterFrequencies["R"] = 0.05987;
	letterFrequencies["S"] = 0.06327;
	letterFrequencies["T"] = 0.09056;
	letterFrequencies["U"] = 0.02758;
	letterFrequencies["V"] = 0.00978;
	letterFrequencies["W"] = 0.02360;
	letterFrequencies["X"] = 0.00150;
	letterFrequencies["Y"] = 0.01974;
	letterFrequencies["Z"] = 0.00074;
	
	
	var images = new Array( "alphabet/a.jpg","alphabet/b.jpg","alphabet/bb.jpg","alphabet/bc.jpg","alphabet/bd.jpg","alphabet/bf.jpg","alphabet/bj.jpg","alphabet/bk.jpg","alphabet/bl.jpg",
	"alphabet/bp.jpg","alphabet/bq.jpg","alphabet/by.jpg","alphabet/c.jpg","alphabet/caret.jpg","alphabet/d.jpg","alphabet/dash.jpg","alphabet/dot.jpg","alphabet/e.jpg",
	"alphabet/f.jpg","alphabet/g.jpg","alphabet/gt.jpg","alphabet/h.jpg","alphabet/i.jpg","alphabet/idl.jpg","alphabet/idr.jpg","alphabet/j.jpg","alphabet/k.jpg",
	"alphabet/l.jpg","alphabet/lt.jpg","alphabet/m.jpg","alphabet/n.jpg","alphabet/n1.jpg","alphabet/n2.jpg","alphabet/n3.jpg","alphabet/n4.jpg","alphabet/n5.jpg",
	"alphabet/n6.jpg","alphabet/n7.jpg","alphabet/n8.jpg","alphabet/n9.jpg","alphabet/o.jpg","alphabet/p.jpg","alphabet/perp.jpg","alphabet/pf.jpg","alphabet/phi.jpg",
	"alphabet/plus.jpg","alphabet/r.jpg","alphabet/s.jpg","alphabet/slash.jpg","alphabet/sq.jpg","alphabet/sqd.jpg","alphabet/sqe.jpg","alphabet/sql.jpg","alphabet/sqr.jpg",
	"alphabet/t.jpg","alphabet/theta.jpg","alphabet/u.jpg","alphabet/v.jpg","alphabet/w.jpg","alphabet/x.jpg","alphabet/y.jpg","alphabet/z.jpg","alphabet/zodiac.jpg");

	function preload() {
		var objImage = new Image();
		for	(i=0; i<images.length; i++)
		{
			objImage.src = images[i];
		}
	}

	function triTest() {
		for (i=0; i<20; i++) {
			for (j=0; j<20; j++) {
				if (i!=j) {
					if (tri[i].charAt(2) == tri[j].charAt(0)) {
						document.write(tri[i]+tri[j].substring(1,3) + ".  ");
					}
				}
			}
		}
	}

	var tri = new Array("the",
	"ing",
	"her",
	"you",
	"thi",
	"ill",
	"eth",
	"all",
	"and",
	"nth",
	"ave",
	"tha",
	"sth",
	"tth",
	"hat",
	"out",
	"his",
	"was",
	"ent",
	"int",
	"one",
	"hem",
	"hav",
	"ver",
	"ith",
	"ist",
	"ere",
	"wil",
	"hen",
	"ont",
	"for",
	"hin",
	"ter",
	"est",
	"oth",
	"not",
	"wit",
	"dth",
	"ast",
	"rth",
	"fth",
	"lli",
	"utt",
	"lle",
	"ome",
	"but",
	"eve",
	"ght",
	"sha",
	"ers",
	"tin",
	"ish",
	"ngt",
	"heb",
	"hey",
	"ear",
	"ple",
	"she",
	"hep",
	"kin",
	"lin",
	"ein",
	"oul",
	"hes",
	"who",
	"uld",
	"eni",
	"edt",
	"era",
	"ice",
	"kil",
	"abo",
	"are",
	"eri",
	"oft",
	"som",
	"sis",
	"bou",
	"res",
	"par",
	"rea",
	"edi",
	"tor",
	"rin",
	"ive",
	"iss",
	"ell",
	"ack",
	"hew",
	"hal",
	"ton",
	"ert",
	"ati",
	"tto",
	"igh",
	"sse",
	"ore",
	"hel",
	"ort",
	"iti",
	"hec",
	"our",
	"red",
	"oun",
	"eof",
	"sho",
	"whe",
	"ate",
	"odi",
	"emi",
	"ons",
	"ran",
	"iam",
	"eco",
	"sta",
	"att",
	"dia",
	"ove",
	"ion",
	"art",
	"ndt",
	"idi",
	"yth",
	"ead",
	"con",
	"unt",
	"der",
	"nge",
	"eca",
	"ngi",
	"itw",
	"ath",
	"hed",
	"eli",
	"lic",
	"nce",
	"sed",
	"wou",
	"isi",
	"peo",
	"een",
	"tot",
	"ean",
	"lea",
	"llo",
	"tof",
	"ero",
	"iha",
	"eto",
	"tis",
	"own",
	"han",
	"way",
	"nto",
	"las",
	"sto",
	"led",
	"llt",
	"nin",
	"tim",
	"san",
	"dit");


	var which = 0;

		var cipherLength = new Array(2);
		var cipher = new Array(
			new Array(
		"HER>pl^VPk|1LTG2d",
	  "Np+B(#O%DWY.<*Kf)",
	  "By:cM+UZGW()L#zHJ",
	  "Spp7^l8*V3pO++RK2",
	  "_9M+ztjd|5FP+&4k/",
	  "p8R^FlO-*dCkF>2D(",
	  "#5+Kq%;2UcXGV.zL|",
	  "(G2Jfj#O+_NYz+@L9",
	  "d<M+b+ZR2FBcyA64K",
	  "-zlUV+^J+Op7<FBy-",
	  "U+R/5tE|DYBpbTMKO",
	  "2<clRJ|*5T4M.+&BF",
	  "z69Sy#+N|5FBc(;8R",
	  "lGFN^f524b.cV4t++",
	  "yBX1*:49CE>VUZ5-+",
	  "|c.3zBK(Op^.fMqG2",
	  "RcT+L16C<+FlWB|)L",
	  "++)WCzWcPOSHT/()p",
	  "|FkdW<7tB_YOB*-Cc",
	  ">MDHNpkSzZO8A|K;+"
	), new Array(
		"9%P/Z/UB%kOR=pX=B",
		"WV+eGYF69HP@K!qYe", 
		"MJY^UIk7qTtNQYD5)", 
		"S(/9#BPORAU%fRlqE", 
		"k^LMZJdr\\pFHVWe8Y",
		"@+qGD9KI)6qX85zS(",
		"RNtIYElO8qGBTQS#B",
		"Ld/P#B@XqEHMU^RRk",
		"cZKqpI)Wq!85LMr9#",
		"BPDR+j=6\\N(eEUHkF",
		"ZcpOVWI5+tL)l^R6H",
		"I9DR_TYr\\de/@XJQA",
		"P5M8RUt%L)NVEKH=G",
		"rI!Jk598LMlNA)Z(P",
		"zUpkA9#BVW\\+VTtOP", 
		"^=SrlfUe67DzG%%IM",
		"Nk)ScE/9%%ZfAP#BV",
		"peXqWq_F#8c+@9A9B",
		"%OT5RUc+_dYq_^SqW",
		"VZeGYKE_TYA9%#Lt_",
		"H!FBX9zXADd\\7L!=q",
		"_ed##6e5PORXQF%Gc",
		"Z@JTtq_8JI+rBPQW6",
		"VEXr9WI6qEHM)=UIk"
		)
	);

	var cipherReset = new Array();
	for (i=0; i<cipher.length; i++) {
		cipherReset[i] = cipher[i].slice();
	}
	
		var cipherline = new Array(
		"HER>pl^VPk|1LTG2d"+
	  "Np+B(#O%DWY.<*Kf)"+
	  "By:cM+UZGW()L#zHJ"+
	  "Spp7^l8*V3pO++RK2"+
	  "_9M+ztjd|5FP+&4k/"+
	  "p8R^FlO-*dCkF>2D("+
	  "#5+Kq%;2UcXGV.zL|"+
	  "(G2Jfj#O+_NYz+@L9"+
	  "d<M+b+ZR2FBcyA64K"+
	  "-zlUV+^J+Op7<FBy-"+
	  "U+R/5tE|DYBpbTMKO"+
	  "2<clRJ|*5T4M.+&BF"+
	  "z69Sy#+N|5FBc(;8R"+
	  "lGFN^f524b.cV4t++"+
	  "yBX1*:49CE>VUZ5-+"+
	  "|c.3zBK(Op^.fMqG2"+
	  "RcT+L16C<+FlWB|)L"+
	  "++)WCzWcPOSHT/()p"+
	  "|FkdW<7tB_YOB*-Cc"+
	  ">MDHNpkSzZO8A|K;+",
	
		"9%P/Z/UB%kOR=pX=B" + 
		"WV+eGYF69HP@K!qYe" + 
		"MJY^UIk7qTtNQYD5)" + 
		"S(/9#BPORAU%fRlqE" + 
		"k^LMZJdr\\pFHVWe8Y" +
		"@+qGD9KI)6qX85zS(" +
		"RNtIYElO8qGBTQS#B" +
		"Ld/P#B@XqEHMU^RRk" +
		"cZKqpI)Wq!85LMr9#" +
		"BPDR+j=6\\N(eEUHkF" +
		"ZcpOVWI5+tL)l^R6H" +
		"I9DR_TYr\\de/@XJQA" +
		"P5M8RUt%L)NVEKH=G" +
		"rI!Jk598LMlNA)Z(P" +
		"zUpkA9#BVW\\+VTtOP" + 
		"^=SrlfUe67DzG%%IM" +
		"Nk)ScE/9%%ZfAP#BV" +
		"peXqWq_F#8c+@9A9B" +
		"%OT5RUc+_dYq_^SqW" +
		"VZeGYKE_TYA9%#Lt_" +
		"H!FBX9zXADd\\7L!=q" +
		"_ed##6e5PORXQF%Gc" +
		"Z@JTtq_8JI+rBPQW6" +
		"VEXr9WI6qEHM)=UIk"
	);

	var alphabet = new Array("ABCDEFGH|JKLMNOPRSTUVWXYZ123456789plkdfycjqbtz()>^+.<-/#_@*%&;:", "ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefjklpqrtz!#%()=+/@\\^_"
	);

		var interestingHash = new Array(new Array(
/*			{name:"killing",decoder:"llvicdlotkrbeggbwmnsehontiitaapinegimteseielldstbirvkaeeibhsgtw"},
			{name:"screaming",decoder:"ostpagnepeiearenmvrrapjytrspuspgalcihodtccrohorspsoestoataaebel"},
			{name:"robberies",decoder:"iotpaisepbboarenovrrapjytsnhrsosnmrssostecfthrbnpioeaatyeountyn"},
			{name:"killedher",decoder:"eeltshavesutlmrpfoeiototaukcttriblnpsrwaoetshdgnicehlnhaioiirnh"},
			{name:"zodiac",decoder:"hictedevniumtnrpfoesotpdtzacitukgaigslnhoetsehaoncehegmnesorard"},
			{name:"murderer",decoder:"tispaedepstsarenuvrrahdytsnsisormrtrnorticenhamhpeothgiitiosaka"},
			{name:"murder banjo",decoder:"eaeteenmnslohiobmoogrietujlaeeeydtrdayocteeeeameueewieefnelreeh"},
		    {name:"got followed",decoder:"eeetoiagdwueyohwtaceotehehnarfeittolelmrneeehaimflsemertnesaeea"},
			{name:"last inches",decoder:"eaetudeotnsoiiaoteshjienwhtrbeewaeneyhpvhoeerdshingcheelleesaee"},
			{name:"another vallejo",decoder:"eoevohnzetoeeseudiashaelemdastesltacriaujyeetitbisnleeehoejrbet"},
			{name:"cages of the north",decoder:"eoebieocapeeahhepeseroeutgfheeeleterctsisaeewirthantleeteeiaeen"},
			{name:"toss blind",decoder:"eataoaetldvsetedsahvnulsalegehdkllblaasliaetonnhsieevicbneicsol"},
			{name:"shall cause slaves death",decoder:"?aiaoastl?v?eledsah?nu?s??hg?hdkl?ll?asvs?eton?l?iae?tcb??ic??e"},
			{name:"because school death",decoder:"oughasirt?a?blp?iaaoalkl?iovidd?snetha?zl?s?eeic??celcd?otlcahl"},
			{name:"halloween killing",decoder:"lig??ealdn????ahslo??mkcii??iilounno??eiwxftd?ee?yh?z?wta?lu?nl"},
			{name:"paradice afterlife",decoder:"?eh?iikga?mlrlecr?eugkeb?sa?ed??ettfare?sabtpnilnleec?fi??oe?cm"},
			{name:"at the current time i new the bombs were on the",decoder:"ctamtsmanfettnbrtsiirbgsnteshfasfdeceioh-sscfghnhuwwessefgroffe"},
			{name:"happy radio dud",decoder:"ostpaonepegeorhodoirapjyarupedpdaleusudtccrehkrshdousisanatabel"},
			{name:"nice some sisters white",decoder:"?m???ee?n???????ite?t?????t?hci?hs?t???e???iosa???ss???r??i?ow?"}*/
		), new Array(
			                               //ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefjklpqrtz!#%()*+/@\^_
			{name:"The correct one",decoder:"wlnesattfsthenifgaoibeouetesaivocdxiaemrrdollnhpeksrny"}/*,
			{name:"experiment398-01",decoder:"tl?eb????w???s??toteare??a??aes?h???ti????l?l???t?r?na"},
			{name:"experiment439-01",decoder:"ke?ddk???n??????i?pnpo?ieo??snt?n????s????e?e????????i"}*/
			
		));

	var commonWords = new Array(
		"the", "name", "of", "very", "to", "through", "and", "just", "a", "form", "in", "sentence", "is", "great", "it",
		"think", "you", "say", "that", "help", "he", "low", "was", "line", "for", "differ", "on", "turn", "are", "cause",
		"with", "much", "as", "mean", "I", "before", "his", "move", "they", "right", "be", "boy", "at", "old", "one",
		"too", "have", "same", "this", "tell", "from", "does", "or", "set", "had", "three", "by", "want", "hot", "air",
		"word", "well", "but", "also", "what", "play", "some", "small", "we", "end", "can", "put", "out", "home", "other",
		"read", "were", "hand", "all", "port", "there", "large", "when", "spell", "up", "add", "use", "even", "your",
		"land", "how", "here", "said", "must", "an", "big", "each", "high", "she", "such", "which", "follow", "do", "act",
		"their", "why", "time", "ask", "if", "men", "will", "change", "way", "went", "about", "light", "many", "kind",
		"then", "off", "them", "need", "write", "house", "would", "picture", "like", "try", "so", "us", "these", "again",
		"her", "animal", "long", "point", "make", "mother", "thing", "world", "see", "near", "him", "build", "two", "self",
		"has", "earth", "look", "father", "more", "head", "day", "stand", "could", "own", "go", "page", "come", "should",
		"did", "country", "number", "found", "sound", "answer", "no", "school", "most", "grow", "people", "study", "my",
		"still", "over", "learn", "know", "plant", "water", "cover", "than", "food", "call", "sun", "first", "four", "who",
		"between", "may", "state", "down", "keep", "side", "eye", "been", "never", "now", "last", "find", "let", "any",
		"thought", "new", "city", "work", "tree", "part", "cross", "take", "farm", "get", "hard", "place", "start", "made",
		"might", "live", "story", "where", "saw", "after", "far", "back", "sea", "little", "draw", "only", "left", "round",
		"late", "man", "run", "year", "dont", "came", "while", "show", "press", "every", "close", "good", "night", "me",
		"real", "give", "life", "our", "few", "under", "north", "open", "ten", "seem", "simple", "together", "several",
		"next", "vowel", "white", "toward", "children", "war", "begin", "lay", "got", "against", "walk", "pattern",
		"example", "slow", "ease", "center", "paper", "love", "group", "person", "always", "money", "music", "serve",
		"those", "appear", "both", "road", "mark", "map", "often", "rain", "letter", "rule", "until", "govern", "mile",
		"pull", "river", "cold", "car", "notice", "feet", "voice", "care", "unit", "second", "power", "book", "town",
		"carry", "fine", "took", "certain", "science", "fly", "eat", "fall", "room", "lead", "friend", "cry", "began",
		"dark", "idea", "machine", "fish", "note", "mountain", "wait", "stop", "plan", "once", "figure", "base", "star",
		"hear", "box", "horse", "noun", "cut", "field", "sure", "rest", "watch", "correct", "color", "able", "face",
		"pound", "wood", "done", "main", "beauty", "enough", "drive", "plain", "stood", "girl", "contain", "usual",
		"front", "young", "teach", "ready", "week", "above", "final", "ever", "gave", "red", "green", "list", "oh",
		"though", "quick", "feel", "develop", "talk", "ocean", "bird", "warm", "soon", "free", "body", "minute",
		"dog", "strong", "family", "special", "direct", "mind", "pose", "behind", "leave", "clear", "song", "tail",
		"measure", "produce", "door", "fact", "product", "street", "black", "inch", "short", "multiply", "numeral",
		"nothing", "class", "course", "wind", "stay", "question", "wheel", "happen", "full", "complete", "force",
		"ship", "blue", "area", "object", "half", "decide", "rock", "surface", "order", "deep", "fire", "moon",
		"south", "island", "problem", "foot", "piece", "system", "told", "busy", "knew", "test", "pass", "record",
		"since", "boat", "top", "common", "whole", "gold", "king", "possible", "space", "plane", "heard", "stead",
		"best", "dry", "hour", "wonder", "better", "laugh", "true", "thousand", "during", "ago", "hundred", "ran",
		"five", "check", "remember", "game", "step", "shape", "early", "equate", "hold", "hot", "west", "miss", "ground",
		"brought", "interest", "heat", "reach", "snow", "fast", "tire", "verb", "bring", "sing", "yes", "listen",
		"distant", "six", "fill", "table", "east", "travel", "paint", "less", "language", "morning", "among", "grand",
		"cat", "ball", "century", "yet", "consider", "wave", "type", "drop", "law", "heart", "bit", "am", "coast",
		"present", "copy", "heavy", "phrase", "dance", "silent", "engine", "tall", "position", "sand", "arm", "soil",
		"wide", "roll", "sail", "temperature", "material", "finger", "size", "industry", "vary", "value", "settle",
		"fight", "speak", "lie", "weight", "beat", "general", "excite", "ice", "natural", "matter", "view", "circle",
		"sense", "pair", "ear", "include", "else", "divide", "quite", "syllable", "broke", "felt", "case", "perhaps",
		"middle", "pick", "kill", "sudden", "son", "count", "lake", "square", "moment", "reason", "scale", "length",
		"loud", "represent", "spring", "art", "observe", "subject", "child", "region", "straight", "energy", "consonant",
		"hunt", "nation", "probable", "dictionary", "bed", "milk", "brother", "speed", "egg", "method", "ride", "organ",
		"cell", "pay", "believe", "age", "fraction", "section", "forest", "dress", "sit", "cloud", "race", "surprise",
		"window", "quiet", "store", "stone", "summer", "tiny", "train", "climb", "sleep", "cool", "prove", "design",
		"lone", "poor", "leg", "lot", "exercise", "experiment", "wall", "bottom", "catch", "key", "mount", "iron", "wish",
		"single", "sky", "stick", "board", "flat", "joy", "twenty", "winter", "skin", "sat", "smile", "written",
		"crease", "wild", "hole", "instrument", "trade", "kept", "melody", "glass", "trip", "grass", "office", "cow",
		"receive", "job", "row", "edge", "mouth", "sign", "exact", "visit", "symbol", "past", "die", "soft", "least",
		"fun", "trouble", "bright", "shout", "gas", "except", "weather", "wrote", "month", "seed", "million", "tone",
		"bear", "join", "finish", "suggest", "happy", "clean", "hope", "break", "flower", "lady", "clothe", "yard",
		"strange", "rise", "gone", "bad", "jump", "blow", "baby", "oil", "eight", "blood", "village", "touch", "meet",
		"grew", "root", "cent", "buy", "mix", "raise", "team", "solve", "wire", "metal", "cost", "whether", "lost",
		"push", "brown", "seven", "wear", "paragraph", "garden", "third", "equal", "shall", "sent", "held", "choose",
		"hair", "fell", "describe", "fit", "cook", "flow", "floor", "fair", "either", "bank", "result", "collect",
		"burn", "save", "hill", "control", "safe", "decimal", "gentle", "truck", "woman", "noise", "captain", "level",
		"practice", "chance", "separate", "gather", "difficult", "shop", "doctor", "stretch", "please", "throw",
		"protect", "shine", "noon", "property", "whose", "column", "locate", "molecule", "ring", "select", "character",
		"wrong", "insect", "gray", "caught", "repeat", "period", "require", "indicate", "broad", "radio", "prepare",
		"spoke", "salt", "atom", "nose", "human", "plural", "history", "anger", "effect", "claim", "electric",
		"continent", "expect", "oxygen", "crop", "sugar", "modern", "death", "element", "pretty", "hit", "skill",
		"student", "women", "corner", "season", "party", "solution", "supply", "magnet", "bone", "silver", "rail",
		"thank", "imagine", "branch", "provide", "match", "agree", "suffix", "thus", "especially", "capital", "fig",
		"wont", "afraid", "chair", "huge", "danger", "sister", "fruit", "steel", "rich", "discuss", "thick", "forward",
		"soldier", "similar", "process", "guide", "operate", "experience", "guess", "score", "necessary", "apple",
		"sharp", "bought", "wing", "led", "create", "pitch", "neighbor", "coat", "wash", "mass", "bat", "card", "rather",
		"band", "crowd", "rope", "corn", "slip", "compare", "win", "poem", "dream", "string", "evening", "bell",
		"condition", "depend", "feed", "meat", "tool", "rub", "total", "tube", "basic", "famous", "smell", "dollar",
		"valley", "stream", "nor", "fear", "double", "sight", "seat", "thin", "arrive", "triangle", "master", "planet",
		"track", "hurry", "parent", "chief", "shore", "colony", "division", "clock", "sheet", "mine", "substance",
		"tie", "favor", "enter", "connect", "major", "post", "fresh", "spend", "search", "chord", "send", "fat",
		"yellow", "glad", "gun", "original", "allow", "share", "print", "station", "dead", "dad", "spot", "bread",
		"desert", "charge", "suit", "proper", "current", "bar", "lift", "offer", "rose", "segment", "continue",
		"slave", "block", "duck", "chart", "instant", "hat", "market", "sell", "degree", "success", "populate",
		"company", "chick", "subtract", "dear", "event", "enemy", "particular", "reply", "deal", "drink", "swim",
		"occur", "term", "support", "opposite", "speech", "wife", "nature", "shoe", "range", "shoulder", "steam",
		"spread", "motion", "arrange", "path", "camp", "liquid", "invent", "log", "cotton", "meant", "born",
		"quotient", "determine", "teeth", "quart", "shell", "nine", "neck"
	);


	var zodiacWords = new Array( "a","abnomily","abot","about","accidents","across","acting","activity","ad","adapted","additional","address","adhesive","adjusted","ads","advertisement","afraid","after","afterlife","afternoon","again","aim","airplane","alive","all","alley","alleys","allways","alone","along","also","always","am","ammo","ammonium","an","anamal","and","anger","angry","anilating",
"ann","announce","anonymously","another","answer","ant","any","anyone","anyway","apart","aprox","are","area","aredead","army","arose","around","arthur","as","ask","asked","asking","ass","asses","at","attention","attn","aug","autographs","averly","avery","awake","away","awfully","baby","babysits","back","backwards","bad","badlands","bag","bags","ball","ban","banjo",
"barrel","basement","bat","bates","battered","battery","bay","bayonet","be","beam","beautiful","because","become","beef","been","before","began","behind","being","belli","best",
"better","betty","beware","big","billiard","billowy","black","blank","blanks","blast","block","blocks","blond","blood","bloodsoak","bluber","blue","bluff","blurb","bomb","boo",
"booboos","book","booth","boots","bore","bought","boughten","bouncing","boy","brand","breast","broke","brown","brunett","brush","bryan","bulb","bullet","bullshit","burn","burned","burning","bury","bus","buss","busses","bussy","but","buton","butons","buttons","by","bye","cab","caen","cages","calif","call","called","calvin","came","can","cancel","cannot","capable","car","cardboard","cars","catch","caught","cecelia","ceiling","cell","cement","cene","cent","center","centuries","cerous","change","chapter","check","checks","cheer","cherry","chief","children","chocked","christmas","christmass","chronicle","cicles","cid","cipher","circle","citizen","city","clean","clever","clews","clock","closeing","clowns","coated","coats","code","collect","collecting","columbus","column","come","comidy","commic","committ","complet","complete","complied","comply","concern","concerning","concerns","conditions","confession","conscience","considerably","consists","consternation","consternt","contains","continually","continues","contrary","control","controol","cop","copper","cops","corner","could","count","country","county","coupled","coupple","coupples","cover","coverage","crack","cracked","crackproof","credit","crime","crooked","cruse","cruzeing","cry","cues","cut","cutting","cyipher","daily","damn","dangertue","darck","dark","darkened","darlene","date","dates","daughters","david","dead","dear","death","deep","delicious","deplorable","deposit","des","descise","describe","described","description","deserve","detail","details","developer","diablo","did","die","died","different","dificult","dig","directed","disappeared","disorder","distributor","do","doesnt","dogs","doing","dont","doo","doomed","door","dot","double","down","dozen","dragon","draining","draw","dress","dressed","drew","dripping","driven","driver","drivers","drove","drownding","dud","dump","dungen","during","each","east","easy","eat","eats","ebeorietemethhpitithe","echo","editor","efect","efective","either","electric","elimination","elizabeth","else","elses","end","ended","engine","enjoy","enough","enterprise","entirle","envelope","epasode","etc","even","evening","events","ever","evere","every","everyone","everything","evidenced","examiner","except","exorcist","experence","expression","extreamly","face","fact","facts","fake","fall","faraday","fart","fat","featuring","fed","feel","feet","fellows","felt","female","ferrin","fertilizer","few","fiddle","figgure","filling","find","finding","fingerprints","fingertip","fingertips","finish","finished","fire","fired","fireing","firm","first","flabby","flash","flicting","floor","foe","followed","following","for","forest","forrest","fought","found","four","fran","francisco","friend","from","front","frunt","fry","full","fun","funny","future","gal","game","gave","geary","get","getting","ghia","gilbert","girl","girls","give","given","gives","glorification","glory","glove","gloves","go","going","good","goof","gorged","got","grabbed","grave","gravel","great","groups","grown","guards","guess","gummed","gun","guy","had","half","hand","hands","handwritten","hang","happen","happy","hard","hartnell","has","hate","have","having","he","head","headquarters","heads","hear","heat","heights","hell","hellhole","help","her","herald","herb","here","herman","hey","high","hill","hills","him","himself","his","hit","hold","holding","hole","holes","holly","home","homicide","hope","horizon","horrible","hose","how","howers","hummerist","hung","i","identity","idiout","if","ignored","im","implied","implore","impriest","in","inches","initials","insane","inside","insist","instead","interesting","inthusiastic","into","irritating","is","isdead","isnt","it","its","ive","jensen","joaquin","job","johns","jolly","judicial","july","just","justifiable","kathleen","keep","kharmann","kicked","kiddies","kids","kill","killed","killedher","killedhim","killer","killing","killings","kind","kissed","kit","knee","knife","know","lack","lady","lake","lamb","last","laugh","laughs","lay","leaf","leaped","leave","leaving","left","legs","let","letter","liberation","library","lies","life","lift","light","like","lips","list","listen","lit","little","living","lonely","long","longer","look","looking","loose","lot","lou","luger","lyeing","machine","made","mageau","mail","mailed","make","making","man","manner","manpower","map","maple","marco","marked","market","mask","mason","massive","masterpiece","material","matte","matter","may","maybe","me","meaning","meannie","meannies","meanwhile","mech","melvin","melvins","men","mery","messy","michael","middle","might","mikado","mildly","mile","miles","min","mind","mine","minutes","mirror","miss","missed","mission","modesto","moment","money","months","more","morning","most","motor","motorcicles","mount","mouth","move","movie","mr","much","murder","murderer","murders","must","my","myself","nails","name","nasty","near","neck","need","needling","needs","negro","neither","never","nevermind","new","news","newspaper","next","nice","night","nights","nine","nineth","nitrate","no","noise","none","norse","north","not","note","notice","now","noze","nucences","oct","of","off","offenders","offered","offs","oh","oil","old","on","once","one","ones","only","open","openly","or","order","orginast","origionaly","other","others","out","oute","outfits","over","own","pace","page","pages","pain","pane","panes","pants","paper","paradice","park","parked","parking","parkway","part","parts","passed","patterned","paul","pay","peek","peekaboo","peeled","pen","pencel","people","pepermint","permits","persons","pestulentual","phantom","phomphit","phone","photoelectric","phraises","piano","pick","pictures","piece","pig","pigs","pine","pines","place","placed","platt","play","players","please","pleass","plunged","point","pointblank","police","polish","poor","posibly","positivily","pow","power","presidio","press","price","print","prior","private","promiced","proof","properly","prove","provences","prowl","ps","psychological","public","published","pulled","punish","punished","put","questions","quietly","quite","race","raceing","races","radians","rage","rain","rampage","rather","re","reach","read","ready","really","reason","reborn","recent","red","refer","reflector","remain","renault","report","reports","requires","rest","ride","rife","right","rile","ring","riverside","road","roat","robberies","rocks","roger","rolled","rope","routine","rub","rubber","rubed","run","runnig","running","rush","safe","said","salt","same","san","sat","saterical","save","saw","say","saying","schedule","school","scream","screaming","searched","searching","seat","see","seeing","seen","self","selling","selves","sensibilities","sent","separation","sept","seranader","serious","set","seven","sfpd","shabbly","shake","shaking","shall","shapely","she","shepard","shirt","shit","shoe","shoes","shook","shoot","shot","shots","should","show","shrink","shure","shut","sick","side","sides","sight","sights","signed","silowets","since","singurly","sirs","sisters","sitting","skin","sla","slaughter","slaves","slay","sloi","slower","slowly","small","smarter","snd","so","society","som","some","someday","someone","something","sorry","sound","south","speaking","spell","spilling","splinters","spoiling","spot","spray","springs","spurting","square","squealing","squirm","squirmed","srounded","st","stab","stabbed","stained","stalking","stamps","start","starting","state","stated","station","stine","stop","store","stored","stove","strange","stray","street","streets","strike","struggle","stumbling","stupid","such","suggest","suicides","sullivan","summer","sun","super","superior","supply","suspicious","swamped","switch","symbionese","sympathy","system","tag","take","talk","talked","tape","taped","targets","task","taste","taxi","teenagers","tell","telling","ten","tenth","teritory","tests","thae","than","thank","thashing","that","thats","the","their","them","then","there","theres","these","they","theyd","thing","thingmebob","things","think","thinking","third","thirteenth","this","thos","those","though","three","thrilling","throat","through","thumbs","thus","tie","till","time","times","tip","tire","tired","tires","tissues","titwillo","to","told","tone","too","tools","top","torture","toschi","town","trace","transparent","trees","trigger","trucks","truley","truly","try","trying","tubes","tut","twich","twiched","twisted","two","type","uncertain","uncompromising","under","underground","unhappy","union","unnoticible","unspoiling","untill","unwilling","up","upon","use","used","useing","vallejo","ventalate","very","victim","victims","victom","violence","violently","volkswagen","wachamacallit","wait","waited","waiting","walking","walks","wall","wallet","wandering","want","warm","warning","was","washington","watch","water","wave","waveing","way","we","wear","wearing","week","well","went","were","west","western","what","whatshisname","when","whence","where","whether","which","while","white","who","whole","whom","whrite","why","wild","will","willing","willingly","window","wipe","wiped","wipeing","wire","wise","wish","wishes","with","wives","woeman","wonder","wondering","wont","word","work","would","wouldnt","wound","write","writing","xmass","xxx","year","years","yes","yet","you","youll","young","your","yours","zodiac");
  var wordHash = new Array();



	var decoder = new Array();

	var getKey = false;
	var getKeyFor;
	var getKeyId;

	var newLoad = false;          
	
	var sequences;
	                  
	/** highlight the given sequence #*/
	function hSeq(seq) {
		/** unhighlight all others */
		for (var i=0; i<sequences.length; i++)
			hmatch(sequences[i], false);
		/** highlight selected sequence */
		hmatch(sequences[seq], true);	
	}

	function resetCipher() {
		for (i=0; i<cipherReset.length; i++) {
			cipher[i] = cipherReset[i].slice();
		}
	}
	
	function d(name) {
		return document.getElementById(name);
	}
	
	function init() {
		document.getElementById("seq").innerHTML = "";
		document.getElementById("ngrams").innerHTML = "";
		resetCipher();
		preload();
		cipherLength[which] = 0;
		for (var i=0; i<cipher[which].length; i++) {
			for (var j=0; j<cipher[which][i].length; j++) {
				decoder[cipher[which][i].substring(j,j+1)] = "";
				cipherLength[which]++;
			}
		}
		for (var i=0; i<commonWords.length; i++)
			wordHash[commonWords[i]] = true;
		for (var i=0; i<zodiacWords.length; i++)
			wordHash[zodiacWords[i]] = true;
		//render();

		computeStatistics();
		setTimeout("render()", 250);
		renderInteresting();
	}

	function renderGridAll() {
		var d = ""; 
				var ctable = document.getElementById("cipher");
				if (ctable.childNodes[0]) ctable.removeChild(ctable.childNodes[0]);

				var newChild = document.createElement("table");
				newChild.className = "cipher";
				var tbody = document.createElement("tbody");
				var cell; var trow;

				trow = document.createElement("tr");
								for (var col=0; col<=cipher[which][0].length; col++) {
									cell = document.createElement("td");
									if (col>0) cell.innerHTML = "<span class='index'>" + (col-1) + "</span>";
									trow.appendChild(cell);
								}
								tbody.appendChild(trow);
				
				
				for (var row=0; row<cipher[which].length; row++) {
					trow = document.createElement("tr");
					cell = document.createElement("td");
					cell.className = "index";
					cell.innerHTML = "<span class='index'>" + (row*cipher[which][row].length) + "</span>";
					trow.appendChild(cell);
					for (var col=0; col<cipher[which][row].length; col++) {
						letter = cipher[which][row].substring(col,col+1);
						id = row + "_" + col;
						d += (getDecoded(letter) == "" ? " " : getDecoded(letter));
				    cell = document.createElement( "td" );
						cell.setAttribute("id",id);
//						cell.onmouseover = new Function("h('" + id + "')");
//						cell.onmouseout = new Function("u('" + id + "')");
//			 		  cell.setAttribute("onmouseout","u('" + id + "')");
//						cell.setAttribute("onclick","g('" + letter + "','" + id + "')");
//						cell.onclick = function() { var temp=new Function("g('" + letter + "','" + id + "')"); temp(); };
						cell.onclick = new Function("g(\"" + (letter == "\\" ? "\\\\" : letter) + "\",\"" + id + "\")");
//						eval("cell.onClick = g('" + letter + "','" + id + "')");
						cell.className = getName(letter, false);
						if (getName(letter, false)=="blank") {
//							cell.style.paddingLeft = "1px";
//						 	cell.style.paddingRight = "1px";
					  }
						else {
//							cell.style.paddingLeft = "10px";
//						 	cell.style.paddingRight = "10px";
						}
						
		//				cell.setAttribute("style",(getName(letter, false) == "blank" ? "padding-left: 1px; padding-right: 1px;" : ""));
						cell.innerHTML = "<center>"+getDecoded(letter) + "</center>&nbsp;" 
						trow.appendChild(cell);
					  //html += "<td id=\"" + id + "\" onmouseover=\"h('" + id + "')\" onmouseout=\"u('" + id + "')\" onclick=\"g('" + letter + "','" + id + "')\" class=\"" + getName(letter, false) + "\" style=\"" + (getName(letter, false) == "blank" ? "padding-left: 1px; padding-right: 1px;" : "") + "\">" + getDecoded(letter) + "</td>";
						//html += "</td>";
					}


					tbody.appendChild(trow);
					//html += "</tr>";
				}
				thead = document.createElement("thead");
				tfoot = document.createElement("tfoot");
				newChild.appendChild(thead);
				newChild.appendChild(tfoot);
				newChild.appendChild(tbody);
				ctable.appendChild(newChild);
				return d;
	}

	function renderCellsFor(symbol, plaintext) {
		var cell;
		var d = "";
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				letter = cipher[which][row].substring(col,col+1);
				d += (getDecoded(letter) == "" ? " " : getDecoded(letter));
				if (letter == symbol) {
					id = row + "_" + col;
				 	cell = document.getElementById(id);
					//			cell.setAttribute("onmouseover","h('" + id + "')");
					//			cell.setAttribute("onmouseout","u('" + id + "')");
//								cell.setAttribute("onclick","g('" + letter + "','" + id + "')");
					//			cell.onClick = "g('" + letter + "','" + id + "')";
								cell.className = getName(letter, false);
								if (getName(letter, false)=="blank") {
//									cell.style.paddingLeft = "1px";
//								 	cell.style.paddingRight = "1px";
							  }
								else {
//									cell.style.paddingLeft = "10px";
//								 	cell.style.paddingRight = "10px";
								}
//					if (cell.childNodes[0]) cell.removeChild(cell.childNodes[0]);
//					cell.appendChild(document.createTextNode(getDecoded(letter)));
					cell.innerHTML = getDecoded(letter);
				}
			}
		}
		document.getElementById("s"+getName(symbol,true)).className = getName(letter, false);
		document.getElementById("s"+getName(symbol,true)).innerHTML = plaintext;
		renderCipherInfo(d);
	}
	
	function renderCipherInfo(d) {
		var decoded = "<p><u>Decoded ciphertext</u>: <b>";
		var plaintext = "";
		
		if (doStats) {
			frequencies = new Array();
			for (var i=0; i<d.length; i++) {
				decoded += d.charAt(i).toLowerCase() + " ";
				plaintext += d.charAt(i);
				if (!frequencies[d.charAt(i)]) frequencies[""+d.charAt(i)] = 0;
				frequencies[""+d.charAt(i)]++;
			}
			decoded += "</b></p>";

			var keys = new Array();
			for (x in frequencies) {
				keys[keys.length] = x;
			}
			var frequenciesSortedKeys = null;
			frequenciesSortedKeys = sortByValue(keys, frequencies);
			decoded += "<p id=\"stats2\" style=\"display:" + document.getElementById("stats").style.display + "\"><u>Letter frequencies</u>: ";
		
			decoded += "<table><tr valign=\"top\"><td style=\"border-right: thin solid #999\"><center><b>PLAINTEXT:</b></center>";
			decoded += "<table class=\"lettertable\" style=\"padding: 5px 5px 5px 5px\">";
			var max = 0; var scale;
			for (x in frequenciesSortedKeys) {
				letter = frequenciesSortedKeys[x];
				if (letter != " ") {
					if (max == 0) {
						max = frequencies[frequenciesSortedKeys[x]]; 
					}
					scale = Math.round(100*frequencies[frequenciesSortedKeys[x]]/max);
					decoded += "<tr>";
					decoded += "<td class=\"letter\">" + letter + "</td>";
					//html += (getDecoded(letter) ? "<div class=\"letter\">" + getDecoded(letter) : "<img src=\"alphabet/" + getName(letter, false) + ".jpg\" style=\"display:inline\"/>") + 
					decoded += "<td nowrap=\"yes\" class=\"normal\">" + frequencies[frequenciesSortedKeys[x]] + " (" + Math.round(100*(frequencies[frequenciesSortedKeys[x]]/cipherLength[which])) + "%)</td>";
					decoded += "<td><img style=\"border-right: thick solid #009; border-left: 2px solid #ccf\" \" src=\"dot1.gif\" height=\"20\" width=\"" + scale + "\"/></td>";
				  decoded += "</tr>";
				}
			}
			decoded += "</table></td><td><center><b>EXPECTED:</b></center>";
			decoded += "<table class=\"lettertable\" style=\"padding: 5px 5px 5px 5px\">";
		
			keys = new Array();
			for (x in letterFrequencies) {
				keys[keys.length] = x;
			}
			var letterFreqSortedKeys = null;
			letterFreqSortedKeys = sortByValue(keys, letterFrequencies);
			max = 0;
			for (x in letterFreqSortedKeys) {
				letter = letterFreqSortedKeys[x];
				if (letter != " ") {
					if (max == 0) {
						max = letterFrequencies[letterFreqSortedKeys[x]]; 
					}
					scale = Math.round(100*letterFrequencies[letterFreqSortedKeys[x]]/max);
					decoded += "<tr>";
					decoded += "<td class=\"letter\">" + letter + "</td>";
					//html += (getDecoded(letter) ? "<div class=\"letter\">" + getDecoded(letter) : "<img src=\"alphabet/" + getName(letter, false) + ".jpg\" style=\"display:inline\"/>") + 
					decoded += "<td nowrap=\"yes\" class=\"normal\">" + Math.round(letterFrequencies[letterFreqSortedKeys[x]]*cipherLength[which]) + " (" + Math.round(100*(letterFrequencies[letterFreqSortedKeys[x]])) + "%)</td>";
					decoded += "<td><img style=\"border-right: thick solid #009; border-left: 2px solid #ccf\" \" src=\"dot1.gif\" height=\"20\" width=\"" + scale + "\"/></td>";
				  decoded += "</tr>";
				}
			}
			decoded += "</table><a href=\"http://en.wikipedia.org/wiki/Letter_frequencies#Relative_frequencies_of_letters_in_the_English_language\">(source)</a>";
		
			decoded += "</td></tr></table>";
		}
		//for (x in frequenciesSortedKeys) if (frequenciesSortedKeys[x] != " ") decoded += "(" + frequenciesSortedKeys[x] + ", " + frequencies[frequenciesSortedKeys[x]] + ") ";


		var found = new Array();
		for (var i=1; i<20; i++) {
			for (var j=0; j<d.length-i-1; j++) {
				if (wordHash[d.substring(j,j+i).toLowerCase()]) {
					found[d.substring(j,j+i).toLowerCase()] = true;
				}
			}
		}
		var words = "";
		for (var x in found) {
			words += x + " | ";
		}
		document.getElementById("found").innerHTML = "<u>Found words</u>: <b>" + words + "</b>" + decoded;


//		document.getElementById("cipherAfter").innerHTML = html;
		document.getElementById("decoder").innerHTML = "Decoder: <br>" + alphabet[which] + "<br>" + getDecoder().toLowerCase().replace(/ /g,"");
		
		if (doStats) {
			html = "<u>Symbol frequencies</u>: <table class=\"lettertable\">";
		
			var max = 0;
			for (x in statsSortedKeys[which]) {
				letter = statsSortedKeys[which][x];
				if (max == 0) {
					max = stats[which][statsSortedKeys[which][x]]; 
				}
				scale = Math.round(60*stats[which][statsSortedKeys[which][x]]/max);
			
			
				html += "<tr valign=\"middle\">";
				html += "<td align=\"right\"><img style=\"border-left: thick solid #009; border-right: 2px solid #ccf\" src=\"dot1.gif\" height=\"20\" width=\"" + scale + "\"/></td>";
				html += "<td align=\"middle\" id=\"s" + getName(letter, true) + "\" onclick=\"g('" + letter + "','bogus')\" class=\"" + getName(letter, false) + "\" style=\"" + (getName(letter, false) == "blank" ? "padding-left: 1px; padding-right: 1px;" : "") + "\"><div class=\"letter\">" + getDecoded(letter) + "</div></td>";
				//html += (getDecoded(letter) ? "<div class=\"letter\">" + getDecoded(letter) : "<img src=\"alphabet/" + getName(letter, false) + ".jpg\" style=\"display:inline\"/>") + 
				html += "<td nowrap=\"yes\" class=\"normal\">" + stats[which][statsSortedKeys[which][x]] + " (" + Math.round(100*(stats[which][statsSortedKeys[which][x]]/cipherLength[which])) + "%)</td>";
				html += "</tr>";
			}
			html += "</table>";
			document.getElementById("stats").innerHTML = html;
		}
		
		
	}
	
	function render() {
		//var html = "<table border=\"1\" class=\"cipher\">";
		var html = "";
		var id;
		var d1 = new Date();
		var d = renderGridAll();
		renderCipherInfo(d);
	}
	
	function renderInteresting() {
		var html;
		
		html = "Interesting decoders: ";
		
		for (var i=0; i<interestingHash[which].length; i++) {
			html += "<a href=\"javascript:getInteresting(" + i + ")\">" + interestingHash[which][i].name + "</a> | ";
		}
		
		var elem = document.getElementById("interesting");
		if (elem)
			elem.innerHTML = html;
		
	}

	function reset() {
		init();
	}

	function h(id) {
		var elem = document.getElementById(id);
		elem.style.border = "thin solid gray";
	}
	function h2(id) {
		var elem = document.getElementById(id);
		elem.style.border = "thick solid red";
	}
	
	function hall(letter) {
		for (var j=0; j<cipher[which].length; j++)
			for (var i=0; i<cipher[which][j].length; i++)
				if (cipher[which][j].charAt(i) == letter) h2(j+"_"+i);
	}
	
	function u(id) {
		var elem = document.getElementById(id);
		elem.style.border = "thin solid white";
	}

	function getDecoded(letter) {
		if (decoder[letter]=="") return "";
		return (decoder[letter]);
	}

	function getDecoder() {
		var d = "";
		for (i=0; i<alphabet[which].length; i++) d+= (!decoder[alphabet[which][i]] || decoder[alphabet[which][i]] == "" ? "?" : decoder[alphabet[which][i]]) + " ";
		return d;
	}


	function g(letter, id) {
		getKey = true;
		getKeyFor = letter;
		document.getElementById("key").innerHTML = "Type the letter you want for <img src=\"alphabet/" + getName(letter, true) + ".jpg\">.  " + 
			"Or, <a href=\"#\" onclick=\"clearletter()\">[reset this letter]</a>.";

		var elem = document.getElementById(id);
		/*if (elem) {
			elem.style.border = "thin solid #0f0";
	  }*/
		getKeyId = id;
	}

     function chr(c) {
     var h = c . toString (16);
     h = unescape ('%'+h);
     return h;
     }

	function getName(letter, ignoreDecoder) {
		if (!ignoreDecoder && decoder[letter] != "") return "blank";
		switch (letter) {
			case "1" : return("n1");
			case "2" : return("n2");
			case "3" : return("n3");
			case "4" : return("n4");
			case "5" : return("n5");
			case "6" : return("n6");
			case "7" : return("n7");
			case "8" : return("n8");
			case "9" : return("n9");
			case "^" : return("caret");
			case "#" : return("sq");
			case "_" : return("sqe");
			case "@" : return("sqd");
			case "*" : return("sql");
			case "%" : return("sqr");
			case "(" : return("theta");
			case ")" : return("phi");
			case "z" : return("zodiac");
			case "t" : return("perp");
			case "&" : return("pf");
			case ";" : return("idl");
			case ":" : return("idr");
			case ">" : return("gt");
			case "." : return("dot");
			case "<" : return("lt");
			case "+" : return("plus");
			case "/" : return("slash");
			case "\\" : return("backslash");
			case "-" : return("dash");
			case "!" : return("funnyi");
			case "=" : return("sidek");
			case "|" : return("bar");

/*			case "a" : return("blank");
			case "g" : return("blank");
			case "h" : return("blank");
			case "i" : return("blank");
			case "m" : return("blank");
			case "n" : return("blank");
			case "o" : return("blank");
			case "s" : return("blank");
			case "t" : return("blank");
			case "u" : return("blank");
			case "v" : return("blank");
			case "w" : return("blank");
			case "x" : return("blank");
			case "z" : return("blank");*/

            case "b" : return("bb");
            case "c" : return("bc");
            case "d" : return("bd");
            case "e" : return("be");
            case "f" : return("bf");
            case "j" : return("bj");
            case "k" : return("bk");
            case "l" : return("bl");
            case "p" : return("bp");
            case "q" : return("bq");
            case "r" : return("br");
            case "y" : return("by");

			case "A" : return("a");
			case "B" : return("b");
			case "C" : return("c");
			case "D" : return("d");
			case "E" : return("e");
			case "F" : return("f");
			case "G" : return("g");
			case "H" : return("h");
			case "I" : return("i");
			case "J" : return("j");
			case "K" : return("k");
			case "L" : return("l");
			case "M" : return("m");
			case "N" : return("n");
			case "O" : return("o");
			case "P" : return("p");
			case "Q" : return("q");
			case "R" : return("r");
			case "S" : return("s");
			case "T" : return("t");
			case "U" : return("u");
			case "V" : return("v");
			case "W" : return("w");
			case "X" : return("x");
			case "Y" : return("y");
			case "Z" : return("z"); 

			// symbols unique to 13- and 32-character ciphers
			case "?" : return("omega");    
			case "0" : return("taurus");
			case "[" : return("t2");

			case " " : return("blank");
			//case "$" : return("tao");
			default : return("unknown");
		}
		//if (letter == letter.toLowerCase()) return "b" + letter.toLowerCase();
		//else return letter.toLowerCase();
	}

	function keypress(event) {
		var key = window.event ? event.keyCode : event.which;
		if (getKey) {
      //alert("key: " + key);
      if (key < 32 || key > 126)
				letter = "";
			else
				letter = chr(key).toUpperCase();

			decoder[getKeyFor] = letter;

			getKey = false;
			renderCellsFor(getKeyFor, letter);
			getKeyFor = null;
			document.getElementById("key").innerHTML = "";
			return false;
		}
	}
	
	function clearletter() {
		decoder[getKeyFor] = ""; getKey = false;
		renderCellsFor(getKeyFor, "");
		getKeyFor = null;
		document.getElementById("key").innerHTML = "";
	}

	function randomize() {
		for (var i=0; i<cipher[which].length; i++) {
			for (var j=0; j<cipher[which][i].length; j++) {
				decoder[cipher[which][i].substring(j,j+1)] = chr(Math.floor(Math.random()*26+65));
			}
		}
		render();
	}

	function getInteresting(w) {
		for (var i=0; i<interestingHash[which][w].decoder.length; i++) {
			letter = interestingHash[which][w].decoder.substring(i,i+1).toUpperCase();
			if (letter != "?") decoder[alphabet[which].substring(i,i+1)] = letter;
			else decoder[alphabet[which].substring(i,i+1)] = "";
		}
		render();
	}
	
	function switchCipher(w) {
		which = w;
		init();
	}
	
	/* highlight all occurrences of the given cipher letters */
	function highlightLetters(letters) {
		unhighlightAll();
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				id = row + "_" + col;
				letter = cipher[which][row].substring(col,col+1);
				for (var i=0; i<letters.length; i++) {
					if (letter == letters.substring(i,i+1)) {
						h(id);
						break;
					}
				}
			}
		}
	}
	
	/* clear all letter highlighting */
	function unhighlightAll() {
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				u(row+"_"+col);
			}
		}
	}
	
	/* compute frequency statistics */
	function computeStatistics() {
		if (doStats) {
			stats[0] = new Array();
			stats[1] = new Array();
			for (var i=0; i<cipher[which].length; i++) {
				for (var j=0; j<cipher[which][i].length; j++) {
					c = cipher[which][i].charAt(j);
					if (stats[which][""+c] == null) stats[which][""+c] = 0;
					stats[which][""+c]++;
				}
			}
		
			var keys = new Array();
			for (x in stats[which]) {
				keys[keys.length] = x;
			}
			statsSortedKeys[which] = sortByValue(keys, stats[which]);
		}
		
	}
	
	function sortByValue(keyArray, valueMap) {
		return keyArray.sort(function(a,b){return valueMap[a]-valueMap[b];}).reverse();
	}
	
	function toggleStats() {
		var elem = document.getElementById("stats").style;
		if (elem.display == "none") {
			elem.display = ""; 
		}
		else {
			elem.display = "none";
		}
		elem = document.getElementById("stats2").style;
		if (elem.display == "none") {
			elem.display = ""; 
		}
		else {
			elem.display = "none";
		}
	}
	
	function flipH(c) {
		var newLine;
		for (i=0; i<c.length; i++) {
			newLine = "";
			for (j=c[i].length-1; j>=0; j--) {
				newLine += c[i].charAt(j);
			}
			c[i] = newLine;
		}
		return c;
	}
	
	function flipV(c) {
		var newArray = new Array();
		for (i=c.length-1; i>=0; i--) {
			newArray[c.length-1 - i] = c[i];
		}
		c = newArray;
		return c;
	}

	function rotateCW(c) {
		var newArray = new Array();
		var newLine;
		for (i=0; i<c[0].length; i++) {
			newLine = "";
			for (j=c.length-1; j>=0; j--) {
				newLine += c[j].charAt(i);
			}
			newArray[newArray.length] = newLine;
		}
		c = newArray;
		return c;
	}
	
	function rotateCCW(c) {
		var newArray = new Array();
		var newLine;
		for (i=c[0].length-1; i>=0; i--) {
			newLine = "";
			for (j=0; j<c.length; j++) {
				newLine += c[j].charAt(i);
			}
			newArray[newArray.length] = newLine;
		}
		c = newArray;
		return c;
	}                           
	
	/* for each letter, map it to list of positions of all its appearances in the cipher. */
	function makeIndex() {
//		var s = "";
		var index = {};
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				var key = cipher[which][row].charAt(col);
//				s += "("+row+","+col+","+key+") ";
				if (index[key]) {
					index[key][index[key].length] = [row, col];
				} else {
					index[key] = [[row, col]];
				}
			}
		}
//    	alert(s);

		return index;
	}

	/** find all repeated sequences */
	function repeatsfind() {repeats(cipher[which])}
	
	function makeFrom(cipherblock) {
		var a = []; var s = "";
		for (var row=0; row<cipherblock.length; row++) {
			a[row] = "";
			for (var col=0; col<cipherblock[row].length; col++) {
				a[row] += cipherblock[row][col];
				s+= cipherblock[row][col];
			}
		}
		cipher[which] = a;
	}
	
	function repeats(cipherblock) {
		makeFrom(cipherblock);
		var index = makeIndex();
		var count1 = 0; var count2 = 0;
		var r = []; // each entry is three arrays: 1) the sequence, 2) the positions of the first sequence, 3) the positions of the second sequence.  then the two directions.
		for (var row=0; row<cipherblock.length; row++) {
			for (var col=0; col<cipherblock[row].length; col++) {
				var found = repeatsFor(index, row, col);
				if (found) {
					count1++;
					for (var i=0; i<found.length; i++) {
						if (found[i][0].length > 1) {
							count2++;
							r[r.length] = new Array(
								found[i][0],
								found[i][1],
								found[i][2],
								found[i][3], found[i][4] // the two directions.
							);
						}
					}
				}
				
			}
		}
		           
		var rNew = [];
		var hash = {};

		/* discard if:
			- sequence length is less than 3
			- sequence is palindromic and matches itself
			- duplicate			
		 */
		for (var i=0; i<r.length; i++) {
			
			/* sequence length is less than 2 */
			var len = r[i][0].length;
			if (len < 2) continue;

			/* check for palindrome.  the two sets of positions will be equal. */
			var check = new Array();
			var count = 0;
			for (var j=0; j<r[i][1].length; j++) {
				var key = ""+r[i][1][j];
				if (!check[key]) {
					count++;
					check[key] = true;
				}
				key = ""+r[i][2][j];
				if (!check[key]) {
					count++;
					check[key] = true;
				}
			}
			if (count == len) continue;
			
			/* is this a dupe? */
			var union = r[i][1].concat(r[i][2]); // combine the two sets positions
			union.sort(); // sort so dupes will look the same in a hash
			var s = "";
			for (var j=0; j<union.length; j++) s+= union[j]+";";
			if (hash[s]) continue;			
			hash[s] = true;
			rNew[rNew.length] = new Array( r[i][0], r[i][1], r[i][2], r[i][3], r[i][4]);
		}
		
		sequences = rNew;
		    
		var lengths = {};
		if (sequences.length == 0) { html += "None found"; return; }
		for (var i=0; i<sequences.length; i++) { // make distinct set of all lengths
			lengths[rNew[i][0].length] = true;
		}
		var l2 = [];
		for (var l in lengths) l2[l2.length] = l;
		l2.sort(function(a,b) { if (a<b) return -1; if (a>=b) return 1; else return 0});
		    
		var html = "Repeated sequences.  Click to highlight.  ";
		
		for (var i=l2.length-1; i>=0; i--) {
			html += "<b>Length: " + l2[i] + "</b>: "
			for (var j=0; j<sequences.length; j++) {
				if (rNew[j][0].length == l2[i]) {
					html += "<a href=\"javascript:hSeq(" + j + ")\">" + rNew[j][0] + "</a> | ";
				}
			}
		}

		html += "<br>Number of sequences by direction: <table>";
		for (var i=0; i<8; i++) {
			html += "<tr valign='top'>";
			html += "<td>";
			if (i==0) html += "right";
			else if (i==1) html += "right-down";
			else if (i==2) html += "down";
			else if (i==3) html += "left-down";
			else if (i==4) html += "left";
			else if (i==5) html += "left-up";
			else if (i==6) html += "up";
			else if (i==7) html += "right-up";
			else html += "???";
			html += "</td><td>";
			html += "</td></tr>";
		}
		html += "</table>";


		document.getElementById("seq").innerHTML = html;
	}

	/** find all repeated sequences for strings starting at [row, col] */
	function repeatsFor(index, row, col) {
		var r = new Array();
		
		var directions = new Array(
			new Array(0, 1), // right
			new Array(1, 1), // right-down
			new Array(1, 0), // down
			new Array(1, -1), // left-down
			new Array(0, -1), // left
			new Array(-1, -1), // left-up
			new Array(-1, 0), // up
			new Array(-1, 1) // right-up
		);
		
		/* inspect each direction */
		var key = get(row, col);
		var candidates = index[key];
		if (candidates) {
			for (var c=0; c<candidates.length; c++) {
				for (var i=0; i<directions.length; i++) {
					for (var j=0; j<directions.length; j++) {
						var result = new Array();
						result[0] = "";
						result[1] = new Array();
						result[2] = new Array();       

						if (row == candidates[c][0] && col == candidates[c][1] && i==j ) {
							; // do nothing, because we don't want to match the sequence at (row,col) with itself. 
						} else {
							matches(result, row, col, directions[i][0], directions[i][1], candidates[c][0], candidates[c][1], directions[j][0], directions[j][1]);
							r[r.length] = new Array(result[0], result[1], result[2], i, j);
						}
					}
				}
			}
		}     

		                            
		return r;
	}               
	
	/** look for string match for sequences beginning at [r0,c0] and [r1,c1].  direction of sequences is determined by 
	    (dr0, dc0) and (dr1, dc1).  "result" is an array that tracks the maximum matched sequence and occurrence positions. */
	function matches(result, r0, c0, dr0, dc0, r1, c1, dr1, dc1) {
		var ch1 = get(r0, c0);
		var ch2 = get(r1, c1);
//		alert("r0 " + r0 + " c0 " + c0 + " dr0 " + dr0 + " dc0 " + dc0 + " r1 " + r1 + " c1 " + c1 + " dr1 " + dr1 + " dc1 "+ dc1 + " ch1 " + ch1 + " ch2 " + ch2);
		
		if (ch1 == ch2) {
			result[0] += ch1;
			result[1][result[1].length] = new Array(getR(r0), getC(c0));
			result[2][result[2].length] = new Array(getR(r1), getC(c1));
			matches(result, (r0+dr0), (c0+dc0), dr0, dc0, (r1+dr1), (c1+dc1), dr1, dc1);
		}
	}          
	
	function ngrams(line, n) {
		var map = {};
		var ngram; var count;
		for (var i=0; i<line.length-n+1; i++) {
			ngram = line.substring(i,i+n);
			count = map[ngram];
			if (count) count++;
			else count=1;
			map[ngram] = count;
		}
		return map;
	}
	
	// transposed ngrams
	function ngramstrans(line, n) {
		var map1 = {}; var map2 = {};
		var ngram; var val; var list;
		
		var positions = {}; var pos;
		var ignore;
		
		for (var i=0; i<line.length-n+1; i++) {
			ngram = line.substring(i,i+n);
			ngramSorted = sortByChar(ngram);
			
			// do not count overlapping matches.  For example, the string ABCA can be split into two 3-grams: ABC and BCA, whose transpositions match each other.
			ignore=false;
			if (positions[ngramSorted]) {
				pos = positions[ngramSorted];
				for (var j=0; j<pos.length; j++) {
					if (i-pos[j] < n) {
						ignore = true;
						break;
					}
				}
			} else pos = [];
			
			if (ignore) continue;
			
			pos[pos.length] = i;
			positions[ngramSorted] = pos;
			
			// first map tracks the count of total occurrences
			if (!map1[ngramSorted]) val = 0;
			else val = map1[ngramSorted];
			val++;
			map1[ngramSorted] = val;
			
			// second map tracks the string sequences
			if (!map2[ngramSorted]) list = {};
			else list = map2[ngramSorted];
			list[ngram] = true;
			map2[ngramSorted] = list;
			
		}
		
		// remove all strictly non-transposed sequences, and remove anything with count of 1.
		var found = false;
		for (var key1 in map2) {
			if (map1[key1] <= 1) {
				map1[key1] = false;
				map2[key1] = false;
				continue;
			}
			var u = {};
			var count = 0;
			for (var key2 in map2[key1]) {
				if (!u[key2]) {
					count++;
					u[key2] = true;
				}
				if (count > 1) break;
			}
			if (count == 1) {
				map1[key1] = false;
				map2[key1] = false;
			} else {
				found = true;
			}
		}
		
		var r = [];
		r.push(map1);
		r.push(map2);
		r.push(found);
		return r;
	}
	
	function sortByChar(s) {
		var r = [];
		for (var i=0; i<s.length; i++) r.push(s.charAt(i));
		r.sort();
		var sorted = "";
		for (var i=0; i<r.length; i++) sorted += r[i];
		return sorted;
	}
	
	function dumpNGrams(all) {
		var line = "";
		for (var i=0; i<cipher[which].length; i++) line += cipher[which][i];
		var html = "";
		
		var go = true;
		var n = 1; var repeats = 0; var uniqueRepeats=0;
		while (go) {
			go=false;
			var map = ngrams(line, n);
			
			html += "<h3>" + n + "-grams:</h3>";
			var keys = [];
			for (var key in map) keys[keys.length] = key;
			var sorted = sortByValue(keys, map);
			
			var result1 = "";
			var result2 = "";
			for (var i=0; i<sorted.length; i++) {
				count = map[sorted[i]];
				if (count > 1) go = true;
				if (all || count > 1) {
					result1 += getImg(sorted[i]) + " (" + count + ") ";
					result2 += sorted[i] + " (" + count + ") ";
				}
				if (count > 1) {
					repeats += count;
					uniqueRepeats++;
				}
			}
			html += result1 + "<br><br>ASCII version:<br>" + result2
		
			html += "<br><br><u>Total repeated " + n + "-grams</u>: <b>" + repeats + " (" + uniqueRepeats + " unique " + n + "-grams.)</b>";
			if (!go) html += "<br>No more repeats found."
			html += "<hr>"
			n++;
		}
		document.getElementById("ngrams").innerHTML = html;
		window.location="#ngrams";
	}
	
	function dumpAlphabetStats() {
		var html = "";
		var html2 = "<pre>";
		var counts = [];
		var symbols = [];
		for (var i=0; i<cipher.length; i++) {
			counts[i] = [];
			for (var row=0; row<cipher[i].length; row++) {
				for (var col=0; col<cipher[i][row].length; col++) {
					var symbol = cipher[i][row].charAt(col);
					symbols[symbol] = symbol;
					if (counts[i][symbol]) counts[i][symbol]++; else counts[i][symbol] = 1;
				}
			}
		}
		
		html += "<table border=1><tr><th>Symbol</th><th>ASCII</th><th>408 count</th><th>340 count</th><th>Total</th><th>Difference</th><th>408 Plaintext</th></tr>";

		var sorted = [];
		for (var symbol in symbols) sorted[sorted.length] = symbol;
		sorted.sort();
		
		var c3 =0; var c4=0;
		for (var i=0; i<sorted.length; i++) {
			var symbol = sorted[i];
			var count3 = (counts[0][symbol] ? counts[0][symbol] : 0);
			var count4 = (counts[1][symbol] ? counts[1][symbol] : 0);
			var decoded = (count4 > 0 ? decode408For(symbol) : "");
			html += "<tr>";
			html += "<td><img src=\"alphabet/" + getName(symbol, true) + ".jpg\"></td>";
			html += "<td>" + symbol + "</td>";
			c3+=count3;
			c4+=count4;
			html += "<td>" + count4 + "</td>";
			html += "<td>" + count3 + "</td>";
			html += "<td>" + (count4+count3) + "</td>";
			html += "<td>" + Math.abs(count4-count3) + "</td>";
			html += "<td>" + decoded + "</td>"
			html += "</tr>";
			
			var bg = (count3 > 0 && count4 > 0) ? "#ccc" :
				(count3 > 0 ? "#ccff99" : "#99ccff");
			
			html2 += "|-valign=\"top\" style=\"background-color: " + bg + "\"\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| [[File:" + getName(symbol, true) + ".jpg]]\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| &lt;tt>" + symbol + "&lt;/tt>\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + count3 + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + count4 + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + (count4+count3) + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + Math.abs(count4-count3) + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + decoded + "\n";
			
		}
//		alert(c3); alert(c4);
		html += "</tr></table>";
		
		
		document.getElementById("seq").innerHTML = html;// + html2;
	}
	
	/** get image corresponding to given string of cipher letters */
	function getImg(s) {
		var result = "";
		for (var i=0; i<s.length; i++) {
			result += "<img src=\"alphabet/" + getName(s.substring(i,i+1)) + ".jpg\">";
		}
		return result;
	}
	
	/** get cipher character at position (r, c).  translate out of bounds values to within bounds values. */
	function get(r, c) {
		
		r=getR(r);
		c=getC(c);
		
		return cipher[which][r].substring(c,c+1);
	}     
	
	function getR(r) {
		r = r % cipher[which].length;
		if (r < 0) r = cipher[which].length + r;
		return r;
	}
	
	function getC(c) {
		c = c % cipher[which][0].length;
		if (c < 0) c = cipher[which][0].length + c;
		return c;
	}
	
	/** highlight/unhighlight the given matches */
	function hmatch(matches, highlight) {
		for (var j=1; j<3; j++) {
			for (var i=0; i<matches[j].length; i++) {
				var id = getR(matches[j][i][0])+"_"+getC(matches[j][i][1]);
//				alert(id);
				if (highlight) h2(id); else u(id);
			}
		}
	}

	// generate list of all homophones for each unique plaintext letter in the 408 solution
	function homophonesFor408() {
		var d = interestingHash[1][0]["decoder"];
		var u = {};
		for (i=0;i<alphabet[1].length; i++) {
			var symbol = alphabet[1][i];
			var plaintext = d[i];
			if (u[plaintext]) u[plaintext].push(symbol);
			else u[plaintext] = [symbol];
		}
		return u;
	}
	
	function decode408For(ch) {
		var d = interestingHash[1][0]["decoder"];
		var i;
		for (i=0;i<alphabet[1].length; i++) if (alphabet[1].charAt(i) == ch) break;
		if (d.charAt(i)) return d.charAt(i).toUpperCase();
		return "";
	}
	
	function decode408(str) {
		var s = "";
		for (var i=0; i<str.length; i++) s+=decode408For(str.charAt(i));
		return s;
	}	
	
