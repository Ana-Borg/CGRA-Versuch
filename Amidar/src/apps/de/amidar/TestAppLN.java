package de.amidar;

public class TestAppLN {

	public TestAppLN() { }

	public int loop() {
		int l = 0;
		int p = 0;
		for (int j = 0; j < 10; j++) {
			for (int k = 0; k < 10; k++) {
				p = p + 1;
			}
			for (int i = 0; i < 10; i++) {
				l++;
			}
		}
		return l;
	}

	public static void main(String[] args) {
		TestAppLN t = new TestAppLN();
		t.loop();
		return;
	}
}
