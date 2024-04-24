package dev.fiz.bootstrap;

import java.util.function.Function;

/**
 * A class that can be one of 2 types.
 * see AnyOf for more details
 */
public abstract class EitherOf<A, B> {

	private EitherOf(Object value) throws NullPointerException {
		AnyOf.failIfNull(value);
	}

	public <R> R match(Function<A, R> funcA, Function<B, R> funcB) {
		return switch(this) {
			case ElementA<A, ?> a -> funcA.apply(a.getValue());
			case ElementB<?, B> b -> funcB.apply(b.getValue());
			default -> throw new IllegalStateException("Failed to match type " + this.getClass().getName());
		};
	}

	public <R> R matchFallible(CheckedFunction<A, R> funcA, CheckedFunction<B, R> funcB) throws Exception {
		return switch(this) {
			case ElementA<A, ?> a -> funcA.apply(a.getValue());
			case ElementB<?, B> b -> funcB.apply(b.getValue());
			default -> throw new IllegalStateException("Failed to match type " + this.getClass().getName());
		};
	}

	protected abstract Object getValue();

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof EitherOf<?, ?> wrapper)
			return getValue().equals(wrapper.getValue());
		else
			return getValue().equals(obj);
	}

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

	public static final class ElementA<A, B> extends EitherOf<A, B> {

		private final A value;

		public ElementA(A value) throws NullPointerException {
			super(value);
			this.value = value;
		}

		@Override
		public A getValue() {
			return value;
		}

	}

	public static final class ElementB<A, B> extends EitherOf<A, B> {

		private final B value;

		public ElementB(B value) throws NullPointerException {
			super(value);
			this.value = value;
		}

		@Override
		public B getValue() {
			return value;
		}

	}

}
