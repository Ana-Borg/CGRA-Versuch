package de.amidar.crypto;

import org.bouncycastle.crypto.engines.SkipjackEngine;

import org.bouncycastle.crypto.params.KeyParameter;

public class SkipjackTest {

    private byte[] key;

    public static void main(String[] args) {

        byte[] key = {
	    (byte)61,  (byte)182, (byte)188, (byte)145, 
	    (byte)64,  (byte)118, (byte)78,  (byte)3,
	    (byte)42,  (byte)171
	};

	byte[] data = {	
	    (byte)17,  (byte)234, (byte)58, (byte)177,
	    (byte)143, (byte)222, (byte)27, (byte)162
	};

	SkipjackTest skipjacktest = new SkipjackTest(key);
	skipjacktest.run(data);

    }

    public SkipjackTest(byte[] key) {
	this.key = key;
    }

    public void run(byte[] data) {

	byte[] encrypted = new byte[data.length];

	SkipjackEngine engine = new SkipjackEngine();

	KeyParameter param = new KeyParameter(key);
	
	engine.init(true, param);
	    
	engine.processBlock(data, 0, encrypted, 0);

    }

}
