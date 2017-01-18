package de.amidar;

public class T01b {

	/**
	 * Calculate scalar product of a and b twice. The CGRA has to use MMEs.
	 */
	public static void main(String[] args) {
		T01b dut = new T01b();
		dut.calc();
	}

	public void calc() {
		double product = 0;

		for (int n = 0; n < 2; n++) {

			float[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
			double[] b = { 9, 8, 7, 6, 5, 4, 3, 2, 1 };
			int length = a.length;

			double[] c = new double[length];

			for (int i = 0; i < length; i++) {
				c[i] = a[i] * b[i];
				product += c[i];
			}
		}
		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '0', '1', 'b' });
		sp.assertInt((int) product, 330);
		sp.printResult();
	}

}