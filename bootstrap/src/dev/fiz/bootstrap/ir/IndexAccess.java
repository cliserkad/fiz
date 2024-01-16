package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.IncompatibleTypeException;
import dev.fiz.bootstrap.MethodHeader;
import dev.fiz.bootstrap.UnimplementedException;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.CommonText;
import dev.fiz.bootstrap.names.InternalName;
import dev.fiz.bootstrap.names.ReturnValue;

/**
 * Represents the access of an array's element
 */
public class IndexAccess extends BasePushable implements CommonText {

	public static final MethodHeader STRING_CHAR_AT = new MethodHeader(InternalName.STRING, "charAt", MethodHeader.toParamList(BaseType.INT.toInternalName()), ReturnValue.CHAR, ACC_PUBLIC);

	public final Variable variable;
	public final Pushable index;

	public IndexAccess(final Variable variable, final Pushable index) {
		this.variable = variable;
		this.index = index;
	}

	@Override
	public IndexAccess push(final Actor visitor) throws Exception {
		visitor.visitVarInsn(ALOAD, variable.localIndex);
		// throw error if value within [ ] isn't an int
		if(index.toBaseType().ordinal() > BaseType.INT.ordinal())
			throw new IncompatibleTypeException("The input for an array access must be an integer");
		else
			index.push(visitor);

		if(variable.isArray()) {
			if(variable.type.isBaseType()) {
				final int loadInstruction = switch(variable.type.toBaseType()) {
					case BOOLEAN, BYTE -> BALOAD;
					case SHORT -> SALOAD;
					case CHAR -> CALOAD;
					case INT -> IALOAD;
					case FLOAT -> FALOAD;
					case LONG -> LALOAD;
					case DOUBLE -> DALOAD;
					case STRING -> AALOAD;
				};
				visitor.visitInsn(loadInstruction);
			} else
				visitor.visitInsn(AALOAD);
		} else if(variable.toBaseType() == BaseType.STRING)
			STRING_CHAR_AT.invoke(visitor);

		else
			throw new IllegalArgumentException(variable + " is not an array nor a string");
		return this;
	}

	@Override
	public InternalName toInternalName() {
		if(!variable.isArray() && variable.toInternalName().equals(InternalName.STRING))
			return InternalName.CHAR;
		else
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

	@Override
	public String toString() {
		return "ArrayAccess --> {\n\t" + variable + "\n\t" + index + "\n}";
	}

}
