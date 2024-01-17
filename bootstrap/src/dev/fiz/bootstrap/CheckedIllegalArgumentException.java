package dev.fiz.bootstrap;

/**
 * IllegalArgumentException that is checked.
 */
public class CheckedIllegalArgumentException extends Exception {

	public CheckedIllegalArgumentException(final Object argument, final String message) {
		super("IllegalArgument: " + argument + " " + message);
	}

}
