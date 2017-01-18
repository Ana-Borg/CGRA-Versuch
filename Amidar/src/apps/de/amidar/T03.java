package de.amidar;

public class T03 {
	double[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	double[] b = { 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	int length = a.length;
	double[] erg = new double[length];

	/**
	 * Calulate the matix product of a and b
	 */
	public static void main(String[] args) {

		T03 dut = new T03();
		dut.calc();
//		dut.calc();
//		dut.calc2();
//		dut.calc2();
//		dut.calc3();
//		dut.calc3();
	}

	public void calc() {
		erg = new double[length];
		int cnt = 11;
		// calc matrix product
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					erg[i * 3 + j] += a[i * 3 + k] * b[k * 3 + j];

				}
			}
//			 System.out.println();
		}

		
//		  for(int i = 0; i< length; i++){ System.out.println(erg[i]); }
		 

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '0', '3' });
		sp.assertDoubleArray(erg, new double[] { 30.0, 24.0, 18.0, 84.0, 69.0,
				54.0, 138.0, 114.0, 90.0 });
		sp.printResult();
	}
	
	public void calc2() {
		erg = new double[length];
		int cnt = 11;
		// calc matrix product
		for (int i = 0; i < 3; i++) {
			int ind1 = i*3;
			for (int j = 0; j < 3; j++) {
				int ind2 = ind1 + j;
				for (int k = 0; k < 3; k++) {
					erg[ind2] += a[ind1 + k] * b[k * 3 + j];

				}
			}
			// System.out.println();
		}

		/*
		 * for(int i = 0; i< length; i++){ System.out.println(erg[i]); }
		 */

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '0', '3','b' });
		sp.assertDoubleArray(erg, new double[] { 30.0, 24.0, 18.0, 84.0, 69.0,
				54.0, 138.0, 114.0, 90.0 });
		sp.printResult();
	}
	
	public void calc3() {
		erg = new double[length];
		int cnt = 0;
		// calc matrix product
		
		for (int i = 0; i < 9; i+=3) {
			for (int j = 0; j < 3; j++) {
				erg[i] += a[i+j]*b[j*3];
			}
			for (int j = 0; j < 3; j++) {
				erg[i+1] += a[i+j]*b[j*3+1];
			}
			for (int j = 0; j < 3; j++) {
				erg[i+2] += a[i+j]*b[j*3+2];
			}
			// System.out.println();
		}

		/*
		 * for(int i = 0; i< length; i++){ System.out.println(erg[i]); }
		 */

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '0', '3','c' });
		sp.assertDoubleArray(erg, new double[] { 30.0, 24.0, 18.0, 84.0, 69.0,
				54.0, 138.0, 114.0, 90.0 });
		sp.printResult();
	}

}
