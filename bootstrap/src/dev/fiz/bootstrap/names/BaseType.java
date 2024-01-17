package dev.fiz.bootstrap.names;

import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.ir.Literal;
import org.objectweb.asm.Opcodes;

public enum BaseType implements ToInternalName {

	BOOLEAN('Z', Opcodes.T_BOOLEAN, boolean.class, Boolean.class, FizParser.BOOLEAN),
	BYTE('B', Opcodes.T_BYTE, byte.class, Byte.class, FizParser.BYTE),
	SHORT('S', Opcodes.T_SHORT, short.class, Short.class, FizParser.SHORT),
	CHAR('C', Opcodes.T_CHAR, char.class, Character.class, FizParser.CHAR),
	INT('I', Opcodes.T_INT, int.class, Integer.class, FizParser.INT),
	FLOAT('F', Opcodes.T_FLOAT, float.class, Float.class, FizParser.FLOAT),
	LONG('J', Opcodes.T_LONG, long.class, Long.class, FizParser.LONG),
	DOUBLE('D', Opcodes.T_DOUBLE, double.class, Double.class, FizParser.DOUBLE),
	STRING("Ljava/lang/String;", 0, String.class, String.class, FizParser.STRING);

	public final String rep;
	public final int id;
	public final Class<?> primitiveClass;
	public final Class<?> wrapperClass;
	public final int tokenID;

	BaseType(String rep, int id, Class<?> primitiveClass, Class<?> wrapperClass, int tokenID) {
		this.rep = rep;
		this.id = id;
		this.primitiveClass = primitiveClass;
		this.wrapperClass = wrapperClass;
		this.tokenID = tokenID;
	}

	BaseType(char rep, int id, Class<?> primitiveClass, Class<?> wrapperClass, int tokenID) {
		this("" + rep, id, primitiveClass, wrapperClass, tokenID);
	}

	public static boolean isBaseType(final Object value) {
		final Class<?> c = value.getClass();
		return matchClass(c) != null;
	}

	/**
	 * Matches a primitive or wrapper class to a BaseType
	 *
	 * @param c any Class
	 * @return BaseType on match, null otherwise
	 */
	public static BaseType matchClass(Class<?> c) {
		for(final BaseType baseType : values())
			if(baseType.primitiveClass.equals(c) || baseType.wrapperClass.equals(c))
				return baseType;
		return null;
	}

	/**
	 * Match only primitive classes, not their wrappers
	 *
	 * @param c any Class
	 * @return BaseType on match, null otherwise
	 */
	public static BaseType matchClassStrict(final Class<?> c) {
		for(final BaseType baseType : values())
			if(baseType.primitiveClass.equals(c))
				return baseType;
		return null;
	}

	public static BaseType matchValue(Object value) {
		return matchClass(value.getClass());
	}

	public static boolean isBaseTypeToken(final int tokenID) {
		return matchParserToken(tokenID) != null;
	}

	public static BaseType matchParserToken(final int tokenID) throws IllegalArgumentException {
		for(final BaseType baseType : values())
			if(baseType.tokenID == tokenID)
				return baseType;
		return null;
	}

	public boolean isIntInternally() {
		return switch(this) {
			case BOOLEAN, BYTE, SHORT, CHAR, INT -> true;
			default -> false;
		};
	}

	@Override
	public InternalName toInternalName() {
		return switch(this) {
			case BOOLEAN -> InternalName.BOOLEAN;
			case BYTE -> InternalName.BYTE;
			case SHORT -> InternalName.SHORT;
			case CHAR -> InternalName.CHAR;
			case INT -> InternalName.INT;
			case FLOAT -> InternalName.FLOAT;
			case LONG -> InternalName.LONG;
			case DOUBLE -> InternalName.DOUBLE;
			case STRING -> InternalName.STRING;
		};
	}

	public Literal<?> getDefaultValue() {
		return Literal.getDefaultForBaseType(this);
	}

	public boolean compatibleNoDirection(ToInternalName other) {
		if(!other.isBaseType())
			return false;
		else
			return compatibleNoDirection(other.toBaseType());
	}

	public boolean compatibleNoDirection(BaseType other) {
		return this.compatibleWith(other) || other.compatibleWith(this);
	}

	public boolean compatibleWith(ToInternalName receiver) {
		if(!receiver.isBaseType())
			return false;
		else
			return compatibleWith(receiver.toBaseType());
	}

	public boolean compatibleWith(BaseType receiver) {
		return ordinal() <= receiver.ordinal();
	}

	@Override
	public String toString() {
		return toInternalName().objectString();
	}

	@Override
	public boolean isBaseType() {
		return true;
	}

	@Override
	public BaseType toBaseType() {
		return this;
	}

}
