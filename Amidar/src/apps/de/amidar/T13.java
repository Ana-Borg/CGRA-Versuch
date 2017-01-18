package de.amidar;

public class T13 {
	/**
	 * Test of dependency calculation for arrays
	 */
	public static void main(String[] args) {
		int[] erg = new int[10];
		int length = erg.length;
		for (int i = 0; i < length; i++) {
			erg[i] = 4;
			if (i % 2 == 1) {
				erg[i] = 1;
			}
			if (i == 4) {
				erg[i] = -2;
			}
			if (i == 6) {
				erg[i] = 3;
			}
			erg[i] = -erg[i];
		}

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '1', '3' });
		sp.assertIntArray(erg, new int[] { -4, -1, -4, -1, 2, -1, -3, -1, -4,
				-1 });
		sp.printResult();
	}

}
