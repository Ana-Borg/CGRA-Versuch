package de.amidar.threadtests;

public class Checker implements Runnable{
	private TenIncrementer tenIncrementer;
	private int id;
	
	public Checker(TenIncrementer tinc, int i){
		tenIncrementer = tinc;
		id = i;
	}
	
	public void run(){
		int value;
		
		while(!tenIncrementer.isCanceled()){
			tenIncrementer.incTen();
			Thread.yield();
			value = tenIncrementer.getValue();
			Thread.yield();
			if(value % 10 != 0){
				tenIncrementer.cancel();
			}
		}
	}

}
