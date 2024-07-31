package dev.fiz.bootstrap;

import java.io.Serial;

public class SymbolResolutionException extends Exception {

	@Serial
	private static final long serialVersionUID = 20240730L;

	public static final String PREPEND = "Fail to resolve symbol ";

	public SymbolResolutionException(final String symbolName) {
		super(PREPEND + symbolName);
	}

}
