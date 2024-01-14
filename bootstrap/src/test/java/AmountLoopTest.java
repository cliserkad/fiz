package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class AmountLoopTest {

	@Test
	public void testAmountLoop() {
		new StandardFizTest("AmountLoop", null, list("This message should be printed 5 timesThis message should be printed 5 timesThis message should be printed 5 timesThis message should be printed 5 timesThis message should be printed 5 times")).testFiz();
	}

}
