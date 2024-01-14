package dev.fiz.bootstrap;

import dev.fiz.bootstrap.names.CommonText;
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

	public static void writeMethods(final CompilationUnit unit, int line) {
		MethodVisitor visitor;
		final String arg = "input";
		MethodHeader def;

		// add println method to class
		visitor = unit.defineMethod(unit.addMethodDef(PRINTLN_MTD.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		unit.getCurrentScope().newVariable(arg, new InternalName(String.class));
		visitor.visitFieldInsn(GETSTATIC, new InternalName(System.class).nameString(), "out", new InternalName(PrintStream.class).objectString());
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invoke(visitor);
		unit.getCurrentScope().end(1, visitor, ReturnValue.VOID);

		// add print method to class
		visitor = unit.defineMethod(unit.addMethodDef(PRINT_MTD.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		unit.getCurrentScope().newVariable(arg, new InternalName(String.class));
		visitor.visitFieldInsn(GETSTATIC, new InternalName(System.class).nameString(), "out", new InternalName(PrintStream.class).objectString());
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PRINT_MTD.invoke(visitor);
		unit.getCurrentScope().end(1, visitor, ReturnValue.VOID);

		// add error method to class
		visitor = unit.defineMethod(unit.addMethodDef(ERROR_MTD.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		unit.getCurrentScope().newVariable(arg, new InternalName(String.class));
		visitor.visitFieldInsn(GETSTATIC, new InternalName(System.class).nameString(), "err", new InternalName(PrintStream.class).objectString());
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PRINTLN_MTD.invoke(visitor);
		unit.getCurrentScope().end(1, visitor, ReturnValue.VOID);

		// add parseInt method to class
		visitor = unit.defineMethod(unit.addMethodDef(PARSE_INT_MTD.withOwner(unit.getClazz()).withAccess(ACC_PUBLIC + ACC_STATIC)));
		unit.getCurrentScope().newVariable(arg, new InternalName(String.class));
		visitor.visitVarInsn(ALOAD, unit.getLocalVariable(arg).localIndex); // load input
		PARSE_INT_MTD.invoke(visitor);
		visitor.visitInsn(IRETURN); // return int
		unit.getCurrentScope().end(1, visitor, ReturnValue.INT);
	}

}
