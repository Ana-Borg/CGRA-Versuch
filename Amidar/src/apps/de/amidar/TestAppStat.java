package de.amidar;

public class TestAppStat {

	private static int k;
	private static int[] l;
	public TestAppStat() {
		l = new int[100];
	}

	public int loop() {
		//int[] l = new int[100];
		for (int j = 0; j < 100; j++) {
			l[j] = j;
		}
		System.out.print(new char[]{'p', ':', ' '});
		for (int j = 0; j < 100; j++)
			System.out.println(l[j]);
		return 0;
	}

	public static void main(String[] args) {
		TestAppStat t = new TestAppStat();
		t.loop();
		return;
	}
}
