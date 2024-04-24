package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.Details;
import dev.fiz.bootstrap.names.InternalName;

/**
 * Component of a type that can be pushed onto the stack.
 */
public abstract class Field implements Assignable {

	public final Details details;
	public final InternalName ownerType;

	public Field(final Details details, final InternalName ownerType) {
		if(details == null)
			throw new NullPointerException("Field Details cannot be null");
		if(ownerType == null)
			throw new NullPointerException("Field ownerType cannot be null");
		this.details = details;
		this.ownerType = ownerType;
	}

	@Override
	public boolean equals(final Object o) {
		if(o instanceof Field f)
			return details.name.equals(f.details.name) && ownerType.equals(f.ownerType);
		else
			return false;
	}

	@Override
	public String toString() {
		return ownerType.nameString() + " " + super.toString();
	}

	@Override
	public InternalName pushType(Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

	@Override
	public InternalName toInternalName() {
		return details.type;
	}

	@Override
	public boolean isBaseType() {
		return details.type.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return details.type.toBaseType();
	}

}
