package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.names.Details;
import dev.fiz.bootstrap.names.InternalName;
import org.objectweb.asm.Opcodes;

/**
 * A Field attached to an object instance.
 */
public class InstanceField extends Field {

	public static final String NO_OWNER = "owner of a field must be known when pushed";

	public final Pushable owner;

	public InstanceField(Details details, Pushable owner) {
		super(details, owner.toInternalName());
		this.owner = owner;
	}

	public InstanceField(String name, InternalName type, boolean mutable, Pushable owner) {
		this(new Details(name, type, mutable), owner);
	}

	public InstanceField(final Details details, final InternalName ownerType) {
		super(details, ownerType);
		this.owner = null;
	}

	@Override
	public InstanceField assign(InternalName incomingType, Actor actor) throws Exception {
		final InternalName ownerType = owner.pushType(actor);
		actor.visitInsn(Opcodes.SWAP);
		actor.visitFieldInsn(Opcodes.PUTFIELD, ownerType.nameString(), details.name, details.type.objectString());
		return this;
	}

	@Override
	public InstanceField assignDefault(Actor actor) throws Exception {
		final InternalName incomingType = details.type.toBaseType().getDefaultValue().pushType(actor);
		assign(incomingType, actor);
		return this;
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		final InternalName ownerType = owner.pushType(actor);
		actor.visitFieldInsn(Opcodes.GETFIELD, ownerType.nameString(), details.name, details.type.objectString());
		return this;
	}

}
