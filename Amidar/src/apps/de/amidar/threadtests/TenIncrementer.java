package de.amidar.threadtests;

public class TenIncrementer {
	private int value = 0;
	private boolean canceled = false;
	
	public synchronized void incTen(){
		incOne();
		incTwo();
		incThree();
		incFour();
	} 
	private void incOne(){
		synchronized(this){
			value++;
			Thread.yield();
		}
	}
	private void incTwo(){
		synchronized(this){
			value++;
			Thread.yield();
			value++;
			Thread.yield();
		}
	}
	private synchronized void incThree(){
		value++;
		Thread.yield();
		value++;
		Thread.yield();
		value++;
		Thread.yield();
	}
	private synchronized void incFour(){
		value++;
		Thread.yield();
		value++;
		Thread.yield();
		value++;
		Thread.yield();
		value++;
		Thread.yield();
	}
	
	public synchronized int getValue(){
		return value;
	}
	
	public void cancel(){
		canceled = true;
	}
	
	public boolean isCanceled(){
		return canceled;
	}
	
	public static void main(String[] args) throws InterruptedException{
		TenIncrementer tenIncrementer = new TenIncrementer();
		int result;
		
		for(int i = 0; i < 10; i++){
			Thread t = new Thread(new Checker(tenIncrementer, i));
			t.setPriority(1);
			t.start();
		}
		
		Thread.sleep(1000);
		tenIncrementer.cancel();
		Thread.sleep(10);
		result = tenIncrementer.getValue();
	}

}
