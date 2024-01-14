package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class FieldTest {

	@Test
	public void testFieldInstantiation() {
		new StandardFizTest("test.fiz.obj.Car", null, list("success")).testFiz();
	}

	@Test
	public void testFieldAccess() {
		new StandardFizTest("test.fiz.obj.Car2", null, list("1998 Honda Accord")).testFiz();
	}

}
