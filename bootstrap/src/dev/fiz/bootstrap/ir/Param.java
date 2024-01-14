package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.Details;

public class Param extends Details {

	public final FizParser.ValueContext defaultValue;

	public Param(Details details, FizParser.ValueContext defaultValue) {
		super(details);
		this.defaultValue = defaultValue;
	}

	public Param(Details details) {
		this(details, null);
	}

}
