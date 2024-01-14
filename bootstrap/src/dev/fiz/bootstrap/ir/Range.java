package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.antlr.FizParser;

public class Range {

	public static final int DEFAULT_MIN = 0;

	public final Pushable min;
	public final Pushable max;

	public Range(final FizParser.RangeContext ctx, final Actor actor) throws Exception {
		if(ctx.expression().size() > 1)
			min = new Expression(ctx.expression(0), actor);
		else
			min = new Literal<>(DEFAULT_MIN);

		max = new Expression(ctx.expression(ctx.expression().size() - 1), actor);
	}

}
