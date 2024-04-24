package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.EitherOf;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.InternalName;

public class Expression extends BasePushable {

	public EitherOf<Pushable, InternalName> a;
	public Pushable b;
	public Operator opr;

	public Expression(Pushable a, Pushable b, Operator opr) {
		if(a == null)
			throw new NullPointerException("Expression a cannot be null");
		if(b == null)
			throw new NullPointerException("Expression b cannot be null");
		if(opr == null)
			throw new NullPointerException("Expression operator cannot be null");
		if(!a.isBaseType() || !b.isBaseType())
			throw new IllegalArgumentException("Expressions may only contain BaseTypes");
		this.a = new EitherOf.ElementA<>(a);
		this.b = b;
		this.opr = opr;
	}

	public Expression(FizParser.ExpressionContext ctx, Actor actor) throws Exception {
		this(Pushable.parse(actor, ctx.component()), parseExpressionContext(ctx.expression(), actor), Operator.match(ctx.operator()));
	}

	/**
	 * Forces the parsing of a value to a Pushable.
	 * If the value is an expression, it will be parsed as such but lots of expressions are actually just single values.
	 */
	public static Pushable parseExpressionContext(FizParser.ExpressionContext ctx, Actor actor) throws Exception {
		if(ctx.expression() == null)
			return Pushable.parse(actor, ctx.component());
		else
			return new Expression(ctx, actor);
	}

	@Override
	public Pushable push(Actor visitor) throws Exception {
		ExpressionHandler.compute(this, visitor);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return a.match(
				(Pushable p) -> p.toInternalName(),
				(InternalName i) -> i
		);
	}

	@Override
	public boolean isBaseType() {
		return toInternalName().isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return toInternalName().toBaseType();
	}

	public String toString() {
		StringBuilder bldr = new StringBuilder();
		bldr.append("Expression: {\n");
		bldr.append("a  : ").append(a).append("\n");
		bldr.append("opr: ").append(opr).append("\n");
		bldr.append("b  : ").append(b).append("\n");
		bldr.append("}");
		return bldr.toString();
	}

}
