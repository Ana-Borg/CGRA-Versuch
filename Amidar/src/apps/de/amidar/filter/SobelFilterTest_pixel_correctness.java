package de.amidar.filter;

public class SobelFilterTest_pixel_correctness {

    public static void main(String[] args) throws Exception {

    	SobelFilterTest_pixel_correctness sobel = new SobelFilterTest_pixel_correctness();
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
	for(int i=0; i< edgedImg.length; i++){						///correctnesscode
		System.out.println(edgedImg[i]);
	}
    	
    }

}
