package de.amidar.threadtests;

import de.amidar.threadtests.Runny;

public class Runny extends Fibonacci implements Runnable{
	private int id;
	private int count;
	
	public void run(){
		for(int i = 0 ; i < count1 * count2 ; i++){
			count++;
			Thread.yield();
			count++;
		}
	}
	
	public Runny(int i){
		this.id = i;
		count = 2 * id;
	}
	
	public static void main(String[] args) throws InterruptedException{
		for(int i = 1; i <= 10 ; i++){
			
			for(int j = 5 * (i - 1); j < 5 * i; j++){
				Thread t = new Thread(new Runny(j));
				t.setPriority(i);
				t.start();
			}
			
			Thread.sleep(5);
		}		
	}
}
