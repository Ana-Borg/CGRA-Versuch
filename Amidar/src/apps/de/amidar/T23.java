package de.amidar;

public class T23 {
	int [] W = new int[5];//{1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9};
	int [] b = {9,8,7,6,5,4,3,2,1};
	int length = W.length;

	/**
	 * Test access to object fields
	 */
	public static void main(String[] args) {
		T23 dut = new T23();
		dut.calc();
		//		dut.calc();
	}

	public void calc(){
		int ones=10;
		int bits = 4;
		int j=0;
		int k = 3;
		for(int i = 0; i < 10; i++){
			if(k < 5)
//				if(i%2==0){
//					//						W[i]=0;
//					bits++;
//				}else{
					
					while(ones != 0){
						if(ones+bits >= 5){
							W[j++]= ones;
							ones =bits;
							bits = 0;
						}else{
							bits +=ones;
							ones = 0;
						}
						W[j++]= 222;
						
//					}
				}
		}
//		for(int i = 0; i<length; i++){
//			System.out.print(W[i]);
//			System.out.print(',');
//		}		
//		System.out.println();
//		System.out.println();
		//		System.out.println(cntB);
		//		System.out.println(cntA);
				SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '2', '3' });
				sp.assertIntArray(W, new int[] { 10, 222,222, 0, 0});
		//		sp.assertInt(c.cntB, 132);
		//		sp.assertInt(c.cntA, 11);
				sp.printResult();
		//
		return;
	}
}

