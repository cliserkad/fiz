package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class ElseIfTest {

	@Test
	public void testElseIf() {
		new StandardFizTest("ElseIf", null, list("pass")).testFiz();
	}

	@Test
	public void testIfElse() {
		new StandardFizTest("IfElse", null, list("passpass")).testFiz();
	}

	@Test
	public void testElseIfNoBrackets() {
		new StandardFizTest("ElseIfNoBrackets", null, list("pass")).testFiz();
	}

	@Test
	public void testIfElseNoBrackets() {
		new StandardFizTest("IfElseNoBrackets", null, list("passpass")).testFiz();
	}

}
