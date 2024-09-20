package test.java;

import org.junit.jupiter.api.Test;

import static xyz.cliserkad.util.BestList.list;

public class ExpressionInComparisonTest {

	@Test
	public void testExpressionInComparison() {
		new StandardFizTest("ExpressionInComparison", null, list("passpass")).testFiz();
	}

}
