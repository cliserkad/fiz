package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.CompilationUnit;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.ExternalMethodRouter;
import dev.fiz.bootstrap.names.CommonText;
import dev.fiz.bootstrap.names.InternalName;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Assertion extends Conditional {

	public Assertion(Condition condition, Actor actor) {
		super(condition, actor);
	}

	@Override
	public void defineOnTrue(FizParser.ConditionalContext ctx, Actor actor) throws Exception {
		actor.visitLabel(labelSet.onTrue);
		actor.visitJumpInsn(GOTO, labelSet.exit); // jump over the false instructions
	}

	@Override
	public void defineOnFalse(FizParser.ConditionalContext ctx, Actor actor) throws Exception {
		actor.visitLabel(labelSet.onFalse);
		StringTemplate debug = new StringTemplate();
		// push the text of the assertion condition
		if(ctx.assertion().condition().getText().equals(CommonText.KEYWORD_FALSE))
			debug.add("Failed \"assert false\". The program likely reached supposedly unreachable code.");
		else {
			debug.add("Failed Assertion in " + actor.unit.unitName() + " @" + ctx.getStart().getLine() + ";" + ctx.getStart().getCharPositionInLine() + "\n" + "literal text: " + actor.unit.commonTokenStream.getText(ctx.getStart(), ctx.getStop()) + "\n" + prettyPrint(condition.toString()));
			if(condition.a != null)
				debug.add("a Eval : ").add(condition.a).add("\n");
			if(condition.b != null)
				debug.add("b Eval : ").add(condition.b).add("\n");
		}
		debug.push(actor);
		// print the text of the assertion condition to the error stream
		ExternalMethodRouter.ERROR_MTD.withOwner(actor.unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC).invoke(actor);
	}

	public static String prettyPrint(String debug) {
		StringBuilder bldr = new StringBuilder(debug.length());
		int indent = 0;
		for(int i = 0; i < debug.length(); i++) {
			bldr.append(debug.charAt(i));
			if(debug.charAt(i) == '{') {
				indent++;
			} else if(debug.charAt(i) == '\n') {
				if(debug.length() > i + 1 && debug.charAt(i + 1) == '}') {
					indent--;
				}
				bldr.append("\t".repeat(Math.max(0, indent)));
			}
		}
		bldr.append("\n");
		return bldr.toString();
	}

}
