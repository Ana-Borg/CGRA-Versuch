package de.amidar;

public class HLSGraph { 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int exponent = 1024;
		int base  = 3;
		long result = exp(base, exponent);
		System.out.println(result);

	}
	
	public static long exp(int base, int exponent){
		int [] TWOPOT = {1,2,4,8};
		
		int [] bin = new int[32];
		int binlength = bin.length;
		char [] hex = new char[binlength/4];
		int hexlength = hex.length;
		
		int cnt = 0; 
		
		for(int i = 0; i < hex.length; i++){
			hex[i] = 32;
		}
		int sum = 0;
		
		long result = 1;
		long pot = base;
		while( exponent > 0){
			if(exponent % 2 == 1){
				result = result * pot;
				bin[binlength-cnt-1] = 1;
				sum += TWOPOT[cnt%4];
			}
			if(cnt % 4 == 3){
				if(sum >9)
					hex[hexlength-1-cnt/4]= (char) (sum + 55);
				else
					hex[hexlength-1-cnt/4]= (char) (sum + 48);
				sum = 0;
					
			}
			pot = pot*pot;
			exponent = exponent/2;
			cnt++;
		}
		if(cnt % 4 != 0){
			if(sum >9)
				hex[hex.length-1-cnt/4]= (char) (sum + 55);
			else
				hex[hex.length-1-cnt/4]= (char) (sum + 48);
			sum = 0;
		}
		
		for(int i = 0; i < bin.length; i ++){
			System.out.print(bin[i]);
		}
		System.out.println();
		System.out.println(hex);
		return result;
	}

}
