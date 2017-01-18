package de.amidar;

/**
 * Adaptive differential pulse code mudulation.
 * 
 * This lossless audio codec is parameterized by
 *  - the prediction order M
 *  - the rice-coefficient K
 *  - the block size N (the overall number of sample needs not to devisible by N)
 * 
 * ### Data format ###
 * The sample format of the encoders input / decoders output can be configured by 
 *   - SAMPLE_BYTES      = number of bytes per sample, 
 *   - SAMPLE_BIG_ENDIAN = true, iff multi-byte samples are stored in big endian format
 *   - SAMPLE_SIGNED     = true, iff samples may be negative
 * 
 * @attention Code is tested for SAMPLE_BYTES=2, SAMPLE_BIG_ENDIAN=true, SAMPLE_SIGNED=true only!
 * 
 * ### Encoder ###
 * Each sample @f$x_i@f$ is predicted as linear combination 
 * @f$ p_i = \sum_{k=1}^M a_k x_{i-k} @f$ if M > 0 (adaptive prediction) or 
 * @f$ p_i = x_{i-1} @f$ if M = 0 (static prediction) respectively.
 * 
 * The prediction error @f$ e_i = x_i - p_i @f$ is mapped to a positive value 
 * @f$ u_i =  \left\{ \begin{array}{cl} 
 *            2 e_i,     & \text{for } e_i \geq 0 \\
 *           -2 e_i - 1, & \text{for } e_i < 0 
 *            \end{array}\right. @f$
 * preserving the property of small / large (absolute) values.
 *  
 * This value is finally passed through a RICE symbol encoder to generate 
 * the unary code of @f$ \frac{u_i}{2^K} @f$ and the 
 * K-bit binary code of @f$ u_i \bmod 2^K @f$.
 * 
 * The adaptive prediction coefficients @f$ a_k @f$ are optimized for each sample block (of size N) to achieve smallest 
 * prediction errors by
 *   - calculating the autocorrelation values @f$ c_k = \frac{1}{N-k} \sum_{i=1}^{N-k} x_i x_{i+k}, 0 \leq k \leq M @f$
 *   - solving the linear equation system
 *     @f$ \begin{pmatrix}
 *           c_0     & c_1     & c_2    & \cdots & c_{M-1} \\
 *           c_1     & c_0     & c_1    & \cdots & c_{M-2} \\
 *           \vdots  & \vdots  & \vdots & \ddots & \vdots  \\
 *           r_{M-1} & r_{M-2} & r_1    & \cdots & r_{0}
 *         \end{pmatrix} \cdot
 *         \begin{pmatrix}
 *           a_1     \\
 *           a_2     \\
 *           \vdots  \\
 *           a_{M}  
 *         \end{pmatrix} =
 *         \begin{pmatrix}
 *           c_1     \\
 *           c_2     \\
 *           \vdots  \\
 *           c_{M}  
 *         \end{pmatrix} @f$
 *                                                                                        
 * These coefficients are represented as signed fixed-point values (INTEGER+FRACTION bits) and written to the output
 * before acually encoding the sample block. For the static predictor (M=0), the block size parameter is ignored.
 *   
 * To initialize the predictor at the decoder, the first @f$ max(1,M) @f$ samples (of the first block) are written to 
 * the output in uncompressed binary block code.
 *   
 * ### Decoder ###
 * After reading the uncompressed initialization samples and the prediction coefficients at the beginning of each block, 
 * the decoder 
 *   - predicts @f$ p_i @f$ the same way as the encoder
 *   - restores @f$ u_i @f$ from the unary / binary input bits
 *   - maps     @f$ e_i =  \frac{1}{2} \left\{ \begin{array}{cl} 
 *                                             u_i,     & \text{for even } u_i\\
 *                                            -u_i - 1, & \text{for odd }  u_i \end{array}\right. @f$
 *   - restores @f$ x_i = p_i + e_i @f$ 
 * 
 * As the codec parameters (M,K,N) are not stored in the encoded bitstream, these have to be provided when calling the
 * decoder.
 *   
 * ### Hardware acceleration ###
 * The decoder and the static encoder (M=0) are single pass algorithms without complex computations. 
 * Therefore, hardware acceleration should focus on (higher order) adaptive encoding. The block size is limited by the
 * available memory and the applications end-to-end delay and should by statically choosen as large as possible. The
 * rice coefficient has to be optimized manually. In most application, a static selection for each data channel will be 
 * sufficient.
 * 
 * ### Usage examples ###
 * 
 * <pre>
 *              action inFile                 N  M  K  outFile                 show statistics
 *   java ADPCM encode channel1            2048  0  4  channel1.2048.00.04     verbose          # 1577 byte
 *   java ADPCM encode channel1            1024  9  4  channel1.1024.09.04     verbose          # 1525 byte
 *   java ADPCM decode channel1.2048.00.04 2048  0  4  channel1.2048.00.04.dec verbose          # must equal channel1
 *   java ADPCM decode channel1.1024.09.04 1024  9  4  channel1.1024.09.04.dec verbose          # must equal channel1
 * </pre>
 *   
 * @author Andreas Engel <engel@esa.cs.tu-darmstadt.de>
 * @date   11/2013
 */
public class ADPCM {

/*
 * static configuration
 **********************************************************************************************************************/
	
  static final int     SAMPLE_BYTES      = 2;     ///< number of bytes per sample
  static final boolean SAMPLE_BIG_ENDIAN = true;  ///< multi-byte samples are stored in big endian format?
  static final boolean SAMPLE_SIGNED     = true;  ///< samples may be negative?
  
  static final int     INTEGER           = 4;     ///< number of integer    bits for the prediction coefficients
  static final int     FRACTION          = 12;    ///< number of fractional bits for the prediction coefficients

  static final boolean ENCODE 	= true;
	static final boolean DECODE 	= false;

/*
 * command line support
 **********************************************************************************************************************/
  
  /**
   * Display usage and terminate.
   */
  static int hhelp() {
    System.out.println(11);
    //System.out.println("usage: adpcm {encode|decode} <inFile> <blockSize> <order> <riceCoefficient> <outFile> [verbose]");
    //System.exit(-1);
    return 1;
  }
  
  /**
   * Parse command line parameters and invoke encoder or decoder.
   * @param args command line parameters
   * This method also performs the File I/O and displays compression statistics.
   */
  public static void main(String[] args) {
        run(2);
//        run(2);
  }

/*
 * Run the Test
*/
  public static void run(int a){
    // check command line parameters
		//    if (args.length < 6) help();
		boolean 	action          = DECODE;
		int     	blockSize       = 1024;
		int     	order           = a;
		int     	riceCoefficient = 4;
		//boolean verbose         = args.length > 6;

		// read inputFile
		byte[] inBytes  = {21,29,-7,-86,0,23,0,28,-32,30,19,51,-59,-118,4,121,85,-32,-73,30,-126,109,-32,-8,13,-115,-95,-106,-115,0,-82,90,40,-127,-50,-70,-66,-103,97,-77,-112,37,74,-43,19,13,22,-119,-117,92,-18,67,38,-78,-64,-66,54,-55,36,-101,-77,94,63,-79,-2,28,70,-32,-28,78,-58,-90,44,-71,12,62,64,9,123,61,-104,-26,7,-123,-123,-43,-11,107,-33,-32,-80,40,-68,65,71,-110,10,54,13,7,117,81,-111,111,16,59,42,43,-89,6,104,20,51,-43,-102,97,16,4,74,-9,57,-15,48,-118,81,95,-101,98,-104,9,105,63,20,-45,73,45,83,66,-105,98,97,-45,119,20,101,-36,-73,-59,7,5,-62,114,-107,8,-22,-79,-55,65,26,44,93,21,18,7,-46,78,45,-57,73,-30,-60,-122,76,14,14,-113,42,-67,-39,-79,111,9,7,-28,-85,82,56,83,126,15,-125,5,-115,90,25,-40,-21,57,-70,93,-43,-83,-68,83,75,38,-58,79,73,-80,100,54,-18,34,-87,103,-56,-120,-105,42,5,-79,86,-94,-106,-22,-120,-88,-32,-47,-75,116,38,45,22,80,6,-102,75,-78,49,-80,85,14,-5,63,-118,127,-76,81,-24,-9,89,-125,-122,-8,21,-44,53,19,-42,74,44,-20,12,-43,-37,-120,-65,87,-77,-15,-15,-7,0,30,66,-104,88,92,-118,50,4,-79,-115,10,-86,-98,81,-77,20,-107,89,-62,14,101,-115,-127,0,-60,58,-72,40,110,18,-72,-81,12,-44,68,-32,75,122,-32,5,-43,37,76,114,103,116,45,-72,7,106,-56,26,36,35,120,93,-87,-38,-78,11,92,-73,4,46,19,7,54,3,88,-89,-37,-108,115,98,-56,-96,-54,-102,-82,-88,-59,19,-15,106,-15,-121,-85,68,-98,-78,58,93,-46,92,62,18,-61,20,-122,-85,56,76,-3,-124,-51,-125,-61
};

		byte[] 	outBytes = new byte[inBytes.length*10];
		int    outSize  = 0;

		// invoke encoder/decoder
		if      (action == ENCODE) outSize = encode(inBytes, blockSize, order, riceCoefficient, outBytes);
		else if (action == DECODE) outSize = decode(inBytes, blockSize, order, riceCoefficient, outBytes);
		else hhelp();

		System.out.println();
		for(int i = 0; i < outBytes.length; i++){
			System.out.print(outBytes[i]);
			System.out.print(',');
		}
		System.out.println('\n');

		// write outputFile
		//    Files.write(Paths.get(outFile), java.util.Arrays.copyOf(outBytes, outSize));

		// report statistics
	//	if (verbose) {
			//      System.out.printf("%s %s [%d bytes] to %s [%d bytes] with blockSize=%4d order=%2d riceCoefficient=%2d\n",
			//        action, inFile,  inBytes.length,
			//                outFile, outSize,
			//                blockSize, order, riceCoefficient);
		//}
  }
  
  /*
   * encoder
   **********************************************************************************************************************/

    /**
     * Invoke the encoder.
     * @param inBytes         sample stream to be encoded
     * @param blockSize       maximum number of samples to derive adaptive prediction parameters from
     * @param order           prediction order (or 0 for first order static prediction)
     * @param riceCoefficient parameter K for symbol coding
     * @param outBytes        encoded bit stream
     * @return size of encoded bit stream (in bytes)
     */
    static int encode(byte[] inBytes, int blockSize, int order, int riceCoefficient, byte[] outBytes) {
      int     i;
      int     sample             = 0;                               // current sample
      int     samples            = 0;                               // number of already processed samples
      int     inByte             = 0;                               // number of read input bytes
      int     outByte            = 0;                               // number of written output bytes
      byte    buffer             = 0;                               // bitbuffer for packing bitstream into bytes
      int     bits               = 0;                               // number of valid bits in buffer
      int     prediction         = 0;                               // sample prediction
      int     error              = 0;                               // prediction error
      int     ones;                                                 // run length of unary code (RICE)
      boolean adaptivePass       = order > 0;                       // pass through samples to adopt coefficients
      int[]   predictionHistory  = new int[order == 0 ? 1 : order]; // last samples used for prediction
      int[]   correlationHistory = new int[order+1];                // last samples used for autocorrelation
      long[]  correlationSum     = new long[order+1];               // accumulators for autocorrelation values
      int[]   coefficients       = new int[order];                  // prediction coefficients
      
      // two different histories are required predict first sample of new block from last samples of old block after the
      // new block was passed ones for autocorrelation
      
      while (inByte < inBytes.length) {
        
        // read next sample
        sample = 0;
        for (i=0; i<SAMPLE_BYTES; i++) {
          if ((inByte < predictionHistory.length * SAMPLE_BYTES) && !adaptivePass) outBytes[outByte++] = inBytes[inByte];
          sample |= (inBytes[inByte++] & 0xFF) << ((SAMPLE_BIG_ENDIAN ? SAMPLE_BYTES-i-1 : i)*8);
        }
        if (SAMPLE_SIGNED && sample >= 1<<(SAMPLE_BYTES*8-1)) {
          sample -= 1<<(SAMPLE_BYTES*8);
        }
        
        // adapt coefficients to current block
        if (adaptivePass) {
        
          // reset accumulators
          if (samples == 0) {
            for (i=0; i<=order; i++) correlationSum[i] = 0;
          }
          
          // buffer sample for correlation
          for (i=order; i>0; i--) correlationHistory[i] = correlationHistory[i-1]; 
          correlationHistory[0] = sample;

          // calculate (non normalized) autocorrelation values
          for (i=0; i<=samples && i<=order; i++) {
            correlationSum[i] += sample * correlationHistory[i];
          }
          
          samples++;

          // end of block reached?
          if ((samples == blockSize) || (inByte == inBytes.length)) {
            inByte      -= samples * SAMPLE_BYTES; // rewind input stream for second pass
            adaptivePass = false;
            
            int   r,k;
            int   abs, maxAbs      = 0;                      // used for numerical stability
            int[] correlationValue = new int[order+1];       // autocorrelation values
            int[] A                = new int[order*order];   // quadratic matrix of linear equation system
            
            // calculate autocorrelation values
            for (i=0; i<=order; i++) {            
              correlationValue[i] = (int) (correlationSum[i] / (samples-i));
              abs = correlationValue[i] < 0 ? -correlationValue[i] : correlationValue[i];
              if (abs > maxAbs) maxAbs = abs;
            }
            
            // reduce values to 16bit (to avoid overflows in integer arithmetic)
            while (maxAbs >= 1<<15) {
              maxAbs /= 2;
              for (i=0; i<=order; i++) correlationValue[i] >>= 1;
            }
            
            // derive linear equation system Ax=B from autocorrelation values, B[i]=correlationValue[i+1]
            for (i=0; i<order; i++) {
              for (k=0; k<order; k++) {
                A[i*order+k] = correlationValue[i > k ? i-k : k-i];
              }
            }
            
            // solve linear equation system by gausian elimination (without pivot search) to calculate coefficients
            for (i=0; i<order-1; i++) {
              maxAbs = 0;
              for (r=i+1; r<order; r++) {
                for (k=i+1; k<order; k++) {
                  A[r*order+k] -= (A[r*order+i] * A[i*order+k]) / A[i*order+i];
                  abs = A[r*order+k];
                  if (abs < 0) abs = -abs;
                  if (abs > maxAbs) maxAbs = abs;
                }
                correlationValue[r+1] -= (A[r*order+i] * correlationValue[i+1]) / A[i*order+i];
                abs = correlationValue[r+1];
                if (abs < 0) abs = -abs;
                if (abs > maxAbs) maxAbs = abs;
                A[r*order+i] = 0;

                // reduce values to 16bit
                while (maxAbs >= 1<<15) {
                  maxAbs /= 2;
                  correlationValue[r+1]  >>= 1;
                  for (k=i+1; k<=order; k++) A[r*order+k] >>= 1;
                }
              }
            }

            // backsubstitution
            for (i=order; i-- != 0; ) {
              coefficients[i] = correlationValue[i+1];
              for (k=order-1; k>i; k--) coefficients[i] -= (A[i*order+k] * coefficients[k]) >> FRACTION;
              coefficients[i] = (coefficients[i] << FRACTION) / A[i*order+i];

              // fallback to simple dpcm if coefficients do not fit into fixedpoint format bits
              abs = coefficients[i];
              if (abs < 0) abs = -abs;
              if (abs >= 1 << (INTEGER+FRACTION-1)) {
                coefficients[0] = 1<<FRACTION;
                for (k=1;k<order;k++) coefficients[k] = 0;
                break;
              }
            }

            // write coefficients to output stream
            for (k=0; k<order; k++) {
              i = INTEGER+FRACTION;
              while (i != 0) {
                if (i + bits >= 8) {
                  bits                = 8-bits;
                  i                  -= bits;
                  outBytes[outByte++] = (byte) ((buffer << bits) | (((1<<bits)-1) & (coefficients[k] >> i)));
                  bits                = 0;
                } else {
                  bits  += i;
                  buffer = (byte) ((buffer << i) | (((1<<i)-1) & coefficients[k]));
                  i      = 0;
                }
              }
            }

            samples = 0;
          }
          
        // predict sample and encode prediction error (except for the first unencoded samples)
        } else {
                  
          if (inByte > predictionHistory.length * SAMPLE_BYTES) {
          
            // static/adaptive prediction
            if (order == 0) {
              prediction = predictionHistory[0];
            } else {
              prediction = 0;
              for (i=0; i<predictionHistory.length; i++) {
                prediction += (predictionHistory[i] * coefficients[i]) >> FRACTION;
              }
            }

            // prepare output of rice coder
            error = sample - prediction;                 // prediction error
            error = error >= 0 ? 2*error : -2*error-1;   // code spreading to positive values
            ones  = error >> riceCoefficient;            // run length of unary code
            error = error & ((1<<riceCoefficient)-1);    // residual for block code
            
            // write ones
            while (ones != 0) {
              if (ones + bits >= 8) {
                bits                = 8-bits;
                ones               -= bits;
                outBytes[outByte++] = (byte) ((buffer << bits) | ((1<<bits)-1));
                bits                = 0;
              } else {
                bits  += ones;
                buffer = (byte) ((buffer << ones) | ((1<<ones)-1));
                ones   = 0;
              }
            }
            
            // write zero (to terminate unary code)
            if (bits == 7) {
              outBytes[outByte++] = (byte) (buffer << 1);
              bits                = 0;
            } else {
              buffer <<= 1;
              bits    += 1;
            }
            
            // write residual block code
            i = riceCoefficient;
            while (i != 0) {
              if (i + bits >= 8) {
                bits                = 8-bits;
                i                  -= bits;
                outBytes[outByte++] = (byte) ((buffer << bits) | (((1<<bits)-1) & (error >> i)));
                bits                = 0;
              } else {
                bits  += i;
                buffer = (byte) ((buffer << i) | (((1<<i)-1) & error));
                i      = 0;
              }
            }
          }

          // buffer sample for next prediction
          for (i=predictionHistory.length-1; i>0; i--) predictionHistory[i] = predictionHistory[i-1]; 
          predictionHistory[0] = sample;

          // end of block reached?
          if ((order > 0) && (samples == blockSize-1)) {
            adaptivePass = true;
            samples = 0;
          } else {
            samples++;
          }
        }
      }
      
      // 1-padding to full byte => avoids misinterpretation of padding as additional sample (unterminated unary code)
      if (bits > 0) {
        outBytes[outByte++] = (byte) (((buffer) << (8-bits)) | ((1<<(8-bits))-1));
      }
      
      return outByte;
    }
    
  /*
   * decoder
   **********************************************************************************************************************/

    /**
     * Invoke the decoder.
     * @param inBytes         bitstream to be decoded
     * @param blockSize       maximum number of samples a set of adaptive prediction parameters is valid for
     * @param order           prediction order (or 0 for first order static prediction)
     * @param riceCoefficient parameter K for symbol coding
     * @param outBytes        decoded byte stream
     * @return size of decoded byte stream
     */
    static int decode(byte[] inBytes, int blockSize, int order, int riceCoefficient, byte[] outBytes) {
      int   i, k;
      int   sample       = 0;                                  // current sample
      int   samples      = 0;                                  // number of already processed samples
      int   inByte       = 0;                                  // number of read input bytes
      int   outByte      = 0;                                  // number of written output bytes
      int   prediction   = 0;                                  // sample prediction
      int   error;                                             // prediction error
      int   bits         = 8;                                  // number of valid bits in current input byte
      int[] lastSamples  = new int[order == 0 ? 1 : order];    // last samples used for prediction
      int[] coefficients = new int[order];                     // prediction coefficients
      int inBytesLength = inBytes.length;
      int lastSamplesLength = lastSamples.length;
      
      while (inByte < inBytesLength-2) {
        //read coefficients
        if ((samples == 0) && (order > 0)) {
          for (k=0; k<order; k++) {
            coefficients[k] = 0;
            i = INTEGER+FRACTION;
            while (i != 0) {
              if (i >= bits) {
                i -= bits;
                coefficients[k] = (coefficients[k] << bits) | (((1<<bits)-1) & (inBytes[inByte] >> (8-bits)));
                bits = 8;
                inByte++;
//                if (inByte == inBytesLength) return outByte;  // 1-padding of last encoded byte may lead us here
              } else {
                coefficients[k] = (coefficients[k] << i) | (((1<<i)-1) & (inBytes[inByte] >> (8-i)));
                inBytes[inByte] <<= i;
                bits -= i;
                i     = 0;
              }
            }
            if (coefficients[k] > (1<<(INTEGER+FRACTION-1))) {
              coefficients[k] -= (1<<(INTEGER+FRACTION));
            }
            int dummy = 0;
          }
        }
        
        
        // read raw initialization sample
        if (outByte < lastSamplesLength*SAMPLE_BYTES) {
          sample = 0;
          for (i=0; i<SAMPLE_BYTES; i++) {
            outBytes[outByte++] = inBytes[inByte];
            sample |= (inBytes[inByte++] & 0xFF) << ((SAMPLE_BIG_ENDIAN ? SAMPLE_BYTES-i-1 : i)*8);
          }
          if (SAMPLE_SIGNED && sample >= 1<<(SAMPLE_BYTES*8-1)) {
            sample -= 1<<(SAMPLE_BYTES*8);
          }
          samples++;
          lastSamples[lastSamplesLength-samples] = sample;
        
        // decode next sample
        } else {
          
          // predict next sample
          if (order == 0) {
            prediction = lastSamples[0];
          } else {
            prediction = 0;
            for (i=0; i<lastSamplesLength; i++) {
              prediction += (lastSamples[i] * coefficients[i]) >> FRACTION;
            }
          }
          
          // decode prediction error
          error = 0;
          
          // runlength of unary code
          while ((inBytes[inByte] & 0x80) != 0) {
            if (--bits == 0) {
              bits = 8;
              inByte++;
//              if (inByte == inBytesLength) return outByte;  // 1-padding of last encoded byte may lead us here
            } else {
              inBytes[inByte] <<= 1;
            };
            error += 1;
          };
          
          // skip terminating zero
          if (--bits == 0) {
            bits = 8;
            inByte++;
          } else {
            inBytes[inByte] <<= 1;
          };
          
          // read residual block code
          i = riceCoefficient;
          while (i != 0) {
            if (i >= bits) {
              i -= bits;
              error = (error << bits) | (((1<<bits)-1) & (inBytes[inByte] >> (8-bits)));
              bits = 8;
              inByte++;
            } else {
              error = (error << i) | (((1<<i)-1) & (inBytes[inByte] >> (8-i)));
              inBytes[inByte] <<= i;
              bits -= i;
              i     = 0;
            }
            int dummy = 9;
          }
          
          // inverse spreading
          error = ((error & 1) == 0) ? error>>1 : -((error+1)>>1);
          
          // calculate real sample
          sample = prediction + error;
          samples++;
          
          // buffer sample for next prediction
          for (i=lastSamplesLength-1; i>0; i--) lastSamples[i] = lastSamples[i-1]; 
          lastSamples[0] = sample;
          
          // output sample
          for (i=0; i<SAMPLE_BYTES; i++) {
            outBytes[outByte++] = (byte) (sample >> ((SAMPLE_BIG_ENDIAN ? SAMPLE_BYTES-i-1 : i)*8));
          }
          
          // end of block?
          if ((order > 0) && (samples == blockSize)) {
            samples = 0;
          }
          int dummy = 0;
        }
       int dummy = 0; 
      }
      return outByte;
    }
  }

  /*
   * Copyright (c) 2013,
   * Embedded Systems and Applications Group,
   * Department of Computer Science,
   * TU Darmstadt,
   * All rights reserved.
   *
   * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
   * following conditions are met:
   * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   *    disclaimer.
   * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
   *    following disclaimer in the documentation and/or other materials provided with the distribution.
   * 3. Neither the name of the institute nor the names of its contributors may be used to endorse or promote products
   *    derived from this software without specific prior written permission.
   *
   * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
   * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   * DISCLAIMED. IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
   * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
   * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
   * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
   * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
   **********************************************************************************************************************/