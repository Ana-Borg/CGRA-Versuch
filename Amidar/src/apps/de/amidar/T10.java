package de.amidar;

public class T10 {
	static double [] aa = {1,2,3,4,5,6,7,8,9};
	static double [] b = {9,8,7,6,5,4,3,2,1};
	static int length = aa.length;
	

	/**
	 * Test efficiency of array storage - compare to performance of T09
	 */
	public static void main(String[] args) {
		int summand1 = 1, summand2 = 3;
		int [] a = new int[16];
		double product = 0;
		
		for( int i = 0; i<length; i++){
			product += aa[i]*b[i];
		}
		
		
		
		for(int io = 0; io< a.length; io++){
			a[0] = summand1 + summand2;
			a[1] = summand1 + summand2;
			a[2] = summand1 + summand2;
			a[3] = summand1 + summand2;
			a[4] = summand1 + summand2;
			a[5] = summand1 + summand2;
			a[6] = summand1 + summand2;
			a[7] = summand1 + summand2;
			a[8] = summand1 + summand2;
			a[9] = summand1 + summand2;
			a[10] = summand1 + summand2;
			a[11] = summand1 + summand2;
			a[12] = summand1 + summand2;
			a[13] = summand1 + summand2;
			a[14] = summand1 + summand2;
			a[15] = summand1 + summand2;
		}
	}

}
