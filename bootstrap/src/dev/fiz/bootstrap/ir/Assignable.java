package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.InternalName;

public interface Assignable extends Pushable {

	public Assignable assign(final InternalName incomingType, final Actor actor) throws Exception;

	public Assignable assignDefault(final Actor actor) throws Exception;

	public static Assignable parse(final FizParser.AssignmentContext ctx, final Actor actor) {
		final FizParser.AddressableContext address = ctx.addressable();

		if(address.ID().size() > 1) {
			StaticField field = null;
			for(int i = 0; i < address.ID().size(); i++) {
				if(field == null)
					field = actor.unit.fields().equivalentKey(new ObjectField(address.ID(i).getText(), null, false, actor.unit.getClazz()));
				else
					field = actor.unit.fields().equivalentKey(new ObjectField(address.ID(i).getText(), null, false, field));
			}
			return field;
		} else if(!address.ID().isEmpty()) {
			if(actor.unit.getCurrentScope().contains(address.ID(0).getText()))
				return actor.unit.getLocalVariable(address.ID(0).getText());
			else
				return actor.unit.fields().equivalentKey(new ObjectField(address.ID(0).getText(), null, false, actor.unit.getClazz()));
		} else {
			System.err.println("Assignable failed to parse anything\nFull Text: " + ctx.getText());
			return null;
		}
	}

}
