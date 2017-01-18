package de.amidar.threadcrypto;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESEngine_inline;

import org.bouncycastle.crypto.params.KeyParameter;

public class AES implements Processable{
	private byte[] encrypted;
    private byte[] key;
    private AESEngine engine;
    private KeyParameter param;

    public AES(byte[] key, byte[] data) {
    	this.key = key;
    	this.encrypted = data;
    	engine = new AESEngine();
    	param = new KeyParameter(key);
    	engine.init(true, param);
    }

    public void process() {
    	byte[] result = new byte[encrypted.length];
    	
    	int blockSize = engine.getBlockSize();
    	int runs = encrypted.length / blockSize; 
    	synchronized(this){
    		for (int i = 0; i < runs; i++){
        	    engine.processBlock(encrypted, i * blockSize, result, i * blockSize);
        	}
        	
        	encrypted = result;
    	}
    }
    
    public byte[] getEncrypted(){
    	return encrypted;
    }
             
    public void setEncrypted(byte[] e){
    	encrypted = e;
    }
    
}