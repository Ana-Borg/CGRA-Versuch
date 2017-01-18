package de.amidar;

public class T11 {

	/**
	 * Long array test
	 */
	public static void main(String[] args) {
		int[] a = new int[15];
		int[] b = new int[10];
		int lengthA = a.length;
		int lengthB = b.length;
		int aa = 0, bb = 0;
		boolean ret = false;
		for (int i = 0; i < lengthA; i++) {

			if (!ret)
				if (i < lengthB) {
					for (int j = 0; j < 100; j++) {
						aa++;
					}
					b[i] = i;

				}
			if (i >= lengthB) {
				a[i] = i;
				for (int j = 0; j < 100; j++) {
					bb++;
				}
			}
		}

		
		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '1', '1' });
		sp.assertInt(aa,1000);
		sp.assertInt(bb,500);
		sp.assertIntArray(a, new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 11,
				12, 13, 14 });
		sp.assertIntArray(b, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		sp.printResult();
	}

}
