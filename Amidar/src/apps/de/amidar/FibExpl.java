package de.amidar;

public class FibExpl {

	
	public static void main(String[] args) {
		double root5=2.236067977;
		int n = 42;
		double fib = (pow((1+root5)/2, n)-pow((1-root5)/2, n))/root5;
		System.out.print(new char[] {'f', 'i', 'b',':', ' '});
		System.out.println(fib);
	}

	
	public static double pow(double base, int exponent){
		double erg  = 1;
		for(int i = 0; i<exponent; i++){
			erg *= base;
		}
		return erg;
	}
}
