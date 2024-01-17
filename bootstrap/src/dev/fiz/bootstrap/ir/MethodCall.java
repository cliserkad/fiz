package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.CommonText;
import dev.fiz.bootstrap.names.InternalName;
import dev.fiz.bootstrap.*;

public class MethodCall extends BasePushable implements CommonText {

	public final MethodInvocation invocation;

	public MethodCall(FizParser.MethodCallContext ctx, Actor actor) throws Exception {
		// parse methodCall alone
		final FizParser.AddressableContext address = ctx.addressable();
		final int addressSize = address.ID().size();
		final String methodName = address.ID().get(addressSize - 1).getText();

		// determine which class owns the method being called
		final Pushable source;
		final InternalName owner;
		boolean requireStatic;
		// FIXME dirty way to pull suspected classname from addressable. Literally just looks for the 2nd to last ID
		// Would pull "Math" out of "Math.max()"

		// literal text of invocation target
		if(addressSize > 1) {
			final String target = address.ID(addressSize - 2).getText();
			if(actor.unit.isImported(target)) {
				source = null;
				owner = actor.unit.resolveAgainstImports(target);
				requireStatic = true;
			} else if(actor.unit.hasLocalVariable(target)) {
				source = actor.unit.getLocalVariable(target);
				owner = source.toInternalName();
				requireStatic = false;
			} else {
				throw new SymbolResolutionException(target + "\nFull Text: " + address.getText());
			}
		} else if(actor.unit.getCurrentScope().contains("this")) {
			source = actor.unit.getLocalVariable("this");
			owner = actor.unit.getClazz().toInternalName();
			requireStatic = false;
		} else {
			source = null;
			owner = actor.unit.getClazz().toInternalName();
			requireStatic = true;
		}

		final BestList<Pushable> args = parseArguments(ctx.parameterSet(), actor);

		MethodTarget known = new MethodTarget(owner, methodName, argTypes(args), requireStatic);
		invocation = known.resolve(actor).withOwner(source).withArgs(args);
	}

	public static BestList<Pushable> parseArguments(FizParser.ParameterSetContext ctx, Actor actor) throws Exception {
		final BestList<Pushable> arguments = new BestList<>();
		if(ctx != null && !ctx.expression().isEmpty()) {
			for(FizParser.ExpressionContext xpr : ctx.expression()) {
				Pushable pushable = Expression.parseExpressionContext(xpr, actor);
				arguments.add(pushable);
			}
		}
		return arguments;
	}

	public BestList<InternalName> argTypes(BestList<Pushable> args) {
		final BestList<InternalName> argTypes = new BestList<>();
		for(Pushable arg : args)
			argTypes.add(arg.toInternalName());
		return argTypes;
	}

	@Override
	public MethodCall push(final Actor actor) throws Exception {
		invocation.push(actor);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return invocation.header.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return invocation.header.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return invocation.header.toBaseType();
	}

	@Override
	public String toString() {
		return "MethodCall --> {\n\tMethod --> " + invocation.header + arguments() + "\n}";
	}

	private String arguments() {
		String out = "\n\tCalculable --> {";
		for(Pushable arg : invocation.args)
			out += "\n\t\t" + arg.toString().replace("\n", "\n\t\t");
		out += "\n\t}";
		return out;
	}

}
