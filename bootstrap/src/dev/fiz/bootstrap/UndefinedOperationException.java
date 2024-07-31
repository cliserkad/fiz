package dev.fiz.bootstrap;

import java.io.Serial;

/**
 * Exception thrown when an operation is undefined.
 * Some parseable code is not valid and will throw this exception.
 */
public class UndefinedOperationException extends Exception {

	@Serial
	private static final long serialVersionUID = 20240730L;

	public UndefinedOperationException(final String message) {
		super(message);
	}

}
