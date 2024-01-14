package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.InternalName;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

public class Constant extends BasePushable {

	public final String name;
	public final InternalName type;
	public final InternalName owner;

	public Constant(final String name, final InternalName type, final InternalName owner) {
		if(name == null || name.isEmpty())
			throw new IllegalArgumentException("Constant name may not be empty");
		this.name = name;
		if(type == null)
			throw new NullPointerException();
		this.type = type;
		if(owner == null)
			throw new NullPointerException();
		this.owner = owner;
	}

	@Override
	public boolean isBaseType() {
		return type.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return type.toBaseType();
	}

	@Override
	public String toString() {
		return "Constant: " + name + " @ " + owner + " --> " + type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, owner);
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Constant) {
			Constant other = (Constant) o;
			return other.name.equals(name) && other.owner.equals(owner);
		}
		return false;
	}

	@Override
	public InternalName toInternalName() {
		return type;
	}

	@Override
	public Pushable push(final Actor actor) {
		actor.visitFieldInsn(Opcodes.GETSTATIC, owner.nameString(), name, type.objectString());
		return this;
	}

}
