package java.io;

public class PrintStream {//extends FilterOutputStream {
    
    public native void print(boolean b);
    
    public native void print(char c);
    
    public native void print(char s[]);
    
    public native void print(int i);
    
    public native void print(long l);
    
    public native void print(float f);
    
    public native void print(double d);
    
    public native void println();
    
    public native void println(boolean b);
    
    public native void println(char c);
    
    public native void println(char s[]);
    
    public native void println(int i);
    
    public native void println(long l);
    
    public native void println(float f);
    
    public native void println(double d);
    
    public native void println(String S);
    
    
    /*private boolean autoFlush = false;
    private boolean trouble = false;

    private OutputStreamWriter charOut;

    public PrintStream(OutputStream out) {
	this(out, false);
    }

    public PrintStream(OutputStream out, boolean autoFlush) {
	super(out);
	if (out == null) {
	    throw new NullPointerException("Null output stream");
	}
	this.autoFlush = autoFlush;
	this.charOut = new OutputStreamWriter(this);
    }

    private void ensureOpen() throws IOException {
	if (out == null)
	    throw new IOException("Stream closed");
    }

    public void flush() {
	synchronized (this) {
	    try {
		ensureOpen();
		out.flush();
	    }
	    catch (IOException x) {
		trouble = true;
	    }
	}
    }

    private boolean closing = false;

    public void close() {
	synchronized (this) {
	    if (! closing) {
		closing = true;
		try {
		    out.close();
		}
		catch (IOException x) {
		    trouble = true;
		}
		charOut = null;
		out = null;
	    }
	}
    }

    public boolean checkError() {
	if (out != null)
	    flush();
	return trouble;
    }

    protected void setError() {
	trouble = true;
    }

    public void write(int b) {
	try {
	    synchronized (this) {
		ensureOpen();
		out.write(b);
		if ((b == '\n') && autoFlush)
		    out.flush();
	    }
	}
	catch (IOException x) {
	    trouble = true;
	}
    }

    public void write(byte buf[], int off, int len) {
	try {
	    synchronized (this) {
		ensureOpen();
		out.write(buf, off, len);
		if (autoFlush)
		    out.flush();
	    }
	}
	catch (IOException x) {
	    trouble = true;
	}
    }


    private void write(char buf[]) {
	try {
	    synchronized (this) {
		ensureOpen();
		charOut.write(buf);
		charOut.flushBuffer();
		if (autoFlush) {
		    for (int i = 0; i < buf.length; i++)
			if (buf[i] == '\n')
			    out.flush();
		}
	    }
	}
	catch (IOException x) {
	    trouble = true;
	}
    }

    private void write(String s) {
	try {
	    synchronized (this) {
		ensureOpen();
		charOut.write(s);
		charOut.flushBuffer();
		if (autoFlush && (s.indexOf('\n') >= 0))
		    out.flush();
	    }
	}
	catch (IOException x) {
	    trouble = true;
	}
    }

    private void newLine() {
	try {
	    synchronized (this) {
		ensureOpen();
		out.write('\n');
		charOut.flushBuffer();
		if (autoFlush)
		    out.flush();
	    }
	}
	catch (IOException x) {
	    trouble = true;
	}
    }
    
    public void print(boolean b) {
	write(b ? "true" : "false");
    }

    public void print(char c) {
	write(String.valueOf(c));
    }

    public void print(int i) {
	write(String.valueOf(i));
    }

    public void print(long l) {
	write(String.valueOf(l));
    }

    public void print(float f) {
	write(String.valueOf(f));
    }

    public void print(double d) {
	write(String.valueOf(d));
    }

    public void print(char s[]) {
	write(s);
    }

   public void print(String s) {
	if (s == null) {
	    s = "null";
	}
	write(s);
    }

    public void print(Object obj) {
	write(String.valueOf(obj));
    }

    public void println() {
	newLine();
    }

    public void println(boolean x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    public void println(char x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    public void println(int x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    public void println(long x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    public void println(float x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    public void println(double x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    public void println(char x[]) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    public void println(String x) {
	synchronized (this) {
		print(x);
	    newLine();
	}
    }

    public void println(Object x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }*/

}
