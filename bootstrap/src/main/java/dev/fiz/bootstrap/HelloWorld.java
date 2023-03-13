package dev.fiz.bootstrap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// Build System Test Source
public class HelloWorld {
	public static void main(String[] args) {
		System.out.println("hello world");
	}

	@Contract(pure = true)
	@NotNull
	public static String getHelloWorld() {
		return "hello world";
	}
}
