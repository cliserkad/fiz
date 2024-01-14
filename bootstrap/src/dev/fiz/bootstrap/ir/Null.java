package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.CommonText;
import dev.fiz.bootstrap.names.InternalName;

public class Null extends BasePushable implements CommonText {

	@Override
	public Pushable push(final Actor visitor) throws Exception {
		visitor.visitInsn(ACONST_NULL);
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return InternalName.OBJECT;
	}

	@Override
	public boolean isBaseType() {
		return false;
	}

	@Override
	public BaseType toBaseType() {
		return null;
	}

}
