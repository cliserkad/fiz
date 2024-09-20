package dev.fiz.bootstrap;

import dev.fiz.bootstrap.antlr.FizLexer;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.antlr.FizParserBaseListener;
import dev.fiz.bootstrap.ir.*;
import dev.fiz.bootstrap.names.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.*;
import xyz.cliserkad.util.BestList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class CompilationUnit extends FizParserBaseListener implements Runnable, CommonText {

	public static final int CONST_ACCESS = ACC_PUBLIC + ACC_STATIC + ACC_FINAL;
	public static final String INCORRECT_FILE_NAME = "The input file name must match its class name.";
	public static final int PASSES = 3;
	public static final MethodHeader STRING_VALUE_OF = new MethodHeader(InternalName.STRING, "valueOf", null, ReturnValue.STRING, ACC_PUBLIC + ACC_STATIC);

	// used to generate a numerical id
	private static int unitCount = 0;

	private final CompilationDispatcher owner;
	private final Set<InternalName> imports;
	private final ClassWriter cw;
	private final int id;
	// input and output
	private File sourceFile;
	private String sourceCode;
	private File outputDir;
	private Scope currentScope;
	private CustomClass clazz;
	private boolean nameSet;
	private String pkgName;
	// FIXME: I'm exposing all of this pre tree stuff, but there is probably a better way to access the original text
	public FizLexer lexer;
	public FizParser parser;
	public CommonTokenStream commonTokenStream;
	private ParseTree tree;

	// pass 1 collects imports, classname, and constant names, methodNames
	// pass 2 assigns values to constants
	// pass 3 defines methods
	private int pass;

	public CompilationUnit(CompilationDispatcher owner, File sourceFile, File outputDir) {
		this.owner = owner;
		pass = 0;
		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		imports = new HashSet<>();
		id = unitCount++;
		addImport(String.class);
		tree = null;
		this.sourceFile = sourceFile;
		this.outputDir = outputDir;
	}

	/**
	 * Converts the top item of the stack in to a string
	 *
	 * @param name    The type of the element on the stack
	 * @param visitor Any MethodVisitor
	 */
	public static void convertToString(final InternalName name, final MethodVisitor visitor) {
		if(name.toBaseType() == BaseType.STRING)
			return;

		final InternalName passableType;
		if(name.isBaseType())
			// treat i8 and i16 as i32
			passableType = switch(name.toBaseType()) {
				case BYTE, SHORT -> InternalName.INT;
				default -> name;
			};
		else
			// treat all other types as generic objects
			passableType = InternalName.OBJECT;
		STRING_VALUE_OF.withParams(MethodHeader.toParamList(passableType)).invoke(visitor);
	}

	public boolean pass() throws Exception {
		if(sourceCode == null)
			sourceCode = new String(Files.readAllBytes(sourceFile.toPath()));
		if(tree == null)
			tree = makeParseTree(sourceCode);

		newPass();
		ParseTreeWalker.DEFAULT.walk((ParseTreeListener) this, tree);

		if(pass == 1)
			addImport(getClazz().toInternalName());

		return pass >= PASSES;
	}

	@Override
	public void run() {
		try {
			pass();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String unitName() {
		if(clazz != null && clazz.name != null && !clazz.name.isEmpty())
			return clazz.name;
		else if(sourceFile != null)
			return sourceFile.getName();
		else
			return id + "";
	}

	private ParseTree makeParseTree(String input) throws Exception {
		SyntaxErrorHandler syntaxErrorHandler = new SyntaxErrorHandler(this);

		lexer = new FizLexer(CharStreams.fromString(input));
		lexer.removeErrorListeners();
		lexer.addErrorListener(syntaxErrorHandler);

		commonTokenStream = new CommonTokenStream(lexer);

		parser = new FizParser(commonTokenStream);
		parser.removeErrorListeners();
		parser.addErrorListener(syntaxErrorHandler);

		final ParseTree tree = parser.source();

		if(syntaxErrorHandler.hasErrors()) {
			syntaxErrorHandler.printErrors();
			throw new Exception("Encountered syntax errors while parsing.");
		}

		return tree;
	}

	/**
	 * Writes the class out to the target file, checking that the file name matches the class name.
	 *
	 * @throws IOException                     file is not writable
	 * @throws CheckedIllegalArgumentException file name does not match class name
	 */
	public File write(final File target) throws IOException, CheckedIllegalArgumentException {
		cw.visitEnd();
		if(target == null)
			write();

		// check input file name
		if(sourceFile != null && !sourceFile.getName().replace(".fiz", "").equalsIgnoreCase(clazz.name))
			throw new CheckedIllegalArgumentException(sourceFile.getName(), INCORRECT_FILE_NAME + " class:" + clazz.name);

		final File destination;
		destination = new File(target, clazz.qualifiedName() + ".class");
		destination.getParentFile().mkdirs();
		destination.createNewFile();

		Files.write(destination.toPath(), cw.toByteArray());
		return destination;
	}

	public File write() throws IOException, CheckedIllegalArgumentException {
		if(sourceFile == null)
			throw new NullPointerException("write() without params in CompilationUnit if the unit wasn't created with a file.");
		// write(File) was made pure, but this preserves the side effect of setting outputDir to its actually used value
		outputDir = write(outputDir);
		return outputDir;
	}

	public InternalName resolveAgainstImports(String classname) throws SymbolResolutionException {
		final InternalName result = resolve(classname);
		if(result != null)
			return result;
		else {
			StringBuilder available = new StringBuilder();
			for(InternalName name : imports)
				available.append(name.nameString()).append("\n");
			throw new SymbolResolutionException("Couldn't recognize type: " + classname + "\nAvailable classes:\n" + available);
		}
	}

	public boolean isImported(String classname) {
		return resolve(classname) != null;
	}

	private InternalName resolve(String classname) {
		for(InternalName in : imports) {
			if(in.matchesClassname(classname))
				return in;
		}
		return null;
	}

	public int getPass() {
		return pass;
	}

	public void newPass() {
		pass++;
	}

	public TrackedMap<Constant, FizParser.ConstantDefContext> constants() {
		return owner.constants;
	}

	public TrackedMap<StaticField, FizParser.FieldDefContext> fields() {
		return owner.fields;
	}

	public Set<MethodHeader> methods() {
		return owner.methods;
	}

	@Override
	public void enterClazz(final FizParser.ClazzContext ctx) {
		if(getPass() == 1) {
			setClassName(ctx.ID().getText());
			ExternalMethodRouter.writeMethods(this, ctx.start.getLine());
		} else if(getPass() == 2) {
			if(!constants().isEmpty()) {
				MethodHeader staticInit = MethodHeader.STATIC_INIT.withOwner(clazz);
				addMethodDef(staticInit);
				Actor actor = new Actor(defineMethod(staticInit), this);

				for(Constant c : constants().keys()) {
					if(c.owner.equals(getClazz().toInternalName())) {
						try {
							final FizParser.ConstantDefContext cDef = constants().get(c);
							final Pushable pushable = Pushable.parse(actor, cDef.value());
							final Constant unsetConst = new Constant(c.name, pushable.toInternalName(), clazz.toInternalName());
							addConstant(unsetConst);
							constants().put(unsetConst, cDef);
							pushable.push(actor);
							actor.visitFieldInsn(PUTSTATIC, unsetConst.owner.nameString(), unsetConst.name, pushable.toInternalName().objectString());
						} catch(Exception e) {
							printException(e);
						}
					}
				}
				getCurrentScope().end(ctx.stop.getLine(), actor, staticInit.returns);
			}

			for(StaticField f : fields().keys()) {
				if(f.ownerType.equals(getClazz().toInternalName()) && f instanceof ObjectField) {
					final FieldVisitor fv;
					final Object defaultValue;
					if(f.type.toBaseType() == BaseType.STRING)
						defaultValue = "placeholder";
					else if(f.type.isBaseType())
						defaultValue = f.toBaseType().getDefaultValue().value;
					else
						defaultValue = null;
					int modifier = 0;
					if(f.mutable)
						modifier = ACC_FINAL;
					fv = cw.visitField(ACC_PUBLIC + modifier, f.name, f.type.objectString(), null, defaultValue);
					fv.visitEnd();
				}
			}

			try {
				addDefaultConstructor();
			} catch(Exception e) {
				printException(e);
			}
		}
	}

	@Override
	public void enterFieldDef(FizParser.FieldDefContext ctx) {
		// collect details
		if(getPass() == 1) {
			try {
				final Details details = new Details(ctx.variableDeclaration().details(), this);
				final ObjectField field = new ObjectField(details, getClazz());
				if(!fields().contains(field))
					fields().put(field, ctx);
				else
					throw new CheckedIllegalArgumentException(details.name, "The field name was already declared within " + getClazz());
			} catch(Exception e) {
				throw new IllegalArgumentException("Couldn't determine the name, type and mutability of a field at " + ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine());
			}
		}
	}

	@Override
	public void enterConstantDef(final FizParser.ConstantDefContext ctx) {
		// collect details
		if(getPass() == 1) {
			final String name = ctx.ID().toString();
			final Constant unsetConst = new Constant(name, InternalName.PLACEHOLDER, getClazz().toInternalName());
			if(!constants().contains(unsetConst))
				constants().put(unsetConst, ctx);
			else
				throw new IllegalArgumentException(unsetConst + " was already declared");
		}
	}

	private void consumeMethodCall(FizParser.MethodCallContext ctx, Actor actor) throws Exception {
		final String methodName = ctx.addressable().ID().get(ctx.addressable().ID().size() - 1).getText();
		if(actor.unit.resolve(methodName) != null)
			new NewObject(ctx, actor).push(actor);
		else
			new MethodCall(ctx, actor).push(actor);
	}

	private void consumeAssignment(FizParser.AssignmentContext ctx, Actor actor) throws Exception {
		final Assignable target = Assignable.parse(ctx, actor);
		final InternalName resultType;
		if(ctx.operatorAssign() != null)
			resultType = new Expression(target, Pushable.parse(actor, ctx.operatorAssign().value()), Operator.match(ctx.operatorAssign().operator())).pushType(actor);
		else
			resultType = Expression.parseExpressionContext(ctx.expression(), actor).pushType(actor);
		target.assign(resultType, actor);
	}

	private void consumeVariableDeclaration(FizParser.VariableDeclarationContext ctx, Actor actor) throws Exception {
		final Variable target = getCurrentScope().newVariable(new Details(ctx.details(), this));
		if(ctx.SET() != null) {
			target.assign(Expression.parseExpressionContext(ctx.expression(), actor).pushType(actor), actor);
		} else
			target.assignDefault(actor);
	}

	public void consumeStatement(final FizParser.StatementContext ctx, Actor actor) throws Exception {
		if(ctx.variableDeclaration() != null) {
			consumeVariableDeclaration(ctx.variableDeclaration(), actor);
		} else if(ctx.assignment() != null) {
			consumeAssignment(ctx.assignment(), actor);
		} else if(ctx.methodCall() != null) {
			consumeMethodCall(ctx.methodCall(), actor);
		} else if(ctx.conditional() != null) {
			// forward to the handler to partition code
			ConditionalHandler.handle(ctx.conditional(), actor);
		} else if(ctx.returnStatement() != null) {
			final ReturnValue rv;
			if(ctx.returnStatement().expression() == null)
				rv = null;
			else
				rv = new ReturnValue(ExpressionHandler.compute(Expression.parseExpressionContext(ctx.returnStatement().expression(), actor), actor));
			actor.writeReturn(rv);
		} else
			throw new UnimplementedException("A type of statement couldn't be interpreted " + ctx.getText());
	}

	public void consumeBlock(final FizParser.BlockContext ctx, Actor actor) throws Exception {
		for(FizParser.StatementContext statement : ctx.statement())
			consumeStatement(statement, actor);
	}

	@Override
	public void enterPackage(final FizParser.PackageContext ctx) {
		if(pass == 1)
			pkgName = ctx.addressable().getText();
	}

	@Override
	public void enterImports(final FizParser.ImportsContext ctx) {
		if(pass == 1) {
			for(FizParser.AddressableContext addressable : ctx.addressable())
				addImport(addressable.getText());
		}
	}

	public void addImport(String text) {
		try {
			final Class<?> jvmClass = Class.forName(text);
			addImport(jvmClass);
		} catch(Exception e) {
			printException(e);
		}
	}

	public void addImport(Class<?> clazz) {
		imports.add(new InternalName(clazz));
		for(Method method : clazz.getMethods()) {
			methods().add(new MethodHeader(clazz, method));
		}
		for(java.lang.reflect.Field field : clazz.getFields()) {
			final Details details = new Details(field.getName(), new InternalName(field.getType()), (field.getModifiers() & ACC_FINAL) == ACC_FINAL);
			if((field.getModifiers() & ACC_STATIC) == ACC_STATIC) {
				fields().add(new StaticField(details, new InternalName(clazz)), null);
			} else {
				fields().add(new ObjectField(details, new InternalName(clazz)), null);
			}
		}
	}

	public void addImport(InternalName internalName) {
		imports.add(internalName);
	}

	@Override
	public void enterMethodDefinition(final FizParser.MethodDefinitionContext ctx) {
		try {
			// parse name and return type
			final Details details;
			if(ctx.details() != null)
				details = new Details(ctx.details(), this).filterName();
			else
				details = new Details(ctx.ID().getText(), null).filterName();
			final ReturnValue rv = new ReturnValue(details.type);

			// parse parameters
			final BestList<Param> params = new BestList<>();
			for(FizParser.ParamContext param : ctx.paramSet().param())
				params.add(new Param(new Details(param.details(), this), param.value()));

			// check if the method accesses any fields
			final boolean initializer;
			final int staticModifier;
			if(details.name.equals(MethodHeader.S_INIT)) {
				staticModifier = 0;
				initializer = true;
			} else {
				if(ctx.paramSet().ID() == null)
					staticModifier = ACC_STATIC;
				else {
					if(ctx.paramSet().ID().getText().equals("this"))
						staticModifier = 0;
					else
						throw new IllegalArgumentException("Only \"this\" may be used as a non typed argument");
				}
				initializer = false;
			}

			MethodHeader def = new MethodHeader(clazz.toInternalName(), details.name, params, rv, ACC_PUBLIC + staticModifier);

			if(getPass() == 2) {
				addMethodDef(def);
			} else if(getPass() == 3) {
				// define user specified method
				final Actor actor = new Actor(defineMethod(def), this);
				// instance of owning type will always occupy slot 0
				if(staticModifier == 0)
					getCurrentScope().newVariable("this", getClazz().toInternalName());
				// parameters will always occupy the first few slots
				for(Param param : params)
					getCurrentScope().newVariable(param.name, param.type);

				if(initializer) {
					// call Object.super(this);
					actor.visitVarInsn(ALOAD, 0);
					actor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
				}

				consumeBlock(ctx.block(), actor);

				getCurrentScope().end(ctx.stop.getLine(), actor, rv);

				// add in helper methods for default values
				for(Param param : params) {
					if(param.defaultValue != null) {
						final ReturnValue returnValue = new ReturnValue(param.toInternalName());
						final MethodHeader defaultProvider = new MethodHeader(clazz.toInternalName(), details.name + "_" + param.name, null, returnValue, def.access + Opcodes.ACC_SYNTHETIC);
						addMethodDef(defaultProvider);
						final Actor defaultWriter = new Actor(defineMethod(defaultProvider), this);
						Pushable.parse(actor, param.defaultValue).push(defaultWriter);
						defaultWriter.writeReturn(returnValue);
						getCurrentScope().end(ctx.start.getLine(), defaultWriter, returnValue);
					}
				}

			}
		} catch(Exception e) {
			printException(e);
		}
	}

	private void printException(final Exception e) {
		System.err.println("From unit: " + unitName());
		e.printStackTrace();
	}

	@Override
	public void enterMain(final FizParser.MainContext ctx) {
		if(getPass() == 2) {
			addMethodDef(MethodHeader.MAIN.withOwner(clazz));
		} else if(getPass() == 3) {
			final Actor actor = new Actor(defineMethod(MethodHeader.MAIN.withOwner(clazz)), this);
			getCurrentScope().newVariable("args", new InternalName(String.class, 1));
			try {
				consumeBlock(ctx.block(), actor);
			} catch(Exception e) {
				printException(e);
			}
			getCurrentScope().end(ctx.stop.getLine(), actor, ReturnValue.VOID);
		}
	}

	public boolean hasLocalVariable(final String name) {
		return getCurrentScope().contains(name.trim());
	}

	public Variable getLocalVariable(final String name) {
		return getCurrentScope().getVariable(name.trim());
	}

	public MethodHeader addMethodDef(MethodHeader md) {
		if(!methods().add(md))
			throw new IllegalArgumentException("The method " + md + " already exists in " + unitName());
		return md;
	}

	public boolean hasConstant(final String name) {
		for(Constant c : constants().keys()) {
			if(c.name.equals(name))
				return true;
		}
		return false;
	}

	public Constant getConstant(final String name) {
		for(Constant c : constants().keys()) {
			if(c.name.equals(name))
				return c;
		}
		throw new IllegalArgumentException("Constant " + name + " does not exist");
	}

	/**
	 * Sets the name of this class to the given className.
	 *
	 * @param name name of class, Ex. Test
	 * @return success of operation
	 */
	public boolean setClassName(final String name) {
		if(!nameSet) {
			pkgName = Text.nonNull(pkgName);
			clazz = new CustomClass(pkgName, name);
			nameSet = true;

			// give name to ClassWriter
			cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, clazz.toInternalName().nameString(), null, InternalName.OBJECT.nameString(), null);
			// FIXME investigate significance of clazz name here
			cw.visitSource(clazz.name + ".FizParser", null);

			return true;
		} else
			return false;
	}

	public CustomClass getClazz() {
		return clazz;
	}

	public File getSourceFile() {
		// copy and return
		return new File(sourceFile.getAbsolutePath());
	}

	public MethodVisitor defineMethod(MethodHeader md) {
		if(methods().contains(md)) {
			final MethodVisitor mv = cw.visitMethod(md.access, md.name, md.descriptor(), null, null);
			currentScope = new Scope("Method " + md.name + " of class " + clazz, mv);
			mv.visitCode();
			return mv;
		} else {
			throw new IllegalArgumentException("None of the detected method definitions match " + md);
		}
	}

	public FieldVisitor addConstant(final Constant c) {
		final FieldVisitor fv;
		final Object defaultValue;
		if(c.toBaseType() == BaseType.STRING)
			defaultValue = "placeholder";
		else if(c.isBaseType())
			defaultValue = c.toBaseType().getDefaultValue().value;
		else
			defaultValue = null;
		fv = cw.visitField(CONST_ACCESS, c.name, c.type.objectString(), null, defaultValue);
		fv.visitEnd();
		return fv;
	}

	public void addDefaultConstructor() throws Exception {
		addMethodDef(MethodHeader.INIT.withOwner(getClazz()));
		final Actor actor = new Actor(defineMethod(MethodHeader.INIT.withOwner(getClazz())), this);
		actor.visitCode();
		final Label start = new Label();
		actor.visitLabel(start);
		actor.visitLineNumber(0, start);

		// call Object.super(this);
		actor.visitVarInsn(ALOAD, 0);
		actor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

		for(StaticField f : fields().keys()) {
			if(f.ownerType.equals(getClazz().toInternalName()) && f instanceof ObjectField) {
				// push "this"
				actor.visitVarInsn(ALOAD, 0);
				if(fields().get(f).variableDeclaration().SET() != null) {
					final Pushable value = Expression.parseExpressionContext(fields().get(f).variableDeclaration().expression(), actor);
					value.pushType(actor);
				} else {
					if(f.type.isBaseType())
						f.type.toBaseType().getDefaultValue().push(actor);
					else
						actor.visitInsn(ACONST_NULL);
				}
				actor.visitFieldInsn(PUTFIELD, getClazz().toInternalName().nameString(), f.name, f.type.objectString());
			}
		}
		final Label finish = new Label();
		actor.visitLabel(finish);
		actor.visitInsn(RETURN);
		actor.visitLocalVariable("this", clazz.toInternalName().objectString(), null, start, finish, 0);
		actor.visitMaxs(0, 0);
		actor.visitEnd();
	}

	public Scope getCurrentScope() {
		return currentScope;
	}

}
