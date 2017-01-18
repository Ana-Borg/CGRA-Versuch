package org.bouncycastle.crypto.digests;

public class MD5Digest_inline {

    private static final int DIGEST_LENGTH = 16;

    private int H1, H2, H3, H4;

    private int xOff;

    public MD5Digest_inline() {
        reset();
    }

    public int getDigestSize() {
        return DIGEST_LENGTH;
    }

    public void reset() {

        H1 = 0x67452301;
        H2 = 0xefcdab89;
        H3 = 0x98badcfe;
        H4 = 0x10325476;

        xOff = 0;

    }

    //
    // round 1 left rotates
    //
    private static final int S11 = 7;
    private static final int S12 = 12;
    private static final int S13 = 17;
    private static final int S14 = 22;

    //
    // round 2 left rotates
    //
    private static final int S21 = 5;
    private static final int S22 = 9;
    private static final int S23 = 14;
    private static final int S24 = 20;

    //
    // round 3 left rotates
    //
    private static final int S31 = 4;
    private static final int S32 = 11;
    private static final int S33 = 16;
    private static final int S34 = 23;

    //
    // round 4 left rotates
    //
    private static final int S41 = 6;
    private static final int S42 = 10;
    private static final int S43 = 15;
    private static final int S44 = 21;

    /*
     * rotate int x left n bits.
     */
    private int rotateLeft(int x, int n) {
        return ((x << n) | (x >>> (32 - n)));
    }

    /*
     * F, G, H and I are the basic MD5 functions.
     */
    private int F(int u, int v, int w) {
        return ((u & v) | (~u & w));
    }

    private int G(int u, int v, int w) {
        return ((u & w) | (v & ~w));
    }

    private int H(int u, int v, int w) {
        return (u ^ v ^ w);
    }

    private int K(int u, int v, int w) {
        return (v ^ (u | ~w));
    }

    public int[] getHash() {
	    return new int[]{ H1, H2, H3, H4 };
    }

    public void processBlock(int[] X) {

        int a = H1;
        int b = H2;
        int c = H3;
        int d = H4;

	for (int i = 0; i < 1; i++) {
        //
        // Round 1 - F cycle, 16 times.
        //
	    int x;
	    x = a + ((b & c) | (~b & d)) + X[ 0] + 0xd76aa478;
	    a = ((x << S11) | (x >>> (32 - S11))) + b;
	    x = d + ((a & b) | (~a & c)) + X[ 1] + 0xe8c7b756;
	    d = ((x << S12) | (x >>> (32 - S12))) + a;
	    x = c + ((d & a) | (~d & b)) + X[ 2] + 0x242070db;
	    c = ((x << S13) | (x >>> (32 - S13))) + d;
	    x = b + ((c & d) | (~c & a)) + X[ 3] + 0xc1bdceee;
	    b = ((x << S14) | (x >>> (32 - S14))) + c;
	    x = a + ((b & c) | (~b & d)) + X[ 4] + 0xf57c0faf;
	    a = ((x << S11) | (x >>> (32 - S11))) + b;
	    x = d + ((a & b) | (~a & c)) + X[ 5] + 0x4787c62a;
	    d = ((x << S12) | (x >>> (32 - S12))) + a;
	    x = c + ((d & a) | (~d & b)) + X[ 6] + 0xa8304613;
	    c = ((x << S13) | (x >>> (32 - S13))) + d;
	    x = b + ((c & d) | (~c & a)) + X[ 7] + 0xfd469501;
	    b = ((x << S14) | (x >>> (32 - S14))) + c;
	    x = a + ((b & c) | (~b & d)) + X[ 8] + 0x698098d8;
	    a = ((x << S11) | (x >>> (32 - S11))) + b;
	    x = d + ((a & b) | (~a & c)) + X[ 9] + 0x8b44f7af;
	    d = ((x << S12) | (x >>> (32 - S12))) + a;
	    x = c + ((d & a) | (~d & b)) + X[10] + 0xffff5bb1;
	    c = ((x << S13) | (x >>> (32 - S13))) + d;
	    x = b + ((c & d) | (~c & a)) + X[11] + 0x895cd7be;
	    b = ((x << S14) | (x >>> (32 - S14))) + c;
	    x = a + ((b & c) | (~b & d)) + X[12] + 0x6b901122;
	    a = ((x << S11) | (x >>> (32 - S11))) + b;
	    x = d + ((a & b) | (~a & c)) + X[13] + 0xfd987193;
	    d = ((x << S12) | (x >>> (32 - S12))) + a;
	    x = c + ((d & a) | (~d & b)) + X[14] + 0xa679438e;
	    c = ((x << S13) | (x >>> (32 - S13))) + d;
	    x = b + ((c & d) | (~c & a)) + X[15] + 0x49b40821;
	    b = ((x << S14) | (x >>> (32 - S14))) + c;
//        }
	// Round 2 - G cycle, 16 times.
        //
  //      for (int i = 0; i < 1; i++) {
//	    int x;
            x = a + ((b & d) | (c & ~d)) + X[ 1] + 0xf61e2562; 
	    a = ((x << S21) | (x >>> (32 - S21))) + b; 
            x = d + ((a & c) | (b & ~c)) + X[ 6] + 0xc040b340;
	    d = ((x << S22) | (x >>> (32 - S22))) + a; 
            x = c + ((d & b) | (a & ~b)) + X[11] + 0x265e5a51;
	    c = ((x << S23) | (x >>> (32 - S23))) + d; 
            x = b + ((c & a) | (d & ~a)) + X[ 0] + 0xe9b6c7aa;
	    b = ((x << S24) | (x >>> (32 - S24))) + c; 
            x = a + ((b & d) | (c & ~d)) + X[ 5] + 0xd62f105d;
	    a = ((x << S21) | (x >>> (32 - S21))) + b; 
            x = d + ((a & c) | (b & ~c)) + X[10] + 0x02441453;
	    d = ((x << S22) | (x >>> (32 - S22))) + a; 
            x = c + ((d & b) | (a & ~b)) + X[15] + 0xd8a1e681;
	    c = ((x << S23) | (x >>> (32 - S23))) + d; 
            x = b + ((c & a) | (d & ~a)) + X[ 4] + 0xe7d3fbc8;
	    b = ((x << S24) | (x >>> (32 - S24))) + c; 
            x = a + ((b & d) | (c & ~d)) + X[ 9] + 0x21e1cde6;
	    a = ((x << S21) | (x >>> (32 - S21))) + b; 
            x = d + ((a & c) | (b & ~c)) + X[14] + 0xc33707d6;
	    d = ((x << S22) | (x >>> (32 - S22))) + a; 
            x = c + ((d & b) | (a & ~b)) + X[ 3] + 0xf4d50d87;
	    c = ((x << S23) | (x >>> (32 - S23))) + d; 
            x = b + ((c & a) | (d & ~a)) + X[ 8] + 0x455a14ed;
	    b = ((x << S24) | (x >>> (32 - S24))) + c;  
            x = a + ((b & d) | (c & ~d)) + X[13] + 0xa9e3e905;
	    a = ((x << S21) | (x >>> (32 - S21))) + b; 
            x = d + ((a & c) | (b & ~c)) + X[ 2] + 0xfcefa3f8;
	    d = ((x << S22) | (x >>> (32 - S22))) + a; 
            x = c + ((d & b) | (a & ~b)) + X[ 7] + 0x676f02d9;
	    c = ((x << S23) | (x >>> (32 - S23))) + d; 
            x = b + ((c & a) | (d & ~a)) + X[12] + 0x8d2a4c8a;
	    b = ((x << S24) | (x >>> (32 - S24))) + c; 

//	}
        //
        // Round 3 - H cycle, 16 times.
        //
  //      for (int i = 0; i < 1; i++) {
//	    int x;
	    x = a + (b ^ c ^ d) + X[ 5] + 0xfffa3942;
	    a = ((x << S31) | (x >>> (32 - S31))) + b;
	    x = d + (a ^ b ^ c) + X[ 8] + 0x8771f681;
	    d = ((x << S32) | (x >>> (32 - S32))) + a;
	    x = c + (d ^ a ^ b) + X[11] + 0x6d9d6122;
	    c = ((x << S33) | (x >>> (32 - S33))) + d;
	    x = b + (c ^ d ^ a) + X[14] + 0xfde5380c;
	    b = ((x << S34) | (x >>> (32 - S34))) + c;
	    x = a + (b ^ c ^ d) + X[ 1] + 0xa4beea44;
	    a = ((x << S31) | (x >>> (32 - S31))) + b;
	    x = d + (a ^ b ^ c) + X[ 4] + 0x4bdecfa9;
	    d = ((x << S32) | (x >>> (32 - S32))) + a;
	    x = c + (d ^ a ^ b) + X[ 7] + 0xf6bb4b60;
	    c = ((x << S33) | (x >>> (32 - S33))) + d;
	    x = b + (c ^ d ^ a) + X[10] + 0xbebfbc70;
	    b = ((x << S34) | (x >>> (32 - S34))) + c;
	    x = a + (b ^ c ^ d) + X[13] + 0x289b7ec6;
	    a = ((x << S31) | (x >>> (32 - S31))) + b;
	    x = d + (a ^ b ^ c) + X[ 0] + 0xeaa127fa;
	    d = ((x << S32) | (x >>> (32 - S32))) + a;
	    x = c + (d ^ a ^ b) + X[ 3] + 0xd4ef3085;
	    c = ((x << S33) | (x >>> (32 - S33))) + d;
	    x = b + (c ^ d ^ a) + X[ 6] + 0x04881d05;
	    b = ((x << S34) | (x >>> (32 - S34))) + c;
	    x = a + (b ^ c ^ d) + X[ 9] + 0xd9d4d039;
	    a = ((x << S31) | (x >>> (32 - S31))) + b;
	    x = d + (a ^ b ^ c) + X[12] + 0xe6db99e5;
	    d = ((x << S32) | (x >>> (32 - S32))) + a;
	    x = c + (d ^ a ^ b) + X[15] + 0x1fa27cf8;
	    c = ((x << S33) | (x >>> (32 - S33))) + d;
	    x = b + (c ^ d ^ a) + X[ 2] + 0xc4ac5665;
	    b = ((x << S34) | (x >>> (32 - S34))) + c;

        //
        // Round 4 - K cycle, 16 times.
        //
//        }

//	for (int i = 0; i < 1; i++) {
//	    int x;
	    x = a + (c ^ (b | ~d)) + X[ 0] + 0xf4292244;

	    a = ((x << S41) | (x >>> (32 - S41))) + b;
	    x = d + (b ^ (a | ~c)) + X[ 7] + 0x432aff97;
	    d = ((x << S42) | (x >>> (32 - S42))) + a;
	    x = c + (a ^ (d | ~b)) + X[14] + 0xab9423a7;
	    c = ((x << S43) | (x >>> (32 - S43))) + d;
	    x = b + (d ^ (c | ~a)) + X[ 5] + 0xfc93a039;
	    b = ((x << S44) | (x >>> (32 - S44))) + c;
	    x = a + (c ^ (b | ~d)) + X[12] + 0x655b59c3;
	    a = ((x << S41) | (x >>> (32 - S41))) + b;
	    x = d + (b ^ (a | ~c)) + X[ 3] + 0x8f0ccc92;
	    d = ((x << S42) | (x >>> (32 - S42))) + a;
	    x = c + (a ^ (d | ~b)) + X[10] + 0xffeff47d;
	    c = ((x << S43) | (x >>> (32 - S43))) + d;
	    x = b + (d ^ (c | ~a)) + X[ 1] + 0x85845dd1;
	    b = ((x << S44) | (x >>> (32 - S44))) + c;
	    x = a + (c ^ (b | ~d)) + X[ 8] + 0x6fa87e4f;
	    a = ((x << S41) | (x >>> (32 - S41))) + b;
	    x = d + (b ^ (a | ~c)) + X[15] + 0xfe2ce6e0;
	    d = ((x << S42) | (x >>> (32 - S42))) + a;
	    x = c + (a ^ (d | ~b)) + X[ 6] + 0xa3014314;
	    c = ((x << S43) | (x >>> (32 - S43))) + d;
	    x = b + (d ^ (c | ~a)) + X[13] + 0x4e0811a1;
	    b = ((x << S44) | (x >>> (32 - S44))) + c;
	    x = a + (c ^ (b | ~d)) + X[ 4] + 0xf7537e82;
	    a = ((x << S41) | (x >>> (32 - S41))) + b;
	    x = d + (b ^ (a | ~c)) + X[11] + 0xbd3af235;
	    d = ((x << S42) | (x >>> (32 - S42))) + a;
	    x = c + (a ^ (d | ~b)) + X[ 2] + 0x2ad7d2bb;
	    c = ((x << S43) | (x >>> (32 - S43))) + d;
	    x = b + (d ^ (c | ~a)) + X[ 9] + 0xeb86d391;
	    b = ((x << S44) | (x >>> (32 - S44))) + c;

	}

        H1 += a;
        H2 += b;
        H3 += c;
        H4 += d;
        

        //
        // reset the offset and clean out the word buffer.
        //
        xOff = 0;
        for (int i = 0; i != 16; i++) {
            X[i] = 0;
        }

    }
    
    public int [] getH(){
    	int [] erg = {H1, H2,H3,H4};
    	return erg;
    }

}
