package test.java;

import org.junit.jupiter.api.Test;

import static xyz.cliserkad.util.BestList.list;

public class AmountLoopTest {

	@Test
	public void testAmountLoop() {
		new StandardFizTest("AmountLoop", null, list("passpasspasspasspass")).testFiz();
	}

}
