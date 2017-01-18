package de.amidar;

public class T22 {
//	int [] W = new int[50];//{1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9};
	int [] b = {9,8,7,6,5,4,3,2,1};
//	int length = W.length;

	/**
	 * Test access to object fields
	 */
	public static void main(String[] args) {
		T22 dut = new T22();
		dut.calc();
		//		dut.calc();
	}

	public void calc(){
		int cnt=0;
		int k = 10;
		for(int i = 0; i < 10; i++){
			if(k < 5)
				if(i%2==0){
					//						W[i]=0;
					cnt++;
				}else{
					for(int j = 0; j<10; j++){
//						W[10*(i/2)+j]=i/2+j;
						cnt++;
					}
				}
		}
//		for(int i = 0; i<length; i++){
//			System.out.print(W[i]);
//			System.out.print(',');
//		}		
//		System.out.println();
//		System.out.println(cnt);
		//		System.out.println(cntB);
		//		System.out.println(cntA);
				SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '2', '2' });
				sp.assertInt(cnt, 0);
		//		sp.assertInt(c.cntA, 11);
				sp.printResult();
		//
		return;
	}
}

