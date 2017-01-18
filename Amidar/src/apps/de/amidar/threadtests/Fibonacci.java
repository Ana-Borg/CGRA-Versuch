package de.amidar.threadtests;

public class Fibonacci {
	private static int seed = fib(2);
	protected static int count1 = fib(seed++);
	protected static int count2 = fib(seed++);
	
	public static int fib(int n){
		return n < 2 ? 1 : fib(n-2) + fib(n - 1);
	}

}
