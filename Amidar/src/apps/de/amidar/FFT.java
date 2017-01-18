package de.amidar;

import cgra.pe.PETrigonometry;

public class FFT {

	int n, m;

	// Lookup tables.  Only need to recompute when size of FFT changes.
	double[] cos;
	double[] sin;

	final static double PI = 3.14159265359;

	double[] window;

	public FFT(int n) {
		this.n = n;

		this.m = 0;
		while(n != 1){
			n = n>>1;
			m++;
		}

		// Make sure n is a power of 2
		if(this.n != (1<<m)){
			System.out.println(-1);
			return;
		}


		// precompute tables
		cos = new double[this.n/2];
		sin = new double[this.n/2];

		//     for(int i=0; i<n/4; i++) {
		//       cos[i] = Math.cos(-2*Math.PI*i/n);
		//       sin[n/4-i] = cos[i];
		//       cos[n/2-i] = -cos[i];
		//       sin[n/4+i] = cos[i];
		//       cos[n/2+i] = -cos[i];
		//       sin[n*3/4-i] = -cos[i];
		//       cos[n-i]   = cos[i];
		//       sin[n*3/4+i] = -cos[i];        
		//     }

		for(int i=0; i<this.n/2; i++) {
			cos[i] = PETrigonometry.cos(-2*PI*i/this.n);
			sin[i] = PETrigonometry.sin(-2*PI*i/this.n);
		}

		makeWindow();//Auskommentieren und es kommt ein error -.-
	}

	protected void makeWindow() {
		// Make a blackman window:
		// w(n)=0.42-0.5cos{(2*PI*n)/(N-1)}+0.08cos{(4*PI*n)/(N-1)};
		window = new double[n];
		for(int i = 0; i < window.length; i++)
			window[i] = 0.42 - 0.5 * PETrigonometry.cos(2*PI*i/(n-1)) 
			+ 0.08 * PETrigonometry.cos(4*PI*i/(n-1));
	}

	public double[] getWindow() {
		return window;
	}


	/***************************************************************
	 * fft.c
	 * Douglas L. Jones 
	 * University of Illinois at Urbana-Champaign 
	 * January 19, 1992 
	 * http://cnx.rice.edu/content/m12016/latest/
	 * 
	 *   fft: in-place radix-2 DIT DFT of a complex input 
	 * 
	 *   input: 
	 * n: length of FFT: must be a power of two 
	 * m: n = 2**m 
	 *   input/output 
	 * x: double array of length n with real part of data 
	 * y: double array of length n with imag part of data 
	 * 
	 *   Permission to copy and use this program is granted 
	 *   as long as this header is included. 
	 ****************************************************************/
	public void fft(double[] x, double[] y)
	{
		double c,s,e,t1,t2;

		int i,j,k,n1,n2,a;


		// Bit-reverse
		j = 0;
		n2 = n/2;
		for (i=1; i < n - 1; i++) {
			n1 = n2;
			while ( j >= n1 ) {
				j = j - n1;
				n1 = n1/2;
			}
			j = j + n1;

			if (i < j) {
				t1 = x[i];
				x[i] = x[j];
				x[j] = t1;
				t1 = y[i];
				y[i] = y[j];
				y[j] = t1;
			}
		}

		// FFT
		n1 = 0;
		n2 = 1;

		for (i=0; i < m; i++) {
			n1 = n2;
			n2 = n2 + n2;
			a = 0;

			for (j=0; j < n1; j++) {
				c = cos[a];
				s = sin[a];
				a +=  1 << (m-i-1);
				for (k=j; k < n; k=k+n2) {
					t1 = c*x[k+n1] - s*y[k+n1];
					t2 = s*x[k+n1] + c*y[k+n1];
					x[k+n1] = x[k] - t1;
					y[k+n1] = y[k] - t2;
					x[k] = x[k] + t1;
					y[k] = y[k] + t2;
				}
			}
		}
	} 

	public void fft2(double[] x, double[] y)
	{
		double c,s,e,t1,t2;

		int i,j,k,n1,n2,a;


		// Bit-reverse
		j = 0;
		n2 = (n>>1);
		for (i=1; i < n - 1; i++) {
			n1 = n2;
			while ( j >= n1 ) {
				j = j - n1;
				n1 = n1>>1;
			}
			j = j + n1;

			if (i < j) {
				t1 = x[i];
				x[i] = x[j];
				x[j] = t1;
				t1 = y[i];
				y[i] = y[j];
				y[j] = t1;
			}
		}

		int nHalf = (n>>1);
		int p = 1;
		int bitmask = n/2-1;
		// FFT
		n1 = 0;
		n2 = 1;

		for (int st =0; st < m; st++) {

			i = 0;
			for (int b = 0; b < nHalf; b++) {
				int r = b << (m-1-st);
				r = (r & (bitmask));
				c = cos[r];
				s = sin[r];
				t1 = c*x[i+p] - s*y[i+p];
				t2 = s*x[i+p] + c*y[i+p];
				x[i+p] = x[i] - t1;
				y[i+p] = y[i] - t2;
				x[i] = x[i] + t1;
				y[i] = y[i] + t2; 							
				i++;
				if((i & p) != 0){
					i+=p;
				}
			}
			p = p << 1;
		}
	} 
	
	public void fft3(double[] x, double[] y)
	{
		double c,s,t1,t2,t3,t4;

		int i,j,n1,n2;

		double [] x1,x2,y1,y2;
		
		x1 = new double[n/2];
		x2 = new double[n/2];
		y1 = new double[n/2];
		y2 = new double[n/2];
		
		// Bit-reverse
		j = 0;
		n2 = (n>>1);
		for (i=1; i < n - 1; i++) {
			n1 = n2;
			while ( j >= n1 ) {
				System.out.println();
				j = j - n1;
				n1 = n1>>1;
			}
			j = j + n1;

			if (i < j) {
				if(i<n/2){
					x1[i]=x[j];
					y1[i]=y[j];
				}else{
					x2[i-n/2]=x[j];
					y2[i-n/2]=y[j];
				}
				if(j<n/2){
					x1[j]=x[i];
					y1[j]=y[i];
				}else{
					x2[j-n/2]=x[i];
					y2[j-n/2]=y[i];
				}
			} else if( i == j){
				if(i<n/2){
					x1[i]=x[j];
					y1[i]=y[j];
				}else{
					x2[i-n/2]=x[j];
					y2[i-n/2]=y[j];
				}
			}
		}
		x1[0]=x[0];
		x2[n/2-1]=x[n-1];

		int nHalf = (n>>1);
		int p = 1;
		int bitmask = n/2-1;
		// FFT
		n1 = 0;
		n2 = 1;

		for (int st =0; st < m-1; st++) { /// omit last stage
			i = 0;
			for (int b = 0; b < nHalf/2; b++) {
				int r = b << (m-1-st);
				r = (r & (bitmask));
				c = cos[r];
				s = sin[r];
				///
				t1 = c*x1[i+p] - s*y1[i+p];
				t2 = s*x1[i+p] + c*y1[i+p];
				x1[i+p] = x1[i] - t1;
				y1[i+p] = y1[i] - t2;
				x1[i] = x1[i] + t1;
				y1[i] = y1[i] + t2;
				/// parallel
				t3 = c*x2[i+p] - s*y2[i+p];
				t4 = s*x2[i+p] + c*y2[i+p];
				x2[i+p] = x2[i] - t3;
				y2[i+p] = y2[i] - t4;
				x2[i] = x2[i] + t3;
				y2[i] = y2[i] + t4;
				////
				i++;
				if((i & p) != 0){
					i+=p;
				}
			}
			p = p << 1;
		}
		
		i = 0;
		for (int b = 0; b < nHalf; b++) {
			System.out.println();
			c = cos[b];
			s = sin[b];
			///
			t1 = c*x2[b] - s*y2[b];
			t2 = s*x2[b] + c*y2[b];
			x[b+p] = x1[b] - t1;
			y[b+p] = y1[b] - t2;
			x[b] = x1[b] + t1;
			y[b] = y1[b] + t2;
			
			
			i++;
		}
		/// last stage
	} 




	// Test the FFT to make sure it's working
	public static void main(String[] args) {
		int N = 32;

		FFT fft = new FFT(N);

		//		double[] window = fft.getWindow();
		double[] re = new double[N];
		double[] im = new double[N];

		// Impulse
		//		re[0] = 1; im[0] = 0;
		//		for(int i=1; i<N; i++)
		//			re[i] = im[i] = 0;
		//		beforeAfter(fft, re, im);

		// Nyquist
		//		for(int i=0; i<N; i++) {
		//			re[i] = Math.pow(-1, i);
		//			im[i] = 0;
		//		}
		//		beforeAfter(fft, re, im);

		// Single sin
		for(int i=0; i<N; i++) {
			re[i] = PETrigonometry.cos(2*PI*i / N);
			im[i] = 0;
		}
//		fft.fft3(re, im);
//		for(int i=0; i<N; i++) {
//			re[i] = PETrigonometry.cos(2*PI*i / N);
//			im[i] = 0;
//		}
//		fft.fft3(re,im);
				
		beforeAfter(fft, re, im);

		// Ramp
		//		for(int i=0; i<N; i++) {
		//			re[i] = i;
		//			im[i] = 0;
		//		}
		//		beforeAfter(fft, re, im);
		//
		//		long time = System.currentTimeMillis();
		//		double iter = 30000;
		//		for(int i=0; i<iter; i++)
		//			fft.fft(re,im);
		//		time = System.currentTimeMillis() - time;
		//		System.out.println("Averaged " + (time/iter) + "ms per iteration");
	}

	protected static void beforeAfter(FFT fft, double[] re, double[] im) {
		System.out.println('B');
		printReIm(re, im);
		fft.fft3(re, im);
		System.out.println('A');
		printReIm(re, im);
	}

	protected static void printReIm(double[] re, double[] im) {
		System.out.println('r');
		for(int i=0; i<re.length; i++){
			System.out.print((double)((int)(re[i]*1000))/1000.0);
			System.out.print(' ');
		}
		System.out.println();
		System.out.println('i');
		for(int i=0; i<im.length; i++){
			System.out.print((double)((int)(im[i]*1000))/1000.0);
			System.out.print(' ');
		}

		System.out.println();
	}
}