package de.amidar;

public class TestAppA {

	public int l;
	public int i;

	public TestAppA() {
	}

	public int loop() {
		int p = 0;
		int r = 0;
		int t = 0;
		int[] l = new int[2];

		for (int j = 0; j < 40; j++) {
			for (int i = 0; i < 2; i++) {
				l[i]++;
			}
		}
		System.out.print(new char[]{'l', ':', ' '});
		System.out.println(l[0]);
		System.out.print(new char[]{'l', ':', ' '});
		System.out.println(l[1]);
		return 0;
	}

	public static void main(String[] args) {
		TestAppA t = new TestAppA();
		t.loop();
		return;
	}
}
