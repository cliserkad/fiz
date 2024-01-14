package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.ExternalMethodRouter;
import dev.fiz.bootstrap.names.CommonText;

public class Assertion extends Conditional {

	public Assertion(Condition condition, Actor actor) {
		super(condition, actor);
	}

	@Override
	public void defineOnTrue(FizParser.ConditionalContext ctx, Actor actor) throws Exception {
		actor.visitLabel(labelSet.onTrue);
		if(actor.unit.hasConstant("ASSERTION_PASS")) {
			actor.unit.getConstant("ASSERTION_PASS").push(actor);
			ExternalMethodRouter.PRINT_MTD.withOwner(actor.unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC).invoke(actor);
		}
		actor.visitJumpInsn(GOTO, labelSet.exit); // jump over the false instructions
	}

	@Override
	public void defineOnFalse(FizParser.ConditionalContext ctx, Actor actor) throws Exception {
		actor.visitLabel(labelSet.onFalse);
		// push the text of the assertion condition
		String msg;
		if(ctx.assertion().condition().getText().equals(CommonText.KEYWORD_FALSE))
			msg = "Failed assertion of false. The program likely reached supposedly unreachable code.";
		else
			msg = "Failed assertion with condition " + ctx.assertion().condition().getText();
		new Literal<>(msg).push(actor);
		// print the text of the assertion condition to the error stream
		ExternalMethodRouter.ERROR_MTD.withOwner(actor.unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC).invoke(actor);
	}

}
