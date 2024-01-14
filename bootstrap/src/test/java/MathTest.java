package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class MathTest {

	@Test
	public void testIntComparisons() {
		new StandardFizTest("IntComparisons", null, list("pass   0 == 1pass   0 != 1pass   0 < 1pass   0 <= 1pass   0 > 1pass   0 >= 1done")).testFiz();
	}

	@Test
	public void testFloatComparisons() {
		new StandardFizTest("FloatComparisons", null, list("pass   0.0 == 1.0pass   0.0 != 1.0pass   0.0 < 1.0pass   0.0 <= 1.0pass   0.0 > 1.0pass   0.0 >= 1.0done")).testFiz();
	}

	@Test
	public void testLongComparisons() {
		new StandardFizTest("LongComparisons", null, list("pass   0 == 1pass   0 != 1pass   0 < 1pass   0 <= 1pass   0 > 1pass   0 >= 1done")).testFiz();
	}

	@Test
	public void testDoubleComparisons() {
		new StandardFizTest("DoubleComparisons", null, list("pass   0.0 == 1.0pass   0.0 != 1.0pass   0.0 < 1.0pass   0.0 <= 1.0pass   0.0 > 1.0pass   0.0 >= 1.0done")).testFiz();
	}

	@Test
	public void testAllMathOperations() {
		new StandardFizTest("AllMathOperations", null, list("All math operations passed")).testFiz();
	}

}
