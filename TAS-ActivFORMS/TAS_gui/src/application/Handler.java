package application;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

public class Handler implements Thread.UncaughtExceptionHandler {
 
	private int uncaught_exception_counter = 0;
	
    private static Logger LOGGER = LoggerFactory.getLogger(Handler.class);
 
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        uncaught_exception_counter++;
        ExceptionCounter.exception_counter++;
        LOGGER.error("Unhandled exception caught! " + ExceptionCounter.exception_counter);
    }
    
    public int getUncaughtExceptionCounter() {
    	return uncaught_exception_counter;
    }
}