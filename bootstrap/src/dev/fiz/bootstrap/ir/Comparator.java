package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.antlr.FizParser;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * All possible comparison operators
 */
public enum Comparator {

	EQUAL,
	MORE_THAN,
	LESS_THAN,
	MORE_OR_EQUAL,
	LESS_OR_EQUAL,
	NOT_EQUAL,
	REF_EQUAL,
	REF_NOT_EQUAL;

	/**
	 * Converts a ComparatorContext into a Comparator. Can fail if the parse tree was generated incorrectly.
	 */
	public static Comparator match(FizParser.ComparatorContext ctx) {
		int token = ctx.getChild(TerminalNode.class, 0).getSymbol().getType();

		return switch(token) {
			case FizParser.EQUAL -> EQUAL;
			case FizParser.MORE_THAN -> MORE_THAN;
			case FizParser.LESS_THAN -> LESS_THAN;
			case FizParser.MORE_OR_EQUAL -> MORE_OR_EQUAL;
			case FizParser.LESS_OR_EQUAL -> LESS_OR_EQUAL;
			case FizParser.NOT_EQUAL -> NOT_EQUAL;
			case FizParser.REF_EQUAL -> REF_EQUAL;
			case FizParser.REF_NOT_EQUAL -> REF_NOT_EQUAL;
			// TODO: better error handling
			default -> throw new IllegalArgumentException("Unexpected value: " + ctx.getText());
		};
	}

}
