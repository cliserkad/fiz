package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.antlr.FizParser;

public class WhileLoop extends Conditional {

	public WhileLoop(Condition condition, Actor actor) {
		super(condition, actor);
	}

	@Override
	public void defineOnTrue(FizParser.ConditionalContext ctx, Actor actor) throws Exception {
		actor.visitLabel(labelSet.onTrue);
		actor.unit.consumeBlock(ctx.r_while().block(), actor);
		actor.visitJumpInsn(GOTO, labelSet.check);
	}

}
