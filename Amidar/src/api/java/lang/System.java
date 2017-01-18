package java.lang;


import java.io.PrintStream;
//import java.io.ConsoleOutputStream;

public final class System extends Object {
   
	public static final PrintStream out = new PrintStream();
	public static final PrintStream err = out;
	
	public static native int identityHashCode(Object x);
	
	public static native long currentTimeMillis();
	
	public static native void arraycopy(Object src, int src_position, Object dest, int dst_position, int length);
}
