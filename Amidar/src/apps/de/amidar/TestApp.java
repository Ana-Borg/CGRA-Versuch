package de.amidar;

class Blafasel {
	public static int TADA;
	public int t1;
	public int t2;
	public Blafasel() {
		TADA = 10;
		t1 = 1;
		t2 = 2;
	}
}

class Blubbclass {
	public int l;
	public Blubbclass() { l = 0; }
}

public class TestApp {

	private boolean field;
	private final float lll = 10;
	private int tada;
	private int[] arr;
	private Blafasel blubb;


	public TestApp() {
		field = false;
		tada = 17;
		blubb = new Blafasel();
		arr = new int[100];
	}

	public int run(boolean a, boolean b, boolean c) {
		int j = 1;
		for (int i = 0; i < 100; i++) {
			if (field) {
				if (c) {
					j = j *tada;
					//continue;
				}
				else
					j = j *i;

				//arr[i] = j + blubb.TADA;
				tada = tada * tada;
				tada = j * tada;
				j = tada * 27 / i;
				j--;
			}
			/*else {
				if (b) {
					if (c)
						j = j *i;
					else
						j = j *j;
				}
				else
					j = j *i;
			}*/
		}
		return j;
	}

	public int loops() {
		int l = 0;

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j ++) {
				for (int k = 0; k < 10; k++) {
					l = i * j * k;
				}
			}
		}

		return l;
	}

	public int loop() {
		int l = 0;
		for (int i = 0; i < 100; i++) {
			l = l + 1 * (l + 1);
		}
		return l;
	}

	public int loop2() {
		int l = 0;
		int k = 0;
		Blubbclass b = new Blubbclass();
		Blubbclass c = new Blubbclass();
		Blubbclass d = new Blubbclass();
		for (int i = 0; i < 100; i++) {
			b.l = b.l + 1;
			if (i == 0)
				b = c;
			else
				b = d;
			k = b.l + 4;
			b.l = k * 7;
		}
		return l;
	}

	public static void main(String[] args) {
		TestApp t = new TestApp();
		boolean a, b, c;

		a = b = c = false;

		if (args.length < 4)
			a = true;

		if (args.length > 3)
			b = true;
		else
			c = false;

		//t.run(a, b, c);
		//t.loops();
		//t.loop();
		t.loop2();
		return;
	}
}
