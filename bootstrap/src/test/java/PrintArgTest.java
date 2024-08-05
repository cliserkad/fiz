package test.java;

import dev.fiz.bootstrap.BestList;
import org.junit.jupiter.api.Test;

public class PrintArgTest {

	public static final String[] ARGS = { "hello", "test" };

	@Test
	public void testPrintArg() {
		BestList<String> printArgArguments = new BestList<>(ARGS);
		new StandardFizTest("PrintArg", printArgArguments, printArgArguments).testFiz();
	}

}
