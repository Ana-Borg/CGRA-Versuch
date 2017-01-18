package de.amidar.digests;

import org.bouncycastle.crypto.digests.SHA1Digest_inline;

public class SHA1Test_inline_short {

    public static void main(String[] args) {

	int[] data = {	
	    17,  234, 58, 177,
	    143, 222, 27, 162, 
	    155, 39,  49, 174,
	    241, 10,  43, 99
	};

	SHA1Test_inline_short sha1test = new SHA1Test_inline_short();
	sha1test.run(data);

    }

    public void run(int[] data) {

	SHA1Digest_inline digest = new SHA1Digest_inline();
	
	digest.processBlock(data);
        digest.processBlock(data);
	    
    }

}
