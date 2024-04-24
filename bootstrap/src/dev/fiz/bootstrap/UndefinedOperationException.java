package dev.fiz.bootstrap;

/**
 * Exception thrown when an operation is undefined.
 * Some parseable code is not valid and will throw this exception.
 */
public class UndefinedOperationException extends Exception {

	public UndefinedOperationException(final String message) {
		super(message);
	}

}
