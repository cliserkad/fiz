package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class HelloWorldTest {

	@Test
	public void testHelloWorld() {
		new StandardFizTest("HelloWorld", null, list("hello world")).testFiz();
	}

}
