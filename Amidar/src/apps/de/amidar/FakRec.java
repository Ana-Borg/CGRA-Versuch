package de.amidar;

public class FakRec {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int l = 10;
		int [] fak = new int[l];
		fak[0]=1;
		for(int i = 1; i < l; i++){
			fak[i]=i*fak[i-1];
		}
		System.out.print(new char[] {'f', 'a', 'k',':', ' '});
		System.out.println(fak[l-1]);
		
	}

}
