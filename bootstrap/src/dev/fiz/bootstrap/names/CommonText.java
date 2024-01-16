package dev.fiz.bootstrap.names;

import org.objectweb.asm.Opcodes;

public interface CommonText extends Opcodes {

	// keywords
	String KEYWORD_BOOLEAN = "boolean";
	String KEYWORD_INT = "int";
	String KEYWORD_STRING = "string";
	String KEYWORD_TRUE = "true";
	String KEYWORD_FALSE = "false";
	String KEYWORD_CLASS = "class";

	String THIS = "this";

	String EMPTY_STRING = "";

	// handled by ExternalMethodRouter
	String PRINT = "print";
	String PRINTLN = "println";
	String ERROR = "error";

	ReturnValue VOID = ReturnValue.VOID;
	String DEFAULT = "default";

	// error messages
	String SWITCH_BASETYPE = "switch on BaseType";
	String INCOMPATIBLE = " has a type which is incompatible with the type of ";

}
