package de.amidar;

public class T05 {
	int [] a = {1,2,3,4,5,6,7,8,9};
	int [] b = {9,8,7,6,5,4,3,2,1};
	int length = a.length;
	
	/**
	 * Test access to object fields
	 */
	public static void main(String[] args) {
		T05 dut = new T05();
		dut.calc();
//		dut.calc();
	}

	public void calc(){
		Counter c = new Counter(0,0);
		int cntB =0;
		int cntA = 0;
		for(int i = 0; i < 11; i++){
			for( int j = 0; j < 12; j++){
				
				c.cntB++;						//Class loader mixes first field of class Counter (int cntB) with first field of T05 (int [] a)
				cntB++;
			}
			c.cntA++;
			cntA++;
		}
		
		System.out.println(cntB);
		System.out.println(cntA);
		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '0', '5' });
		sp.assertInt(c.cntB, 132);
		sp.assertInt(c.cntA, 11);
		sp.printResult();

		return;
	}
}

