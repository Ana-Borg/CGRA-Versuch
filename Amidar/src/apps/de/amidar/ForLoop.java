package de.amidar;

public class ForLoop {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		forLoop1(30);
		forLoop2(new double[30]);
		forLoop3(30);
	}
	
	public static double forLoop1(int n){
		double erg = 0;
		for(int i = 0; i < n; i++){
			erg += 1.1;
		}
		
		return erg;
	}
	
	public static void forLoop2(double [] a){
		for(int i = 0; i<a.length; i++){
			a[i] = 1.2;
		}
	}
	
	public static double forLoop3(int n){
		double erg = 0;
		for(int i = 0; i < n; i++){
			if(i%2 == 0)
				erg+=i/0.3;
			else
				erg-=i*0.3;
		}
		return erg;
	}

}
