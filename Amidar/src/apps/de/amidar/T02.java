package de.amidar;


public class T02 {
	double[] a = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	double[] b = { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };

	int length = a.length;

	/**
	 * Calculate product of a and b elementwise
	 */
	public static void main(String[] args) {
		T02 dut = new T02();
//		dut.b[9]=0;
		dut.calc();
//		dut.calc();
		// dut.calc();
		// dut.calc();
		// dut.calc();
	}

	public void calc() {
		double[] erg = new double[length];
		
		// calc product element wise
		for (int i = 0; i < length; i++) {
			// System.out.println(i);
			erg[i] = a[i] * b[i];
		}

		
//		  for(int i=0; i<length; i++){ System.out.println(erg[i]);}
//		  System.out.println(dummy[i]); }
		 
		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '0', '2' });
		sp.assertDoubleArray(erg, new double[] { 0.0, 8.0, 14.0, 18.0, 20.0,
				20.0, 18.0, 14.0, 8.0, 0.0 });
		sp.printResult();
	}

}
