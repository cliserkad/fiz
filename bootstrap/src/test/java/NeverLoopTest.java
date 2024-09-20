package test.java;

import org.junit.jupiter.api.Test;

import static xyz.cliserkad.util.BestList.list;

public class NeverLoopTest {

	@Test
	public void testNeverLoop() {
		new StandardFizTest("NeverLoop", null, list("hello from NeverLoop")).testFiz();
	}

}
