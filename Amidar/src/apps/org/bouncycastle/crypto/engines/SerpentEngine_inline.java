package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Serpent is a 128-bit 32-round block cipher with variable key lengths,
 * including 128, 192 and 256 bit keys conjectured to be at least as
 * secure as three-key triple-DES.
 * <p>
 * Serpent was designed by Ross Anderson, Eli Biham and Lars Knudsen as a
 * candidate algorithm for the NIST AES Quest.>
 * <p>
 * For full details see the <a href="http://www.cl.cam.ac.uk/~rja14/serpent.html">The Serpent home page</a>
 */
public class SerpentEngine_inline
{
    private int BLOCK_SIZE;

    private int ROUNDS;
    private int PHI ;       // (sqrt(5) - 1) * 2**31

    private boolean encrypting;
    private int[] wKey;

    private int X0, X1, X2, X3;    // registers

    public SerpentEngine_inline() {
	this.BLOCK_SIZE = 16;
	this.ROUNDS = 32;
	this.PHI = 0x9E3779B9;
    }

    /**
     * initialise a Serpent cipher.
     *
     * @param encrypting whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    public void init(
        boolean             encrypting,
        KeyParameter    params)
    {
            this.encrypting = encrypting;
            this.wKey = makeWorkingKey(((KeyParameter)params).getKey());
            return;

    }

    public String getAlgorithmName()
    {
        return "Serpent";
    }

    public int getBlockSize()
    {
        return BLOCK_SIZE;
    }

    /**
     * Process one block of input from the array in and write it to
     * the out array.
     *
     * @param in the array containing the input data.
     * @param inOff offset into the in array the data starts at.
     * @param out the array the output data will be copied into.
     * @param outOff the offset into the out array the output will start at.
     * @exception DataLengthException if there isn't enough data in in, or
     * space in out.
     * @exception IllegalStateException if the cipher isn't initialised.
     * @return the number of bytes processed and produced.
     */
    public final int processBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
    /*    if (wKey == null)
        {
        }

        if ((inOff + BLOCK_SIZE) > in.length)
        {
        }

        if ((outOff + BLOCK_SIZE) > out.length)
        {
        }
*/
        if (encrypting)
        {
            encryptBlock(in, inOff, out, outOff);
        }
        else
        {
            decryptBlock(in, inOff, out, outOff);
        }

        return BLOCK_SIZE;
    }

    public void reset()
    {
    }

    /**
     * Expand a user-supplied key material into a session key.
     *
     * @param key  The user-key bytes (multiples of 4) to use.
     * @exception IllegalArgumentException
     */
    private int[] makeWorkingKey(
        byte[] key)
    throws  IllegalArgumentException
    {
        //
        // pad key to 256 bits
        //
        int[]   kPad = new int[16];
        int     off = 0;
        int     length = 0;

        for (off = key.length - 4; off > 0; off -= 4)
        {
//            kPad[length++] = bytesToWord(key, off);
	    kPad[length++] = (((key[off] & 0xff) << 24) | ((key[off + 1] & 0xff) <<  16) | ((key[off + 2] & 0xff) << 8) | ((key[off + 3] & 0xff)));
        }

        if (off == 0)
        {
//            kPad[length++] = bytesToWord(key, 0);
            kPad[length++] = (((key[0] & 0xff) << 24) | ((key[1] & 0xff) <<  16) | ((key[2] & 0xff) << 8) | ((key[3] & 0xff)));
            if (length < 8)
            {
                kPad[length] = 1;
            }
        }
        else
        {
        }

        //
        // expand the padded key up to 33 x 128 bits of key material
        //
        int     amount = (ROUNDS + 1) * 4;
        int[]   w = new int[amount];

        //
        // compute w0 to w7 from w-8 to w-1
        //
        for (int i = 8; i < 16; i++)
        {
//            kPad[i] = rotateLeft(kPad[i - 8] ^ kPad[i - 5] ^ kPad[i - 3] ^ kPad[i - 1] ^ PHI ^ (i - 8), 11);
	    int x = kPad[i - 8] ^ kPad[i - 5] ^ kPad[i - 3] ^ kPad[i - 1] ^ PHI ^ (i - 8);
	    kPad[i] =  ((x << 11) | (x >>> (32 - 11)));
        }

	for (int i = 0; i < 8; i++) w[i] = kPad[8+i];
//        System.arraycopy(kPad, 8, w, 0, 8);

        //
        // compute w8 to w136
        //
        for (int i = 8; i < amount; i++)
        {
//            w[i] = rotateLeft(w[i - 8] ^ w[i - 5] ^ w[i - 3] ^ w[i - 1] ^ PHI ^ i, 11);
	    int x = w[i - 8] ^ w[i - 5] ^ w[i - 3] ^ w[i - 1] ^ PHI ^ i;
	    w[i] = ((x << 11) | (x >>> (32 - 11)));
        }

        //
        // create the working keys by processing w with the Sbox and IP
        //
        int a, b, c ,d;
	int t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12;
        for (int i = 0; i < 128; i+=32) {
//	sb3(w[i+0], w[i+1], w[i+2], w[i+3]);	
            a = w[i+0];
	    b = w[i+1];
	    c = w[i+2];
	    d = w[i+3];
            t1 = a ^ b;        
            t2 = a & c;        
            t3 = a | d;        
            t4 = c ^ d;        
            t5 = t1 & t3;    
            t6 = t2 | t5;    
            X2 = t4 ^ t6;    
            t8 = b ^ t3;    
            t9 = t6 ^ t8;    
            t10 = t4 & t9;    
            X0 = t1 ^ t10;    
            t12 = X2 & X0;    
            X1 = t9 ^ t12;    
            X3 = (b | d) ^ (t4 ^ t12);
            w[i+0] = X0; w[i+1] = X1; w[i+2] = X2; w[i+3] = X3; 

//            sb2(w[i+4], w[i+5], w[i+6], w[i+7]);
	    a = w[i+4];
	    b = w[i+5];
	    c = w[i+6];
	    d = w[i+7];
	    t1 = ~a;        
            t2 = b ^ d;
            t3 = c & t1;
            X0 = t2 ^ t3;
            t5 = c ^ t1;
            t6 = c ^ X0;
            t7 = b & t6;
            X3 = t5 ^ t7;
            X2 = a ^ ((d | t7) & (X0 | t5));
            X1 = (t2 ^ X3) ^ (X2 ^ (d | t1));
            w[i+4] = X0; w[i+5] = X1; w[i+6] = X2; w[i+7] = X3; 

	    
  //          sb1(w[i+8], w[i+9], w[i+10], w[i+11]);
	    a = w[i+8];
	    b = w[i+9];
	    c = w[i+10];
	    d = w[i+11];
	    t2 = b ^ (~a);    
            t5 = c ^ (a | t2);    
            X2 = d ^ t5;        
            t7 = b ^ (d | t2);    
            t8 = t2 ^ X2;    
            X3 = t8 ^ (t5 & t7);    
            t11 = t5 ^ t7;    
            X1 = X3 ^ t11;    
	    X0 = t5 ^ (t8 & t11);
            w[i+8] = X0; w[i+9] = X1; w[i+10] = X2; w[i+11] = X3; 

//            sb0(w[i+12], w[i+13], w[i+14], w[i+15]);
	    a = w[i+12];
	    b = w[i+13];
	    c = w[i+14];
	    d = w[i+15];
	    t1 = a ^ d;        
            t3 = c ^ t1;    
            t4 = b ^ t3;    
            X3 = (a & d) ^ t4;    
            t7 = a ^ (b & t1);    
            X2 = t4 ^ (c | t7);    
            t12 = X3 & (t3 ^ t7);    
            X1 = (~t3) ^ t12;    
            X0 = t12 ^ (~t7);
            w[i+12] = X0; w[i+13] = X1; w[i+14] = X2; w[i+15] = X3; 

      //      sb7(w[i+16], w[i+17], w[i+18], w[i+19]);
	    a = w[i+16];
	    b = w[i+17];
	    c = w[i+18];
	    d = w[i+19];
	    t1 = b ^ c;        
            t2 = c & t1;    
            t3 = d ^ t2;    
            t4 = a ^ t3;    
            t5 = d | t1;    
            t6 = t4 & t5;    
            X1 = b ^ t6;        
            t8 = t3 | X1;    
            t9 = a & t4;    
            X3 = t1 ^ t9;    
            t11 = t4 ^ t8;    
            t12 = X3 & t11;    
            X2 = t3 ^ t12;    
            X0 = (~t11) ^ (X3 & X2);
            w[i+16] = X0; w[i+17] = X1; w[i+18] = X2; w[i+19] = X3; 

    //        sb6(w[i+20], w[i+21], w[i+22], w[i+23]);
	    a = w[i+20];
	    b = w[i+21];
	    c = w[i+22];
	    d = w[i+23];
	    t1 = ~a;        
            t2 = a ^ d;        
            t3 = b ^ t2;    
            t4 = t1 | t2;    
            t5 = c ^ t4;    
            X1 = b ^ t5;        
            t7 = t2 | X1;    
            t8 = d ^ t7;    
            t9 = t5 & t8;    
            X2 = t3 ^ t9;    
            t11 = t5 ^ t8;    
            X0 = X2 ^ t11;    
            X3 = (~t5) ^ (t3 & t11);
            w[i+20] = X0; w[i+21] = X1; w[i+22] = X2; w[i+23] = X3; 

  //          sb5(w[i+24], w[i+25], w[i+26], w[i+27]);
	    a = w[i+24];
	    b = w[i+25];
	    c = w[i+26];
	    d = w[i+27];
	    t1 = ~a;        
            t2 = a ^ b;        
            t3 = a ^ d;        
            t4 = c ^ t1;    
            t5 = t2 | t3;    
            X0 = t4 ^ t5;    
            t7 = d & X0;        
            t8 = t2 ^ X0;    
            X1 = t7 ^ t8;    
            t10 = t1 | X0;    
            t11 = t2 | t7;    
            t12 = t3 ^ t10;    
            X2 = t11 ^ t12;    
            X3 = (b ^ t7) ^ (X1 & t12);
            w[i+24] = X0; w[i+25] = X1; w[i+26] = X2; w[i+27] = X3; 

//            sb4(w[i+28], w[i+29], w[i+30], w[i+31]);
	    a = w[i+28];
	    b = w[i+29];
	    c = w[i+30];
	    d = w[i+31];
	    t1 = a ^ d;        
            t2 = d & t1;    
            t3 = c ^ t2;    
            t4 = b | t3;    
            X3 = t1 ^ t4;    
            t6 = ~b;        
            t7 = t1 | t6;    
            X0 = t3 ^ t7;    
            t9 = a & X0;        
            t10 = t1 ^ t6;    
            t11 = t4 & t10;    
            X2 = t9 ^ t11;    
            X1 = (a ^ t3) ^ (t10 & X2);
	    w[i+28] = X0; w[i+29] = X1; w[i+30] = X2; w[i+31] = X3; 

	}

        sb3(w[128], w[129], w[130], w[131]);
        w[128] = X0; w[129] = X1; w[130] = X2; w[131] = X3; 

        return w;
    }

    private int rotateLeft(
        int     x,
        int     bits)
    {
        return ((x << bits) | (x >>> (32 - bits)));
    }

    private int rotateRight(
        int     x,
        int     bits)
    {
        return ((x >>> bits) | (x << (32 - bits)));
    }

    private int bytesToWord(
        byte[]  src,
        int     srcOff)
    {
        return (((src[srcOff] & 0xff) << 24) | ((src[srcOff + 1] & 0xff) <<  16) |
          ((src[srcOff + 2] & 0xff) << 8) | ((src[srcOff + 3] & 0xff)));
    }

    private void wordToBytes(
        int     word,
        byte[]  dst,
        int     dstOff)
    {
        dst[dstOff + 3] = (byte)(word);
        dst[dstOff + 2] = (byte)(word >>> 8);
        dst[dstOff + 1] = (byte)(word >>> 16);
        dst[dstOff]     = (byte)(word >>> 24);
    }

    /**
     * Encrypt one block of plaintext.
     *
     * @param in the array containing the input data.
     * @param inOff offset into the in array the data starts at.
     * @param out the array the output data will be copied into.
     * @param outOff the offset into the out array the output will start at.
     */
    private void encryptBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        X3 = bytesToWord(in, inOff);
        X2 = bytesToWord(in, inOff + 4);
        X1 = bytesToWord(in, inOff + 8);
        X0 = bytesToWord(in, inOff + 12);

	int a, b, c, d;
	int t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12;
	int x0, x1, x2, x3;

	for (int i = 0; i < 128; i+=32) {

//            sb0(wKey[i+0] ^ X0, wKey[i+1] ^ X1, wKey[i+2] ^ X2, wKey[i+3] ^ X3); 
	    a = wKey[i] ^ X0;
	    b = wKey[i+1] ^ X1;
	    c = wKey[i+2] ^ X2;
	    d = wKey[i+3] ^ X3;
	    t1 = a ^ d;        
            t3 = c ^ t1;    
            t4 = b ^ t3;    
            X3 = (a & d) ^ t4;    
            t7 = a ^ (b & t1);    
            X2 = t4 ^ (c | t7);    
            t12 = X3 & (t3 ^ t7);    
            X1 = (~t3) ^ t12;    
            X0 = t12 ^ (~t7);

            x0 = ((X0 << 13) | (X0 >>> (32 - 13)));
            x2  = ((X2 << 3) | (X2 >>> (32 - 3)));
            x1  = X1 ^ x0 ^ x2 ;
            x3  = X3 ^ x2 ^ x0 << 3;
            X1 = ((x1 << 1) | (x1 >>> (32 - 1)));
            X3 = ((x3 << 7) | (x3 >>> (32 - 7)));
            X0 = (((x0 ^ X1 ^ X3) << 5) | ((x0 ^ X1 ^ X3) >>> (32 - 5)));
            X2 = (((x2 ^ X3 ^ (X1 << 7)) << 22) | ((x2 ^ X3 ^ (X1 << 7)) >>> (32 - 22)));

//            sb1(wKey[i+4] ^ X0, wKey[i+5] ^ X1, wKey[i+6] ^ X2, wKey[i+7] ^ X3);
	    a = wKey[i+4] ^ X0;
	    b = wKey[i+5] ^ X1;
	    c = wKey[i+6] ^ X2;
	    d = wKey[i+7] ^ X3;
	    t2 = b ^ (~a);    
            t5 = c ^ (a | t2);    
            X2 = d ^ t5;        
            t7 = b ^ (d | t2);    
            t8 = t2 ^ X2;    
            X3 = t8 ^ (t5 & t7);    
            t11 = t5 ^ t7;    
            X1 = X3 ^ t11;    
            X0 = t5 ^ (t8 & t11);

            x0 = ((X0 << 13) | (X0 >>> (32 - 13)));
            x2  = ((X2 << 3) | (X2 >>> (32 - 3)));
            x1  = X1 ^ x0 ^ x2 ;
            x3  = X3 ^ x2 ^ x0 << 3;
            X1 = ((x1 << 1) | (x1 >>> (32 - 1)));
            X3 = ((x3 << 7) | (x3 >>> (32 - 7)));
            X0 = (((x0 ^ X1 ^ X3) << 5) | ((x0 ^ X1 ^ X3) >>> (32 - 5)));
            X2 = (((x2 ^ X3 ^ (X1 << 7)) << 22) | ((x2 ^ X3 ^ (X1 << 7)) >>> (32 - 22)));

//            sb2(wKey[i+8] ^ X0, wKey[i+9] ^ X1, wKey[i+10] ^ X2, wKey[i+11] ^ X3);
	    a = wKey[i+8] ^ X0;
	    b = wKey[i+9] ^ X1;
	    c = wKey[i+10] ^ X2;
	    d = wKey[i+11] ^ X3;
	    t1 = ~a;        
            t2 = b ^ d;
            t3 = c & t1;
            X0 = t2 ^ t3;
            t5 = c ^ t1;
            t6 = c ^ X0;
            t7 = b & t6;
            X3 = t5 ^ t7;
            X2 = a ^ ((d | t7) & (X0 | t5));
            X1 = (t2 ^ X3) ^ (X2 ^ (d | t1));

            x0 = ((X0 << 13) | (X0 >>> (32 - 13)));
            x2  = ((X2 << 3) | (X2 >>> (32 - 3)));
            x1  = X1 ^ x0 ^ x2 ;
            x3  = X3 ^ x2 ^ x0 << 3;
            X1 = ((x1 << 1) | (x1 >>> (32 - 1)));
            X3 = ((x3 << 7) | (x3 >>> (32 - 7)));
            X0 = (((x0 ^ X1 ^ X3) << 5) | ((x0 ^ X1 ^ X3) >>> (32 - 5)));
            X2 = (((x2 ^ X3 ^ (X1 << 7)) << 22) | ((x2 ^ X3 ^ (X1 << 7)) >>> (32 - 22)));

//            sb3(wKey[i+12] ^ X0, wKey[i+13] ^ X1, wKey[i+14] ^ X2, wKey[i+15] ^ X3);
	    a = wKey[i+12] ^ X0;
	    b = wKey[i+13] ^ X1;
	    c = wKey[i+14] ^ X2;
	    d = wKey[i+15] ^ X3;
	    t1 = a ^ b;        
            t2 = a & c;        
            t3 = a | d;        
            t4 = c ^ d;        
            t5 = t1 & t3;    
            t6 = t2 | t5;    
            X2 = t4 ^ t6;    
            t8 = b ^ t3;    
            t9 = t6 ^ t8;    
            t10 = t4 & t9;    
            X0 = t1 ^ t10;    
            t12 = X2 & X0;    
            X1 = t9 ^ t12;    
            X3 = (b | d) ^ (t4 ^ t12);

            x0 = ((X0 << 13) | (X0 >>> (32 - 13)));
            x2  = ((X2 << 3) | (X2 >>> (32 - 3)));
            x1  = X1 ^ x0 ^ x2 ;
            x3  = X3 ^ x2 ^ x0 << 3;
            X1 = ((x1 << 1) | (x1 >>> (32 - 1)));
            X3 = ((x3 << 7) | (x3 >>> (32 - 7)));
            X0 = (((x0 ^ X1 ^ X3) << 5) | ((x0 ^ X1 ^ X3) >>> (32 - 5)));
            X2 = (((x2 ^ X3 ^ (X1 << 7)) << 22) | ((x2 ^ X3 ^ (X1 << 7)) >>> (32 - 22)));

//            sb4(wKey[i+16] ^ X0, wKey[i+17] ^ X1, wKey[i+18] ^ X2, wKey[i+19] ^ X3);
	    a = wKey[i+16] ^ X0;
	    b = wKey[i+17] ^ X1;
	    c = wKey[i+18] ^ X2;
	    d = wKey[i+19] ^ X3;
	    t1 = a ^ d;        
            t2 = d & t1;    
            t3 = c ^ t2;    
            t4 = b | t3;    
            X3 = t1 ^ t4;    
            t6 = ~b;        
            t7 = t1 | t6;    
            X0 = t3 ^ t7;    
            t9 = a & X0;        
            t10 = t1 ^ t6;    
            t11 = t4 & t10;    
            X2 = t9 ^ t11;    
            X1 = (a ^ t3) ^ (t10 & X2);

            x0 = ((X0 << 13) | (X0 >>> (32 - 13)));
            x2  = ((X2 << 3) | (X2 >>> (32 - 3)));
            x1  = X1 ^ x0 ^ x2 ;
            x3  = X3 ^ x2 ^ x0 << 3;
            X1 = ((x1 << 1) | (x1 >>> (32 - 1)));
            X3 = ((x3 << 7) | (x3 >>> (32 - 7)));
            X0 = (((x0 ^ X1 ^ X3) << 5) | ((x0 ^ X1 ^ X3) >>> (32 - 5)));
            X2 = (((x2 ^ X3 ^ (X1 << 7)) << 22) | ((x2 ^ X3 ^ (X1 << 7)) >>> (32 - 22)));

//            sb5(wKey[i+20] ^ X0, wKey[i+21] ^ X1, wKey[i+22] ^ X2, wKey[i+23] ^ X3);
	    a = wKey[i+20] ^ X0;
	    b = wKey[i+21] ^ X1;
	    c = wKey[i+22] ^ X2;
	    d = wKey[i+23] ^ X3;
	    t1 = ~a;        
            t2 = a ^ b;        
            t3 = a ^ d;        
            t4 = c ^ t1;    
            t5 = t2 | t3;    
            X0 = t4 ^ t5;    
            t7 = d & X0;        
            t8 = t2 ^ X0;    
            X1 = t7 ^ t8;    
            t10 = t1 | X0;    
            t11 = t2 | t7;    
            t12 = t3 ^ t10;    
            X2 = t11 ^ t12;    
            // X3 = (b ^ t7) ^ (X1 & t12);
            X3 = X1 & t12 ^ b ^ t7;

            x0 = ((X0 << 13) | (X0 >>> (32 - 13)));
            x2  = ((X2 << 3) | (X2 >>> (32 - 3)));
            x1  = X1 ^ x0 ^ x2 ;
            x3  = X3 ^ x2 ^ x0 << 3;
            X1 = ((x1 << 1) | (x1 >>> (32 - 1)));
            X3 = ((x3 << 7) | (x3 >>> (32 - 7)));
            X0 = (((x0 ^ X1 ^ X3) << 5) | ((x0 ^ X1 ^ X3) >>> (32 - 5)));
            X2 = (((x2 ^ X3 ^ (X1 << 7)) << 22) | ((x2 ^ X3 ^ (X1 << 7)) >>> (32 - 22)));

//            sb6(wKey[i+24] ^ X0, wKey[i+25] ^ X1, wKey[i+26] ^ X2, wKey[i+27] ^ X3);
	    a = wKey[i+24] ^ X0;
	    b = wKey[i+25] ^ X1;
	    c = wKey[i+26] ^ X2;
	    d = wKey[i+27] ^ X3;
	    t1 = ~a;        
            t2 = a ^ d;        
            t3 = b ^ t2;    
            t4 = t1 | t2;    
            t5 = c ^ t4;    
            X1 = b ^ t5;        
            t7 = t2 | X1;    
            t8 = d ^ t7;    
            t9 = t5 & t8;    
            X2 = t3 ^ t9;    
            t11 = t5 ^ t8;    
            X0 = X2 ^ t11;    
            X3 = (~t5) ^ (t3 & t11);

            x0 = ((X0 << 13) | (X0 >>> (32 - 13)));
            x2  = ((X2 << 3) | (X2 >>> (32 - 3)));
            x1  = X1 ^ x0 ^ x2 ;
            x3  = X3 ^ x2 ^ x0 << 3;
            X1 = ((x1 << 1) | (x1 >>> (32 - 1)));
            X3 = ((x3 << 7) | (x3 >>> (32 - 7)));
            X0 = (((x0 ^ X1 ^ X3) << 5) | ((x0 ^ X1 ^ X3) >>> (32 - 5)));
            X2 = (((x2 ^ X3 ^ (X1 << 7)) << 22) | ((x2 ^ X3 ^ (X1 << 7)) >>> (32 - 22)));

//            sb7(wKey[i+28] ^ X0, wKey[i+29] ^ X1, wKey[i+30] ^ X2, wKey[i+31] ^ X3);
	    a = wKey[i+28] ^ X0;
	    b = wKey[i+29] ^ X1;
	    c = wKey[i+30] ^ X2;
	    d = wKey[i+31] ^ X3;
	    t1 = b ^ c;        
            t2 = c & t1;    
            t3 = d ^ t2;    
            t4 = a ^ t3;    
            t5 = d | t1;    
            t6 = t4 & t5;    
            X1 = b ^ t6;        
            t8 = t3 | X1;    
            t9 = a & t4;    
            X3 = t1 ^ t9;    
            t11 = t4 ^ t8;    
            t12 = X3 & t11;    
            X2 = t3 ^ t12;    
            X0 = (~t11) ^ (X3 & X2);

	    if (i < 96) {

		x0 = ((X0 << 13) | (X0 >>> (32 - 13)));
            	x2  = ((X2 << 3) | (X2 >>> (32 - 3)));
            	x1  = X1 ^ x0 ^ x2 ;
            	x3  = X3 ^ x2 ^ x0 << 3;
            	X1 = ((x1 << 1) | (x1 >>> (32 - 1)));
            	X3 = ((x3 << 7) | (x3 >>> (32 - 7)));
            	X0 = (((x0 ^ X1 ^ X3) << 5) | ((x0 ^ X1 ^ X3) >>> (32 - 5)));
            	X2 = (((x2 ^ X3 ^ (X1 << 7)) << 22) | ((x2 ^ X3 ^ (X1 << 7)) >>> (32 - 22)));
	    };
	}

        wordToBytes(wKey[131] ^ X3, out, outOff);
        wordToBytes(wKey[130] ^ X2, out, outOff + 4);
        wordToBytes(wKey[129] ^ X1, out, outOff + 8);
        wordToBytes(wKey[128] ^ X0, out, outOff + 12);
    }

    /**
     * Decrypt one block of ciphertext.
     *:w

     * @param in the array containing the input data.
     * @param inOff offset into the in array the data starts at.
     * @param out the array the output data will be copied into.
     * @param outOff the offset into the out array the output will start at.
     */
    private void decryptBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        X3 = wKey[131] ^ bytesToWord(in, inOff);
        X2 = wKey[130] ^ bytesToWord(in, inOff + 4);
        X1 = wKey[129] ^ bytesToWord(in, inOff + 8);
        X0 = wKey[128] ^ bytesToWord(in, inOff + 12);

	int x0, x1, x2, x3;
	int t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12;
	int a, b, c, d;

	for (int i = 100; i > 3; i-=32) {
	    if (i < 96) {
		X0 ^= wKey[i+28]; X1 ^= wKey[i+29]; X2 ^= wKey[i+30]; X3 ^= wKey[i+31];

		x2 = (((X2) >>> 22) | (X2 << (32 - 22))) ^ X3 ^ (X1 << 7);
		x0 = ((X0 >>> 5) | (X0 << (32 - 5))) ^ X1 ^ X3;
		x3 = ((X3 >>> 7) | (X3 << (32 - 7)));
		x1 = ((X1 >>> 1) | (X1 << (32 - 1)));
		X3 = x3 ^ x2 ^ x0 << 3;
		X1 = x1 ^ x0 ^ x2;
		X2 = ((x2 >>> 3) | (x2 << (32 - 3)));
		X0 = ((x0 >>> 13) | (x0 << (32 - 13)));

	    }

	    a = X0;
	    b = X1;
	    c = X2;
	    d = X3;
	    t3 = c | (a & b);
            t4 = d & (a | b);
            X3 = t3 ^ t4;
            t6 = ~d;
            t7 = b ^ t4;
            t9 = t7 | (X3 ^ t6);
            X1 = a ^ t9;
            X0 = (c ^ t7) ^ (d | X1);
            X2 = (t3 ^ X1) ^ (X0 ^ (a & X3));

            X0 ^= wKey[i+24]; X1 ^= wKey[i+25]; X2 ^= wKey[i+26]; X3 ^= wKey[i+27];

	    x2 = (((X2) >>> 22) | (X2 << (32 - 22))) ^ X3 ^ (X1 << 7);
	    x0 = ((X0 >>> 5) | (X0 << (32 - 5))) ^ X1 ^ X3;
	    x3 = ((X3 >>> 7) | (X3 << (32 - 7)));
	    x1 = ((X1 >>> 1) | (X1 << (32 - 1)));
	    X3 = x3 ^ x2 ^ x0 << 3;
	    X1 = x1 ^ x0 ^ x2;
	    X2 = ((x2 >>> 3) | (x2 << (32 - 3)));
	    X0 = ((x0 >>> 13) | (x0 << (32 - 13)));

	    a = X0;
	    b = X1;
	    c = X2;
	    d = X3;
	    t1 = ~a;        
            t2 = a ^ b;        
            t3 = c ^ t2;    
            t4 = c | t1;    
            t5 = d ^ t4;    
            X1 = t3 ^ t5;    
            t7 = t3 & t5;    
            t8 = t2 ^ t7;    
            t9 = b | t8;    
            X3 = t5 ^ t9;    
            t11 = b | X3;    
            X0 = t8 ^ t11;    
            X2 = (d & t1) ^ (t3 ^ t11);

            X0 ^= wKey[i+20]; X1 ^= wKey[i+21]; X2 ^= wKey[i+22]; X3 ^= wKey[i+23];

	    x2 = (((X2) >>> 22) | (X2 << (32 - 22))) ^ X3 ^ (X1 << 7);
	    x0 = ((X0 >>> 5) | (X0 << (32 - 5))) ^ X1 ^ X3;
	    x3 = ((X3 >>> 7) | (X3 << (32 - 7)));
	    x1 = ((X1 >>> 1) | (X1 << (32 - 1)));
	    X3 = x3 ^ x2 ^ x0 << 3;
	    X1 = x1 ^ x0 ^ x2;
	    X2 = ((x2 >>> 3) | (x2 << (32 - 3)));
	    X0 = ((x0 >>> 13) | (x0 << (32 - 13)));

	    a = X0;
	    b = X1;
	    c = X2;
	    d = X3;
            t1 = ~c;
            t2 = b & t1;
            t3 = d ^ t2;
            t4 = a & t3;
            t5 = b ^ t1;
            X3 = t4 ^ t5;
            t7 = b | X3;
            t8 = a & t7;
            X1 = t3 ^ t8;
            t10 = a | d;
            t11 = t1 ^ t7;
            X0 = t10 ^ t11;
            X2 = (b & t10) ^ (t4 | (a ^ c));

            X0 ^= wKey[i+16]; X1 ^= wKey[i+17]; X2 ^= wKey[i+18]; X3 ^= wKey[i+19];

	    x2 = (((X2) >>> 22) | (X2 << (32 - 22))) ^ X3 ^ (X1 << 7);
	    x0 = ((X0 >>> 5) | (X0 << (32 - 5))) ^ X1 ^ X3;
	    x3 = ((X3 >>> 7) | (X3 << (32 - 7)));
	    x1 = ((X1 >>> 1) | (X1 << (32 - 1)));
	    X3 = x3 ^ x2 ^ x0 << 3;
	    X1 = x1 ^ x0 ^ x2;
	    X2 = ((x2 >>> 3) | (x2 << (32 - 3)));
	    X0 = ((x0 >>> 13) | (x0 << (32 - 13)));

	    a = X0;
	    b = X1;
	    c = X2;
	    d = X3;
	    t1 = c | d;        
            t2 = a & t1;    
            t3 = b ^ t2;    
            t4 = a & t3;    
            t5 = c ^ t4;    
            X1 = d ^ t5;        
            t7 = ~a;        
            t8 = t5 & X1;    
            X3 = t3 ^ t8;    
            t10 = X1 | t7;    
            t11 = d ^ t10;    
            X0 = X3 ^ t11;    
            X2 = (t3 & t11) ^ (X1 ^ t7);

            X0 ^= wKey[i+12]; X1 ^= wKey[i+13]; X2 ^= wKey[i+14]; X3 ^= wKey[i+15];

	    x2 = (((X2) >>> 22) | (X2 << (32 - 22))) ^ X3 ^ (X1 << 7);
	    x0 = ((X0 >>> 5) | (X0 << (32 - 5))) ^ X1 ^ X3;
	    x3 = ((X3 >>> 7) | (X3 << (32 - 7)));
	    x1 = ((X1 >>> 1) | (X1 << (32 - 1)));
	    X3 = x3 ^ x2 ^ x0 << 3;
	    X1 = x1 ^ x0 ^ x2;
	    X2 = ((x2 >>> 3) | (x2 << (32 - 3)));
	    X0 = ((x0 >>> 13) | (x0 << (32 - 13)));

	    a = X0;
	    b = X1;
	    c = X2;
	    d = X3;
            t1 = a | b;        
            t2 = b ^ c;        
            t3 = b & t2;    
            t4 = a ^ t3;    
            t5 = c ^ t4;    
            t6 = d | t4;    
            X0 = t2 ^ t6;    
            t8 = t2 | t6;    
            t9 = d ^ t8;    
            X2 = t5 ^ t9;    
            t11 = t1 ^ t9;    
            t12 = X0 & t11;    
            X3 = t4 ^ t12;    
            X1 = X3 ^ (X0 ^ t11);

            X0 ^= wKey[i+8]; X1 ^= wKey[i+9]; X2 ^= wKey[i+10]; X3 ^= wKey[i+11];

	    x2 = (((X2) >>> 22) | (X2 << (32 - 22))) ^ X3 ^ (X1 << 7);
	    x0 = ((X0 >>> 5) | (X0 << (32 - 5))) ^ X1 ^ X3;
	    x3 = ((X3 >>> 7) | (X3 << (32 - 7)));
	    x1 = ((X1 >>> 1) | (X1 << (32 - 1)));
	    X3 = x3 ^ x2 ^ x0 << 3;
	    X1 = x1 ^ x0 ^ x2;
	    X2 = ((x2 >>> 3) | (x2 << (32 - 3)));
	    X0 = ((x0 >>> 13) | (x0 << (32 - 13)));

	    a = X0;
	    b = X1;
	    c = X2;
	    d = X3;
            t1 = b ^ d;        
            t2 = ~t1;        
            t3 = a ^ c;
            t4 = c ^ t1;
            t5 = b & t4;
            X0 = t3 ^ t5;
            t7 = a | t2;
            t8 = d ^ t7;
            t9 = t3 | t8;
            X3 = t1 ^ t9;
            t11 = ~t4;        
            t12 = X0 | X3;
            X1 = t11 ^ t12;
            X2 = (d & t11) ^ (t3 ^ t12);

            X0 ^= wKey[i+4]; X1 ^= wKey[i+5]; X2 ^= wKey[i+6]; X3 ^= wKey[i+7];

	    x2 = (((X2) >>> 22) | (X2 << (32 - 22))) ^ X3 ^ (X1 << 7);
	    x0 = ((X0 >>> 5) | (X0 << (32 - 5))) ^ X1 ^ X3;
	    x3 = ((X3 >>> 7) | (X3 << (32 - 7)));
	    x1 = ((X1 >>> 1) | (X1 << (32 - 1)));
	    X3 = x3 ^ x2 ^ x0 << 3;
	    X1 = x1 ^ x0 ^ x2;
	    X2 = ((x2 >>> 3) | (x2 << (32 - 3)));
	    X0 = ((x0 >>> 13) | (x0 << (32 - 13)));

	    a = X0;
	    b = X1;
	    c = X2;
	    d = X3;
	    t1 = b ^ d;        
            t3 = a ^ (b & t1);    
            t4 = t1 ^ t3;    
            X3 = c ^ t4;        
            t7 = b ^ (t1 & t3);    
            t8 = X3 | t7;    
            X1 = t3 ^ t8;    
            t10 = ~X1;        
            t11 = X3 ^ t7;    
            X0 = t10 ^ t11;    
            X2 = t4 ^ (t10 | t11);

            X0 ^= wKey[i]; X1 ^= wKey[i+1]; X2 ^= wKey[i+2]; X3 ^= wKey[i+3];

	    x2 = (((X2) >>> 22) | (X2 << (32 - 22))) ^ X3 ^ (X1 << 7);
	    x0 = ((X0 >>> 5) | (X0 << (32 - 5))) ^ X1 ^ X3;
	    x3 = ((X3 >>> 7) | (X3 << (32 - 7)));
	    x1 = ((X1 >>> 1) | (X1 << (32 - 1)));
	    X3 = x3 ^ x2 ^ x0 << 3;
	    X1 = x1 ^ x0 ^ x2;
	    X2 = ((x2 >>> 3) | (x2 << (32 - 3)));
	    X0 = ((x0 >>> 13) | (x0 << (32 - 13)));

	    a = X0;
	    b = X1;
	    c = X2;
	    d = X3;
            t1 = ~a;        
            t2 = a ^ b;        
            t4 = d ^ (t1 | t2);    
            t5 = c ^ t4;    
            X2 = t2 ^ t5;    
            t8 = t1 ^ (d & t2);    
            X1 = t4 ^ (X2 & t8);    
            X3 = (a & t4) ^ (t5 | X1);    
            X0 = X3 ^ (t5 ^ t8);

	}

        wordToBytes(X3 ^ wKey[3], out, outOff);
        wordToBytes(X2 ^ wKey[2], out, outOff + 4);
        wordToBytes(X1 ^ wKey[1], out, outOff + 8);
        wordToBytes(X0 ^ wKey[0], out, outOff + 12);
    }

    /**
     * The sboxes below are based on the work of Brian Gladman and
     * Sam Simpson, whose original notice appears below.
     * <p>
     * For further details see:
     *      http://fp.gladman.plus.com/cryptography_technology/serpent/
     */

    /* Partially optimised Serpent S Box boolean functions derived  */
    /* using a recursive descent analyser but without a full search */
    /* of all subtrees. This set of S boxes is the result of work    */
    /* by Sam Simpson and Brian Gladman using the spare time on a    */
    /* cluster of high capacity servers to search for S boxes with    */
    /* this customised search engine. There are now an average of    */
    /* 15.375 terms    per S box.                                        */
    /*                                                              */
    /* Copyright:   Dr B. R Gladman (gladman@seven77.demon.co.uk)   */
    /*                and Sam Simpson (s.simpson@mia.co.uk)            */
    /*              17th December 1998                                */
    /*                                                              */
    /* We hereby give permission for information in this file to be */
    /* used freely subject only to acknowledgement of its origin.    */

    /**
     * S0 - { 3, 8,15, 1,10, 6, 5,11,14,13, 4, 2, 7, 0, 9,12 } - 15 terms.
     */
    private void sb0(int a, int b, int c, int d)    
    {
        int    t1 = a ^ d;        
        int    t3 = c ^ t1;    
        int    t4 = b ^ t3;    
        X3 = (a & d) ^ t4;    
        int    t7 = a ^ (b & t1);    
        X2 = t4 ^ (c | t7);    
        int    t12 = X3 & (t3 ^ t7);    
        X1 = (~t3) ^ t12;    
        X0 = t12 ^ (~t7);
    }

    /**
     * InvSO - {13, 3,11, 0,10, 6, 5,12, 1,14, 4, 7,15, 9, 8, 2 } - 15 terms.
     */
    private void ib0(int a, int b, int c, int d)    
    {
        int    t1 = ~a;        
        int    t2 = a ^ b;        
        int    t4 = d ^ (t1 | t2);    
        int    t5 = c ^ t4;    
        X2 = t2 ^ t5;    
        int    t8 = t1 ^ (d & t2);    
        X1 = t4 ^ (X2 & t8);    
        X3 = (a & t4) ^ (t5 | X1);    
        X0 = X3 ^ (t5 ^ t8);
    }

    /**
     * S1 - {15,12, 2, 7, 9, 0, 5,10, 1,11,14, 8, 6,13, 3, 4 } - 14 terms.
     */
    private void sb1(int a, int b, int c, int d)    
    {
        int    t2 = b ^ (~a);    
        int    t5 = c ^ (a | t2);    
        X2 = d ^ t5;        
        int    t7 = b ^ (d | t2);    
        int    t8 = t2 ^ X2;    
        X3 = t8 ^ (t5 & t7);    
        int    t11 = t5 ^ t7;    
        X1 = X3 ^ t11;    
        X0 = t5 ^ (t8 & t11);
    }

    /**
     * InvS1 - { 5, 8, 2,14,15, 6,12, 3,11, 4, 7, 9, 1,13,10, 0 } - 14 steps.
     */
    private void ib1(int a, int b, int c, int d)    
    {
        int    t1 = b ^ d;        
        int    t3 = a ^ (b & t1);    
        int    t4 = t1 ^ t3;    
        X3 = c ^ t4;        
        int    t7 = b ^ (t1 & t3);    
        int    t8 = X3 | t7;    
        X1 = t3 ^ t8;    
        int    t10 = ~X1;        
        int    t11 = X3 ^ t7;    
        X0 = t10 ^ t11;    
        X2 = t4 ^ (t10 | t11);
    }

    /**
     * S2 - { 8, 6, 7, 9, 3,12,10,15,13, 1,14, 4, 0,11, 5, 2 } - 16 terms.
     */
    private void sb2(int a, int b, int c, int d)    
    {
        int    t1 = ~a;        
        int    t2 = b ^ d;
        int    t3 = c & t1;
        X0 = t2 ^ t3;
        int    t5 = c ^ t1;
        int    t6 = c ^ X0;
        int    t7 = b & t6;
        X3 = t5 ^ t7;
        X2 = a ^ ((d | t7) & (X0 | t5));
        X1 = (t2 ^ X3) ^ (X2 ^ (d | t1));
    }

    /**
     * InvS2 - {12, 9,15, 4,11,14, 1, 2, 0, 3, 6,13, 5, 8,10, 7 } - 16 steps.
     */
    private void ib2(int a, int b, int c, int d)    
    {
        int    t1 = b ^ d;        
        int    t2 = ~t1;        
        int    t3 = a ^ c;
        int    t4 = c ^ t1;
        int    t5 = b & t4;
        X0 = t3 ^ t5;
        int    t7 = a | t2;
        int    t8 = d ^ t7;
        int    t9 = t3 | t8;
        X3 = t1 ^ t9;
        int    t11 = ~t4;        
        int    t12 = X0 | X3;
        X1 = t11 ^ t12;
        X2 = (d & t11) ^ (t3 ^ t12);
    }

    /**
     * S3 - { 0,15,11, 8,12, 9, 6, 3,13, 1, 2, 4,10, 7, 5,14 } - 16 terms.
     */
    private void sb3(int a, int b, int c, int d)    
    {
        int    t1 = a ^ b;        
        int    t2 = a & c;        
        int    t3 = a | d;        
        int    t4 = c ^ d;        
        int    t5 = t1 & t3;    
        int    t6 = t2 | t5;    
        X2 = t4 ^ t6;    
        int    t8 = b ^ t3;    
        int    t9 = t6 ^ t8;    
        int    t10 = t4 & t9;    
        X0 = t1 ^ t10;    
        int    t12 = X2 & X0;    
        X1 = t9 ^ t12;    
        X3 = (b | d) ^ (t4 ^ t12);
    }

    /**
     * InvS3 - { 0, 9,10, 7,11,14, 6,13, 3, 5,12, 2, 4, 8,15, 1 } - 15 terms
     */
    private void ib3(int a, int b, int c, int d)    
    {
        int    t1 = a | b;        
        int    t2 = b ^ c;        
        int    t3 = b & t2;    
        int    t4 = a ^ t3;    
        int    t5 = c ^ t4;    
        int    t6 = d | t4;    
        X0 = t2 ^ t6;    
        int    t8 = t2 | t6;    
        int    t9 = d ^ t8;    
        X2 = t5 ^ t9;    
        int    t11 = t1 ^ t9;    
        int    t12 = X0 & t11;    
        X3 = t4 ^ t12;    
        X1 = X3 ^ (X0 ^ t11);
    }

    /**
     * S4 - { 1,15, 8, 3,12, 0,11, 6, 2, 5, 4,10, 9,14, 7,13 } - 15 terms.
     */
    private void sb4(int a, int b, int c, int d)    
    {
        int    t1 = a ^ d;        
        int    t2 = d & t1;    
        int    t3 = c ^ t2;    
        int    t4 = b | t3;    
        X3 = t1 ^ t4;    
        int    t6 = ~b;        
        int    t7 = t1 | t6;    
        X0 = t3 ^ t7;    
        int    t9 = a & X0;        
        int    t10 = t1 ^ t6;    
        int    t11 = t4 & t10;    
        X2 = t9 ^ t11;    
        X1 = (a ^ t3) ^ (t10 & X2);
    }

    /**
     * InvS4 - { 5, 0, 8, 3,10, 9, 7,14, 2,12,11, 6, 4,15,13, 1 } - 15 terms.
     */
    private void ib4(int a, int b, int c, int d)    
    {
        int    t1 = c | d;        
        int    t2 = a & t1;    
        int    t3 = b ^ t2;    
        int    t4 = a & t3;    
        int    t5 = c ^ t4;    
        X1 = d ^ t5;        
        int    t7 = ~a;        
        int    t8 = t5 & X1;    
        X3 = t3 ^ t8;    
        int    t10 = X1 | t7;    
        int    t11 = d ^ t10;    
        X0 = X3 ^ t11;    
        X2 = (t3 & t11) ^ (X1 ^ t7);
    }

    /**
     * S5 - {15, 5, 2,11, 4,10, 9,12, 0, 3,14, 8,13, 6, 7, 1 } - 16 terms.
     */
    private void sb5(int a, int b, int c, int d)    
    {
        int    t1 = ~a;        
        int    t2 = a ^ b;        
        int    t3 = a ^ d;        
        int    t4 = c ^ t1;    
        int    t5 = t2 | t3;    
        X0 = t4 ^ t5;    
        int    t7 = d & X0;        
        int    t8 = t2 ^ X0;    
        X1 = t7 ^ t8;    
        int    t10 = t1 | X0;    
        int    t11 = t2 | t7;    
        int    t12 = t3 ^ t10;    
        X2 = t11 ^ t12;    
        X3 = (b ^ t7) ^ (X1 & t12);
    }

    /**
     * InvS5 - { 8,15, 2, 9, 4, 1,13,14,11, 6, 5, 3, 7,12,10, 0 } - 16 terms.
     */
    private void ib5(int a, int b, int c, int d)    
    {
        int    t1 = ~c;
        int    t2 = b & t1;
        int    t3 = d ^ t2;
        int    t4 = a & t3;
        int    t5 = b ^ t1;
        X3 = t4 ^ t5;
        int    t7 = b | X3;
        int    t8 = a & t7;
        X1 = t3 ^ t8;
        int    t10 = a | d;
        int    t11 = t1 ^ t7;
        X0 = t10 ^ t11;
        X2 = (b & t10) ^ (t4 | (a ^ c));
    }

    /**
     * S6 - { 7, 2,12, 5, 8, 4, 6,11,14, 9, 1,15,13, 3,10, 0 } - 15 terms.
     */
    private void sb6(int a, int b, int c, int d)    
    {
        int    t1 = ~a;        
        int    t2 = a ^ d;        
        int    t3 = b ^ t2;    
        int    t4 = t1 | t2;    
        int    t5 = c ^ t4;    
        X1 = b ^ t5;        
        int    t7 = t2 | X1;    
        int    t8 = d ^ t7;    
        int    t9 = t5 & t8;    
        X2 = t3 ^ t9;    
        int    t11 = t5 ^ t8;    
        X0 = X2 ^ t11;    
        X3 = (~t5) ^ (t3 & t11);
    }

    /**
     * InvS6 - {15,10, 1,13, 5, 3, 6, 0, 4, 9,14, 7, 2,12, 8,11 } - 15 terms.
     */
    private void ib6(int a, int b, int c, int d)    
    {
        int    t1 = ~a;        
        int    t2 = a ^ b;        
        int    t3 = c ^ t2;    
        int    t4 = c | t1;    
        int    t5 = d ^ t4;    
        X1 = t3 ^ t5;    
        int    t7 = t3 & t5;    
        int    t8 = t2 ^ t7;    
        int    t9 = b | t8;    
        X3 = t5 ^ t9;    
        int    t11 = b | X3;    
        X0 = t8 ^ t11;    
        X2 = (d & t1) ^ (t3 ^ t11);
    }

    /**
     * S7 - { 1,13,15, 0,14, 8, 2,11, 7, 4,12,10, 9, 3, 5, 6 } - 16 terms.
     */
    private void sb7(int a, int b, int c, int d)    
    {
        int    t1 = b ^ c;        
        int    t2 = c & t1;    
        int    t3 = d ^ t2;    
        int    t4 = a ^ t3;    
        int    t5 = d | t1;    
        int    t6 = t4 & t5;    
        X1 = b ^ t6;        
        int    t8 = t3 | X1;    
        int    t9 = a & t4;    
        X3 = t1 ^ t9;    
        int    t11 = t4 ^ t8;    
        int    t12 = X3 & t11;    
        X2 = t3 ^ t12;    
        X0 = (~t11) ^ (X3 & X2);
    }

    /**
     * InvS7 - { 3, 0, 6,13, 9,14,15, 8, 5,12,11, 7,10, 1, 4, 2 } - 17 terms.
     */
    private void ib7(int a, int b, int c, int d)    
    {
        int t3 = c | (a & b);
        int    t4 = d & (a | b);
        X3 = t3 ^ t4;
        int    t6 = ~d;
        int    t7 = b ^ t4;
        int    t9 = t7 | (X3 ^ t6);
        X1 = a ^ t9;
        X0 = (c ^ t7) ^ (d | X1);
        X2 = (t3 ^ X1) ^ (X0 ^ (a & X3));
    }

    /**
     * Apply the the linear transformation to the register set.
     */
    private void LT()
    {
        //int x0  = rotateLeft(X0, 13);
        int x0 = ((X0 << 13) | (X0 >>> (32 - 13)));
        //int x2  = rotateLeft(X2, 3);
        int x2  = ((X2 << 3) | (X2 >>> (32 - 3)));
        int x1  = X1 ^ x0 ^ x2 ;
        int x3  = X3 ^ x2 ^ x0 << 3;

//        X1  = rotateLeft(x1, 1);
        X1 = ((x1 << 1) | (x1 >>> (32 - 1)));
//        X3  = rotateLeft(x3, 7);
        X3 = ((x3 << 7) | (x3 >>> (32 - 7)));
//        X0  = rotateLeft(x0 ^ X1 ^ X3, 5);
        X0 = (((x0 ^ X1 ^ X3) << 5) | ((x0 ^ X1 ^ X3) >>> (32 - 5)));
//        X2  = rotateLeft(x2 ^ X3 ^ (X1 << 7), 22);
        X2 = (((x2 ^ X3 ^ (X1 << 7)) << 22) | ((x2 ^ X3 ^ (X1 << 7)) >>> (32 - 22)));
    }

    /**
     * Apply the inverse of the linear transformation to the register set.
     */
    private void inverseLT()
    {
//        int x2 = rotateRight(X2, 22) ^ X3 ^ (X1 << 7);
        int x2 = (((X2) >>> 22) | (X2 << (32 - 22))) ^ X3 ^ (X1 << 7);
//        int x0 = rotateRight(X0, 5) ^ X1 ^ X3;
        int x0 = ((X0 >>> 5) | (X0 << (32 - 5))) ^ X1 ^ X3;
//        int x3 = rotateRight(X3, 7);
        int x3 = ((X3 >>> 7) | (X3 << (32 - 7)));
//        int x1 = rotateRight(X1, 1);
        int x1 = ((X1 >>> 1) | (X1 << (32 - 1)));
        X3 = x3 ^ x2 ^ x0 << 3;
        X1 = x1 ^ x0 ^ x2;
//        X2 = rotateRight(x2, 3);
        X2 = ((x2 >>> 3) | (x2 << (32 - 3)));
//        X0 = rotateRight(x0, 13);
        X0 = ((x0 >>> 13) | (x0 << (32 - 13)));
    }
}
