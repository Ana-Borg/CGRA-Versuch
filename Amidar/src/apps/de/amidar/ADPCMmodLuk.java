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
public class ADPCMmodLuk {

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
	static void help() {
		//    System.out.println("usage: adpcm {encode|decode} <inFile> <blockSize> <order> <riceCoefficient> <outFile> [verbose]");
		//    System.exit(-1);
	}

	/**
	 * Parse command line parameters and invoke encoder or decoder.
	 * @param args command line parameters
	 * This method also performs the File I/O and displays compression statistics.
	 */
	public static void main(String[] args) {

		// check command line parameters
		//    if (args.length < 6) help();

		boolean 	action          = DECODE;
		int     	blockSize       = 1024;
		int     	order           = 2;
		int     	riceCoefficient = 4;
		boolean verbose         = args.length > 6;

		// read inputFile
		byte[] inBytes  = {//0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				//    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				    		0,1,2,3,4,5,6,7,8,9,
				0,1,2,3,4,5,6,7,8,9};

		byte[] 	outBytes = new byte[inBytes.length*10];
		int    outSize  = 0;

		// invoke encoder/decoder
		if      (action == ENCODE) outSize = encode(inBytes, blockSize, order, riceCoefficient, outBytes);
		else if (action == DECODE) outSize = decode(inBytes, blockSize, order, riceCoefficient, outBytes);
		else help();

		for(int i = 0; i < outBytes.length; i++){
			System.out.print(outBytes[i]&0xFF);
			System.out.print(',');
		}
		System.out.println('\n');

		// write outputFile
		//    Files.write(Paths.get(outFile), java.util.Arrays.copyOf(outBytes, outSize));

		// report statistics
		if (verbose) {
			//      System.out.printf("%s %s [%d bytes] to %s [%d bytes] with blockSize=%4d order=%2d riceCoefficient=%2d\n",
			//        action, inFile,  inBytes.length,
			//                outFile, outSize,
			//                blockSize, order, riceCoefficient);
		}
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
		int   i, k, dummy;
		int   sample       = 0;                                  // current sample
		int   samples      = 0;                                  // number of already processed samples
		int   inByte       = 0;                                  // number of read input bytes
		int   outByte      = 0;                                  // number of written output bytes
		int   prediction   = 0;                                  // sample prediction
		int   error;                                             // prediction error
		int   bits         = 8;                                  // number of valid bits in current input byte
		int[] lastSamples  = new int[order == 0 ? 1 : order];    // last samples used for prediction
		int[] coefficients = new int[order];                     // prediction coefficients

		int cccnt = 0;

		int inBytesLength = inBytes.length;
		int lastSamplesLength = lastSamples.length;
		int returnVal = 0;
		boolean ret1 = false, ret2 = false;
		while (!ret1 && !ret2 && (inByte < inBytesLength)) {
//			System.out.println(inByte);
			//read coefficients
			if ((samples == 0) && (order > 0)) {
				for (k=0; (!ret1) && (k<order);k++) {
					
//					System.out.println(k);
					coefficients[k] = 0;
					i = INTEGER+FRACTION;
					while (i != 0) {
//						System.out.println('s');
						if (i >= bits) {
							i -= bits;
							coefficients[k] = (coefficients[k] << bits) | (((1<<bits)-1) & (inBytes[inByte] >> (8-bits)));
							bits = 8;
							inByte++;
							if (inByte == inBytesLength){
								returnVal = outByte;  // 1-padding of last encoded byte may lead us here
								ret1 = true;
								i = 0;
							}
							dummy = 1;
						}
						else{
							coefficients[k] = (coefficients[k] << i) | (((1<<i)-1) & (inBytes[inByte] >> (8-i)));
							inBytes[inByte] <<= i;
							bits -= i;
							i     = 0;
						}
						dummy = 123;
					}
					if (!ret1 && coefficients[k] > (1<<(INTEGER+FRACTION-1))) {
						coefficients[k] -= (1<<(INTEGER+FRACTION));
					}
					dummy = 0;
				}
			}
//System.out.println(999999999);
//System.out.println(samples);
//System.out.println(outByte);
//System.out.println(lastSamplesLength);
			if(!ret1){
System.out.println(11);
				// read raw initialization sample
				int outByteBuffer = outByte;
				if (outByte < lastSamplesLength*SAMPLE_BYTES) {
					sample = 0;
					for (i=0; i<SAMPLE_BYTES; i++) {
						outBytes[outByte++] = inBytes[inByte];
						sample |= (inBytes[inByte++] & 0xFF) << ((SAMPLE_BIG_ENDIAN ? SAMPLE_BYTES-i-1 : i)*8);
					}
					if (SAMPLE_SIGNED && sample >= 1<<(SAMPLE_BYTES*8-1)) {
						sample -= 1<<(SAMPLE_BYTES*8);
					}
					int dummy233;
					
					if(samples == 3)
						dummy233 = lastSamples[samples*44];
					else
						dummy233 = 3;
					samples++;
					if(samples == 3)
						dummy233 = lastSamples[samples];
					cccnt++;
//System.out.println(9999999);					
//System.out.println(samples);
//System.out.println(outByte);
					lastSamples[lastSamplesLength-samples] = sample;

					// decode next sample
				} if (outByteBuffer >= lastSamplesLength*SAMPLE_BYTES) {
					// predict next sample
					if (order == 0) {
						prediction = lastSamples[0];
					} if (order != 0) {
						prediction = 0;
						for (i=0; i<lastSamplesLength; i++) {
							prediction += (lastSamples[i] * coefficients[i]) >> FRACTION;
						}
					}

					// decode prediction error
					error = 0;

					// runlength of unary code
//					boolean loop = (!ret2) & ((inBytes[inByte] & 0x80) != 0);
					while ((!ret2) && ((inBytes[inByte] & 0x80) != 0)) { 
//													System.out.println('s');
						if (--bits == 0) {
							bits = 8;
							inByte++;
							if (inByte == inBytesLength){
								returnVal = outByte;  // 1-padding of last encoded byte may lead us here
								ret2 = true;
							}
						} else{
							inBytes[inByte] <<= 1;
						}
						error += 1;
//						loop = (!ret2) & ((inBytes[inByte] & 0x80) != 0);
					}
					
					if(!ret2){
						// skip terminating zero
						if (--bits == 0) {
							bits = 8;
							inByte++;
						} else {
							inBytes[inByte] <<= 1;
						}

						// read residual block code
						i = riceCoefficient;
						while (i != 0) {
							//System.out.println("989");             	
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
							dummy = 0;
						}

						// inverse spreading
						error = ((error & 1) == 0) ? error>>1 : -((error+1)>>1);

						// calculate real sample
						sample = prediction + error;
						samples++;

						// buffer sample for next prediction
						for (i=lastSamplesLength-1; i>0; i--){
							lastSamples[i] = lastSamples[i-1]; 
						}
						lastSamples[0] = sample;

						// output sample
						for (i=0; i<SAMPLE_BYTES; i++) {
							outBytes[outByte++] = (byte) (sample >> ((SAMPLE_BIG_ENDIAN ? SAMPLE_BYTES-i-1 : i)*8));
						}

						// end of block?
						if ((order > 0) && (samples == blockSize)) {
							samples = 0;
						}
						dummy = 4;
					}
					
					dummy = 3;
				} 
				dummy = 2;
			}
			dummy = 1;
		}
//		System.out.println(cccnt);
		if(ret1 || ret2) return returnVal;
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