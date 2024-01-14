package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class NeverLoopTest {

	@Test
	public void testNeverLoop() {
		new StandardFizTest("NeverLoop", null, list("hello from NeverLoop")).testFiz();
	}

}
