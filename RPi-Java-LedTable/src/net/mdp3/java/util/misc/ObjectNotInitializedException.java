/**
 * 
 */
package net.mdp3.java.util.misc;

/**
 * Custom exception to be thrown when a required object was not intialized 
 * 
 * @author Mikel
 *
 */
public class ObjectNotInitializedException extends Exception {

	private static final long serialVersionUID = 2993512881656200086L;

	public ObjectNotInitializedException() {
		super();
	}

	/**
	 * @param message
	 */
	public ObjectNotInitializedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ObjectNotInitializedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ObjectNotInitializedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ObjectNotInitializedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
