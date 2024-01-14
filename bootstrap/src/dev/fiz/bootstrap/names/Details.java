package dev.fiz.bootstrap.names;

import dev.fiz.bootstrap.CompilationUnit;
import dev.fiz.bootstrap.MethodHeader;
import dev.fiz.bootstrap.UnimplementedException;
import dev.fiz.bootstrap.antlr.FizParser;

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

	public Details(final FizParser.DetailsContext ctx, final CompilationUnit unit) throws Exception {
		String name = ctx.ID().getText();

		InternalName type;
		if(ctx.type().basetype() != null) {
			if(ctx.type().basetype().BOOLEAN() != null)
				type = InternalName.BOOLEAN;
			else if(ctx.type().basetype().BYTE() != null)
				type = InternalName.BYTE;
			else if(ctx.type().basetype().SHORT() != null)
				type = InternalName.SHORT;
			else if(ctx.type().basetype().CHAR() != null)
				type = InternalName.CHAR;
			else if(ctx.type().basetype().INT() != null)
				type = InternalName.INT;
			else if(ctx.type().basetype().FLOAT() != null)
				type = InternalName.FLOAT;
			else if(ctx.type().basetype().LONG() != null)
				type = InternalName.LONG;
			else if(ctx.type().basetype().DOUBLE() != null)
				type = InternalName.DOUBLE;
			else if(ctx.type().basetype().STRING() != null)
				type = InternalName.STRING;
			else
				throw new UnimplementedException(CommonText.SWITCH_BASETYPE);
		} else {
			type = unit.resolveAgainstImports(ctx.type().getText());
			if(type == null)
				throw new IllegalArgumentException("Couldn't recognize type");
		}

		if(ctx.type().BRACE_OPEN() != null) {
			int dimensions = 0;
			for(int i = 0; i < ctx.type().BRACE_OPEN().size(); i++)
				dimensions++;
			type = type.toArray(dimensions);
		}

		this.name = name;
		this.type = type;
		this.mutable = ctx.MUTABLE() != null;
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
		if(object instanceof Details) {
			final Details other = (Details) object;
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
