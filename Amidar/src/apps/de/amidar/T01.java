package de.amidar;
import cgra.pe.*;

public class T01 {
	double [] a = {1,2,3,4,5,6,7,8,9};
	double [] b = {9,8,7,6,5,4,3,2,1};
	int length = a.length;

	/**
	 * Calculate scalar product of a and b
	 */
	public static void main(String[] args) {
		T01 dut = new T01();
		dut.calc();
	}

	public void calc(){
		double product = 0;
		
		float aa = 0;
		int x = 6, y =16, z = 0;
		long k=(1l<<32)+2 , l=1l<<32 , m=0;
//		float bb = 0; 
//		aa = PETrigonometry.cos(0.4234f);
//		bb = PETrigonometry.cos(0.4234f);
//		System.out.println(aa*aa+bb*bb);
	
		for( int i = 0; i<length; i++){
			product += a[i]*b[i];
			aa = PETrigonometry.sin(0.432f);
//			z = (x + y)>>1;
			z = PESimple.average(x, y);
			m = PESimple64.average(k, l);
		}
		System.out.println(z);
		System.out.println(m);
		System.out.println(aa);
		SuccessPrinter sp = new SuccessPrinter(new char [] {'T', '0', '1'});
		sp.assertInt((int)product, 165);
		sp.printResult();
	}	

}
