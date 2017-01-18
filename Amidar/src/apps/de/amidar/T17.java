package de.amidar;

public class T17 {
	
	public static void main(String[] args){
		T17 dut = new T17();
		dut.calc();
//		dut.calc();
	}
	
	
	private void calc(){
		int i= 0, j = 0;
		int dummy = 0;
		int cnt = 0; 
//		int index = 0;
//		int [] hist = new int [100];
		
		while( i < 30){
//			hist[index++] = i;
			j = 0;						
			while(j < 10){				//18:IF_CMPGE
				if(j%2==0){				//24:IF_NE
					
					i++;				//////////1/////////
				}else{
					dummy = 11111;
				}
				j++;
				cnt++;			
			}
			if(j == 1880)				//48:IF_CMPNE
 				i++;					//////////2/////////      ///Wenn das hier auskommentiert ist - dann ließt das while 3 zeilen untendrunter nicht das j von zwe zeilen drunter sondern 4 zeilen obendrüber - j= 0 ist falsch schedulded - bzw fehler liegt schon in den dependencies
																  ///Andernfalls ließt das /////3////// das das erg von  //////1///// und nicht das eigene nach nem durchlauf... <- NOP macht hier probleme...
//			hist[index++] = i;
			j = 0;
			while(j < 10){
				if(j%2==0){
					i++;				//////////3/////////
				}else{
					dummy = 2;
				}
				j++;
//				cnt++;
			}
			dummy = 4;
//			hist[index++] = i;
//			hist[index++] = 333;
		}
		System.out.println(i);
		System.out.print(' ');
		System.out.println(j);
		SuccessPrinter sp = new SuccessPrinter(new char[] { 'T', '1', '7' });
        sp.assertInt(cnt, 30);
        sp.printResult();
//        for(int pe = 0; pe<hist.length; pe++){
//        	System.out.println(hist[pe]);
//        }
	}

}
