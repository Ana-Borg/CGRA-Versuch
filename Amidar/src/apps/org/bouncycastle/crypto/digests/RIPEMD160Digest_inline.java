package org.bouncycastle.crypto.digests;

/**
 * implementation of RIPEMD see,
 * http://www.esat.kuleuven.ac.be/~bosselae/ripemd160.html
 */
public class RIPEMD160Digest_inline
{
    private static final int DIGEST_LENGTH = 20;

    private int H0, H1, H2, H3, H4; // IV's

    private int[] X = new int[16];
    private int xOff;

    public int[] getHash() {
	int[] hash = new int[5];
	hash[0] = H0;
	hash[1] = H1;
	hash[2] = H2;
	hash[3] = H3;
	hash[4] = H4;
	return hash;
    }

    private static final int BYTE_LENGTH = 64;
    private byte[]  xBuf;
    private int     xBufOff;

    private long    byteCount;

    public void update(
        byte in)
    {
        xBuf[xBufOff++] = in;

        if (xBufOff == xBuf.length)
        {
            processWord(xBuf, 0);
            xBufOff = 0;
        }

        byteCount++;
    }

    public void update(
        byte[]  in,
        int     inOff,
        int     len)
    {
        //
        // fill the current word
        //
        while ((xBufOff != 0) && (len > 0))
        {
            update(in[inOff]);

            inOff++;
            len--;
        }

        //
        // process whole words.
        //
        while (len > xBuf.length)
        {
            processWord(in, inOff);

            inOff += xBuf.length;
            len -= xBuf.length;
            byteCount += xBuf.length;
        }

        //
        // load in the remainder.
        //
        while (len > 0)
        {
            update(in[inOff]);

            inOff++;
            len--;
        }
    }

    public void finish()
    {
        long    bitLength = (byteCount << 3);

        //
        // add the pad bytes.
        //
        update((byte)128);

        while (xBufOff != 0)
        {
            update((byte)0);
        }

        processLength(bitLength);

        processBlock();
    }

    public int getByteLength()
    {
        return BYTE_LENGTH;
    }


    /**
     * Standard constructor
     */
    public RIPEMD160Digest_inline()
    {
        xBuf = new byte[4];
        xBufOff = 0;
        reset();
    }

    public String getAlgorithmName()
    {
        return "RIPEMD160";
    }

    public int getDigestSize()
    {
        return DIGEST_LENGTH;
    }

    public void processWord(
        byte[] in,
        int inOff)
    {
        X[xOff++] = (in[inOff] & 0xff) | ((in[inOff + 1] & 0xff) << 8)
            | ((in[inOff + 2] & 0xff) << 16) | ((in[inOff + 3] & 0xff) << 24); 

        if (xOff == 16)
        {
            processBlock();
        }
    }

    public void processLength(
        long bitLength)
    {
        if (xOff > 14)
        {
        processBlock();
        }

        X[14] = (int)(bitLength & 0xffffffff);
        X[15] = (int)(bitLength >>> 32);
    }

    private void unpackWord(
        int word,
        byte[] out,
        int outOff)
    {
        out[outOff]     = (byte)word;
        out[outOff + 1] = (byte)(word >>> 8);
        out[outOff + 2] = (byte)(word >>> 16);
        out[outOff + 3] = (byte)(word >>> 24);
    }

    public int doFinal(
        byte[] out,
        int outOff)
    {
        finish();

        unpackWord(0, out, outOff);
        unpackWord(H1, out, outOff + 4);
        unpackWord(H2, out, outOff + 8);
        unpackWord(H3, out, outOff + 12);
        unpackWord(H4, out, outOff + 16);

        reset();

        return DIGEST_LENGTH;
    }

    /**
    * reset the chaining variables to the IV values.
    */
    public void reset()
    {

        byteCount = 0;

        xBufOff = 0;
        for (int i = 0; i < xBuf.length; i++)
        {
            xBuf[i] = 0;
        }

        H0 = 0x67452301;
        H1 = 0xefcdab89;
        H2 = 0x98badcfe;
        H3 = 0x10325476;
        H4 = 0xc3d2e1f0;

        xOff = 0;

        for (int i = 0; i != X.length; i++)
        {
            X[i] = 0;
        }
    }

    /*
     * rotate int x left n bits.
     */
    private int RL(
        int x,
        int n)
    {
        return (x << n) | (x >>> (32 - n));
    }

    /*
     * f1,f2,f3,f4,f5 are the basic RIPEMD160 functions.
     */

    /*
     * rounds 0-15
     */
    private int f1(
        int x,
        int y,
        int z)
    {
        return x ^ y ^ z;
    }

    /*
     * rounds 16-31
     */
    private int f2(
        int x,
        int y,
        int z)
    {
        return (x & y) | (~x & z);
    }

    /*
     * rounds 32-47
     */
    private int f3(
        int x,
        int y,
        int z)
    {
        return (x | ~y) ^ z;
    }

    /*
     * rounds 48-63
     */
    private int f4(
        int x,
        int y,
        int z)
    {
        return (x & z) | (y & ~z);
    }

    /*
     * rounds 64-79
     */
    private int f5(
        int x,
        int y,
        int z)
    {
        return x ^ (y | ~z);
    }

    public void processBlock()
    {
        int a, aa;
        int b, bb;
        int c, cc;
        int d, dd;
        int e, ee;

        a = aa = H0;
        b = bb = H1;
        c = cc = H2;
        d = dd = H3;
        e = ee = H4;

        //
        // Rounds 1 - 16
        //
        // left
        //
        for (int i = 0; i < 1; i++) {
        //
        a = ((a + (b ^ c ^ d) + X[ 0] <<  11) | (a + (b ^ c ^ d) + X[ 0] >>> 21)) + e;
	c = ((c <<  10) | (c >>> 22));
        e = ((e + (a ^ b ^ c) + X[ 1] <<  14) | (e + (a ^ b ^ c) + X[ 1] >>> 18)) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + (e ^ a ^ b) + X[ 2] <<  15) | (d + (e ^ a ^ b) + X[ 2] >>> 17)) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + (d ^ e ^ a) + X[ 3] <<  12) | (c + (d ^ e ^ a) + X[ 3] >>> 20)) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + (c ^ d ^ e) + X[ 4] <<   5) | (b + (c ^ d ^ e) + X[ 4] >>> 27)) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + (b ^ c ^ d) + X[ 5] <<   8) | (a + (b ^ c ^ d) + X[ 5] >>> 24)) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + (a ^ b ^ c) + X[ 6] <<   7) | (e + (a ^ b ^ c) + X[ 6] >>> 25)) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + (e ^ a ^ b) + X[ 7] <<   9) | (d + (e ^ a ^ b) + X[ 7] >>> 23)) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + (d ^ e ^ a) + X[ 8] <<  11) | (c + (d ^ e ^ a) + X[ 8] >>> 21)) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + (c ^ d ^ e) + X[ 9] <<  13) | (b + (c ^ d ^ e) + X[ 9] >>> 19)) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + (b ^ c ^ d) + X[10] <<  14) | (a + (b ^ c ^ d) + X[10] >>> 18)) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + (a ^ b ^ c) + X[11] <<  15) | (e + (a ^ b ^ c) + X[11] >>> 17)) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + (e ^ a ^ b) + X[12] <<   6) | (d + (e ^ a ^ b) + X[12] >>> 26)) + c;
	a = ((a <<  10) | (a >>> 22));
        c = ((c + (d ^ e ^ a) + X[13] <<   7) | (c + (d ^ e ^ a) + X[13] >>> 25)) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + (c ^ d ^ e) + X[14] <<   9) | (b + (c ^ d ^ e) + X[14] >>> 23)) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + (b ^ c ^ d) + X[15] <<   8) | (a + (b ^ c ^ d) + X[15] >>> 24)) + e; 
	c = ((c <<  10) | (c >>> 22));

        // right
        aa = ((aa + (bb ^ (cc | ~dd) + X[ 5] + 0x50a28be6 <<   8)) | (aa + (bb ^ (cc | ~dd) + X[ 5] + 0x50a28be6 >>> 24))) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + (aa ^ (bb | ~cc) + X[14] + 0x50a28be6 <<   9)) | (ee + (aa ^ (bb | ~cc) + X[14] + 0x50a28be6 >>> 23))) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + (ee ^ (aa | ~bb) + X[ 7] + 0x50a28be6 <<   9)) | (dd + (ee ^ (aa | ~bb) + X[ 7] + 0x50a28be6 >>> 23))) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + (dd ^ (ee | ~aa) + X[ 0] + 0x50a28be6 <<  11)) | (cc + (dd ^ (ee | ~aa) + X[ 0] + 0x50a28be6 >>> 21))) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + (cc ^ (dd | ~ee) + X[ 9] + 0x50a28be6 <<  13)) | (bb + (cc ^ (dd | ~ee) + X[ 9] + 0x50a28be6 >>> 19))) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + (bb ^ (cc | ~dd) + X[ 2] + 0x50a28be6 <<  15)) | (aa + (bb ^ (cc | ~dd) + X[ 2] + 0x50a28be6 >>> 17))) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + (aa ^ (bb | ~cc) + X[11] + 0x50a28be6 <<  15)) | (ee + (aa ^ (bb | ~cc) + X[11] + 0x50a28be6 >>> 17))) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + (ee ^ (aa | ~bb) + X[ 4] + 0x50a28be6 <<   5)) | (dd + (ee ^ (aa | ~bb) + X[ 4] + 0x50a28be6 >>> 27))) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + (dd ^ (ee | ~aa) + X[13] + 0x50a28be6 <<   7)) | (cc + (dd ^ (ee | ~aa) + X[13] + 0x50a28be6 >>> 25))) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + (cc ^ (dd | ~ee) + X[ 6] + 0x50a28be6 <<   7)) | (bb + (cc ^ (dd | ~ee) + X[ 6] + 0x50a28be6 >>> 25))) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + (bb ^ (cc | ~dd) + X[15] + 0x50a28be6 <<   8)) | (aa + (bb ^ (cc | ~dd) + X[15] + 0x50a28be6 >>> 24))) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + (aa ^ (bb | ~cc) + X[ 8] + 0x50a28be6 <<  11)) | (ee + (aa ^ (bb | ~cc) + X[ 8] + 0x50a28be6 >>> 21))) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + (ee ^ (aa | ~bb) + X[ 1] + 0x50a28be6 <<  14)) | (dd + (ee ^ (aa | ~bb) + X[ 1] + 0x50a28be6 >>> 18))) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + (dd ^ (ee | ~aa) + X[10] + 0x50a28be6 <<  14)) | (cc + (dd ^ (ee | ~aa) + X[10] + 0x50a28be6 >>> 18))) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + (cc ^ (dd | ~ee) + X[ 3] + 0x50a28be6 <<  12)) | (bb + (cc ^ (dd | ~ee) + X[ 3] + 0x50a28be6 >>> 20))) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + (bb ^ (cc | ~dd) + X[12] + 0x50a28be6 <<   6)) | (aa + (bb ^ (cc | ~dd) + X[12] + 0x50a28be6 >>> 26))) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));

        //
        // Rounds 16-31
        //
        // left
        e = ((e + ((a & b) | (~a & c) + X[ 7] + 0x5a827999 <<   7)) | (e + ((a & b) | (~a & c) + X[ 7] + 0x5a827999 >>> 25))) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + ((e & a) | (~e & b) + X[ 4] + 0x5a827999 <<   6)) | (d + ((e & a) | (~e & b) + X[ 4] + 0x5a827999 >>> 26))) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + ((d & e) | (~d & a) + X[13] + 0x5a827999 <<   8)) | (c + ((d & e) | (~d & a) + X[13] + 0x5a827999 >>> 24))) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + ((c & d) | (~c & e) + X[ 1] + 0x5a827999 <<  13)) | (b + ((c & d) | (~c & e) + X[ 1] + 0x5a827999 >>> 19))) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + ((b & c) | (~b & d) + X[10] + 0x5a827999 <<  11)) | (a + ((b & c) | (~b & d) + X[10] + 0x5a827999 >>> 21))) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + ((a & b) | (~a & c) + X[ 6] + 0x5a827999 <<   9)) | (e + ((a & b) | (~a & c) + X[ 6] + 0x5a827999 >>> 23))) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + ((e & a) | (~e & b) + X[15] + 0x5a827999 <<   7)) | (d + ((e & a) | (~e & b) + X[15] + 0x5a827999 >>> 25))) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + ((d & e) | (~d & a) + X[ 3] + 0x5a827999 <<  15)) | (c + ((d & e) | (~d & a) + X[ 3] + 0x5a827999 >>> 17))) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + ((c & d) | (~c & e) + X[12] + 0x5a827999 <<   7)) | (b + ((c & d) | (~c & e) + X[12] + 0x5a827999 >>> 25))) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + ((b & c) | (~b & d) + X[ 0] + 0x5a827999 <<  12)) | (a + ((b & c) | (~b & d) + X[ 0] + 0x5a827999 >>> 20))) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + ((a & b) | (~a & c) + X[ 9] + 0x5a827999 <<  15)) | (e + ((a & b) | (~a & c) + X[ 9] + 0x5a827999 >>> 17))) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + ((e & a) | (~e & b) + X[ 5] + 0x5a827999 <<   9)) | (d + ((e & a) | (~e & b) + X[ 5] + 0x5a827999 >>> 23))) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + ((d & e) | (~d & a) + X[ 2] + 0x5a827999 <<  11)) | (c + ((d & e) | (~d & a) + X[ 2] + 0x5a827999 >>> 21))) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + ((c & d) | (~c & e) + X[14] + 0x5a827999 <<   7)) | (b + ((c & d) | (~c & e) + X[14] + 0x5a827999 >>> 25))) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + ((b & c) | (~b & d) + X[11] + 0x5a827999 <<  13)) | (a + ((b & c) | (~b & d) + X[11] + 0x5a827999 >>> 19))) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + ((a & b) | (~a & c) + X[ 8] + 0x5a827999 <<  12)) | (e + ((a & b) | (~a & c) + X[ 8] + 0x5a827999 >>> 20))) + d; 
	b = ((b <<  10) | (b >>> 22));

        // right
        ee = ((ee + ((aa & cc) | (bb & ~cc)) + X[ 6] + 0x5c4dd124 <<   9) | (ee + ((aa & cc) | (bb & ~cc)) + X[ 6] + 0x5c4dd124 >>> 23)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + ((ee & bb) | (aa & ~bb)) + X[11] + 0x5c4dd124 <<  13) | (dd + ((ee & bb) | (aa & ~bb)) + X[11] + 0x5c4dd124 >>> 19)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + ((dd & aa) | (ee & ~aa)) + X[ 3] + 0x5c4dd124 <<  15) | (cc + ((dd & aa) | (ee & ~aa)) + X[ 3] + 0x5c4dd124 >>> 17)) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + ((cc & ee) | (dd & ~ee)) + X[ 7] + 0x5c4dd124 <<   7) | (bb + ((cc & ee) | (dd & ~ee)) + X[ 7] + 0x5c4dd124 >>> 25)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + ((bb & dd) | (cc & ~dd)) + X[ 0] + 0x5c4dd124 <<  12) | (aa + ((bb & dd) | (cc & ~dd)) + X[ 0] + 0x5c4dd124 >>> 20)) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + ((aa & cc) | (bb & ~cc)) + X[13] + 0x5c4dd124 <<   8) | (ee + ((aa & cc) | (bb & ~cc)) + X[13] + 0x5c4dd124 >>> 24)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + ((ee & bb) | (aa & ~bb)) + X[ 5] + 0x5c4dd124 <<   9) | (dd + ((ee & bb) | (aa & ~bb)) + X[ 5] + 0x5c4dd124 >>> 23)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + ((dd & aa) | (ee & ~aa)) + X[10] + 0x5c4dd124 <<  11) | (cc + ((dd & aa) | (ee & ~aa)) + X[10] + 0x5c4dd124 >>> 21)) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + ((cc & ee) | (dd & ~ee)) + X[14] + 0x5c4dd124 <<   7) | (bb + ((cc & ee) | (dd & ~ee)) + X[14] + 0x5c4dd124 >>> 25)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + ((bb & dd) | (cc & ~dd)) + X[15] + 0x5c4dd124 <<   7) | (aa + ((bb & dd) | (cc & ~dd)) + X[15] + 0x5c4dd124 >>> 25)) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + ((aa & cc) | (bb & ~cc)) + X[ 8] + 0x5c4dd124 <<  12) | (ee + ((aa & cc) | (bb & ~cc)) + X[ 8] + 0x5c4dd124 >>> 20)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + ((ee & bb) | (aa & ~bb)) + X[12] + 0x5c4dd124 <<   7) | (dd + ((ee & bb) | (aa & ~bb)) + X[12] + 0x5c4dd124 >>> 25)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + ((dd & aa) | (ee & ~aa)) + X[ 4] + 0x5c4dd124 <<   6) | (cc + ((dd & aa) | (ee & ~aa)) + X[ 4] + 0x5c4dd124 >>> 26)) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + ((cc & ee) | (dd & ~ee)) + X[ 9] + 0x5c4dd124 <<  15) | (bb + ((cc & ee) | (dd & ~ee)) + X[ 9] + 0x5c4dd124 >>> 17)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + ((bb & dd) | (cc & ~dd)) + X[ 1] + 0x5c4dd124 <<  13) | (aa + ((bb & dd) | (cc & ~dd)) + X[ 1] + 0x5c4dd124 >>> 19)) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + ((aa & cc) | (bb & ~cc)) + X[ 2] + 0x5c4dd124 <<  11) | (ee + ((aa & cc) | (bb & ~cc)) + X[ 2] + 0x5c4dd124 >>> 21)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));

        //
        // Rounds 32-47
        //
        // left
        d = ((d + ((e | ~a) ^ b) + X[ 3] + 0x6ed9eba1 <<  11) | (d + ((e | ~a) ^ b) + X[ 3] + 0x6ed9eba1 >>> 21)) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + ((d | ~e) ^ a) + X[10] + 0x6ed9eba1 <<  13) | (c + ((d | ~e) ^ a) + X[10] + 0x6ed9eba1 >>> 19)) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + ((c | ~d) ^ e) + X[14] + 0x6ed9eba1 <<   6) | (b + ((c | ~d) ^ e) + X[14] + 0x6ed9eba1 >>> 26)) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + ((b | ~c) ^ d) + X[ 4] + 0x6ed9eba1 <<   7) | (a + ((b | ~c) ^ d) + X[ 4] + 0x6ed9eba1 >>> 25)) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + ((a | ~b) ^ c) + X[ 9] + 0x6ed9eba1 <<  14) | (e + ((a | ~b) ^ c) + X[ 9] + 0x6ed9eba1 >>> 18)) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + ((e | ~a) ^ b) + X[15] + 0x6ed9eba1 <<   9) | (d + ((e | ~a) ^ b) + X[15] + 0x6ed9eba1 >>> 23)) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + ((d | ~e) ^ a) + X[ 8] + 0x6ed9eba1 <<  13) | (c + ((d | ~e) ^ a) + X[ 8] + 0x6ed9eba1 >>> 19)) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + ((c | ~d) ^ e) + X[ 1] + 0x6ed9eba1 <<  15) | (b + ((c | ~d) ^ e) + X[ 1] + 0x6ed9eba1 >>> 17)) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + ((b | ~c) ^ d) + X[ 2] + 0x6ed9eba1 <<  14) | (a + ((b | ~c) ^ d) + X[ 2] + 0x6ed9eba1 >>> 18)) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + ((a | ~b) ^ c) + X[ 7] + 0x6ed9eba1 <<   8) | (e + ((a | ~b) ^ c) + X[ 7] + 0x6ed9eba1 >>> 24)) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + ((e | ~a) ^ b) + X[ 0] + 0x6ed9eba1 <<  13) | (d + ((e | ~a) ^ b) + X[ 0] + 0x6ed9eba1 >>> 19)) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + ((d | ~e) ^ a) + X[ 6] + 0x6ed9eba1 <<   6) | (c + ((d | ~e) ^ a) + X[ 6] + 0x6ed9eba1 >>> 26)) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + ((c | ~d) ^ e) + X[13] + 0x6ed9eba1 <<   5) | (b + ((c | ~d) ^ e) + X[13] + 0x6ed9eba1 >>> 27)) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + ((b | ~c) ^ d) + X[11] + 0x6ed9eba1 <<  12) | (a + ((b | ~c) ^ d) + X[11] + 0x6ed9eba1 >>> 20)) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + ((a | ~b) ^ c) + X[ 5] + 0x6ed9eba1 <<   7) | (e + ((a | ~b) ^ c) + X[ 5] + 0x6ed9eba1 >>> 25)) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + ((e | ~a) ^ b) + X[12] + 0x6ed9eba1 <<   5) | (d + ((e | ~a) ^ b) + X[12] + 0x6ed9eba1 >>> 27)) + c; 
	a = ((a <<  10) | (a >>> 22));

        // right
        dd = ((dd + ((ee | ~aa) ^ bb) + X[15] + 0x6d703ef3 <<   9) | (dd + ((ee | ~aa) ^ bb) + X[15] + 0x6d703ef3 >>> 23)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + ((dd | ~ee) ^ aa) + X[ 5] + 0x6d703ef3 <<   7) | (cc + ((dd | ~ee) ^ aa) + X[ 5] + 0x6d703ef3 >>> 25)) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + ((cc | ~dd) ^ ee) + X[ 1] + 0x6d703ef3 <<  15) | (bb + ((cc | ~dd) ^ ee) + X[ 1] + 0x6d703ef3 >>> 17)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + ((bb | ~cc) ^ dd) + X[ 3] + 0x6d703ef3 <<  11) | (aa + ((bb | ~cc) ^ dd) + X[ 3] + 0x6d703ef3 >>> 21)) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + ((aa | ~bb) ^ cc) + X[ 7] + 0x6d703ef3 <<   8) | (ee + ((aa | ~bb) ^ cc) + X[ 7] + 0x6d703ef3 >>> 24)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + ((ee | ~aa) ^ bb) + X[14] + 0x6d703ef3 <<   6) | (dd + ((ee | ~aa) ^ bb) + X[14] + 0x6d703ef3 >>> 26)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + ((dd | ~ee) ^ aa) + X[ 6] + 0x6d703ef3 <<   6) | (cc + ((dd | ~ee) ^ aa) + X[ 6] + 0x6d703ef3 >>> 26)) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + ((cc | ~dd) ^ ee) + X[ 9] + 0x6d703ef3 <<  14) | (bb + ((cc | ~dd) ^ ee) + X[ 9] + 0x6d703ef3 >>> 18)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + ((bb | ~cc) ^ dd) + X[11] + 0x6d703ef3 <<  12) | (aa + ((bb | ~cc) ^ dd) + X[11] + 0x6d703ef3 >>> 20)) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + ((aa | ~bb) ^ cc) + X[ 8] + 0x6d703ef3 <<  13) | (ee + ((aa | ~bb) ^ cc) + X[ 8] + 0x6d703ef3 >>> 19)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + ((ee | ~aa) ^ bb) + X[12] + 0x6d703ef3 <<   5) | (dd + ((ee | ~aa) ^ bb) + X[12] + 0x6d703ef3 >>> 27)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + ((dd | ~ee) ^ aa) + X[ 2] + 0x6d703ef3 <<  14) | (cc + ((dd | ~ee) ^ aa) + X[ 2] + 0x6d703ef3 >>> 18)) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + ((cc | ~dd) ^ ee) + X[10] + 0x6d703ef3 <<  13) | (bb + ((cc | ~dd) ^ ee) + X[10] + 0x6d703ef3 >>> 19)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + ((bb | ~cc) ^ dd) + X[ 0] + 0x6d703ef3 <<  13) | (aa + ((bb | ~cc) ^ dd) + X[ 0] + 0x6d703ef3 >>> 19)) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + ((aa | ~bb) ^ cc) + X[ 4] + 0x6d703ef3 <<   7) | (ee + ((aa | ~bb) ^ cc) + X[ 4] + 0x6d703ef3 >>> 25)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + ((ee | ~aa) ^ bb) + X[13] + 0x6d703ef3 <<   5) | (dd + ((ee | ~aa) ^ bb) + X[13] + 0x6d703ef3 >>> 27)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));

        //
        // Rounds 48-63
        //
        // left
        c = ((c + ((d & a) | (e & ~a)) + X[ 1] + 0x8f1bbcdc <<  11) | (c + ((d & a) | (e & ~a)) + X[ 1] + 0x8f1bbcdc >>> 21)) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + ((c & e) | (d & ~e)) + X[ 9] + 0x8f1bbcdc <<  12) | (b + ((c & e) | (d & ~e)) + X[ 9] + 0x8f1bbcdc >>> 20)) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + ((b & d) | (c & ~d)) + X[11] + 0x8f1bbcdc <<  14) | (a + ((b & d) | (c & ~d)) + X[11] + 0x8f1bbcdc >>> 18)) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + ((a & c) | (b & ~c)) + X[10] + 0x8f1bbcdc <<  15) | (e + ((a & c) | (b & ~c)) + X[10] + 0x8f1bbcdc >>> 17)) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + ((e & b) | (a & ~b)) + X[ 0] + 0x8f1bbcdc <<  14) | (d + ((e & b) | (a & ~b)) + X[ 0] + 0x8f1bbcdc >>> 18)) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + ((d & a) | (e & ~a)) + X[ 8] + 0x8f1bbcdc <<  15) | (c + ((d & a) | (e & ~a)) + X[ 8] + 0x8f1bbcdc >>> 17)) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + ((c & e) | (d & ~e)) + X[12] + 0x8f1bbcdc <<   9) | (b + ((c & e) | (d & ~e)) + X[12] + 0x8f1bbcdc >>> 23)) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + ((b & d) | (c & ~d)) + X[ 4] + 0x8f1bbcdc <<   8) | (a + ((b & d) | (c & ~d)) + X[ 4] + 0x8f1bbcdc >>> 24)) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + ((a & c) | (b & ~c)) + X[13] + 0x8f1bbcdc <<   9) | (e + ((a & c) | (b & ~c)) + X[13] + 0x8f1bbcdc >>> 23)) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + ((e & b) | (a & ~b)) + X[ 3] + 0x8f1bbcdc <<  14) | (d + ((e & b) | (a & ~b)) + X[ 3] + 0x8f1bbcdc >>> 18)) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + ((d & a) | (e & ~a)) + X[ 7] + 0x8f1bbcdc <<   5) | (c + ((d & a) | (e & ~a)) + X[ 7] + 0x8f1bbcdc >>> 27)) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + ((c & e) | (d & ~e)) + X[15] + 0x8f1bbcdc <<   6) | (b + ((c & e) | (d & ~e)) + X[15] + 0x8f1bbcdc >>> 26)) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + ((b & d) | (c & ~d)) + X[14] + 0x8f1bbcdc <<   8) | (a + ((b & d) | (c & ~d)) + X[14] + 0x8f1bbcdc >>> 24)) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + ((a & c) | (b & ~c)) + X[ 5] + 0x8f1bbcdc <<   6) | (e + ((a & c) | (b & ~c)) + X[ 5] + 0x8f1bbcdc >>> 26)) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + ((e & b) | (a & ~b)) + X[ 6] + 0x8f1bbcdc <<   5) | (d + ((e & b) | (a & ~b)) + X[ 6] + 0x8f1bbcdc >>> 27)) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + ((d & a) | (e & ~a)) + X[ 2] + 0x8f1bbcdc <<  12) | (c + ((d & a) | (e & ~a)) + X[ 2] + 0x8f1bbcdc >>> 20)) + b; 
	e = ((e <<  10) | (e >>> 22));

        // right
        cc = ((cc + ((dd & ee) | (~dd & aa) + X[ 8] + 0x7a6d76e9 <<  15)) | (cc + ((dd & ee) | (~dd & aa) + X[ 8] + 0x7a6d76e9 >>> 17))) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + ((cc & dd) | (~cc & ee) + X[ 6] + 0x7a6d76e9 <<   5)) | (bb + ((cc & dd) | (~cc & ee) + X[ 6] + 0x7a6d76e9 >>> 27))) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + ((bb & cc) | (~bb & dd) + X[ 4] + 0x7a6d76e9 <<   8)) | (aa + ((bb & cc) | (~bb & dd) + X[ 4] + 0x7a6d76e9 >>> 24))) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + ((aa & bb) | (~aa & cc) + X[ 1] + 0x7a6d76e9 <<  11)) | (ee + ((aa & bb) | (~aa & cc) + X[ 1] + 0x7a6d76e9 >>> 21))) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + ((ee & aa) | (~ee & bb) + X[ 3] + 0x7a6d76e9 <<  14)) | (dd + ((ee & aa) | (~ee & bb) + X[ 3] + 0x7a6d76e9 >>> 18))) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + ((dd & ee) | (~dd & aa) + X[11] + 0x7a6d76e9 <<  14)) | (cc + ((dd & ee) | (~dd & aa) + X[11] + 0x7a6d76e9 >>> 18))) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + ((cc & dd) | (~cc & ee) + X[15] + 0x7a6d76e9 <<   6)) | (bb + ((cc & dd) | (~cc & ee) + X[15] + 0x7a6d76e9 >>> 26))) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + ((bb & cc) | (~bb & dd) + X[ 0] + 0x7a6d76e9 <<  14)) | (aa + ((bb & cc) | (~bb & dd) + X[ 0] + 0x7a6d76e9 >>> 18))) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + ((aa & bb) | (~aa & cc) + X[ 5] + 0x7a6d76e9 <<   6)) | (ee + ((aa & bb) | (~aa & cc) + X[ 5] + 0x7a6d76e9 >>> 26))) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + ((ee & aa) | (~ee & bb) + X[12] + 0x7a6d76e9 <<   9)) | (dd + ((ee & aa) | (~ee & bb) + X[12] + 0x7a6d76e9 >>> 23))) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + ((dd & ee) | (~dd & aa) + X[ 2] + 0x7a6d76e9 <<  12)) | (cc + ((dd & ee) | (~dd & aa) + X[ 2] + 0x7a6d76e9 >>> 20))) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + ((cc & dd) | (~cc & ee) + X[13] + 0x7a6d76e9 <<   9)) | (bb + ((cc & dd) | (~cc & ee) + X[13] + 0x7a6d76e9 >>> 23))) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + ((bb & cc) | (~bb & dd) + X[ 9] + 0x7a6d76e9 <<  12)) | (aa + ((bb & cc) | (~bb & dd) + X[ 9] + 0x7a6d76e9 >>> 20))) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + ((aa & bb) | (~aa & cc) + X[ 7] + 0x7a6d76e9 <<   5)) | (ee + ((aa & bb) | (~aa & cc) + X[ 7] + 0x7a6d76e9 >>> 27))) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + ((ee & aa) | (~ee & bb) + X[10] + 0x7a6d76e9 <<  15)) | (dd + ((ee & aa) | (~ee & bb) + X[10] + 0x7a6d76e9 >>> 17))) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + ((dd & ee) | (~dd & aa) + X[14] + 0x7a6d76e9 <<   8)) | (cc + ((dd & ee) | (~dd & aa) + X[14] + 0x7a6d76e9 >>> 24))) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));

        //
        // Rounds 64-79
        //
        // left
        b = ((b + (c ^ (d | ~e) + X[ 4] + 0xa953fd4e <<   9)) | (b + (c ^ (d | ~e) + X[ 4] + 0xa953fd4e >>> 23))) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + (b ^ (c | ~d) + X[ 0] + 0xa953fd4e <<  15)) | (a + (b ^ (c | ~d) + X[ 0] + 0xa953fd4e >>> 17))) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + (a ^ (b | ~c) + X[ 5] + 0xa953fd4e <<   5)) | (e + (a ^ (b | ~c) + X[ 5] + 0xa953fd4e >>> 27))) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + (e ^ (a | ~b) + X[ 9] + 0xa953fd4e <<  11)) | (d + (e ^ (a | ~b) + X[ 9] + 0xa953fd4e >>> 21))) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + (d ^ (e | ~a) + X[ 7] + 0xa953fd4e <<   6)) | (c + (d ^ (e | ~a) + X[ 7] + 0xa953fd4e >>> 26))) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + (c ^ (d | ~e) + X[12] + 0xa953fd4e <<   8)) | (b + (c ^ (d | ~e) + X[12] + 0xa953fd4e >>> 24))) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + (b ^ (c | ~d) + X[ 2] + 0xa953fd4e <<  13)) | (a + (b ^ (c | ~d) + X[ 2] + 0xa953fd4e >>> 19))) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + (a ^ (b | ~c) + X[10] + 0xa953fd4e <<  12)) | (e + (a ^ (b | ~c) + X[10] + 0xa953fd4e >>> 20))) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + (e ^ (a | ~b) + X[14] + 0xa953fd4e <<   5)) | (d + (e ^ (a | ~b) + X[14] + 0xa953fd4e >>> 27))) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + (d ^ (e | ~a) + X[ 1] + 0xa953fd4e <<  12)) | (c + (d ^ (e | ~a) + X[ 1] + 0xa953fd4e >>> 20))) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + (c ^ (d | ~e) + X[ 3] + 0xa953fd4e <<  13)) | (b + (c ^ (d | ~e) + X[ 3] + 0xa953fd4e >>> 19))) + a; 
	d = ((d <<  10) | (d >>> 22));
        a = ((a + (b ^ (c | ~d) + X[ 8] + 0xa953fd4e <<  14)) | (a + (b ^ (c | ~d) + X[ 8] + 0xa953fd4e >>> 18))) + e; 
	c = ((c <<  10) | (c >>> 22));
        e = ((e + (a ^ (b | ~c) + X[11] + 0xa953fd4e <<  11)) | (e + (a ^ (b | ~c) + X[11] + 0xa953fd4e >>> 21))) + d; 
	b = ((b <<  10) | (b >>> 22));
        d = ((d + (e ^ (a | ~b) + X[ 6] + 0xa953fd4e <<   8)) | (d + (e ^ (a | ~b) + X[ 6] + 0xa953fd4e >>> 24))) + c; 
	a = ((a <<  10) | (a >>> 22));
        c = ((c + (d ^ (e | ~a) + X[15] + 0xa953fd4e <<   5)) | (c + (d ^ (e | ~a) + X[15] + 0xa953fd4e >>> 27))) + b; 
	e = ((e <<  10) | (e >>> 22));
        b = ((b + (c ^ (d | ~e) + X[13] + 0xa953fd4e <<   6)) | (b + (c ^ (d | ~e) + X[13] + 0xa953fd4e >>> 26))) + a; 
	d = ((d <<  10) | (d >>> 22));

        // right
        bb = ((bb + (cc ^ dd ^ ee) + X[12] <<   8) | (bb + (cc ^ dd ^ ee) + X[12] >>> 24)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + (bb ^ cc ^ dd) + X[15] <<   5) | (aa + (bb ^ cc ^ dd) + X[15] >>> 27)) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + (aa ^ bb ^ cc) + X[10] <<  12) | (ee + (aa ^ bb ^ cc) + X[10] >>> 20)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + (ee ^ aa ^ bb) + X[ 4] <<   9) | (dd + (ee ^ aa ^ bb) + X[ 4] >>> 23)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + (dd ^ ee ^ aa) + X[ 1] <<  12) | (cc + (dd ^ ee ^ aa) + X[ 1] >>> 20)) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + (cc ^ dd ^ ee) + X[ 5] <<   5) | (bb + (cc ^ dd ^ ee) + X[ 5] >>> 27)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + (bb ^ cc ^ dd) + X[ 8] <<  14) | (aa + (bb ^ cc ^ dd) + X[ 8] >>> 18)) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + (aa ^ bb ^ cc) + X[ 7] <<   6) | (ee + (aa ^ bb ^ cc) + X[ 7] >>> 26)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + (ee ^ aa ^ bb) + X[ 6] <<   8) | (dd + (ee ^ aa ^ bb) + X[ 6] >>> 24)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + (dd ^ ee ^ aa) + X[ 2] <<  13) | (cc + (dd ^ ee ^ aa) + X[ 2] >>> 19)) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + (cc ^ dd ^ ee) + X[13] <<   6) | (bb + (cc ^ dd ^ ee) + X[13] >>> 26)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));
        aa = ((aa + (bb ^ cc ^ dd) + X[14] <<   5) | (aa + (bb ^ cc ^ dd) + X[14] >>> 27)) + ee; 
	cc = ((cc <<  10) | (cc >>> 22));
        ee = ((ee + (aa ^ bb ^ cc) + X[ 0] <<  15) | (ee + (aa ^ bb ^ cc) + X[ 0] >>> 17)) + dd; 
	bb = ((bb <<  10) | (bb >>> 22));
        dd = ((dd + (ee ^ aa ^ bb) + X[ 3] <<  13) | (dd + (ee ^ aa ^ bb) + X[ 3] >>> 19)) + cc; 
	aa = ((aa <<  10) | (aa >>> 22));
        cc = ((cc + (dd ^ ee ^ aa) + X[ 9] <<  11) | (cc + (dd ^ ee ^ aa) + X[ 9] >>> 21)) + bb; 
	ee = ((ee <<  10) | (ee >>> 22));
        bb = ((bb + (cc ^ dd ^ ee) + X[11] <<  11) | (bb + (cc ^ dd ^ ee) + X[11] >>> 21)) + aa; 
	dd = ((dd <<  10) | (dd >>> 22));

	}

        dd += c + H1;
        H1 = H2 + d + ee;
        H2 = H3 + e + aa;
        H3 = H4 + a + bb;
        H4 = H0 + b + cc;
        H0 = dd;

        //
        // reset the offset and clean out the word buffer.
        //
        xOff = 0;
        for (int i = 0; i != X.length; i++)
        {
            X[i] = 0;
        }
    }
}
