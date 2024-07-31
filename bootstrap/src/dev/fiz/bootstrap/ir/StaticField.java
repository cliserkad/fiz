package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.Details;
import dev.fiz.bootstrap.names.InternalName;
import org.objectweb.asm.Opcodes;

public class StaticField extends Field {

	public StaticField(final Details details, final InternalName ownerType) {
		super(details, ownerType);
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
			actor.visitFieldInsn(Opcodes.GETSTATIC, ownerType.nameString(), details.name, details.type.objectString());
			return this;
	}

	@Override
	public StaticField assign(InternalName incomingType, Actor actor) throws Exception {
		actor.visitFieldInsn(Opcodes.PUTSTATIC, ownerType.nameString(), details.name, details.type.objectString());
		return this;
	}

	@Override
	public StaticField assignDefault(Actor actor) throws Exception {
		if(isBaseType())
			toBaseType().getDefaultValue().push(actor);
		else
			actor.visitInsn(Opcodes.ACONST_NULL);
		return assign(details.type, actor);
	}

}
