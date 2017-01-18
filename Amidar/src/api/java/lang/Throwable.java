package java.lang;

import java.io.Serializable;

public class Throwable extends Object implements Serializable {

   private String message;

	private Throwable cause;
   
   private static final long serialVersionUID = -3042686055658047285L;
   
   public Throwable() {

   }

   public Throwable (String s) {
      message = s;
   }

	public Throwable(String s, Throwable cause) {
		this.message = s;
		this.cause = cause;
	}

	public Throwable(Throwable cause) {
		this.cause = cause;
	}

//   public native Throwable fillInStackTrace();


   public String getMessage() {
      return message;
   }


   public String getLocalizedMessage() {
      return getMessage();
   }

   public native void printStackTrace();

/*
   public void printStackTrace(PrintStream s) {

   }
*/
/*
   public void printStackTrace(PrintWriter s) {

   }
*/
/*
   public String toString() {

   }

*/
}
