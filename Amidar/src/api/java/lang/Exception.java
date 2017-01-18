package java.lang;

import java.io.Serializable;

public class Exception extends Throwable implements Serializable {

	public Exception() {
		super();
	}

	public Exception(String s) {
		super(s);
	}

	public Exception(String s, Throwable cause) {
		super(s, cause);
	}

	public Exception(Throwable cause) {
		super(cause);
	}

}
