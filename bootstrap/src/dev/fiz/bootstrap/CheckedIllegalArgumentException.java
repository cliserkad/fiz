package dev.fiz.bootstrap;

import java.io.Serial;

/**
 * IllegalArgumentException that is checked.
 */
public class CheckedIllegalArgumentException extends Exception {

	@Serial
	private static final long serialVersionUID = 20240730L;

	public CheckedIllegalArgumentException(final Object argument, final String message) {
		super("IllegalArgument: " + argument + " " + message);
	}

}
