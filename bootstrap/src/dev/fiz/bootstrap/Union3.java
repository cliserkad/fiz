package dev.fiz.bootstrap;

import java.util.function.Function;

/**
 * A class that can be one of three types. This is useful for three unrelated types that need to be stored in the same variable. This class will refuse to store null values.
 */
public abstract sealed class Union3<TypeA, TypeB, TypeC> {

	/**
	 * prevent outside subclassing by making default constructor private refuse to store null values
	 */
	private Union3(Object obj) throws NullPointerException {
		failIfNull(obj);
	}

	/**
	 * used by callers to apply 1 of 3 functions to the value, depending on its type
	 */
	public <ReturnType> ReturnType match(Function<TypeA, ReturnType> funcA, Function<TypeB, ReturnType> funcB, Function<TypeC, ReturnType> funcC) {
		return switch(this) {
			case Union3.A<TypeA, ?, ?> a -> funcA.apply(a.getValue());
			case Union3.B<?, TypeB, ?> b -> funcB.apply(b.getValue());
			case Union3.C<?, ?, TypeC> c -> funcC.apply(c.getValue());
			default -> throw new IllegalStateException("Failed to match type " + this.getClass().getName());
		};
	}

	/**
	 * This method is protected to force the caller to access the value through a subclass to guarantee a valid type.
	 */
	protected abstract Object getValue();

	/**
	 * Provides access to equals() method of underlying value.
	 * If the Object given is a Union3, it will be unwrapped to compare underlying values.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Union3<?, ?, ?> wrapper)
			return getValue().equals(wrapper.getValue());
		else
			return getValue().equals(obj);
	}

	/**
	 * Provides access to hashCode() method of underlying value.
	 */
	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	/**
	 * Provides access to toString() method of underlying value.
	 */
	@Override
	public String toString() {
		return getValue().toString();
	}

	public static void failIfNull(Object o) throws NullPointerException {
		if(o == null)
			throw new NullPointerException("value cannot be null");
	}

	public static final class A<TypeA, TypeB, TypeC> extends Union3<TypeA, TypeB, TypeC> {

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

	public static final class B<TypeA, TypeB, TypeC> extends Union3<TypeA, TypeB, TypeC> {

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

	public static final class C<TypeA, TypeB, TypeC> extends Union3<TypeA, TypeB, TypeC> {

		private final TypeC value;

		public C(TypeC value) throws NullPointerException {
			super(value);
			this.value = value;
		}

		@Override
		public TypeC getValue() {
			return value;
		}

	}

}
