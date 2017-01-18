package de.amidar;


public class TestSimple {

	private boolean field;
	private final float lll = 10;
	private int tada;
	private int[] arr;


	public TestSimple() {
		field = false;
		tada = 17;
		arr = new int[100];
	}

	public int run(boolean a, boolean b, boolean c) {
		int j = 1;
		for (int i = 0; i < 100; i++)
			j = j + 1;
		return j;
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

		t.run(a, b, c);
		return;
	}
}
