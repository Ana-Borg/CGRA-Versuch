package de.amidar;

public class T21 {
	int [] a = {1,2,3,4,5,6,7,8,9};
	int [] b = {9,8,7,6,5,4,3,2,1};
	int aLength = a.length;
	int bLength = b.length;

	/**
	 * Calculate the convolution of the vectors a and b
	 * @param args
	 */
	public static void main(String[] args) {
		T21 dut = new T21();
		dut.calc();
		//		dut.calc();
	}

	public void calc(){

		for(int i = 0; i< 3; i++){
			a[0] += 1;
			for(int j = 1; j < 5; j++){
				a[j] += 2;
			}
			for(int j = 5; j < 9; j++){
				a[j] += 3;
			}
			for(int j = 0; j < 9; j++){
				b[j] = -a[j];
			}
		}


		
		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '2', '1' });
		sp.assertIntArray(a, new int[] { 4, 8, 9, 10, 11,
				15, 16, 17, 18 });
		sp.assertIntArray(b, new int[] { -4, -8, -9, -10, -11,
				-15, -16, -17, -18 });
		sp.printResult();
		sp.printResult();
	}
}
