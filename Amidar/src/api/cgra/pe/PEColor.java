package cgra.pe;

public class PEColor {
	
	/**
	 * Returns the Y color component for the given RGB values
	 * @param r the red value
	 * @param g the green value
	 * @param b the blue value
	 * @return the Y component as float
	 */
	public static float Y(int r, int g, int b){
		return 0.299F * (float)r + 0.587F * (float)g + 0.114F * (float)b; 
	}
	
	/**
	 * Returns the Cr color component for the given RGB values
	 * @param r the red value
	 * @param g the green value
	 * @param b the blue value
	 * @return the Cr component as float
	 */
	public static float Cr(int r, int g, int b){
		return 128F + -0.16874F * r - 0.33126F * g + 0.5F * b; 
	}
	
	/**
	 * Returns the Cb color component for the given RGB values
	 * @param r the red value
	 * @param g the green value
	 * @param b the blue value
	 * @return the Cb component as float
	 */
	public static float Cb(int r, int g, int b){
		return 128F + 0.5F * r - 0.41869F * g - 0.08131F * b; 
	}
	
}
