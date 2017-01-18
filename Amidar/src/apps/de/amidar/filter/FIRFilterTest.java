package de.amidar.filter;

public class FIRFilterTest {

    public static void main(String[] args) {

	float[] xcoeffs = { 
	    -0.0101070734F, -0.0038223149F, +0.0107194638F, +0.0164538728F,
	    +0.0030315227F, -0.0164538728F, -0.0150066844F, +0.0154689447F,
	    +0.0424433156F, +0.0154689447F, -0.0750277721F, -0.1568445061F,
	    -0.1061032954F, +0.1568445061F, +0.5786338820F, +0.9744945940F,
	    +1.1366177750F, +0.9744945940F, +0.5786338820F, +0.1568445061F,
	    -0.1061032954F, -0.1568445061F, -0.0750277721F, +0.0154689447F,
	    +0.0424433156F, +0.0154689447F, -0.0150066844F, -0.0164538728F,
	    +0.0030315227F, +0.0164538728F, +0.0107194638F, -0.0038223149F,
	    -0.0101070734F,
	};

	FIRFilterTest firfiltertest = new FIRFilterTest();
	firfiltertest.run(xcoeffs);

    }

    public void run(float[] xcoeffs) {

	FIRFilter filter = new FIRFilter(xcoeffs);

	filter.getOutputSample(10000.20000F);
	filter.getOutputSample(10000.20000F);
	filter.getOutputSample(10000.20000F);
	filter.getOutputSample(10000.20000F);
	filter.getOutputSample(10000.20000F);
	filter.getOutputSample(10000.20000F);
	filter.getOutputSample(10000.20000F);

    }

}
