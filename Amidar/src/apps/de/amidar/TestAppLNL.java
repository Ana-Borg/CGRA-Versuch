package de.amidar;

public class TestAppLNL {

	public TestAppLNL() { }

	public int loop() {
		long l = 0;
		int p = 0;
		for (int j = 0; j < 10; j++) {
			for (int k = 0; k < 10; k++) {
				p = p + 1;
			}
			for (int i = 0; i < 10; i++) {
				l++;
			}
		}
		System.out.print(new char[]{ 'l', ':', ' ' });
		System.out.println(l);
		System.out.print(new char[]{ 'p', ':', ' ' });
		System.out.println(p);
		return (int)l;
	}

	public static void main(String[] args) {
		TestAppLNL t = new TestAppLNL();
		t.loop();
		return;
	}
}
