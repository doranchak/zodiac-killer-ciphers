package com.zodiackillerciphers.ciphers;

import java.util.HashMap;
import java.util.Map;

/* http://www.zodiackillersite.com/viewtopic.php?f=108&t=1919 */
public class AceVenturaCipher {
	public static String[] cipher = new String[] {
		"db!i nb*^d(+ ri st?d dbl q=!n( r *~ dsge!>x", 
		"*t?wd r> =nd?tl+ ?u i!9df urq( ~f sw>d hb?", 
		"grqlh !> id g=wri hrlh u+?~ gw>x n*>n(+ i?", 
		"dbsd isdw+h*f &l h+=ql db(+l d? t( &rdb ~f", 
		"w>ngl si i?=> *i &l s++rq(h bl !>u=+~lh bri", 
		"t+?db(+i s>h iridl+i db*d bl &si &?++!(h", 
		"st=wd bri ?>gf i=> t+! *>h b? bsh awid", 
		"++!q(h !> qrld>s~ sudl+ a?!>r>x db( ~*+!>li", 
		"r> ~!h *wxwid +ln(>df b!i gldd(+i b*h", 
		"id?^^(h ~f n=$ir>i sgg ?ghl+ &(+l ^g*f!>x", 
		"=wdi!h( *i dblf ~sh( d? ~wnb >=ril !> dbl", 
		"x+rlq!>x b?~( dblf x=d * w?gh =u t+rs>i", 
		"ie*d( t?s+h *> &(+l ies!>x => dbl +?*h !", 
		"sd ir9 f(s+* ?gh &*i d= f?w>x d= tl sd db(", 
		"id+((d *>h &si xldd!>x in=gh(h u+?~ dbl", 
		"hwgdi s>h ?gh(+ n=wi!>i s ng?il ~gl n?wir>", 
		"x+*tt(h s>h n*++rlh ~( d= dbl ~!hhg( ?u", 
		"dbl u+=>d f*+h s>h i*rh ur+~gf h?>d ~=q( !", 
		"&*i dw+>r>x ~f b(sh t*ne s>h u?+db tld(l>", 
		"db( id+l(d *>h dbl u+?>d h?=+ si t=db x+?w^i", 
		"&(+l x!qr>x ~( =+h(+i ?> &bsd d= h= r> dbl", 
		"f*+h =u dbri i~sgg +*>nb b?~( => ?>( i^r> =u", 
		"~f t?hf ! is& * ugrnel+!>x ~s> ibs^(h", 
		"u!xw+l r g?=elh t*ne s>h is& >=dr>x twd db(", 
		">lfd d!~l r dw+>(h ig?&gf dbl u!xw+l &*i", 
		"db(+l rd &*i s ~s> u+=~ db( &*rid h?&> twd", 
		"st?ql *gg ! is& &si tg=&r>x +!^^gr>x hs+e", 
		"ng?db &b!nb in*+(h ~l s>h ! dw+>(h *&sf", 
		"idrgg tl!>x ?+hl+(h d= id*f ^wd ! n=>dr>w(h d=", 
		"^s> d? db( b=wi( dbl> d= dbl +=*h db( >l9d", 
		"dr~l db( ~s> &*i db(+l *>h g=?er>x +!x dbsd", 
		"d&(>df &!db s uwgg t(s+h rd &*i s &*+~ hsf", 
		"twd b( &?+l * b(sqf a*ne(d s>h * +sr>n=*d", 
		"=ql+ !d sgi? * wdrg!df t(gd s>h *r>df hlx+(l", 
		"ugsibg!d( &!db s +(h gl.i &b!nb r &si", 
		"n*^drq*d(h tf u=+ s dr~l twd &b(> ! ids++(h", 
		"t*ne sd db( w>e>?&> ~*> ids>hr>x !> t+rxbd", 
		"iw>grd( ! >=drnlh s x+lf d!>d grer b( &si", 
		"ids>hr>x !> h*+e ibsh( dbl n=g?+i &(+l", 
		"~wd(h tln*wi( db(+l &si>d *>fdrb>x u=+ db(", 
		"g!xbd d? +(uglnd =uu ?u *i b( id*++(h sd ~( r x?d", 
		"dbl u(lg!>x b( &si g?=er>x *d ~f grul tf bri", 
		"(9^+lii!=>i db( >l9d d? gsid &*i s a*&", 
		"h+?^^!>x w>t(grlu u?gg=&(b tf n*g~", 
		"*nn(^ds>n( db(> s x!+g n?wi!> d=wnb(h ~( si", 
		"ib( &?>hl+(h &brf &*i g?=er>x *d >=db!>x", 
		"twd r h!hnd &s>d d= g?=e *&sf *>h &si idrgg", 
		"g=ne ?> dbri ~*> &b(> ib( sielh &bsd !", 
		"&si g?=er>x *d r is!h bd( ~s> dbl> ib(", 
		"dw+>lh ~( d?&*+h b(! r g=id i!xbd ?u br~ s>b", 
		">(ql+ i*& br~ sx*i> ! &si idw>>(h tf dbl", 
		"(ql>d *h ?>gf isrh d= bl+ i?~(?>li n=~r>x", 
		"~r>wd(i g*d(+ s >sqf ns+ n*( w^ dbl", 
		"id+(ld &>h id?^^(h r> u+=>d ?u dbl b?~( d&=", 
		"h+(iilh >*qf ~(> x?d =$d s>h ids+d(h", 
		"d?&s+hi db( u+=>d h=?+ &b(> dblf ?^(>lh db(", 
		"h=?+ s>h d*gelh d? ~f w>ng( s h((^ n+f ns~l", 
		"&*i h(sh r &si u=+x?dd(> r> db( ~=~(>d id!gg", 
		"id*>hr>x r> dbl i*~l i^?d sudl+ d(> ~!>wd(i", 
		"db( *hwgdi !lsg!6(h r &*i >?d &!db db(~ sgi=", 
		"dbl n?wir> ~$id b*ql d=gh t+!s>i hsh b*+=gh", 
		"&b*d bsh x?>l => &!db ~( ~!>wd(i t(u?+l r>", 
		"db( fs+h u+=~ db( ^=+nb b( &*>d(h d? e>?&", 
		"&b(+l r is& db( ~s> i= r d? db( i^=d", 
		"a$~^!>x => rd ! ish +rxbd b(+l bs+?gh idsdlh", 
		"n+fr>x bs+h db(", 
		"t+r*> sg&*fi ^gsf(h *i r x?d d= dbl i^?d i!9", 
		"!>nb(i tbrh ~f b(*h r b(s+h s q?rn(", 
		"&b!i^(+r>x * i(>dl>nl ! n=wgh >?d", 
		"$>h(+id*>h rd si b*+=gh s>h db( xr+g &(+l", 
		"sgi? d*ger>x db( q=rn( r h(in+t( r> dbl t==e", 
		"ri !hl>drnsg d? db( q&rn( ! bls+h dbsd h*f r", 
		"tlgr(ql dbl q?rn( !i t+rs>i s>?dbl! nw+r=w", 
		">=dl st?$d dbri iwtalnd ri dbsd ~f", 
		"x+*>h~=db(+ hrlh sudl+ d(> ^~ db(", 
		"i*!l >rxbd si nb(+r tsdli"
	};
	
	/* symbol distribution:
	 
	 254 d
	 179 >
	 160 (
	 155 i
	 147 b
	 134 h
	 133 +
	 128 s
	 122 l
	 112 r
	  97 *
	  94 ?
	  86 =
	  84 g
	  80 !
	  72 &
	  65 ~
	  60 n
	  56 x
	  54 w
	  49 f
	  46 t
	  39 u
	  29 ^
	  24 q
	  24 e
	   6 a
	   6 9
	   6 $
	   1 6
	   1 .
	*/	
	
	public static Map<Character, Character> key = new HashMap<Character, Character>();
	static {
		key.put('!','i');
		key.put('$','u');
		key.put('&','w');
		key.put('(','e');
		key.put('*','a');
		key.put('+','r');
		key.put('.','e');
		key.put('6','n');
		key.put('9','x');
		key.put('=','o');
		key.put('>','n');
		key.put('?','o');
		key.put('^','p');
		key.put('a','j');
		key.put('b','h');
		key.put('d','t');
		key.put('e','k');
		key.put('f','y');
		key.put('g','l');
		key.put('h','d');
		key.put('i','s');
		key.put('l','e');
		key.put('n','c');
		key.put('q','v');
		key.put('r','i');
		key.put('s','a');
		key.put('t','b');
		key.put('u','f');
		key.put('w','u');
		key.put('x','g');
		key.put('~','m');
	}
	
	static void decode() {
		for (String line : cipher) {
			String decoded = "";
			for (int i=0; i<line.length(); i++) {
				char ch = line.charAt(i);
				if (ch == ' ') decoded += " ";
				else {
					Character plain = key.get(ch);
					decoded += plain == null ? "?" : plain;
				}
			}
			System.out.println(line);
			System.out.println(decoded);
		}
	}
	
	public static void main(String[] args) {
		decode();
	}
}
