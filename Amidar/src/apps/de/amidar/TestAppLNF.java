package de.amidar;

public class TestAppLNF {

	public TestAppLNF() { }

	public int loop() {
		float l = 100.1f;
		float m = 1.1f;
		int p = 0;
		for (int j = 0; j < 10; j++) {
			for (int k = 0; k < 9; k++) {
				p = p + 1;
			}
			for (int i = 0; i < 11; i++) {
				l = -l / m;
			}
		}
		System.out.print(new char[]{ 'l', ':', ' ' });
		System.out.println(l);
		System.out.print(new char[]{ 'p', ':', ' ' });
		System.out.println(p);
		return 0;
	}

	public static void main(String[] args) {
		TestAppLNF t = new TestAppLNF();
		t.loop();
		return;
	}
}
