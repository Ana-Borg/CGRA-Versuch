package de.amidar.digests;

import org.bouncycastle.crypto.digests.SHA256Digest_inline;

public class SHA256Test_inline_long {

    public static void main(String[] args) {

	int[] data = {	
	    17,  234, 58, 177,
	    143, 222, 27, 162, 
	    155, 39,  49, 174,
	    241, 10,  43, 99
	};

	SHA256Test_inline_long sha256test = new SHA256Test_inline_long();
	sha256test.run(data);

    }

    public void run(int[] data) {

	SHA256Digest_inline digest = new SHA256Digest_inline();
	
	digest.processBlock(data);
        digest.processBlock(data);
        digest.processBlock(data);
	    
    }

}
