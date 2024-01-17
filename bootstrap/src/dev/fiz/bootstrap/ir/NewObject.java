package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.BestList;
import dev.fiz.bootstrap.CompilationUnit;
import dev.fiz.bootstrap.MethodHeader;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.InternalName;
import dev.fiz.bootstrap.names.ReturnValue;
import org.objectweb.asm.Opcodes;

public class NewObject extends BasePushable implements Opcodes {

	public final InternalName type;
	private final BestList<Pushable> arguments;

	public NewObject(final FizParser.MethodCallContext ctx, Actor actor) throws Exception {
		type = actor.unit.resolveAgainstImports(ctx.addressable().ID(0).getText());
		if(type.isBaseType())
			throw new IllegalArgumentException("Can't instantiate a base type with a constructor. Use a literal instead");

		arguments = new BestList<>();
		if(ctx.parameterSet() != null && !ctx.parameterSet().expression().isEmpty()) {
			for(FizParser.ExpressionContext xpr : ctx.parameterSet().expression()) {
				Pushable pushable = Expression.parseExpressionContext(xpr, actor);
				arguments.add(pushable);
			}
		}
	}

	@Override
	public NewObject push(Actor visitor) throws Exception {
		final BestList<InternalName> paramTypes = new BestList<>();
		for(Pushable arg : arguments) {
			if(arg.toInternalName() == null)
				throw new NullPointerException("The type of an argument (" + arg + ") evaluated to null");
			paramTypes.add(arg.toInternalName());
		}
		visitor.visitTypeInsn(NEW, type.nameString());
		visitor.visitInsn(DUP);
		for(int i = 0; i < arguments.size(); i++) {
			arguments.get(i).push(visitor);
			if(paramTypes.get(i) == InternalName.STRING) {
				CompilationUnit.convertToString(arguments.get(i).toInternalName(), visitor);
			}
		}
		new MethodHeader(type, MethodHeader.S_INIT, MethodHeader.toParamList(paramTypes), ReturnValue.VOID, ACC_PUBLIC).invoke(visitor);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return type;
	}

	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}

}
