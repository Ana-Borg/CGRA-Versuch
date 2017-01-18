package de.amidar;

class JungTestLong{
	
	public static void main(String[] args){
		int[] d = {1,2,3,4,5,6,7,8,9,0};
		int[] vektor = {0,0,0,0,0,0,0,0,0,0};
		int[] arr = new int[4];
		for (int i = 0; i<100; i++){
			
			for(int j= 0; j<10; j++){
				vektor[j]=i+j;
				vektor[j]= vektor[j]*d[j];
			}
//			doNothing();		
//			arr[1];

//			if (vektor[0]-2 == 0){	
				for(int j= 0; j<20; j++){
					int k = j*j;
				}
//			}
		}
	}

	public static void doNothing(){
		int i = 1;
	}
}
