package de.amidar;

public class SuccessPrinter {
	private char[] testname;
	private boolean successful;

	public SuccessPrinter(char[] testname) {
		this.testname = testname;
		this.successful = true;
	}

	private void printTestname() {
		System.out.print(testname);
	}

	private void printAssertFailed(char[] type) {
		System.out.print(new char[] { ':', ' ', 'a', 's', 's', 'e', 'r', 't' });
		System.out.print(type);
		System.out.print(new char[] { ' ', 'f', 'a', 'i', 'l', 'e', 'd', ':' });
	}

	private void printExpected(char[] item) { 
		System.out.print(new char[] { ' ', 'e', 'x', 'p', 'e', 'c', 't', 'e',
				'd' });
		System.out.print(item);
		System.out.print(new char[] { '=', ' ' });
	}

	private void printFound(char[] item) {
		System.out.print(new char[] { ',', ' ', 'f', 'o', 'u', 'n', 'd' });
		System.out.print(item);
		System.out.print(new char[] { '=', ' ' });
	}

	private boolean assertArrayLength(char[] type, int actual, int expected) {
		if (actual != expected) {
			printTestname();
			printAssertFailed(type);
			printExpected(new char[] { ' ', 'l', 'e', 'n', 'g', 't', 'h', ' ' });
			System.out.print(expected);
			printFound(new char[] { ' ', 'l', 'e', 'n', 'g', 't', 'h', ' ' });
			System.out.print(actual);
			System.out.println();
			successful = false;
			return false;
		} else
			return true;
	}

	public void assertInt(int actual, int expected) {
		if (actual != expected) {
			printTestname();
			printAssertFailed(new char[] { 'I', 'n', 't' });
			printExpected(new char[] { ' ' });
			System.out.print(expected);
			printFound(new char[] { ' ' });
			System.out.print(actual);
			System.out.println();
			successful = false;
		}
	}
	
	public void assertLong(long actual, long expected) {
		if (actual != expected) {
			printTestname();
			printAssertFailed(new char[] { 'I', 'n', 't' });
			printExpected(new char[] { ' ' });
			System.out.print(expected);
			printFound(new char[] { ' ' });
			System.out.print(actual);
			System.out.println();
			successful = false;
		}
	}
	
	public void assertDouble(double actual, double expected) {
		if (actual != expected) {
			printTestname();
			printAssertFailed(new char[] { 'I', 'n', 't' });
			printExpected(new char[] { ' ' });
			System.out.print(expected);
			printFound(new char[] { ' ' });
			System.out.print(actual);
			System.out.println();
			successful = false;
		}
	}

	public void assertIntArray(int[] actual, int[] expected) {
		if (assertArrayLength(new char[] { 'I', 'n', 't', 'A', 'r', 'r', 'a',
				'y' }, actual.length, expected.length)) {
			for (int i = 0; i < actual.length; i++) {
				if (actual[i] != expected[i]) {
					printTestname();
					printAssertFailed(new char[] { 'I', 'n', 't', 'A', 'r',
							'r', 'a', 'y' });
					printExpected(new char[] { ' ' });
					System.out.print(expected[i]);
					printFound(new char[] { ' ' });
					System.out.print(actual[i]);
					System.out.print(new char[] { ' ', 'a', 't', ' ' });
					System.out.println(i);
					successful = false;
					break;
				}
			}
		}
	}

	public void assertDoubleArray(double[] actual, double[] expected) {
		if (assertArrayLength(new char[] { 'D', 'o', 'u', 'b', 'l', 'e', 'A',
				'r', 'r', 'a', 'y' }, actual.length, expected.length)) {
			for (int i = 0; i < actual.length; i++) {
				if (actual[i] != expected[i]) {
					printTestname();
					printAssertFailed(new char[] { 'D', 'o', 'u', 'b', 'l',
							'e', 'A', 'r', 'r', 'a', 'y' });
					printExpected(new char[0]);
					System.out.print(expected[i]);
					printFound(new char[0]);
					System.out.print(actual[i]);
					System.out.print(new char[] { ' ', 'a', 't', ' ' });
					System.out.println(i);
					successful = false;
					break;
				}
			}
		}
	}

	public void printResult() {
		printTestname();

		if (successful) {
			System.out.println(new char[] { ':', ' ', 'S', 'u', 'c', 'c', 'e',
					's', 's', 'f', 'u', 'l' });
		} else {
			System.out.println(new char[] { ':', ' ', 'F', 'a', 'i', 'l', 'e',
					'd' });
		}
		System.out.println();
	}
}
