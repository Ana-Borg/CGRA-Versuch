package de.amidar;

public class T04 {
	double[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	double[] b = { 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	int aLength = a.length;
	int bLength = b.length;

	/**
	 * Calculate the convolution of the vectors a and b
	 */
	public static void main(String[] args) {
		T04 dut = new T04();
		dut.calc();
//		dut.calc();
	}

	public void calc() {
		double[] erg = new double[aLength + bLength - 1];
		int eLength = erg.length;
		// calc convolution
		for (int i = 0; i < eLength; i++) {
			int limitA = 0;
			int limitB = 0;
			int start = 0;

			if (i < aLength) {
				limitA = i + 1;
			} else {
				limitA = aLength;
			}
			if (i < bLength) {
				limitB = i;
				start = 0;
			} else {
				limitB = bLength - 1;
				start = i - bLength + 1;
			}

			for (int j = start; j < limitA; j++) {
				erg[i] += a[j] * b[limitB - j + start];
			}
		}
		
//		  for( int i = 0; i < eLength; i++){ System.out.println(erg[i]); }
		 
//		SuccessPrinter sp = new SuccessPrinter(
//				new char[] { 'T', '0', '4' });
//		sp.assertDoubleArray(erg, new double[] { 9.0, 26.0, 50.0, 80.0, 115.0,
//				154.0, 196.0, 240.0, 285.0, 240.0, 196.0, 154.0, 115.0, 80.0,
//				50.0, 26.0, 9.0 });
//		sp.printResult();
	}
}
