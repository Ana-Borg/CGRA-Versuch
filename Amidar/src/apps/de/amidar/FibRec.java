package de.amidar;

public class FibRec {
	
	public static void main(String[] args){
		int l = 43;
		int[] fib = new int[l];
		fib[0]=0;
		fib[1]=1;
		
		for (int i = 2; i < l; i++){
			fib[i]=fib[i-1]+fib[i-2];
		}
		System.out.print(new char[] {'f', 'i', 'b',':', ' '});
		System.out.println(fib[l-1]);
		
	}

}
