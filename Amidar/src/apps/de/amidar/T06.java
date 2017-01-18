package de.amidar;

public class T06 {
	int [] a = {1,2,3,4,5,6,7,8,9};
	int [] b = {9,8,7,6,5,4,3,2,1};
	int length = a.length;
	
	/**
	 * Calculate product of a and b elementwise with double
	 */
	public static void main(String[] args) {
		T06 dut = new T06();
		dut.calc();
		dut.calc();
//		dut.calc();
//		dut.calc();
//		dut.calc();
	}

	public void calc(){
		int[] erg = new int[length];
		// calc product element wise
		for(int i = 0; i < length; i++){
			//System.out.println(i);
			erg[i] = a[i]*b[i];
		}

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '0', '6' });
		sp.assertIntArray(erg, new int[] { 9, 16, 21, 24, 25,
				24, 21, 16, 9 });
		sp.printResult();
	}
}
