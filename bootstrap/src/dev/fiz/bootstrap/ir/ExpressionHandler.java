package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.CompilationUnit;
import dev.fiz.bootstrap.IncompatibleTypeException;
import dev.fiz.bootstrap.MethodHeader;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.CommonText;
import dev.fiz.bootstrap.names.InternalName;
import dev.fiz.bootstrap.names.ReturnValue;
import org.objectweb.asm.MethodVisitor;

public interface ExpressionHandler extends CommonText {

	public static final MethodHeader INIT_STRING_BUILDER = new MethodHeader(new InternalName(StringBuilder.class), MethodHeader.S_INIT, null, null, ACC_PUBLIC);
	MethodHeader SB_APPEND = new MethodHeader(new InternalName(StringBuilder.class), "append", MethodHeader.toParamList(new InternalName(String.class)), new ReturnValue(new InternalName(StringBuilder.class)), ACC_PUBLIC);
	MethodHeader SB_TO_STRING = new MethodHeader(new InternalName(StringBuilder.class), "toString", null, ReturnValue.STRING, ACC_PUBLIC);

	public static InternalName compute(final Expression xpr, final Actor actor) throws Exception {
		final Pushable res = xpr.a;
		final Pushable calc = xpr.b;
		final Operator opr = xpr.opr;

		if(xpr.isSingleValue()) {
			return res.pushType(actor);
		} else {
			return switch(res.toBaseType()) {
				case BOOLEAN, BYTE, SHORT, CHAR, INT -> computeInt(res, calc, opr, actor).toInternalName();
				case FLOAT -> computeFloat(res, calc, opr, actor).toInternalName();
				case LONG -> computeLong(res, calc, opr, actor).toInternalName();
				case DOUBLE -> computeDouble(res, calc, opr, actor).toInternalName();
				case STRING -> computeString(res, calc, opr, actor).toInternalName();
			};
		}
	}

	static BaseType computeDouble(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		if(res2.toBaseType() == BaseType.STRING)
			throw new IncompatibleTypeException(BaseType.DOUBLE + INCOMPATIBLE + BaseType.STRING);
		else {
			res1.push(actor);
			res2.push(actor);
			final int operatorInstruction = switch(opr) {
				case ADD -> DADD;
				case SUB -> DSUB;
				case MUL -> DMUL;
				case DIV -> DDIV;
				case MOD -> DREM;
			};
			actor.visitInsn(operatorInstruction);
		}
		return BaseType.DOUBLE;
	}

	static BaseType computeLong(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		if(res2.toBaseType() == BaseType.STRING)
			throw new IncompatibleTypeException(BaseType.LONG + INCOMPATIBLE + BaseType.STRING);
		else {
			res1.push(actor);
			res2.push(actor);
			final int operatorInstruction = switch(opr) {
				case ADD -> LADD;
				case SUB -> LSUB;
				case MUL -> LMUL;
				case DIV -> LDIV;
				case MOD -> LREM;
			};
			actor.visitInsn(operatorInstruction);
		}
		return BaseType.LONG;
	}

	static BaseType computeFloat(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		if(res2.toBaseType() == BaseType.STRING)
			throw new IncompatibleTypeException(BaseType.FLOAT + INCOMPATIBLE + BaseType.STRING);
		else {
			res1.push(actor);
			res2.push(actor);
			final int operatorInstruction = switch(opr) {
				case ADD -> FADD;
				case SUB -> FSUB;
				case MUL -> FMUL;
				case DIV -> FDIV;
				case MOD -> FREM;
			};
			actor.visitInsn(operatorInstruction);
		}
		return BaseType.FLOAT;
	}

	/**
	 * Puts two new StringBuilders on the stack
	 */
	public static void createStringBuilder(MethodVisitor visitor) {
		visitor.visitTypeInsn(NEW, new InternalName(StringBuilder.class).nameString());
		visitor.visitInsn(DUP);
		INIT_STRING_BUILDER.invoke(visitor);
	}

	public static BaseType computeString(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		if(opr == Operator.ADD) {
			createStringBuilder(actor);
			res1.push(actor);
			SB_APPEND.invoke(actor);
			res2.push(actor);
			if(res2.toBaseType() != BaseType.STRING) {
				CompilationUnit.convertToString(res2.toBaseType().toInternalName(), actor);
			}
			SB_APPEND.invoke(actor);
			SB_TO_STRING.invoke(actor);
		} else {
			throw new IllegalArgumentException("Operator " + opr + " is not supported for strings. Only ADD is supported.");
		}
		return BaseType.STRING;
	}

	public static BaseType computeInt(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		if(res2.toBaseType() == BaseType.STRING)
			throw new IncompatibleTypeException(BaseType.INT + INCOMPATIBLE + BaseType.STRING);
		// under the hood booleans should be either 0 or 1
		else {
			res1.push(actor);
			res2.push(actor);
			final int operatorInstruction = switch(opr) {
				case ADD -> IADD;
				case SUB -> ISUB;
				case MUL -> IMUL;
				case DIV -> IDIV;
				case MOD -> IREM;
			};
			actor.visitInsn(operatorInstruction);
		}
		return BaseType.INT;
	}

}
