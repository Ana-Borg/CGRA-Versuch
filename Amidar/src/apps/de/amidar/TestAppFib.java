package de.amidar;

public class TestAppFib {

	public TestAppFib() {
	}

	public int loop() {
		int[] l = new int[47];
		l[0] = 0;
		l[1] = 1;

		for (int i = 2; i < 47; i++)
			l[i] = l[i-1] + l[i-2];

		for (int i = 0; i < 47; i++) {
			System.out.print('l');
			System.out.print('[');
			System.out.print(i);
			System.out.print(']');
			System.out.print(':');
			System.out.print(' ');
			System.out.println(l[i]);
		}
		return 0;
	}

	public static void main(String[] args) {
		TestAppFib t = new TestAppFib();
		//t.loop();
		t.loop();
		t.loop();
		return;
	}
}
