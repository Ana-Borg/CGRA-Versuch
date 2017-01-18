package de.amidar;

public class TestAppIF {

	private int p;
	private int q;

	public TestAppIF() {
		p = 0;
		q = 0;
	}

	public int loop() {
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 3; i++) {
				if (i % 3 == 0)
					p = p + 1;
				else
					p = p - 1;
			}
			q += p;
		}
		System.out.print(new char[]{'q', ':', ' '});
		System.out.println(q);
		return 0;
	}

	public static void main(String[] args) {
		TestAppIF t = new TestAppIF();
		t.loop();
		t.p = 0;
		t.q = 0;
		t.loop();
		return;
	}
}
