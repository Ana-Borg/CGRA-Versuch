package cgra.pe;

public class PEMpeg {
	
	static float[] CA = { -0.5144957554270f, -0.4717319685650f, -0.3133774542040f, -0.1819131996110f,
            -0.0945741925262f, -0.0409655828852f, -0.0141985685725f, -0.00369997467375f,0,0,0,0,0,0,0,0,0,0 };
    static float[] CS = { 0.857492925712f, 0.881741997318f, 0.949628649103f, 0.983314592492f, 0.995517816065f,
            0.999160558175f, 0.999899195243f, 0.999993155067f,0,0,0,0,0,0,0,0,0,0,0,0 };
	
	public static float leftChannel(float mid, float side){
		return (mid + side)*  0.707106781f;
	}
	
	public static float rightChannel(float mid, float side){
		return (mid - side)*  0.707106781f;
	}
	
	public static float butterfly1(float in1, float in2, int index){
		return (in1 * CS[index]) - (in2 * CA[index]);
	}
	
	public static float butterfly2(float in1, float in2, int index){
		return (in2 * CS[index]) + (in1 * CA[index]);
	}

}
