package dev.fiz.bootstrap;

import dev.fiz.bootstrap.names.CommonText;
import dev.fiz.bootstrap.names.Details;
import dev.fiz.bootstrap.names.InternalName;
import dev.fiz.bootstrap.names.ReturnValue;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;

import static dev.fiz.bootstrap.MethodHeader.toParamList;

/**
 * Used to inject external methods into a class that are not defined in the source code.
 */
public class ExternalMethodRouter implements CommonText {

	public static final MethodHeader PRINTLN_MTD = new MethodHeader(new InternalName(PrintStream.class), PRINTLN, MethodHeader.toParamList(InternalName.STRING), VOID, ACC_PUBLIC);
	public static final MethodHeader PRINT_MTD = new MethodHeader(new InternalName(PrintStream.class), PRINT, MethodHeader.toParamList(InternalName.STRING), VOID, ACC_PUBLIC);
	public static final MethodHeader ERROR_MTD = new MethodHeader(new InternalName(PrintStream.class), ERROR, MethodHeader.toParamList(InternalName.STRING), VOID, ACC_PUBLIC);
	public static final MethodHeader PARSE_INT_MTD = new MethodHeader(InternalName.INT_WRAPPER, "parseInt", MethodHeader.toParamList(InternalName.STRING), ReturnValue.INT, ACC_PUBLIC + ACC_STATIC);
	public static final InternalName SYSTEM = new InternalName(System.class);
	public static final InternalName PRINT_STREAM = new InternalName(PrintStream.class);
	public static final Details ARG_DETAILS = new Details("input", InternalName.STRING);

	public static void writeMethods(final CompilationUnit unit, int line) {
		addPrintlnMethod(unit, line);
		addPrintMethod(unit, line);
		addErrorMethod(unit, line);

		// add parseInt method to class
		MethodVisitor visitor = unit.defineMethod(unit.addMethodDef(PARSE_INT_MTD.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		unit.getCurrentScope().newVariable(ARG_DETAILS).push(new Actor(visitor, unit)); // register argument as a local variable and push it on to the stack
		PARSE_INT_MTD.invoke(visitor); // invoke the static method Integer.parseInt(String)
		visitor.visitInsn(IRETURN); // return int
		unit.getCurrentScope().end(line, visitor, ReturnValue.INT);
	}

	public static void addStringPrintingMethod(final CompilationUnit unit, final int line, final String printStreamName, final MethodHeader methodToWrite, final MethodHeader methodToInvoke) {
		MethodVisitor visitor = unit.defineMethod(unit.addMethodDef(methodToWrite.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC))); // register method definition
		unit.getCurrentScope().newVariable(ARG_DETAILS); // register argument as a local variable
		visitor.visitFieldInsn(GETSTATIC, SYSTEM.nameString(), printStreamName, PRINT_STREAM.objectString()); // push printStream on to stack
		unit.getLocalVariable(ARG_DETAILS.name).push(new Actor(visitor, unit)); // push argument on to stack
		methodToInvoke.invoke(visitor); // invoke the printing method against the printStream
		unit.getCurrentScope().end(line, visitor, ReturnValue.VOID); // end method
	}

	public static void addStringPrintingMethod(final CompilationUnit unit, final int line, final String printStreamName, final MethodHeader methodToWrite) {
		addStringPrintingMethod(unit, line, printStreamName, methodToWrite, methodToWrite);
	}

	public static void addPrintlnMethod(final CompilationUnit unit, final int line) {
		addStringPrintingMethod(unit, line, "out", PRINTLN_MTD);
	}

	public static void addPrintMethod(final CompilationUnit unit, final int line) {
		addStringPrintingMethod(unit, line, "out", PRINT_MTD);
	}

	public static void addErrorMethod(final CompilationUnit unit, final int line) {
		addStringPrintingMethod(unit, line, "err", ERROR_MTD, PRINTLN_MTD);
	}

}
