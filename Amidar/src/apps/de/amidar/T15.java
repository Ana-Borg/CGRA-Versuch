package de.amidar;

public class T15 {

	/**
	 * Test correct functionality of CTRLPorts for nested if / loop structures
	 */
	public static void main(String[] args) {
		int[] erg = new int[10];
		int a = 5;
		int cnt1 = 0;
		int cnt2 = 0;
		while (a < 11) {
			if (a == 10) {

				for (int i = 0; i < 10; i++) {
					cnt1++;
					if (i % 2 == 1) {
						erg[i] = 1;
					} else
						erg[i] = 0;
				}
			} else { // Fehler treten hier auf - quick fix: else durch boolschen
						// Ausdruck ersetzen

				for (int i = 0; i < 10; i++) {
					cnt2++;
					if (i % 2 == 1) {
						erg[i] = 2;
					} else
						erg[i] = 0;
				}
			}
			a++;
		}

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '1', '5' });
		sp.assertIntArray(erg, new int[] { 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 });
		sp.assertInt(cnt1, 10);
		sp.assertInt(cnt2, 50);
		sp.printResult();
	}

}
