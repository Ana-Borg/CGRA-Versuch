package de.amidar;

public class FIRTest_int {
	int [] x = {918,806,693,592,515,470,462,492,557,649,758,872,977,1058,1104,1105,1055,951,796,597,364,109,-153,-405,-633,-826,-972,-1067,-1108,-1099,-1047,-960,-853,-739,-632,-543,-484,-460,-475,-526,-608,-712,-825,-935,-1028,-1090,-1111,-1082,-1001,-867,-685,-464,-216,44,302,542,751,917,1034,1097,1109,1073,1000,900,787,675,577,505,466,464,500,570,666,778,891,992,1069,1108,1100,1041,929,766,560,322,65,-196,-445,-669,-854,-992,-1078,-1110,-1093,-1034,-944,-834,-720,-615,-531,-477,-460,-481,-538,-624,-730,-844,-952,-1041,-1097,-1109,-1072,-982,-839,-650,-424,-173,88,344,580,782,941,1048,1103,1106,1063,984,881,767,657,563,496,463,468,510,585,684,797,909,1007,1078,1110,1094,1026,905,734,522,280,21,-239,-485,-703,-881,-1010,-1087,-1111,-1086,-1021,-926,-815,-702,-599,-520,-472,-461,-488,-550,-641,-749,-863,-969,-1053,-1102,-1107,-1061,-961,-811,-615,-383,-130,132,385,616,812,962,1061,1107,1102,1052,968,862,748,640,550,488,461,472,520,600,703,816,927,1021,1086,1111,1086,1009,879,701,483,237,-23,-282,-524,-736,-906,-1027,-1094,-1110,-1078,-1007,-908,-796,-683,-584,-509,-467,-463,-496,-564,-658,-769,-882,-985,-1064,-1106,-1103,-1048,-939,-781,-578,-342,-86,175,426,652,841,983,1073,1109,1096,1040,952,843,729,623,537,480,460,478,532,616,721,835,944,1035,1094,1110,1077,991,852,667,443,194,-67,-324,-562,-768,-930,-1042,-1101,-1107,-1068,-992,-890,-777,-666,-570,-500,-464,-466,-505,-578,-676,-788,-901,-1000,-1074,-1109,-1097,-1033,-916,-749,-540,-300,-42,219,466,687,868,1002,1083,1111,1090,1027,934,824,711,607,525,474,460,484,544,633,740,854,961,1047,1100,1108,1066,971,824,632,403,150,-111,-366,-599,-798,-952,-1055,-1105,-1104,-1057,-976,-871,-757,-648,-556,-491,-462,-470,-515,-593,-694,-807,-919,-1015,-1083,-1110,-1090,-1017,-891,-717,-502,-257};
	int filterLength = 32;
	int [] h =new int[filterLength];
	int signalLength = x.length;
	
	FIRTest_int(){
		for(int i = 0; i< filterLength; i++){
			h[i] = 31;
		}
	}
	
	public static void main(String [] args){
		FIRTest_int fir = new FIRTest_int();
		int[] filtered = fir.filterOpt2();
//		filtered = fir.filterOpt2();
//		filtered = fir.filterOpt2();
//		for(int i = 0; i< filtered.length; i++){
//			System.out.print(filtered[i]);
//			System.out.print(',');
//		}
//		System.out.println();
	}
	
	int [] filter(){
		int [] erg = new int[signalLength-filterLength];
		for(int i = 0; i< signalLength-filterLength; i++){
			for(int j = 0; j < filterLength; j++){
				erg[i] += h[j]*x[i+j];
			}
		}
		return erg;
	}
	
	int [] filterOpt(){
		int [] erg = new int[signalLength-filterLength];
		for(int i = 0; i< signalLength-filterLength; i++){
			int prod00 = x[i + 00]*h[00];
			int prod01 = x[i + 01]*h[01];
			int prod02 = x[i + 02]*h[02];
			int prod03 = x[i + 03]*h[03];
			int prod04 = x[i + 04]*h[04];
			int prod05 = x[i + 05]*h[05];
			int prod06 = x[i + 06]*h[06];
			int prod07 = x[i + 07]*h[07];
			int prod08 = x[i + 8]*h[8];
			int prod09 = x[i + 9]*h[9];
			int prod10 = x[i + 10]*h[10];
			int prod11 = x[i + 11]*h[11];
			int prod12 = x[i + 12]*h[12];
			int prod13 = x[i + 13]*h[13];
			int prod14 = x[i + 14]*h[14];
			int prod15 = x[i + 15]*h[15];
			int prod16 = x[i + 16]*h[16];
			int prod17 = x[i + 17]*h[17];
			int prod18 = x[i + 18]*h[18];
			int prod19 = x[i + 19]*h[19];
			int prod20 = x[i + 20]*h[20];
			int prod21 = x[i + 21]*h[21];
			int prod22 = x[i + 22]*h[22];
			int prod23 = x[i + 23]*h[23];
			int prod24 = x[i + 24]*h[24];
			int prod25 = x[i + 25]*h[25];
			int prod26 = x[i + 26]*h[26];
			int prod27 = x[i + 27]*h[27];
			int prod28 = x[i + 28]*h[28];
			int prod29 = x[i + 29]*h[29];
			int prod30 = x[i + 30]*h[30];
			int prod31 = x[i + 31]*h[31];
			
			prod00 = prod00+prod01;
			prod02 = prod02+prod03;
			prod04 = prod04+prod05;
			prod06 = prod06+prod07;
			prod08 = prod08+prod09;
			prod10 = prod10+prod11;
			prod12 = prod12+prod13;
			prod14 = prod14+prod15;
			prod16 = prod16+prod17;
			prod18 = prod18+prod19;
			prod20 = prod20+prod21;
			prod22 = prod22+prod23;
			prod24 = prod24+prod25;
			prod26 = prod26+prod27;
			prod28 = prod28+prod29;
			prod30 = prod30+prod31;
			
			prod00 = prod00+prod02;
			prod04 = prod04+prod06;
			prod08 = prod08+prod10;
			prod12 = prod12+prod14;
			prod16 = prod16+prod18;
			prod20 = prod20+prod22;
			prod24 = prod24+prod26;
			prod28 = prod28+prod30;
			
			prod00 = prod00+prod04;
			prod08 = prod08+prod12;
			prod16 = prod16+prod20;
			prod24 = prod24+prod28;
			
			prod00 = prod00+prod08;
			prod16 = prod16+prod24;
			
			erg[i] = prod00 + prod16;
		}
		return erg;
	}
	
	int [] filterOpt3(){
		int [] erg = new int[signalLength-filterLength];
		for(int i = 0; i< signalLength-filterLength-1; i+=2){
			int val00 = x[i + 00];
			int val01 = x[i + 01];
			int val02 = x[i + 02];
			int val03 = x[i + 03];
			int val04 = x[i + 04];
			int val05 = x[i + 05];
			int val06 = x[i + 06];
			int val07 = x[i + 07];
			int val08 = x[i + 8];
			int val09 = x[i + 9];
			int val10 = x[i + 10];
			int val11 = x[i + 11];
			int val12 = x[i + 12];
			int val13 = x[i + 13];
			int val14 = x[i + 14];
			int val15 = x[i + 15];
			int val16 = x[i + 16];
			int val17 = x[i + 17];
			int val18 = x[i + 18];
			int val19 = x[i + 19];
			int val20 = x[i + 20];
			int val21 = x[i + 21];
			int val22 = x[i + 22];
			int val23 = x[i + 23];
			int val24 = x[i + 24];
			int val25 = x[i + 25];
			int val26 = x[i + 26];
			int val27 = x[i + 27];
			int val28 = x[i + 28];
			int val29 = x[i + 29];
			int val30 = x[i + 30];
			int val31 = x[i + 31];
			int val32 = x[i + 32];
			
			int prod00 =val00*h[00];
			int prod01 =val01*h[01];
			int prod02 =val02*h[02];
			int prod03 =val03*h[03];
			int prod04 =val04*h[04];
			int prod05 =val05*h[05];
			int prod06 =val06*h[06];
			int prod07 =val07*h[07];
			int prod08 =val08*h[8];
			int prod09 =val09*h[9];
			int prod10 =val10*h[10];
			int prod11 =val11*h[11];
			int prod12 =val12*h[12];
			int prod13 =val13*h[13];
			int prod14 =val14*h[14];
			int prod15 =val15*h[15];
			int prod16 =val16*h[16];
			int prod17 =val17*h[17];
			int prod18 =val18*h[18];
			int prod19 =val19*h[19];
			int prod20 =val20*h[20];
			int prod21 =val21*h[21];
			int prod22 =val22*h[22];
			int prod23 =val23*h[23];
			int prod24 =val24*h[24];
			int prod25 =val25*h[25];
			int prod26 =val26*h[26];
			int prod27 =val27*h[27];
			int prod28 =val28*h[28];
			int prod29 =val29*h[29];
			int prod30 =val30*h[30];
			int prod31 =val31*h[31];
			
			prod00 = prod00+prod01;
			prod02 = prod02+prod03;
			prod04 = prod04+prod05;
			prod06 = prod06+prod07;
			prod08 = prod08+prod09;
			prod10 = prod10+prod11;
			prod12 = prod12+prod13;
			prod14 = prod14+prod15;
			prod16 = prod16+prod17;
			prod18 = prod18+prod19;
			prod20 = prod20+prod21;
			prod22 = prod22+prod23;
			prod24 = prod24+prod25;
			prod26 = prod26+prod27;
			prod28 = prod28+prod29;
			prod30 = prod30+prod31;
			
			prod00 = prod00+prod02;
			prod04 = prod04+prod06;
			prod08 = prod08+prod10;
			prod12 = prod12+prod14;
			prod16 = prod16+prod18;
			prod20 = prod20+prod22;
			prod24 = prod24+prod26;
			prod28 = prod28+prod30;
			
			prod00 = prod00+prod04;
			prod08 = prod08+prod12;
			prod16 = prod16+prod20;
			prod24 = prod24+prod28;
			
			prod00 = prod00+prod08;
			prod16 = prod16+prod24;
			
			erg[i] = prod00 + prod16;
			
			
			
			int prod200 =val01*h[00];
			int prod201 =val02*h[01];
			int prod202 =val03*h[02];
			int prod203 =val04*h[03];
			int prod204 =val05*h[04];
			int prod205 =val06*h[05];
			int prod206 =val07*h[06];
			int prod207 =val08*h[07];
			int prod208 =val09*h[8];
			int prod209 =val10*h[9];
			int prod210 =val11*h[10];
			int prod211 =val12*h[11];
			int prod212 =val13*h[12];
			int prod213 =val14*h[13];
			int prod214 =val15*h[14];
			int prod215 =val16*h[15];
			int prod216 =val17*h[16];
			int prod217 =val18*h[17];
			int prod218 =val19*h[18];
			int prod219 =val20*h[19];
			int prod220 =val21*h[20];
			int prod221 =val22*h[21];
			int prod222 =val23*h[22];
			int prod223 =val24*h[23];
			int prod224 =val25*h[24];
			int prod225 =val26*h[25];
			int prod226 =val27*h[26];
			int prod227 =val28*h[27];
			int prod228 =val29*h[28];
			int prod229 =val30*h[29];
			int prod230 =val31*h[30];
			int prod231 =val32*h[31];
			
			prod200 = prod200+prod201;
			prod202 = prod202+prod203;
			prod204 = prod204+prod205;
			prod206 = prod206+prod207;
			prod208 = prod208+prod209;
			prod210 = prod210+prod211;
			prod212 = prod212+prod213;
			prod214 = prod214+prod215;
			prod216 = prod216+prod217;
			prod218 = prod218+prod219;
			prod220 = prod220+prod221;
			prod222 = prod222+prod223;
			prod224 = prod224+prod225;
			prod226 = prod226+prod227;
			prod228 = prod228+prod229;
			prod230 = prod230+prod231;
			
			prod200 = prod200+prod202;
			prod204 = prod204+prod206;
			prod208 = prod208+prod210;
			prod212 = prod212+prod214;
			prod216 = prod216+prod218;
			prod220 = prod220+prod222;
			prod224 = prod224+prod226;
			prod228 = prod228+prod230;
			
			prod200 = prod200+prod204;
			prod208 = prod208+prod212;
			prod216 = prod216+prod220;
			prod224 = prod224+prod228;
			
			prod200 = prod200+prod208;
			prod216 = prod216+prod224;
			
			erg[i+1] = prod200 + prod216;
		}
		return erg;
	}
	
	int [] filterOpt2(){
		int [] erg = new int[signalLength-filterLength];
		for(int i = 0; i< signalLength-filterLength; i++){
			int prod00 = x[i + 00]*h[00];
			int prod01 = x[i + 01]*h[01];
			int prod02 = x[i + 02]*h[02];
			int prod03 = x[i + 03]*h[03];
			int prod04 = x[i + 04]*h[04];
			int prod05 = x[i + 05]*h[05];
			int prod06 = x[i + 06]*h[06];
			int prod07 = x[i + 07]*h[07];
			int prod08 = x[i + 8]*h[8];
			int prod09 = x[i + 9]*h[9];
			int prod10 = x[i + 10]*h[10];
			int prod11 = x[i + 11]*h[11];
			int prod12 = x[i + 12]*h[12];
			int prod13 = x[i + 13]*h[13];
			int prod14 = x[i + 14]*h[14];
			int prod15 = x[i + 15]*h[15];
			int prod16 = x[i + 16]*h[16];
			int prod17 = x[i + 17]*h[17];
			int prod18 = x[i + 18]*h[18];
			int prod19 = x[i + 19]*h[19];
			int prod20 = x[i + 20]*h[20];
			int prod21 = x[i + 21]*h[21];
			int prod22 = x[i + 22]*h[22];
			int prod23 = x[i + 23]*h[23];
			int prod24 = x[i + 24]*h[24];
			int prod25 = x[i + 25]*h[25];
			int prod26 = x[i + 26]*h[26];
			int prod27 = x[i + 27]*h[27];
			int prod28 = x[i + 28]*h[28];
			int prod29 = x[i + 29]*h[29];
			int prod30 = x[i + 30]*h[30];
			int prod31 = x[i + 31]*h[31];
			
			erg[i] = prod00+prod01
			+ prod02+prod03
			+ prod04+prod05
			+ prod06+prod07
			+ prod08+prod09
			+ prod10+prod11
			+ prod12+prod13
			+ prod14+prod15
			+ prod16+prod17
			+ prod18+prod19
			+ prod20+prod21
			+ prod22+prod23
			+ prod24+prod25
			+ prod26+prod27
			+ prod28+prod29
			+ prod30+prod31;
		}
		return erg;
	}
}
