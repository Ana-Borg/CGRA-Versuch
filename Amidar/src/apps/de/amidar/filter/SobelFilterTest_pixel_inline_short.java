package de.amidar.filter;


public class SobelFilterTest_pixel_inline_short {

    public static void main(String[] args) throws Exception {

    	SobelFilterTest_pixel_inline_short sobel = new SobelFilterTest_pixel_inline_short();
    	sobel.run();

    }

    public void run() throws Exception {

        int[] img = {
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1
        };

    	int[] edgedImg;
	int X = 17;
	int Y = 3;
    	
    	SobelFilter_inline filter = new SobelFilter_inline();

	edgedImg = filter.sobelEdgeDetection(img, X, Y);
    	
    }

}
