package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.antlr.FizParser;

public class IfElse extends Conditional {

	public IfElse(Condition condition, Actor actor) {
		super(condition, actor);
	}

	@Override
	public void defineOnTrue(FizParser.ConditionalContext ctx, Actor actor) throws Exception {
		actor.visitLabel(labelSet.onTrue);
		if(ctx.r_if().statement() != null)
			actor.unit.consumeStatement(ctx.r_if().statement(), actor);
		else if(ctx.r_if().block() != null)
			actor.unit.consumeBlock(ctx.r_if().block(), actor);
		else
			throw new IllegalArgumentException("Missing block for if statement");
		actor.visitJumpInsn(GOTO, labelSet.exit);
	}

	@Override
	public void defineOnFalse(FizParser.ConditionalContext ctx, Actor actor) throws Exception {
		actor.visitLabel(labelSet.onFalse);
		if(ctx.r_if().r_else() != null) {
			if(ctx.r_if().r_else().statement() != null)
				actor.unit.consumeStatement(ctx.r_if().r_else().statement(), actor);
			else if(ctx.r_if().r_else().block() != null)
				actor.unit.consumeBlock(ctx.r_if().r_else().block(), actor);
			else
				throw new IllegalArgumentException("Missing block for else clause of if statement");
		}
	}

}
