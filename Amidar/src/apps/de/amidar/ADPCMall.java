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
public class ADPCMall {

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
		run(14);
//		run(14);
	}

	/*
	 * Run the Test
	 */
	public static void run(int a){
		// check command line parameters
		//    if (args.length < 6) help();

		boolean 	action          = ENCODE;
		int     	blockSize       = 1024;
		int     	order           = a;
		int     	riceCoefficient = 4;
		//boolean verbose         = args.length > 6;

		// read inputFile
		// decodee order 14 input
		//		byte[] inBytes  = {27,-12,-30,-45,26,50,-24,-5,17,-29,-10,-62,3,-91,4,109,-14,-85,20,0,-23,-101,21,-37,-16,0,7,55,0,23,0,28,0,50,0,51,0,30,0,21,0,21,0,51,0,75,0,85,0,86,0,44,0,28,0,50,-121,15,53,-15,-13,35,-126,86,19,61,-60,69,-16,84,124,-122,-51,52,43,-83,113,33,-26,99,-94,-112,55,77,-93,-118,103,77,81,-96,70,-52,-110,64,117,24,-50,-55,-52,-98,79,-110,-106,72,-97,50,40,-24,31,0,99,96,-40,91,-110,63,65,34,-94,115,78,89,50,62,-97,-103,-9,73,-27,-28,-84,92,73,-14,116,76,-53,-82,50,-106,113,83,21,1,-96,102,53,16,22,34,-26,-44,-116,-111,35,5,99,-44,-73,33,-50,99,-95,37,85,72,-85,5,8,104,14,68,51,83,32,-114,-28,64,6,-8,-90,45,83,9,-126,103,21,80,96,-94,98,8,-38,-110,9,-102,-29,-59,-127,-104,32,65,9,-103,-122,81,-11,42,35,64,-82,44,-99,-66,-21,57,-82,-49,-51,78,-94,65,-79,-72,-71,83,16,-75,67,36,65,-32,66,-72,28,-67,-40,22,124,79,-121,18,-7,-76,-75,-48,-40,-98,9,52,100,-122,-87,8,-87,-52,86,65,9,37,123,-52,73,-18,-59,115,-32,-96,76,-63,102,77,8,-56,-20,-37,2,-83,80,14,-37,-68,-36,-72,83,127,48,104,-119,-8,19,20,14,-63,-100,-55,22,67,98,-126,60,-59,-93,-47,84,-14,-3,22,-28,-59,-97,-39,121,-22,-89,69,11,92,-128,61,9,-114,-104,-44,-95,102,-111,57,46,52,84,49,-109,2,108,23,113,60,-41,4,-46,-126,9,44,100,-58,57,70,-100,-120,-109,21,26,-114,-90,60,-56,96,-62,-125,-96,-47,84,-15,-40,-19,25,56,-78,11,52,-112,48,85,26,-115,18,106,-55,1,33,26,-55,18,-82,66,-92,86,8,-103,36,10,-30,-77,78,103,113,35,79,36,-13,13,89,-101,-117,106,25,14,75,-55,69,122,8,-82,-62,-101};
		// encode input
		byte [] inBytes = {0, 23, 0, 28, 0, 50, 0, 51, 0, 30, 0, 21, 0, 21, 0, 51, 
				0, 75, 0, 85, 0, 86, 0, 44, 0, 28, 0, 50, 0, 38, 0, 12, 
				0, 19, 0, 22, -1, -2, 0, 12, 0, 55, 0, 70, 0, 52, 0, 33, 
				0, 21, 0, 2, 0, 5, 0, 25, 0, 30, 0, 15, -1, -4, 0, 0, 
				0, 13, 0, 25, 0, 33, 0, 40, 0, 53, 0, 39, 0, 1, -1, -26, 
				-1, -45, -1, -36, -1, -5, 0, 6, 0, 5, -1, -2, -1, -11, -1, -10, 
				-1, -1, 0, 7, 0, 12, 0, 21, 0, 42, 0, 57, 0, 55, 0, 35, 
				0, 12, 0, 8, 0, 9, 0, 13, 0, 24, 0, 39, 0, 55, 0, 55, 
				0, 42, 0, 23, 0, 6, 0, 7, 0, 16, 0, 28, 0, 52, 0, 75, 
				0, 61, 0, 48, 0, 84, 0, 82, 0, 41, 0, 38, 0, 36, 0, 55, 
				0, 83, 0, 82, 0, 97, 0, 87, 0, 70, 0, 66, 0, 40, 0, 43, 
				0, 43, 0, 36, 0, 55, 0, 57, 0, 54, 0, 42, 0, 55, 0, 93, 
				0, 82, 0, 82, 0, 75, 0, 32, 0, 18, 0, 7, 0, 22, 0, 71, 
				0, 62, 0, 28, 0, 43, 0, 58, 0, 57, 0, 56, 0, 34, 0, 22, 
				0, 14, 0, 13, 0, 45, 0, 61, 0, 74, 0, 94, 0, 92, 0, 87, 
				0, 73, 0, 68, 0, 73, 0, 59, 0, 50, 0, 35, 0, 18, 0, 10, 
				0, 5, 0, 32, 0, 53, 0, 53, 0, 55, 0, 39, 0, 29, 0, 41, 
				0, 50, 0, 53, 0, 47, 0, 37, 0, 42, 0, 59, 0, 71, 0, 70, 
				0, 65, 0, 57, 0, 51, 0, 39, 0, 24, 0, 38, 0, 47, 0, 38, 
				0, 40, 0, 42, 0, 41, 0, 42, 0, 43, 0, 36, 0, 21, 0, 31, 
				0, 53, 0, 61, 0, 65, 0, 60, 0, 42, 0, 18, 0, 2, 0, 19, 
				0, 35, 0, 49, 0, 60, 0, 48, 0, 52, 0, 59, 0, 47, 0, 33, 
				0, 31, 0, 43, 0, 49, 0, 42, 0, 30, 0, 37, 0, 61, 0, 55, 
				0, 44, 0, 59, 0, 53, 0, 37, 0, 36, 0, 40, 0, 61, 0, 79, 
				0, 80, 0, 80, 0, 78, 0, 75, 0, 58, 0, 44, 0, 48, 0, 59, 
				0, 64, 0, 55, 0, 50, 0, 45, 0, 32, 0, 25, 0, 36, 0, 65, 
				0, 83, 0, 85, 0, 87, 0, 72, 0, 47, 0, 35, 0, 52, 0, 77, 
				0, 76, 0, 64, 0, 49, 0, 47, 0, 59, 0, 62, 0, 63, 0, 69, 
				0, 66, 0, 83, 0, 110, 0, 77, 0, 62, 0, 89, 0, 111, 0, 127, 
				0, 99, 0, 78, 0, 64, 0, 51, 0, 75, 0, 90, 0, 109, 0, 111, 
				0, 77, 0, 52, 0, 21, 0, 22, 0, 60, 0, 86, 0, 103, 0, 93, 
				0, 75, 0, 65, 0, 61, 0, 84, 0, 92, 0, 101, 0, 123, 0, 93, 
				0, 80, 0, 97, 0, 73, 0, 50, 0, 61, 0, 71, 0, 57, 0, 56, 
				0, 72, 0, 78, 0, 61, 0, 36, 0, 25, 0, 33, 0, 51, 0, 56, 
				0, 75, 0, 100, 0, 98, 0, 94, 0, 79, 0, 62, 0, 67, 0, 67, 
				0, 70, 0, 59, 0, 38, 0, 30, 0, 24, 0, 41, 0, 49, 0, 32, 
				0, 26, 0, 8, -1, -8, 0, 6, 0, 19, 0, 19, 0, 21, 0, 43, 
				0, 67, 0, 47, 0, 29, 0, 31, 0, 23, 0, 26, 0, 44, 0, 61, 
				0, 74, 0, 72, 0, 58, 0, 50, 0, 52, 0, 63, 0, 80, 0, 86, 
				0, 74, 0, 70, 0, 75, 0, 61, 0, 26, 0, 23, 0, 76, 0, 78, 
				0, 24, 0, 1, -1, -19, -1, -7, 0, 36, 0, 63, 0, 84, 0, 60, 
				0, 29, 0, 22, 0, 22, 0, 49, 0, 53, 0, 54, 0, 52, 0, 8, 
				-1, -9, -1, -4, 0, 11, 0, 45, 0, 53, 0, 62, 0, 46, 0, 20, 
				0, 24, 0, 20, 0, 61, 0, 110, 0, 70, 0, 22, 0, 35, 0, 37, 
				0, 33, 0, 54, 0, 54, 0, 52, 0, 47, 0, 24, 0, 19, 0, 19, 
				0, 29, 0, 48, 0, 50, 0, 42, 0, 25, 0, 13, 0, 11, 0, 13, 
				0, 25, 0, 13, -1, -20, -1, -35, -1, -34, -1, -15, 0, 3, 0, 21, 
				0, 39, 0, 17, 0, 6, 0, 27, 0, 26, 0, 21, 0, 22, 0, 19, 
				0, 16, 0, 11, 0, 8, 0, 3, 0, 12, 0, 37, 0, 55, 0, 50, 
				0, 26, 0, 11, 0, 10, 0, 5, -1, -6, -1, -11, -1, -2, 0, 13, 
				0, 26, 0, 35, 0, 35, 0, 21, -1, -3, -1, -19, -1, -17, -1, -16, 
				-1, -1, 0, 17, 0, 17, 0, 20, 0, 25, 0, 31, 0, 25, 0, 7, 
				0, 6, -1, -5, -1, -17, -1, -7, -1, -4, -1, -8, -1, -17, -1, -26, 
				-1, -27, -1, -27, -1, -14, 0, 1, 0, 8, 0, 2, -1, -27, -1, -30, 
				-1, -9, 0, 6, 0, 31, 0, 55, 0, 60, 0, 35, 0, 11, 0, 15, 
				0, 13, 0, 10, 0, 14, 0, 15, 0, 19, 0, 17, 0, 25, 0, 41, 
				0, 47, 0, 59, 0, 63, 0, 51, 0, 27, -1, -5, -1, -20, -1, -15, 
				0, 4, 0, 28, 0, 39, 0, 38, 0, 23, 0, 9, 0, 14, 0, 29, 
				0, 45, 0, 50, 0, 44, 0, 39, 0, 25, 0, 33, 0, 54, 0, 23, 
				0, 7, 0, 28, 0, 53, 0, 67, 0, 29, 0, 13, 0, 12, -1, -2, 
				0, 15, 0, 29, 0, 47, 0, 48, 0, 19, 0, 2, -1, -15, -1, -12, 
				-1, -1, 0, 5, 0, 19, 0, 3, -1, -6, -1, -7, -1, -16, 0, 2, 
				0, 10, 0, 31, 0, 52, 0, 22
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
		//int     i;
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
		int[]  correlationSum     = new int[order+1];               // accumulators for autocorrelation values
		int[]   coefficients       = new int[order];                  // prediction coefficients

		// two different histories are required predict first sample of new block from last samples of old block after the
		// new block was passed ones for autocorrelation
		int dummy = 0; 	//needed for while loop bug  

		int inBytesLength = inBytes.length;
		int predictionHistoryLength = predictionHistory.length; 
		int[] correlationValue = new int[order+1];       // autocorrelation values
		int[] A                = new int[order*order];   // quadratic matrix of linear equation system

//		int[] valueContainer = new int [10003];
		int cnttt = 0;

		while (inByte < inBytesLength) {
			//System.out.println(samples);
			//   System.out.println(outByte);
			// read next sample
			sample = 0;
//			valueContainer[cnttt++]= outByte;
			for (int i=0; i<SAMPLE_BYTES; i++) {
				if (!adaptivePass &&(inByte < predictionHistoryLength * SAMPLE_BYTES)) {
					outBytes[outByte] = inBytes[inByte];
					outByte++;
				}
				int shift;
				if(SAMPLE_BIG_ENDIAN)
					shift = (SAMPLE_BYTES-i-1)*8;
				else
					shift = i*8;
				sample |= (inBytes[inByte++] & 0xFF) << shift;
			}
			if (SAMPLE_SIGNED && sample >= 1<<(SAMPLE_BYTES*8-1)) {
				sample -= 1<<(SAMPLE_BYTES*8);
			}
//			valueContainer[cnttt++]= outByte;
//			valueContainer[cnttt++]= 9999;
			// adapt coefficients to current block
			if (adaptivePass) {

				// reset accumulators
				if (samples == 0) {
					for (int i=0; i<=order; i++) correlationSum[i] = 0;
				}

				// buffer sample for correlation
				for (int i=order; i>0; i--) correlationHistory[i] = correlationHistory[i-1]; 
				correlationHistory[0] = sample;
				//System.out.println(1);
				// calculate (non normalized) autocorrelation values
				int limit;
				if (samples < order)
					limit = samples;
				else 
					limit = order;
				for (int i=0; i<=limit; i++) {
					//System.out.print(44);///ffff
					correlationSum[i] += sample * correlationHistory[i];
				}
				//System.out.println(2);          
				samples++;

//				// end of block reached?
//				boolean dec = (samples == blockSize); 
//				boolean dec2 = (inByte == inBytesLength);
//
//				if (dec | dec2) {
			    if ((samples == blockSize) | (inByte == inBytesLength)) {
//					valueContainer[cnttt++] = 999;
							inByte      -= samples * SAMPLE_BYTES; // rewind input stream for second pass
					adaptivePass = false;
					//System.out.println(3);            
					// int   r,k;
					int   abs, maxAbs      = 0;                      // used for numerical stability


					// calculate autocorrelation values
					for (int i=0; i<=order; i++) {            
						correlationValue[i] = (int) (correlationSum[i] / (samples-i));
						if(correlationValue[i] < 0)
							abs = -correlationValue[i];
						else
							abs = correlationValue[i];
						//              abs = correlationValue[i] < 0 ? -correlationValue[i] : correlationValue[i];
						if (abs > maxAbs) maxAbs = abs;
					}
					// reduce values to 16bit (to avoid overflows in integer arithmetic)
					while (maxAbs >= 1<<15) {
						maxAbs /= 2;
						for (int i=0; i<=order; i++){ 
							correlationValue[i] >>= 1;
						}
						dummy = 1;
					}

					// derive linear equation system Ax=B from autocorrelation values, B[i]=correlationValue[i+1]
					for (int i=0; i<order; i++) {
						for (int k=0; k<order; k++) {
							int index;
							if (i>k)
								index = i-k;
							else
								index = k-i;
							A[i*order+k] = correlationValue[index];
						}
					}

					// solve linear equation system by gausian elimination (without pivot search) to calculate coefficients
					for (int i=0; i<order-1; i++) {
						maxAbs = 0;
						for (int r=i+1; r<order; r++) {
							for (int k=i+1; k<order; k++) {
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
							for (int k=i+1; k<=order; k++) A[r*order+k] >>= 1;
							dummy = 1;
							}
						}
					}

					// backsubstitution
					for (int i=order; i-- != 0; ) {
						//System.out.println(3);
						coefficients[i] = correlationValue[i+1];
						for (int k=order-1; k>i; k--){
							coefficients[i] -= (A[i*order+k] * coefficients[k]) >> FRACTION;
					//		System.out.print(66);
						}

						coefficients[i] = (coefficients[i] << FRACTION) / A[i*order+i];
						// fallback to simple dpcm if coefficients do not fit into fixedpoint format bits
						abs = coefficients[i];
						if (abs < 0) abs = -abs;
						if (abs >= 1 << (INTEGER+FRACTION-1)) {
							coefficients[0] = 1<<FRACTION;
							for (int k=1;k<order;k++){ 
								coefficients[k] = 0;
							}
							i = 0;
							//		System.out.println(55);
							//               break;
						}
						dummy = 1;
					}

					// write coefficients to output stream
					for (int k=0; k<order; k++) {
						int i = INTEGER+FRACTION;
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
							dummy = 1;
						}
					}

					samples = 0;
				}

				// predict sample and encode prediction error (except for the first unencoded samples)
			} else {

				if (inByte > predictionHistoryLength * SAMPLE_BYTES) {

					// static/adaptive prediction
					if (order == 0) {
						prediction = predictionHistory[0];
					} else {
						prediction = 0;
						for (int i=0; i<predictionHistoryLength; i++) {
							prediction += ((predictionHistory[i] * coefficients[i]) >> FRACTION);
						}
					}
					// prepare output of rice coder
					error = sample - prediction;                 // prediction error
					if (error >= 0)
						error = 2*error;
					else
						error = -2*error-1;
					//            error = error >= 0 ? 2*error : -2*error-1;   // code spreading to positive values
					
//					valueContainer[cnttt++]=error;
//					valueContainer[cnttt++]=1111;
					ones  = (error >> riceCoefficient);            // run length of unary code
					error = error & ((1<<riceCoefficient)-1);    // residual for block code
//					valueContainer[cnttt++]=ones;
//					valueContainer[cnttt++]=error;
//					valueContainer[cnttt++]=22222;
					
					
//					outByte++;
//					outByte--;
					
					// write ones
					while (ones != 0) {
						if (ones + bits >= 8) {
							bits                = 8-bits;
							ones               -= bits;
							int store = ((buffer << bits) | ((1<<bits)-1));
//							valueContainer[cnttt++]= store;
//							valueContainer[cnttt++]=(byte) store;
							outBytes[outByte++] = (byte) store;
//							valueContainer[cnttt++]= outByte;
//							valueContainer[cnttt++]=outBytes[outByte-1];
							bits                = 0;
						} else {
							bits  += ones;
							buffer = (byte) ((buffer << ones) | ((1<<ones)-1));
//							valueContainer[cnttt++]=buffer;
							ones   = 0;
						}
						dummy = 1;
//						valueContainer[cnttt++]=2222;
					}
					
//					valueContainer[cnttt++]=outBytes[outByte-1];
					// write zero (to terminate unary code)
					if (bits == 7) {
						outBytes[outByte++] = (byte) (buffer << 1);
						bits                = 0;
					} else {
						buffer <<= 1;
						bits    += 1;
					}

					// write residual block code
					int r = riceCoefficient;
					while (r != 0) {
						if (r + bits >= 8) {
							bits                = 8-bits;
							r                  -= bits;
							outBytes[outByte++] = (byte) ((buffer << bits) | (((1<<bits)-1) & (error >> r)));
							bits                = 0;
						} else {
							bits  += r;
							buffer = (byte) ((buffer << r) | (((1<<r)-1) & error));
							r      = 0;
						}
						dummy = 1;
					}
				}

				// buffer sample for next prediction
				for (int i=predictionHistoryLength-1; i>0; i--) predictionHistory[i] = predictionHistory[i-1]; 
				predictionHistory[0] = sample;

				// end of block reached?
//				if (!((order <= 0) | (samples != blockSize-1))) {
				if ((order > 0) && (samples == blockSize-1)) {
					adaptivePass = true;
					samples = 0;
				} else {
					samples++;
				}
				dummy = 1;
			}
			dummy = 1;
		}

		// 1-padding to full byte => avoids misinterpretation of padding as additional sample (unterminated unary code)
		if (bits > 0) {
			outBytes[outByte++] = (byte) (((buffer) << (8-bits)) | ((1<<(8-bits))-1));
		}

//		for(int i =0; i<valueContainer.length; i++){
//			System.out.print(valueContainer[i]);
//			System.out.print(',');
//		}
//		System.out.println();
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
		int   error=0;                                             // prediction error
		int   bits         = 8;                                  // number of valid bits in current input byte
		byte  cb           = 0;
		int[] lastSamples  = new int[order == 0 ? 1 : order];    // last samples used for prediction
		int[] coefficients = new int[order];                     // prediction coefficients


		int dummy = 1;											// needed for while loop bug
		int inBytesLength = inBytes.length;
		int lastSamplesLength = lastSamples.length;
		cb=inBytes[inByte];

		while (outByte < lastSamplesLength*SAMPLE_BYTES) {

			//for(int j = 0; j < lastSamples.length; j ++){
			//    		System.out.print(lastSamples[j]);
			//   		System.out.print(',');
			//    	}
			//    	System.out.println();
			//System.out.println(inByte+ " " +outByte);

			//read coefficients
			if ((samples == 0) && (order > 0)) {
				for (k=0; k<order; k++) {
					coefficients[k] = 0;
					i = INTEGER+FRACTION;
					while (i != 0) {
						//        	  System.out.println(3);
						if (i >= bits) {
							i -= bits;
							coefficients[k] = (coefficients[k] << bits) | (((1<<bits)-1) & (cb >> (8-bits)));
							bits = 8;
							inByte++;
							//              if (inByte == inBytesLength) return outByte;  // 1-padding of last encoded byte may lead us here
							cb = inBytes[inByte];
						} else {
							coefficients[k] = (coefficients[k] << i) | (((1<<i)-1) & (cb >> (8-i)));
							cb <<= i;
							bits -= i;
							i     = 0;
						}
						dummy = 1;
					}
					if (coefficients[k] > (1<<(INTEGER+FRACTION-1))) {
						coefficients[k] -= (1<<(INTEGER+FRACTION));
					}
				}
			}


			// read raw initialization sample

			sample = 0;
			for (i=0; i<SAMPLE_BYTES; i++) {
				//System.out.println(3);
				outBytes[outByte++] = cb;
				//System.out.println(outBytes[outByte-1]);
				sample |= (cb & 0xFF) << ((SAMPLE_BIG_ENDIAN ? SAMPLE_BYTES-i-1 : i)*8);
				inByte++;
				cb=inBytes[inByte];
			}
			if (SAMPLE_SIGNED && sample >= 1<<(SAMPLE_BYTES*8-1)) {
				sample -= 1<<(SAMPLE_BYTES*8);
			}
			samples++;
			lastSamples[lastSamplesLength-samples] = sample;

			// decode next sample

		}
		//    inByte = inByte+2;
		//    inByte = inByte-2;
		//    int cnt = -1;
		while (inByte < inBytesLength-2) {

			if ((samples == 0) && (order > 0)) {
				for (int kk=0; kk<order; kk++) {
					//System.out.println(22);
					coefficients[kk] = 0;
					i = INTEGER+FRACTION;
					while (i != 0) {
						//        	  System.out.println(3);
						if (i >= bits) {
							i -= bits;
							coefficients[kk] = (coefficients[kk] << bits) | (((1<<bits)-1) & (cb >> (8-bits)));
							bits = 8;
							inByte++;
							//              if (inByte == inBytesLength) return outByte;  // 1-padding of last encoded byte may lead us here
							cb=inBytes[inByte];
						} else {
							coefficients[kk] = (coefficients[kk] << i) | (((1<<i)-1) & (cb >> (8-i)));
							cb <<= i;
							bits -= i;
							i     = 0;
						}
						dummy = 1;
					}
					if (coefficients[kk] > (1<<(INTEGER+FRACTION-1))) {
						coefficients[kk] -= (1<<(INTEGER+FRACTION));
					}
				}
			}

			//    	cnt++;
			//    	dummy = outBytes[(int)(outByte*3.3)];
			//    	System.out.print(2);
			// predict next sample
			//    	if (order == 0) {
			//    		prediction = lastSamples[0];
			//    	} else {

			prediction = 0;
			//    		valuecontainer[cntt++] = prediction;
			for (int j=0; j<lastSamplesLength; j++) {
				prediction += (lastSamples[j] * coefficients[j]) >> FRACTION;
			//    		valuecontainer[cntt++] = prediction;
			//    		valuecontainer[cntt++] = j;
			}

			//    	}
			// decode prediction error
			//    		boolean looptaken = false;
			error = 0;
			//    	errorA[cnt]=error;
			// runlength of unary code
			while ((cb & 0x80) != 0) {		///420:IFEQ /// 430:IFEQ
				if (--bits == 0) {				///428:IFNE			/// 438:IFNE
					bits = 8;
					inByte++;					/////////1/////////	/// 445:IINC
					cb=inBytes[inByte];
					//    	            if (inByte == inBytesLength) return outByte;  // 1-padding of last encoded byte may lead us here
				} else {
					cb <<= 1;
				}
				error += 1;
				//    		looptaken = true;
			}
			//    	if(looptaken)											///471:IFEQ
			//    	errorB[cnt]=error;
			//    	else
			//    		errorB[cnt]=9;
			//    	if(inByte<0)
			//    	dummy = inBytes[inByte*1000];
			//    	inByte++; //komisch, brauch ich irgendwie sonst gibts ne nullpointer exception im ifblock danach
			//    	inByte--; //
			//    	errorC[cnt]=error;

			// skip terminating zero
			if (--bits == 0) {				//461:IFNE				/// 503:IFNE
				bits = 8;
				inByte++;					/////////2/////////		/// 510:IINC
				cb=inBytes[inByte];
			} else {
				cb <<= 1;
			}

			//    	
			//    	error++; //komisch, brauch ich irgendwie sonst gibts ne nullpointer exception im ifblock danach
			//    	error--; //

			// read residual block code
			i = riceCoefficient;
			while (i != 0) {
				if (i >= bits) {
					i -= bits;
					error = (error << bits) | (((1<<bits)-1) & (cb >> (8-bits)));
					bits = 8;
					inByte++;				/////////3/////////		/// 588:IINC
					cb=inBytes[inByte];
				} else {
					error = (error << i) | (((1<<i)-1) & (cb >> (8-i)));
					cb <<= i;
					bits -= i;
					i     = 0;
				}
				dummy = 2;
			}
			//    	inByte++;
			//    	valuecontainer[cntt++] = 2222;
			//    	valuecontainer[cntt++] = error;
			// inverse spreading
			if((error & 1) == 0){
				error = error /2;
			} else{
				error = -((error+1)/2);
			}

			// calculate real sample
			sample = prediction + error;
			samples++;
			//    	valuecontainer[cntt++] = 3333;
			//    	valuecontainer[cntt++] = prediction;
			//    	valuecontainer[cntt++] = error;
			//    	valuecontainer[cntt++] = sample;
			// buffer sample for next prediction
			for (int j=lastSamplesLength-1; j>0; j--) lastSamples[j] = lastSamples[j-1]; 
			lastSamples[0] = sample;

			// output sample
			for (int j=0; j<SAMPLE_BYTES; j++) {
				int shift = 0;
				if(SAMPLE_BIG_ENDIAN)
					shift = (SAMPLE_BYTES-j-1)*8;
				else
					shift = j*8;

				outBytes[outByte] = (byte) (sample >> (shift));
				outByte++;
			}
			dummy  = 1;

			// end of block?
			if ((order > 0) && (samples == blockSize)) {
				samples = 0;
			}
			dummy = 1;
		}
		//    System.out.println(outByte);
		//s    System.out.println(inByte);
		//    for(int j = 0; j< errorA.length; j++){
		//    	System.out.print(errorA[j]);
		//    	System.out.print(',');
		//    }
		//    System.out.println();
		//    for(int j = 0; j< errorA.length; j++){
		//    	System.out.print(errorB[j]);
		//    	System.out.print(',');
		//    }
		//    System.out.println();
		//    for(int j = 0; j< errorA.length; j++){
		//    	System.out.print(errorC[j]);
		//    	System.out.print(',');
		//    }
		//    System.out.println();
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
