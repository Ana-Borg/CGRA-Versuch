package de.amidar;

import cgra.pe.PEColor;

class JungTest{

	public static void main(String[] args){
		float y=0, cr=0, cb=0;
//		int r = 2;
//		int g = 4;
//		int b = 6;
		
		int [] peter  = {1,2,3,4,5,7};
		
		for(int i= 0; i< 5; i++){
			int a = peter[i]+peter[i+1];
		}
		
		
		for(int i = 0; i< 10 ; i++){
			int r = 2;
			int g = 4;
			int b = 6;
			y = PEColor.Y(r, g, b);
			cr = PEColor.Cr(r, g, b);
			cb = PEColor.Cb(r, g, b);
		}
		
		System.out.println(y);
		System.out.println(cr);
		System.out.println(cb);

	}
}
