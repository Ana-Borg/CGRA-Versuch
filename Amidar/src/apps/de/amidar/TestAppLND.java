package de.amidar;

public class TestAppLND {

	public TestAppLND() { }

	public int loop() {
		float l = 0.1f;
		int p = 0;
		for (int j = 0; j < 10; j++) {
			for (int k = 0; k < 10; k++) {
				p = p + 1;
			}
			for (int i = 0; i < 10; i++) {
				l = l + 0.1f;
			}
		}
		System.out.print(new char[]{ 'l', ':', ' ' });
		System.out.println(l);
		System.out.print(new char[]{ 'p', ':', ' ' });
		System.out.println(p);
		return (int)l;
	}

	public static void main(String[] args) {
		TestAppLND t = new TestAppLND();
		t.loop();
		return;
	}
}
