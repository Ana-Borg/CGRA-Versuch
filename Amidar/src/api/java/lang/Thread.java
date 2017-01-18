package java.lang;

public class Thread extends Object implements Runnable {
	/* Constant declaration */  
    public final static int MIN_PRIORITY = 1;
    
    public final static int NORM_PRIORITY = 5;
   
    public final static int MAX_PRIORITY = 10;
    
    protected Runnable target;

    protected String threadName;
    
    private static int threadNumber;
    private static synchronized int nextThreadNumber () {
        return threadNumber++;
    }
    
    public static native Thread currentThread();
    
    public static native void yield();
    
    public static native void sleep(long millis) throws InterruptedException;
    
    public static void sleep(long millis, int nanos)
    throws InterruptedException {
        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }

        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            millis++;
        }

        sleep(millis);
    }
    
    /* The constructors */
    public Thread (Runnable target) {
        int priority;
        Thread parent;
      
        parent = currentThread();
        priority = parent.getPriority();
    
        this.target=target;
        
        register(priority);
    }
    
    public Thread () {
        this (null);
    }
    
    private native void register(int priority);
    
    public native void start();
    
    public void run() {
        if (target != null) {
            target.run();
        }
    }
    
    public native void interrupt();
    
    public static boolean interrupted() {
        return currentThread().isInterrupted(true);
    }
    
    public boolean isInterrupted() {
        return isInterrupted(false);
    }
    
    private native boolean isInterrupted(boolean ClearInterrupted);
    
    public final native boolean isAlive();

    public final native void setPriority(int newPriority);
    
    public final native int getPriority();

    public final void setName(String name) {
        threadName = name;
	    setInternalName(name);
    }
    
    private final native void setInternalName(String name);

    public final String getName() {
        return threadName;
    }
    
    /**
     * Da System.currentTimeMillis() noch nicht implementiert ist, können die folgenden 3 join-Methoden nicht richtig funktionieren.
     */
    public final synchronized void join(long millis)
    throws InterruptedException {
        long base = System.currentTimeMillis();
        long now = 0;

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (millis == 0) {
            while (isAlive()) {
                wait(0);
            }
        } else {
            while (isAlive()) {
                long delay = millis - now;
                if (delay <= 0) {
                    break;
                }
                wait(delay);
                now = System.currentTimeMillis() - base;
            }
        }
    }

    public final synchronized void join(long millis, int nanos)
    throws InterruptedException {

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }

        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            millis++;
        }

        join(millis);
    }

    public final void join() throws InterruptedException {
        join(0);
    }	
}
