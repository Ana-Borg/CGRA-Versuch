package de.amidar.filter;
//import java.awt.image.BufferedImage;
public class SobelFilter_inline {
	private float[] hx = new float[] {
			-1, 0, 1,
            -2, 0, 2,
            -1, 0, 1
            };
	
	private float[] hy = new float[]{
			-1,-2,-1,
            0, 0, 0,
            1, 2, 1
            };
	
	private int kernWidth = 3, kernHeight = 3;
		
	public int[] sobelEdgeDetection(int[] img, int imgWidth, int imgHeight) {
		int[] edged = new int[img.length];
		
        int[] rgbX = new int[3]; 
        int[] rgbY = new int[3];
        
        int halfWidth = kernWidth/2, halfHeight = kernHeight/2, component, i, row, column, imgRGB, r, g, b;
        float sumX, sumY;
        
        int hxlength = hx.length;
         //ignore border pixels strategy
        for(int x = 1; x < imgWidth - 1; x++)
            for(int y = 1; y < imgHeight - 1; y++) { 	
//                convolvePixel(hx, img, imgWidth, imgHeight, x, y, rgbX);
//                convolvePixel(hy, img, imgWidth, imgHeight, x, y, rgbY);
            	
            	//hx, hy
                for(component = 0; component < 3; component++) {
                    sumX = 0;
                    for(i = 0; i < hxlength; i++) {
                       row = (i/kernWidth)-halfWidth;  //current row in kernel
                       column = (i-(kernWidth*(row + 1)))-halfHeight; //current column in kernel
                       
                       //range check
                       if(x-row < 0 || x-row > imgWidth) 
                    	   continue;
                       if(y-column < 0 || y-column > imgHeight) 
                    	   continue;

                       imgRGB = img[(y - column) * imgWidth + x-row];                       
                       sumX = sumX + hx[i]*((imgRGB >> (16 - 8 * component)) & 0xff);
                    }
                    rgbX[component] = (int) sumX;
                }

                for(component = 0; component < 3; component++) {
                    sumY = 0;
                    for(i = 0; i < hxlength; i++) {
                       row = (i/kernWidth)-halfWidth;  //current row in kernel
                       column = (i-(kernWidth*(row + 1)))-halfHeight; //current column in kernel
                       
                       //range check
                       if(x-row < 0 || x-row > imgWidth) 
                    	   continue;
                       if(y-column < 0 || y-column > imgHeight) 
                    	   continue;

                       imgRGB = img[(y - column) * imgWidth + x-row];                       
                       sumY = sumY + hy[i]*((imgRGB >> (16 - 8 * component)) & 0xff);
                    }
                    rgbY[component] = (int) sumY;
                }

                //instead of using sqrt function for eculidean distance
                //just do an estimation
//                int r = abs(rgbX[0]) + abs(rgbY[0]);
//                int g = abs(rgbX[1]) + abs(rgbY[1]);
//                int b = abs(rgbX[2]) + abs(rgbY[2]);
                r = (rgbX[0] < 0 ? -rgbX[0] : rgbX[0]) + (rgbY[0] < 0 ? -rgbY[0] : rgbY[0]);
                g = (rgbX[1] < 0 ? -rgbX[1] : rgbX[1]) + (rgbY[1] < 0 ? -rgbY[1] : rgbY[1]);
                b = (rgbX[2] < 0 ? -rgbX[2] : rgbX[2]) + (rgbY[2] < 0 ? -rgbY[2] : rgbY[2]);
                //range check
                if(r > 255) 
                	r = 255;
                if(g > 255) 
                	g = 255;
                if(b > 255) 
                	b = 255;
                
//                setRGB(edged, imgWidth, x, y, (r<<16)|(g<<8)|b);
                edged[y * imgWidth + x] = (r<<16)|(g<<8)|b;
            }
        return edged;
    }
	
//    private int[] convolvePixel(float[] kernel, int[] img, int imgWidth, int imgHeight, int x, int y, int[] rgb) {
//    	if(rgb == null) 
//    		rgb = new int[3];
//         
//        int halfWidth = kernWidth/2;
//        int halfHeight = kernHeight/2;
//        
//         /*this algorithm pretends as though the kernel is indexed from -halfWidth 
//          *to halfWidth horizontally and -halfHeight to halfHeight vertically.  
//          *This makes the center pixel indexed at row 0, column 0.*/
//        
//        for(int component = 0; component < 3; component++) {
//            float sum = 0;
//            for(int i = 0; i < kernel.length; i++) {
//               int row = (i/kernWidth)-halfWidth;  //current row in kernel
//               int column = (i-(kernWidth*(row + 1)))-halfHeight; //current column in kernel
//               
//               //range check
//               if(x-row < 0 || x-row > imgWidth) 
//            	   continue;
//               if(y-column < 0 || y-column > imgHeight) 
//            	   continue;
//               
//               int imgRGB = getRGB(img, imgWidth, x-row, y-column);
//               
//               sum = sum + kernel[i]*((imgRGB >> (16 - 8 * component)) & 0xff);
//            }
//            rgb[component] = (int) sum;
//        }
//       return rgb;
//    }
//    
//    public int getRGB(int[] img, int imgWidth, int x, int y) {
//    	return img[y * imgWidth + x];
//    }
//    
//    public void setRGB(int[] img, int imgWidth, int x, int y, int rgb) {
//    	img[y * imgWidth + x] = rgb;
//    }
//    
//    private int abs(int v) {
//    	return v < 0 ? -v : v;
//    }
    
//    public int[] getPixels(BufferedImage img) {
//    	int[] result = new int[img.getWidth() * img.getHeight()];
//    	
//    	for(int x = 0 ; x < img.getWidth(); x++) {
//    		for(int y = 0; y < img.getHeight(); y++) {
//    			setRGB(result, img.getWidth(), x, y, img.getRGB(x, y)); 
//    		}
//    	}
//    	
//    	return result;
//    }
//    
//    public void setPixels(int[] pixels, BufferedImage img) {
//    	
//    	for(int x = 0 ; x < img.getWidth(); x++) {
//    		for(int y = 0; y < img.getHeight(); y++) {
//    			img.setRGB(x, y, getRGB(pixels, img.getWidth(), x, y)); 
//    		}
//    	}
//    	
//    }
}
