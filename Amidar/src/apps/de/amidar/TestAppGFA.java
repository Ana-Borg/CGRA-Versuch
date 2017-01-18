package de.amidar;

public class TestAppGFA {

	private int[] p;
	private int q;

	public TestAppGFA() {
		p = new int[46];
		p[0] = 0;
		p[1] = 1;
		q = 0;
	}

	public int loop() {
		for (int j = 2; j < 46; j++) {
			p[j] = p[j-1] + p[j-2];
		}
		System.out.print(new char[]{'p', ':', ' '});
		for (int i = 0; i < 46; i++)
			System.out.println(p[i]);
		return 0;
	}

	public static void main(String[] args) {
		TestAppGFA t = new TestAppGFA();
		t.loop();
		t.loop();
		return;
	}
}
