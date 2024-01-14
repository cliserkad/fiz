package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.antlr.FizParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public enum Operator {

	ADD,
	SUB,
	MUL,
	DIV,
	MOD;

	public static Operator match(FizParser.OperatorContext ctx) {
		// TODO make a better method for extracting the token id / terminal node type
		int token = ctx.getChild(TerminalNode.class, 0).getSymbol().getType();

		return switch(token) {
			case FizParser.ADD -> ADD;
			case FizParser.SUB -> SUB;
			case FizParser.MUL -> MUL;
			case FizParser.DIV -> DIV;
			case FizParser.MOD -> MOD;
			// TODO: better error handling
			default -> throw new IllegalArgumentException("Unexpected value: " + ctx.getText());
		};
	}

}
