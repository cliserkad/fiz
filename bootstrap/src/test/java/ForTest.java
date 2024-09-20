package test.java;

import org.junit.jupiter.api.Test;

import static xyz.cliserkad.util.BestList.list;

public class ForTest {

	@Test
	public void testForRange() {
		new StandardFizTest("ForRange", null, list("01234567890123456789123456789")).testFiz();
	}

	@Test
	public void testForArgs() {
		new StandardFizTest("ForArgs", list("hello world"), list("helloworld")).testFiz();
	}

}
