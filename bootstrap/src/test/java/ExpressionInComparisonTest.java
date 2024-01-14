package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class ExpressionInComparisonTest {

	@Test
	public void testExpressionInComparison() {
		new StandardFizTest("ExpressionInComparison", null, list("passpass")).testFiz();
	}

}
