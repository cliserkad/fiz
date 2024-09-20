package test.java;

import org.junit.jupiter.api.Test;
import xyz.cliserkad.util.BestList;

public class PrintArgTest {

	public static final String[] ARGS = { "hello", "test" };

	@Test
	public void testPrintArg() {
		BestList<String> printArgArguments = new BestList<>(ARGS);
		new StandardFizTest("PrintArg", printArgArguments, printArgArguments).testFiz();
	}

}
