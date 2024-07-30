package dev.fiz.bootstrap;

import java.util.function.Function;

@FunctionalInterface
public interface CheckedFunction<T, R> {

	/**
	 * Applies this function to the given argument.
	 * This may throw an Exception.
	 *
	 * @param t the function argument
	 * @return the function result
	 * @throws Exception if an error occurs
	 */
	R apply(T t) throws Exception;

	/**
	 * Returns a function that always returns its input argument.
	 *
	 * @param <T> the type of the input and output objects to the function
	 * @return a function that always returns its input argument
	 */
	static <T> Function<T, T> identity() {
		return t -> t;
	}

}
