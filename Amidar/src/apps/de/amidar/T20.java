package de.amidar;

public class T20 {
	int [] a = {1,2,3,4,5,6,7,8,9};
	int [] b = {9,8,7,6,5,4,3,2,1};
	int aLength = a.length;
	int bLength = b.length;

	/**
	 * Calculate the convolution of the vectors a and b
	 */
	public static void main(String[] args) {
		T20 dut = new T20();
		dut.calc();
		//		dut.calc();
	}

	public void calc(){
		int value = 0;
		for(int i = 2; i< aLength; i++){
			value = 1;
			if(a[i] <= 1111)
				value = 3;
			value = 5;

		}

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '2', '0' });
		sp.assertInt(value, 5);
		sp.printResult();
	}
}
