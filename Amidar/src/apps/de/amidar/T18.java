package de.amidar;

public class T18 {

	public static void main(String[] args) {
		int i=0, j=0, cntI=0, cntO=0, cnt=0;
		int[] cntArr = new int[100];

		while(i<100){
			j=0;
			cnt = cnt +1 -1;
			while(j<10){
				j++;
//				cntI++;
				cnt += 3 ;
			}
			cnt+=2;
			cntArr[i]=cnt;
			i++;
//			cntO++;
		}
//		System.out.println(cntI);
//		System.out.println(cntO);

		int[] expected = new int[100];
		for(int k = 0; k<100; k++)
			expected[k] = (k+1)*32;

		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '1', '8' });
		sp.assertIntArray(cntArr, expected);
		sp.assertInt(cnt, 3200);
		sp.printResult();
	}

}
