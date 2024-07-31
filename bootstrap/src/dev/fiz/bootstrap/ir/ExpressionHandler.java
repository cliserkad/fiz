package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.*;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.CommonText;
import dev.fiz.bootstrap.names.InternalName;
import dev.fiz.bootstrap.names.ReturnValue;
import org.objectweb.asm.MethodVisitor;

public interface ExpressionHandler extends CommonText {

	MethodHeader INIT_STRING_BUILDER = new MethodHeader(InternalName.STRING_BUILDER, MethodHeader.S_INIT, null, null, ACC_PUBLIC);
	MethodHeader SB_APPEND = new MethodHeader(InternalName.STRING_BUILDER, "append", MethodHeader.toParamList(InternalName.STRING), new ReturnValue(InternalName.STRING_BUILDER), ACC_PUBLIC);
	MethodHeader SB_TO_STRING = new MethodHeader(InternalName.STRING_BUILDER, "toString", null, ReturnValue.STRING, ACC_PUBLIC);

	public static InternalName compute(final Pushable p, final Actor actor) throws Exception {
		if(p instanceof Expression xpr)
			return compute(xpr, actor);
		else
			return p.pushType(actor);
	}

	static InternalName compute(final Expression xpr, final Actor actor) throws Exception {
		final Union2<Pushable, InternalName> res = xpr.a;
		final Pushable calc = xpr.b;
		final Operator opr = xpr.opr;

		return res.matchFallible((Pushable p) -> {
			BaseType basetype = p.toBaseType();
			switch(basetype) {
				case BOOLEAN, BYTE, SHORT, CHAR, INT -> computeInt(p, calc, opr, actor);
				case FLOAT -> computeFloat(p, calc, opr, actor);
				case LONG -> computeLong(p, calc, opr, actor);
				case DOUBLE -> computeDouble(p, calc, opr, actor);
				case STRING -> computeString(p, calc, opr, actor);
			}
			;
			return basetype.toInternalName();
		}, (InternalName name) -> {
			actor.visitFieldInsn(GETSTATIC, name.nameString(), "FIXME", name.nameString());
			return name;
		});
	}

	static BaseType computeDouble(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		if(res2.toBaseType() == BaseType.STRING)
			throw new IncompatibleTypeException(BaseType.DOUBLE + INCOMPATIBLE + BaseType.STRING);
		else {
			res1.push(actor);
			res2.push(actor);
			final int operatorInstruction = switch(opr) {
				case ACCESS -> throw new UndefinedOperationException("Access operator not supported for f64");
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
				case ACCESS -> throw new UndefinedOperationException("Access operator not supported for i64");
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
				case ACCESS -> throw new UndefinedOperationException("Access operator not supported for f32");
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
	static void createStringBuilder(MethodVisitor visitor) {
		visitor.visitTypeInsn(NEW, new InternalName(StringBuilder.class).nameString());
		visitor.visitInsn(DUP);
		INIT_STRING_BUILDER.invoke(visitor);
	}

	static BaseType computeString(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
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

	static BaseType computeInt(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		if(res2.toBaseType() == BaseType.STRING)
			throw new IncompatibleTypeException(BaseType.INT + INCOMPATIBLE + BaseType.STRING);
			// under the hood booleans should be either 0 or 1
		else {
			res1.push(actor);
			res2.push(actor);
			final int operatorInstruction = switch(opr) {
				case ACCESS -> throw new UndefinedOperationException("Access operator not supported for i32");
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
