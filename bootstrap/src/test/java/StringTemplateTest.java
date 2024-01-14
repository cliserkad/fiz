package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class StringTemplateTest {

	@Test
	public void testTemplate() {
		new StandardFizTest("StringTemplate", null, list("STR is val1 and a is val2")).testFiz();
	}

	@Test
	public void testEscape() {
		new StandardFizTest("StringTemplate2", null, list("$10 is less than $55 because 10 < 55$$$ money money money $$$")).testFiz();
	}

	@Test
	public void testStore() {
		new StandardFizTest("StringTemplate3", null, list("Template with val1 and val2 stored to a string")).testFiz();
	}

	@Test
	public void testVarsEndingInNum() {
		new StandardFizTest("StringTemplate4", null, list("99 is lower than 230")).testFiz();
	}

}
