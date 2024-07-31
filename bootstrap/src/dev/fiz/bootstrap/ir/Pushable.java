package dev.fiz.bootstrap.ir;

import dev.fiz.bootstrap.Actor;
import dev.fiz.bootstrap.SymbolResolutionException;
import dev.fiz.bootstrap.UnimplementedException;
import dev.fiz.bootstrap.antlr.FizParser;
import dev.fiz.bootstrap.names.Details;
import dev.fiz.bootstrap.names.InternalName;
import dev.fiz.bootstrap.names.ToInternalName;

/**
 * Represents anything that may be pushed on to the JVM stack.
 * Resolvables are a type of Calculable that do not require any instructions to be executed after pushing.
 * After the push method is invoked, only 1 value should be added to the stack.
 */
public interface Pushable extends ToInternalName {
	/**
	 * Pushes this value on to the stack. Executes sub-pushes and instructions if needed. Use this over pushType() whenever an InternalName is not required.
	 *
	 * @param actor any Actor
	 * @return instance of implementing class; whatever "this" is
	 * @throws Exception if pushing is impossible
	 */
	public Pushable push(final Actor actor) throws Exception;

	/**
	 * Pushes this value on to the stack. Returns the type of the value. Should call push().
	 *
	 * @param actor any Actor
	 * @return pushed value type
	 * @throws Exception if pushing is impossible
	 * @see Pushable#push(Actor)
	 */
	public InternalName pushType(final Actor actor) throws Exception;

	/**
	 * Attempts to parse a Resolvable symbol
	 *
	 * @param actor any Actor
	 * @param val   The symbol
	 * @return A Resolvable whose actual type corresponds to the symbol
	 * @throws UnimplementedException thrown if missing a symbol from the grammar
	 */
	public static Pushable parse(final Actor actor, final FizParser.ComponentContext val) throws Exception {
		// FIXME copied code from Assignable
		final FizParser.ExpressionContext targetExpression = ctx.expression(0);

		if(ctx.expression() == null) {
			Field field = actor.unit.getEquivalentField(new InstanceField(ctx.component().ID().getText(), null, false, actor.unit.getClazz()));
		} else {

		}

		if(targetExpression.expression() == null) {
			final String varname = targetExpression.component().ID().getText();
			if(actor.unit.getCurrentScope().contains(varname))
				return actor.unit.getLocalVariable(varname);
			else
				return actor.unit.getEquivalentField(new InstanceField(varname, null, false, actor.unit.getClazz()));
		} else {

			if(address.ID().size() > 1) {
				StaticField field = null;
				for(int i = 0; i < address.ID().size(); i++) {
					if(field == null)

					else
					field = actor.unit.getEquivalentField(new InstanceField(address.ID(i).getText(), null, false, field));
				}
				return field;
			}
		}

		if(val.literal() != null)
			return Literal.parseLiteral(val.literal(), actor);
		else if(val.addressable() != null && val.addressable().ID().size() == 1) {
			final String id = val.addressable().ID(0).getText();
			if(actor.unit.hasLocalVariable(id))
				return actor.unit.getLocalVariable(id);
			else if(actor.unit.hasConstant(id))
				return actor.unit.getConstant(id);
			else
				throw new SymbolResolutionException(id);
		} else if(val.indexAccess() != null)
			return new IndexAccess(actor.unit.getLocalVariable(val.indexAccess().ID().getText()), Expression.parseExpressionContext(val.indexAccess().expression(), actor));
		else if(val.subSequence() != null)
			return new SubSequence(val.subSequence(), actor);
		else if(val.arrayLength() != null)
			return new ArrayLength(actor.unit.getLocalVariable(val.arrayLength().ID().getText()));
		else if(val.R_NULL() != null)
			return new Null();
		else if(val.methodCall() != null) {
			// FIXME this code is duplicated from consumeMethodCall() in CompilationUnit
			final FizParser.MethodCallContext mtd = val.methodCall();
			final String methodName = mtd.addressable().ID().get(mtd.addressable().ID().size() - 1).getText();
			if(actor.unit.isImported(methodName))
				return new NewObject(mtd, actor);
			else
				return new MethodCall(mtd, actor);
		} else if(val.addressable().ID().size() > 1) {

		} else
			throw new UnimplementedException("a type of Pushable wasn't parsed correctly\n The input text was \"" + val.getText() + "\"");
	}

}
