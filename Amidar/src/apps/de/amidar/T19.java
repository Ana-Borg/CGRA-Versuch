package de.amidar;

public class T19 {

	public static void main(String[] args) {
		int i = 0, j = 0, cnt = 0;
		while (i < 100){
			j=0;
//			cnt = cnt +1 -1;
			while ( j < 10 ){
				cnt +=2;
				i++;
				j++;
			}
			i--;
			cnt += 3;
			
		}
		
		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '1', '9' });
		sp.assertInt(cnt, 276);
		sp.printResult();
	}

}
