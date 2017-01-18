package de.amidar;

public class T03b {
	double[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	double[] b = { 9, 8, 7};
	int length = b.length;
	double[] erg = new double[length];

	/**
	 * Calculate matrix vector product 
	 */
	public static void main(String[] args) {

		T03b dut = new T03b();
		dut.calc();
		dut.calc2();
	}

	public void calc() {
		double[] erg = new double[length];
		// calc matrix vector product
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				erg[i] += a[i*3+j]*b[j];
			}
			// System.out.println();
		}

		/*
		 * for(int i = 0; i< length; i++){ System.out.println(erg[i]); }
		 */

		SuccessPrinter sp = new SuccessPrinter(
				new char[] { 'T', '0', '3', 'b' });
		sp.assertDoubleArray(erg, new double[] { 46.0, 118.0, 190.0});
		sp.printResult();
	}
	
	public void calc2() {
		double[] erg = new double[length];
		// calc matrix vector product
		for (int i = 0; i < 3; i++) {
			int index = i*3;
			for (int j = 0; j < 3; j++) {
				erg[i] += a[index+j]*b[j];
			}
			// System.out.println();
		}

		/*
		 * for(int i = 0; i< length; i++){ System.out.println(erg[i]); }
		 */

		SuccessPrinter sp = new SuccessPrinter(
				new char[] { 'T', '0', '3', 'b' });
		sp.assertDoubleArray(erg, new double[] { 46.0, 118.0, 190.0});
		sp.printResult();
	}

}
