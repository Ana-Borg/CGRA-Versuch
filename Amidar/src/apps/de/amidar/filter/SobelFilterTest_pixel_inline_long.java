package de.amidar.filter;

public class SobelFilterTest_pixel_inline_long {

    public static void main(String[] args) throws Exception {

    	SobelFilterTest_pixel_inline_long sobel = new SobelFilterTest_pixel_inline_long();
    	sobel.run();

    }

    public void run() throws Exception {

        int[] img = {
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1
        };

	int[] edgedImg;
	int X = 18;
	int Y = 3;
    	
    	SobelFilter_inline filter = new SobelFilter_inline();

	edgedImg = filter.sobelEdgeDetection(img, X, Y);
    	
    }

}
