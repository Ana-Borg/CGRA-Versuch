package de.amidar;

public class T12 {

	/**
	 * Test multiple condtions in while loop conditions
	 */
	public static void main(String[] args) {

		int a = 10, b = 2;
		boolean c =  a > 2 && b != 3; 
		
// Using c as a condition instead casts an Exception
		while(c){
//		while(b != 3  && a > 2){
//		while(a > 2 && b != 3 ){				
			a--;
			b =  a - b;
			c =  a > 2 & b != 3;
//			c =  !(a <= 2 | b == 3);
//			int dummy  = 0 ;
		}
		
		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '1', '2' });
		sp.assertInt(a, 2);
		sp.assertInt(b, -2);
		sp.printResult();
	}

}
