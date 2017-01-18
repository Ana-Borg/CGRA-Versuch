package de.amidar;

public class FakExpl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int fak=1;
		for (int i = 1; i < 10; i++){
			fak *= i;
		}
		System.out.print(new char[] {'f', 'a', 'k',':', ' '});
		System.out.println(fak);
		
	}

}
