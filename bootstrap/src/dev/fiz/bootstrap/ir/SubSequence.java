package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.MethodHeader;
import dev.fiz.bootstrap.UnimplementedException;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.InternalName;
import dev.fiz.bootstrap.names.ReturnValue;
import org.objectweb.asm.Opcodes;

public class SubSequence extends BasePushable {

	public static final MethodHeader SUB_STRING = new MethodHeader(InternalName.STRING, "substring", MethodHeader.toParamList(InternalName.INT, InternalName.INT), ReturnValue.STRING, Opcodes.ACC_PUBLIC);

	public final Variable variable;
	public final Range range;

	public SubSequence(final FizParser.SubSequenceContext ctx, final Actor actor) throws Exception {
		this(actor.unit.getLocalVariable(ctx.ID().getText()), new Range(ctx.range(), actor));
	}

	public SubSequence(final Variable variable, final Range range) {
		this.variable = variable;
		this.range = range;
	}

	@Override
	public SubSequence push(Actor visitor) throws Exception {
		if(!variable.isArray() && variable.toBaseType() == BaseType.STRING) {
			variable.push(visitor);
			range.min.push(visitor);
			range.max.push(visitor);
			SUB_STRING.invoke(visitor);
		} else {
			throw new UnimplementedException("Subsequence only implemented for strings");
		}
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return variable.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return variable.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return variable.toBaseType();
	}

}
