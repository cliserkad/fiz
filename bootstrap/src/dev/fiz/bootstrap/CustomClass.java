package dev.fiz.bootstrap;

import dev.fiz.bootstrap.ir.Pushable;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.InternalName;
import dev.fiz.bootstrap.names.ToInternalName;
import org.objectweb.asm.Opcodes;

public class CustomClass implements ToInternalName, Pushable {

	public final String pkg;
	public final String name;

	public CustomClass(String pkg, String name) {
		this.pkg = Text.nonNull(pkg);
		if(name == null)
			throw new NullPointerException("A CustomClass' name must not be null");
		if(name.trim().isEmpty())
			throw new IllegalArgumentException("A CustomClass' name must not be empty");
		this.name = name;
	}

	public CustomClass(String name) {
		this(null, name);
	}

	/**
	 * Full name with internal separator. Looks like "dev/fiz/bootstrap/CustomClass"
	 */
	public String qualifiedName() {
		return fullName().replace(InternalName.SOURCE_SEPARATOR, InternalName.INTERNAL_SEPARATOR);
	}

	public String fullName() {
		if(!pkg.isEmpty())
			return pkg + InternalName.SOURCE_SEPARATOR + name;
		else
			return name;
	}

	@Override
	public String toString() {
		return toInternalName().objectString();
	}

	@Override
	public InternalName toInternalName() {
		return new InternalName(this);
	}

	@Override
	public boolean isBaseType() {
		return false;
	}

	@Override
	public BaseType toBaseType() {
		return null;
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		// attempt to load "this"
		actor.visitVarInsn(Opcodes.ALOAD, 0);
		return this;
	}

	@Override
	public InternalName pushType(Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

}
