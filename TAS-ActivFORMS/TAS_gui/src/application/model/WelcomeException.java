package application.model;

@SuppressWarnings("serial")
public class WelcomeException extends Exception{

	
	private static int count = 0;
	private static final Object countLock = new Object();
	
	public WelcomeException(String mssg) {
		super(mssg);
		synchronized(countLock) {
			count++;
		}
	}
	
	public static int getCount() {
		System.out.println("NÚMERO DE EXCEPTIONS " + count);
		return count;
	}
}
