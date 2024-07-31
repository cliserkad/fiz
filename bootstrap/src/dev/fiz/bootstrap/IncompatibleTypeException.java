package dev.fiz.bootstrap;

import java.io.Serial;

public class IncompatibleTypeException extends Exception {

	@Serial
	private static final long serialVersionUID = 20240730L;

	public IncompatibleTypeException(String msg) {
		super(msg);
	}

}
