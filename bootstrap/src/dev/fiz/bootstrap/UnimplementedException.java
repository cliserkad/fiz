package dev.fiz.bootstrap;

import java.io.Serial;

/**
 * Thrown when a feature is not yet implemented.
 */
public class UnimplementedException extends Exception {

	@Serial
	private static final long serialVersionUID = 20240730L;

	public UnimplementedException(String msg) {
		super(msg);
	}

}
