package dev.fiz.bootstrap.names;

import dev.fiz.bootstrap.CompilationUnit;
import dev.fiz.bootstrap.MethodHeader;
import dev.fiz.bootstrap.SymbolResolutionException;
import dev.fiz.bootstrap.antlr.FizParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Details implements ToInternalName {

	public static final String DEFAULT_NAME = "unknown";
	public static final InternalName DEFAULT_TYPE = null;
	public static final boolean DEFAULT_MUTABLE = false;
	public static final boolean DEFAULT_NULLABLE = false;

	public static final String CHEVRON_REGEX = "[<>]";

	public final String name;
	public final InternalName type;
	public final boolean mutable;

	public Details(final Details details) {
		this.name = details.name;
		this.type = details.type;
		this.mutable = details.mutable;
	}

	public Details(final String name, final InternalName type, final boolean mutable) {
		this.name = name;
		this.type = type;
		this.mutable = mutable;
	}

	public Details(final String name, final InternalName type) {
		this(name, type, DEFAULT_MUTABLE);
	}

	public Details(final String name) {
		this(name, DEFAULT_TYPE);
	}

	public Details() {
		this(DEFAULT_NAME);
	}

	public Details(final FizParser.DetailsContext ctx, final CompilationUnit unit) throws SymbolResolutionException {
		String name = ctx.ID().getText();

		final InternalName type;
		if(ctx.type().basetype() != null) {
			type = resolveBaseTypeContext(ctx.type().basetype());
		} else {
			type = unit.resolveAgainstImports(ctx.type().getText());
		}

		if(ctx.type().BRACE_OPEN() != null) {
			this.type = type.toArray(ctx.type().BRACE_OPEN().size());
		} else {
			this.type = type;
		}

		this.name = name;
		this.mutable = ctx.MUTABLE() != null;
	}

	public InternalName resolveBaseTypeContext(FizParser.BasetypeContext ctx) throws SymbolResolutionException {
		int tokenID = ctx.getChild(TerminalNode.class, 0).getSymbol().getType();
		for(final BaseType baseType : BaseType.values()) {
			if(baseType.tokenID == tokenID) {
				return baseType.toInternalName();
			}
		}
		throw new SymbolResolutionException("Couldn't resolve basetype " + ctx.getText());
	}

	public Details withName(final String name) {
		return new Details(name, type, mutable);
	}

	/**
	 * Transforms init() and prep() to their respective special names
	 */
	public Details filterName() {
		if(name.equals(MethodHeader.S_INIT.replaceAll(CHEVRON_REGEX, CommonText.EMPTY_STRING)))
			return withName(MethodHeader.S_INIT);
		else if(name.equals(MethodHeader.S_STATIC_INIT.replaceAll(CHEVRON_REGEX, CommonText.EMPTY_STRING)))
			return withName(MethodHeader.S_STATIC_INIT);
		else
			return this;
	}

	/**
	 * Forwarding method
	 *
	 * @see InternalName#isBaseType()
	 */
	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}

	/**
	 * Forwarding method
	 *
	 * @see InternalName#toBaseType()
	 */
	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}

	/**
	 * @return type
	 */
	@Override
	public InternalName toInternalName() {
		return type;
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof Details other) {
			return name.equals(other.name) && type.equals(other.type) && mutable == other.mutable;
		} else
			return false;
	}

	public String toString() {
		final String name;
		if(this.name != null)
			name = this.name;
		else
			name = "???";

		final String type;
		if(this.type != null)
			type = this.type.nameString();
		else
			type = "null";

		final String mutable;
		if(this.mutable)
			mutable = "~";
		else
			mutable = "";

		return type + mutable + " " + name;
	}

}
