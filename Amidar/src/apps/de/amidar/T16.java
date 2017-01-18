package de.amidar;

public class T16 {
	/**
	 * Test of dependency calculation for arrays
	 */
	public static void main(String[] args) {
		int[] arr = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
		int length = arr.length;
		// TODO Auto-generated method stub
		for (int i = 0; i < length; i++) {
			arr[i] = 1;
			arr[i] += i;
			arr[i] = -arr[i];
		}

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '1', '6' });
		sp.assertIntArray(arr, new int[] { -1, -2, -3, -4, -5, -6, -7, -8, -9,
				-10 });
		sp.printResult();
	}

}
