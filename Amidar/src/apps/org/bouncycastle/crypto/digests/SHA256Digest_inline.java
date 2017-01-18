package org.bouncycastle.crypto.digests;

public class SHA256Digest_inline
{
    private int    DIGEST_LENGTH = 32;

    private int     H1, H2, H3, H4, H5, H6, H7, H8;

    /**
     * Standard constructor
     */
    public SHA256Digest_inline()
    {

     int[] K = {
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };
    this.K = K;

        reset();
    }

    public int getDigestSize()
    {
        return DIGEST_LENGTH;
    }

    public void reset()
    {

        /* SHA-256 initial hash value
         * The first 32 bits of the fractional parts of the square roots
         * of the first eight prime numbers
         */

        H1 = 0x6a09e667;
        H2 = 0xbb67ae85;
        H3 = 0x3c6ef372;
        H4 = 0xa54ff53a;
        H5 = 0x510e527f;
        H6 = 0x9b05688c;
        H7 = 0x1f83d9ab;
        H8 = 0x5be0cd19;

    }

    public void processBlock(int[] data)
    {

	int[] X = new int[64];
	System.arraycopy(data, 0, X, 0, data.length);
        //
        // expand 16 word block into 64 word blocks.
        //
        for (int t = 16; t <= 63; t++)
        {
            X[t] = (((X[t - 2] >>> 17) | (X[t - 2] << 15)) ^ ((X[t - 2] >>> 19) | (X[t - 2] << 13)) ^ (X[t - 2] >>> 10)) + X[t - 7] + (((X[t - 15] >>> 7) | (X[t - 15] << 25)) ^ ((X[t - 15] >>> 18) | (X[t - 15] << 14)) ^ (X[t - 15] >>> 3)) + X[t - 16];
        }

        //
        // set up working variables.
        //
        int     a = H1;
        int     b = H2;
        int     c = H3;
        int     d = H4;
        int     e = H5;
        int     f = H6;
        int     g = H7;
        int     h = H8;

        int t = 0;     
        for(int i = 0; i < 8; i ++)
        {
            // t = 8 * i
            h += (((e >>> 2) | (e << 30)) ^ ((e >>> 13) | (e << 19)) ^ ((e >>> 22) | (e << 10))) + ((e & f) ^ ((~e) & g)) + K[t] + X[t];
            d += h;
            h += (((a >>> 2) | (a << 30)) ^ ((a >>> 13) | (a << 19)) ^ ((a >>> 22) | (a << 10))) + ((a & b) ^ (a & c) ^ (b & c));
            ++t;

            // t = 8 * i + 1
            g += (((d >>> 2) | (d << 30)) ^ ((d >>> 13) | (d << 19)) ^ ((d >>> 22) | (d << 10))) + ((d & e) ^ ((~d) & f)) + K[t] + X[t];
            c += g;
            g += (((h >>> 2) | (h << 30)) ^ ((h >>> 13) | (h << 19)) ^ ((h >>> 22) | (h << 10))) + ((h & a) ^ (h & b) ^ (a & b));
            ++t;

            // t = 8 * i + 2
            f += (((c >>> 2) | (c << 30)) ^ ((c >>> 13) | (c << 19)) ^ ((c >>> 22) | (c << 10))) + ((c & d) ^ ((~c) & e)) + K[t] + X[t];
            b += f;
            f += (((g >>> 2) | (g << 30)) ^ ((g >>> 13) | (g << 19)) ^ ((g >>> 22) | (g << 10))) + ((g & h) ^ (g & a) ^ (h & a));
            ++t;

            // t = 8 * i + 3
            e += (((b >>> 2) | (b << 30)) ^ ((b >>> 13) | (b << 19)) ^ ((b >>> 22) | (b << 10))) + ((b & c) ^ ((~b) & d)) + K[t] + X[t];
            a += e;
            e += (((f >>> 2) | (f << 30)) ^ ((f >>> 13) | (f << 19)) ^ ((f >>> 22) | (f << 10))) + ((f & g) ^ (f & h) ^ (g & h));
            ++t;

            // t = 8 * i + 4
            d += (((a >>> 2) | (a << 30)) ^ ((a >>> 13) | (a << 19)) ^ ((a >>> 22) | (a << 10))) + ((a & b) ^ ((~a) & c)) + K[t] + X[t];
            h += d;
            d += (((e >>> 2) | (e << 30)) ^ ((e >>> 13) | (e << 19)) ^ ((e >>> 22) | (e << 10))) + ((e & f) ^ (e & g) ^ (f & g));
            ++t;

            // t = 8 * i + 5
            c += (((h >>> 2) | (h << 30)) ^ ((h >>> 13) | (h << 19)) ^ ((h >>> 22) | (h << 10))) + ((h & a) ^ ((~h) & b)) + K[t] + X[t];
            g += c;
            c += (((d >>> 2) | (d << 30)) ^ ((d >>> 13) | (d << 19)) ^ ((d >>> 22) | (d << 10))) + ((d & e) ^ (d & f) ^ (e & f));
            ++t;

            // t = 8 * i + 6
            b += (((g >>> 2) | (g << 30)) ^ ((g >>> 13) | (g << 19)) ^ ((g >>> 22) | (g << 10))) + ((g & h) ^ ((~g) & a)) + K[t] + X[t];
            f += b;
            b += (((c >>> 2) | (c << 30)) ^ ((c >>> 13) | (c << 19)) ^ ((c >>> 22) | (c << 10))) + ((c & d) ^ (c & e) ^ (d & e));
            ++t;

            // t = 8 * i + 7
            a += (((f >>> 2) | (f << 30)) ^ ((f >>> 13) | (f << 19)) ^ ((f >>> 22) | (f << 10))) + ((f & g) ^ ((~f) & h)) + K[t] + X[t];
            e += a;
            a += (((b >>> 2) | (b << 30)) ^ ((b >>> 13) | (b << 19)) ^ ((b >>> 22) | (b << 10))) + ((b & c) ^ (b & d) ^ (c & d));
            ++t;
        }

        H1 += a;
        H2 += b;
        H3 += c;
        H4 += d;
        H5 += e;
        H6 += f;
        H7 += g;
        H8 += h;


        
        //
        // reset the offset and clean out the word buffer.
        //
        for (int i = 0; i < 16; i++)
        {
            X[i] = 0;
        }
    }
    
    public int[] getH(){
    	int [] erg = {H1,H2,H3,H3,H4,H5,H6,H7,H8};
    	return erg;
    }

    /* SHA-256 functions */
    private int Ch(
        int    x,
        int    y,
        int    z)
    {
        return ((x & y) ^ ((~x) & z));
    }

    private int Maj(
        int    x,
        int    y,
        int    z)
    {
        return ((x & y) ^ (x & z) ^ (y & z));
    }

    private int Sum0(
        int    x)
    {
        return (((x >>> 2) | (x << 30)) ^ ((x >>> 13) | (x << 19)) ^ ((x >>> 22) | (x << 10)));
    }

    private int Sum1(
        int    x)
    {
        return (((x >>> 6) | (x << 26)) ^ ((x >>> 11) | (x << 21)) ^ ((x >>> 25) | (x << 7)));
    }

    private int Theta0(
        int    x)
    {
        return (((x >>> 7) | (x << 25)) ^ ((x >>> 18) | (x << 14)) ^ (x >>> 3));
    }

    private int Theta1(
        int    x)
    {
        return (((x >>> 17) | (x << 15)) ^ ((x >>> 19) | (x << 13)) ^ (x >>> 10));
    }

    /* SHA-256 Constants
     * (represent the first 32 bits of the fractional parts of the
     * cube roots of the first sixty-four prime numbers)
     */
    int K[];
}

