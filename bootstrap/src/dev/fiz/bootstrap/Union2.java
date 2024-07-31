package dev.fiz.bootstrap;

import java.util.function.Function;

/**
 * A class that can be one of 2 types.
 * see Union3 for more details
 */
public abstract sealed class Union2<TypeA, TypeB> {

	private Union2(Object value) throws NullPointerException {
		Union3.failIfNull(value);
	}

	public <ReturnType> ReturnType match(Function<TypeA, ReturnType> funcA, Function<TypeB, ReturnType> funcB) {
		return switch(this) {
			case Union2.A<TypeA, ?> a -> funcA.apply(a.getValue());
			case Union2.B<?, TypeB> b -> funcB.apply(b.getValue());
		};
	}

	public <ReturnType> ReturnType matchFallible(CheckedFunction<TypeA, ReturnType> funcA, CheckedFunction<TypeB, ReturnType> funcB) throws Exception {
		return switch(this) {
			case Union2.A<TypeA, ?> a -> funcA.apply(a.getValue());
			case Union2.B<?, TypeB> b -> funcB.apply(b.getValue());
		};
	}

	protected abstract Object getValue();

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Union2<?, ?> wrapper)
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

	public static final class A<TypeA, TypeB> extends Union2<TypeA, TypeB> {

		private final TypeA value;

		public A(TypeA value) throws NullPointerException {
			super(value);
			this.value = value;
		}

		@Override
		public TypeA getValue() {
			return value;
		}

	}

	public static final class B<TypeA, TypeB> extends Union2<TypeA, TypeB> {

		private final TypeB value;

		public B(TypeB value) throws NullPointerException {
			super(value);
			this.value = value;
		}

		@Override
		public TypeB getValue() {
			return value;
		}

	}

}
