package de.amidar;

/**
 * Code taken from http://ptolemy.eecs.berkeley.edu/eecs20/week12/implementation.html Date: 21.01.14
 * @author Uni berkley + Lukas Jung
 *
 */
public class FIR {
	private int length;
	private float[] delayLine;
	private float[] impulseResponse;
	private int count = 0;

	FIR(float[] coefs) {
		length = coefs.length;
		impulseResponse = coefs;
		delayLine = new float[length];
	}
	float getOutputSample(float inputSample) {
		delayLine[count] = inputSample;
		float result = 0.0f;
		int index = count;
		for (int i=0; i<length; i++) {
			result += impulseResponse[i] * delayLine[index--];
			if (index < 0) index = length-1;
		}
		if (++count >= length) count = 0;
		return result;
	}
	
	float getOutputSample2(float inputSample) {
		delayLine[count] = inputSample;
		float result = 0.0f;
		int index = count;
		for (int i=0; i<length; i++) {
			result += impulseResponse[i] * delayLine[index--];
			if (index < 0) index = length-1;
		}
		if (++count >= length) count = 0;
		return result;
	}
	
	
	public static void main(String[] args){
		float[] co = {1,0.8f,0.5f,0.1f,-0.1f,-0.2f,-0.1f,0f};
		int coLength = co.length;
		

		float[] output = new float[100];
		int oLength = output.length;
		float[] del = new float[co.length];
		int cnt = 0;
		
//		FIR filter = new FIR(co);		
//		for(int i = 0; i< oLength; i++){
//			del[cnt] = i%2;
//			float result = 0.0f;
//			int index = cnt;
//			for (int j=0; j<coLength; j++) {
//				result += co[j] * del[index--];
//				if (index < 0) index = coLength-1;
//			}
//
//			if (++cnt >= coLength) cnt = 0;
//			output[i] = result;
//		}

		
		
		System.out.println();
		
//		for(int i = 0; i< oLength; i++){
//			del[cnt] = i%2;
//			float result00 = co[0]*del[cnt]+co[1]*del[(coLength+cnt-1) % coLength];
//			float result01 = co[2]*del[(coLength+cnt-2) % coLength]+co[3]*del[(coLength+cnt-3) % coLength];
//			float result02 = co[4]*del[(coLength+cnt-4) % coLength]+co[5]*del[(coLength+cnt-5) % coLength];
//			float result03 = co[6]*del[(coLength+cnt-6) % coLength]+co[7]*del[(coLength+cnt-7) % coLength];
//			float result10 = result00 + result01;
//			float result11 = result02 + result03;
//			output[i] = result10 + result11;
//			cnt = (cnt+1)%coLength;
//		}
		
		
		for(int i = 0; i< oLength; i++){
			del[7] = del[6];
			del[6] = del[5];
			del[5] = del[4];
			del[4] = del[3];
			del[3] = del[2];
			del[2] = del[1];
			del[1] = del[0];
			del[0] = i%2;
			float result00 = co[0]*del[0]+co[1]*del[1];
			float result01 = co[2]*del[2]+co[3]*del[3];
			float result02 = co[4]*del[4]+co[5]*del[5];
			float result03 = co[6]*del[6]+co[7]*del[7];
			float result10 = result00 + result01;
			float result11 = result02 + result03;
			output[i] = result10 + result11;
			cnt = (cnt+1)%coLength;
		}		
		
		for(int i = 0; i< oLength; i++){
			System.out.print(output[i]);
			System.out.print(',');
		}
	}

}
