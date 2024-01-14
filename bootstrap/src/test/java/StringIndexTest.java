package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class StringIndexTest {

	@Test
	public void testStringIndex() {
		new StandardFizTest("StringIndex", null, list("l")).testFiz();
	}

}
