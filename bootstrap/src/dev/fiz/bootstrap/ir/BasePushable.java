package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.names.InternalName;

public abstract class BasePushable implements Pushable {

	@Override
	public abstract Pushable push(final Actor actor) throws Exception;

	@Override
	public InternalName pushType(final Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

}
