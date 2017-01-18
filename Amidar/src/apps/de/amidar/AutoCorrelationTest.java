package de.amidar;

public class AutoCorrelationTest {

    public static void main(String[] args) {

	AutoCorrelationTest actest = new AutoCorrelationTest();
	actest.run();

    }

    public void run() {

	int[] input = {1, 2, 3, 4, 5 ,6};

	autoCorrelation(input);

    }

    private int[] autoCorrelation(int[] input) {

	int size = input.length;
	int[] r = new int[size];

	for (int i = 0; i < size; i++) {

	    int sum = 0;

	    for (int j = 0; j < size - i; j++) {
		sum += input[j] * input[j + i];
	    }

	    r[i] = sum;

	}

	return r;

    }

}
