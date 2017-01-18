package de.amidar.threadcrypto;


public class EncryptNode implements Runnable{
	private Processable encryptor;
	private EncryptNode predecessor;
	private EncryptNode successor;
	
	public EncryptNode(Processable e){
		encryptor = e;
	}
	
	public void setPredecessor(EncryptNode p){
		predecessor = p;
	}
	
	public void setSuccessor(EncryptNode s){
		successor = s;
	}
	
	public byte[] getEncrypted(){
		return encryptor.getEncrypted();
	}
	
	public void setEncrypted(byte[] e){
		encryptor.setEncrypted(e);
	}
	
	public void run(){
		try{
			if(encryptor.getEncrypted() != null){
				encryptor.process();
				Thread.sleep(1);
			}else{
				synchronized(this){
					this.wait();
				}
				
				encryptor.setEncrypted(predecessor.getEncrypted());
				encryptor.process();
			}
			
			if(successor != null){
				synchronized(successor){
					successor.notify();
				}
			}
		
			
		}catch(InterruptedException ie){}
	}
}
