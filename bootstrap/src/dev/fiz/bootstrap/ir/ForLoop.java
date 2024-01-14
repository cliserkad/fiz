package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.InternalName;

public class ForLoop extends Conditional {

	public final Variable iterator;

	/**
	 * Sets up the condition, gives it to super, then extracts fields from super's condition.
	 */
	public ForLoop(final FizParser.For_loopContext forLoop, final Actor actor) throws Exception {
		super(setUpForLoop(forLoop, actor), actor);
		iterator = (Variable) condition.a;
	}

	public static Condition setUpForLoop(final FizParser.For_loopContext forLoop, final Actor actor) throws Exception {
		final Range r = new Range(forLoop.range(), actor);
		final Variable increment = actor.unit.getCurrentScope().newVariable(forLoop.ID().getText(), InternalName.INT, true);
		r.min.push(actor);
		increment.assign(InternalName.INT, actor);
		return new Condition(increment, r.max, Comparator.LESS_THAN);
	}

	@Override
	public void defineOnTrue(final FizParser.ConditionalContext ctx, final Actor actor) throws Exception {
		actor.visitLabel(labelSet.onTrue);
		actor.unit.consumeBlock(ctx.for_loop().block(), actor);
		new Expression(iterator, new Literal<>(1), Operator.ADD).push(actor);
		iterator.assign(InternalName.INT, actor);
		actor.visitJumpInsn(GOTO, labelSet.check);
	}

}
