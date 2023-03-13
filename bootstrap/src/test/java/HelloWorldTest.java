package test.java;

import dev.fiz.bootstrap.HelloWorld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {

	@Test
	public void testHelloWorld() {
		Assertions.assertEquals(HelloWorld.getHelloWorld(), "hello world");
	}

}
