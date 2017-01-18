package de.amidar;

public class T07 {

	int [] a = new int[81];
	int [] b = new int[81];
	int aLength = a.length;
	int bLength = b.length;

	/**
	 * Test four nested loops
	 */
	public static void main(String[] args) {
		T07 dut = new T07();
		dut.calc();
		dut.calc();
	}

	public void calc(){
		double[] erg = new double[aLength];
		int eLength = erg.length;
		
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				for(int k = 0; k < 3; k++){
					for(int l = 0; l < 3; l++){
						erg[i*27+j*9+k*3+l]=l;
					}
				}
			}
		}
		
		
		double[] expected = new double[eLength];
		for( int i = 0; i < eLength; i++)
			expected[i] = i%3;

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '0', '7' });
		sp.assertDoubleArray(erg, expected);
		sp.printResult();
	}

}
