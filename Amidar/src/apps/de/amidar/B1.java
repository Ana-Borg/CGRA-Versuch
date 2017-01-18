package de.amidar;

public class B1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double ad = 1.22, bd = 2.11;
		float af = 3.44f, bf = 4.33f;
		double erg = ad+bd;
		erg = ad-bd;
		System.out.println(erg);
		erg = ad*bd;
		erg = ad/bd;
		erg = ad%bd;
		if(ad<bd) System.out.println("ad less bd");
		if(ad>bd) System.out.println("ad greater bd");
		float ergf = af+bf;
		ergf = af-bf;
		ergf = af*bf;
		ergf = af/bf;
		ergf = af%bf;
		erg = -erg;
		int in = (int)erg;
		long lo = (long)erg;
		ergf = (float)erg;
		if(af<bf) System.out.println("af less bf");
		if(af>bf) System.out.println("af greater bf");
		in = (int)ergf;
		lo = (long)ergf;
		erg = (double)ergf;
		ergf = -ergf;

	}

}
