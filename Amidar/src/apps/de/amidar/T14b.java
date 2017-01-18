package de.amidar;

public class T14b {

	/**
	 * Test proper loop detection for multiple goto -xx for one while loop
	 */
	public static void main(String[] args) {
		int[] erg = new int[10];
		int length = erg.length;
		int i = -1;
		while (i < length - 1) {
			i++;
			if (i % 3 == 1) {
				erg[i] = 1;
			} else if(i % 3 == 2) {
				erg[i] = 2;
			} else {
				erg[i] = 3;
			}
//			 int dummy = 0; // Workaround: Uncomment this line and Test will
			// run successfully
		}

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '1', '4','b' });
		sp.assertIntArray(erg, new int[] { 3, 1, 2, 3, 1, 2, 3, 1, 2, 3 });
		sp.printResult();
//		for(int j =0; j < erg.length; j++)
//			System.out.println(erg[j]);
	}

}
