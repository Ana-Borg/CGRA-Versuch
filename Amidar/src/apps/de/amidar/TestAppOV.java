package de.amidar;

public class TestAppOV {

	private int l;
	private static int zz;

	public TestAppOV() {
		l = 0;
	}

	public int loop() {
		int p = 0;
		//Blubb s = new Blubb();
		//Blubb t = new Blubb();
		//Blubb u;
		//s.l = 0.1;
		//t.l = 0.1;

		for (int j = 0; j < 10; j++) {
			/*for (int k = 0; k < 10; k++) {
				p = p + 1;
			}*/
			//u = s;
			//s = t;
			//t = u;/**/
			for (int i = 0; i < 10; i++) {
				l++;
				//zz++;
			}
		}
		System.out.print(new char[]{'s', ':', ' '});
		System.out.println(l);
		//System.out.println(t.l);
		//System.out.print(new char[]{'t', ':', ' '});
		//System.out.println(t.l);
		return 0;
	}

	public static void main(String[] args) {
		TestAppOV t = new TestAppOV();
		t.loop();
		return;
	}
}
