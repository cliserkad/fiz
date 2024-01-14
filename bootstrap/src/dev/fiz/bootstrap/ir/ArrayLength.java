package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.CommonText;
import dev.fiz.bootstrap.names.InternalName;

/**
 * Represents the access of an array's length.
 */
public class ArrayLength extends BasePushable implements CommonText {

	public final Variable array;

	public ArrayLength(final Variable array) {
		this.array = array;
	}

	/**
	 * Pushes an int to the stack that is equal to the array's length
	 */
	@Override
	public Pushable push(Actor actor) throws Exception {
		array.push(actor);
		actor.visitInsn(ARRAYLENGTH);
		return this;
	}

	/**
	 * @return InternalName.INT
	 */
	@Override
	public InternalName toInternalName() {
		return InternalName.INT;
	}

	/**
	 * Determines if this will provide a base type
	 *
	 * @return true
	 */
	@Override
	public boolean isBaseType() {
		return true;
	}

	/**
	 * An array's length is always an INT
	 *
	 * @return BaseType.INT
	 */
	@Override
	public BaseType toBaseType() {
		return BaseType.INT;
	}

}
