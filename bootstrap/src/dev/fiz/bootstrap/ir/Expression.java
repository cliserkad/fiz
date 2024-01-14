package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.InternalName;

public class Expression extends BasePushable {

	Pushable a;
	Pushable b;
	Operator opr;

	public Expression(Pushable a, Pushable b, Operator opr) {
		if(!a.isBaseType() || !b.isBaseType())
			throw new IllegalArgumentException("Expressions may only contain BaseTypes");
		this.a = a;
		this.b = b;
		this.opr = opr;
	}

	public Expression(FizParser.ExpressionContext ctx, Actor actor) throws Exception {
		this.a = Pushable.parse(actor, ctx.value());
		if(ctx.expression() != null)
			this.b = new Expression(ctx.expression(), actor);
		else
			this.b = null;
		if(ctx.operator() != null)
			opr = Operator.match(ctx.operator());
		else
			opr = null;
		if(opr != null && b == null) {
			throw new IllegalStateException("Expressions must have a right side if they have an operator");
		}
	}

	public boolean isSingleValue() {
		return a != null && b == null && opr == null;
	}

	@Override
	public Pushable push(Actor visitor) throws Exception {
		ExpressionHandler.compute(this, visitor);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return a.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return a.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return a.toBaseType();
	}

	public String toString() {
		return a + " " + opr + " " + b;
	}

}
