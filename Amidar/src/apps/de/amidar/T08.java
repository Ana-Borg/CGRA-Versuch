package de.amidar;
	
public class T08 {
	int aff = 3;
	double a = 0;
	double b = 0;
	int ai = 0;
	int bi = 0;
	
	/**
	 * Test the transmission of objects to the CGRA 
	 */
	public static void main(String[] args) {
		T08 dut = new T08();
		
		calc(dut);	
		for(int i = 0; i < 11; i++){
			for( int j = 0; j < 12; j++){
				
				dut.a++;
			}
			dut.b++;
		}
		
		
//		System.out.println(dut.a);
//		System.out.println(dut.b);
		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '0', '8' });
		sp.assertDouble(dut.a, 132);
		sp.assertDouble(dut.b, 11);
		sp.assertInt(dut.ai, 132);
		sp.assertInt(dut.bi, 11);
		sp.printResult();
	}
	
	
	
	
	public static void calc(T08 dut){
		for(int i = 0; i < 11; i++){
			for( int j = 0; j < 12; j++){
				
				dut.ai++;
			}
			dut.bi++;
		}
	}

}	
