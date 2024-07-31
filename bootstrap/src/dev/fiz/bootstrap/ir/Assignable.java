package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.SymbolResolutionException;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.InternalName;

public interface Assignable extends Pushable {

	public Assignable assign(final InternalName incomingType, final Actor actor) throws Exception;

	public Assignable assignDefault(final Actor actor) throws Exception;

}
