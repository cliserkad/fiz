package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.CompilationUnit;
import dev.fiz.bootstrap.names.BaseType;
import dev.fiz.bootstrap.names.InternalName;

import java.util.ArrayList;
import java.util.List;

public class StringTemplate extends BasePushable {

	private final List<Pushable> elements;

	public StringTemplate() {
		elements = new ArrayList<>();
	}

	public void add(String s) {
		elements.add(new Literal<>(s));
	}

	public void add(Pushable pushable) {
		elements.add(pushable);
	}

	public boolean isTextOnly() {
		for(Pushable p : elements)
			if(!(p instanceof Literal<?>))
				return false;
		return true;
	}

	@Override
	public InternalName toInternalName() {
		return InternalName.STRING;
	}

	@Override
	public boolean isBaseType() {
		return true;
	}

	@Override
	public BaseType toBaseType() {
		return BaseType.STRING;
	}

	@Override
	public Pushable push(final Actor actor) throws Exception {
		ExpressionHandler.createStringBuilder(actor);
		for(Pushable p : elements) {
			if(p instanceof Constant)
				p = actor.unit.getConstant(((Constant) p).name);
			InternalName type = p.pushType(actor);
			if(!type.equals(InternalName.STRING))
				CompilationUnit.convertToString(type, actor);
			ExpressionHandler.SB_APPEND.invoke(actor);
		}
		ExpressionHandler.SB_TO_STRING.invoke(actor);
		return this;
	}

	@Override
	public String toString() {
		String out = "";
		for(Pushable p : elements) {
			if(p instanceof Literal<?>) {
				Literal<?> lit = (Literal<?>) p;
				out += lit.value;
			} else
				out += p;
		}
		return out;
	}

}
