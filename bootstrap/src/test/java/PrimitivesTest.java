package test.java;

import org.junit.jupiter.api.Test;

import static dev.fiz.bootstrap.BestList.list;

public class PrimitivesTest {

	public static final String OUTPUT0 = "true" + "120" + "32000" + "C" + "64000" + "1.5" + "string";
	public static final String OUTPUT1 = "133444444444444" + "9.2348936E7" + OUTPUT0;

	@Test
	public void testTheOnesThatAreNormal() {
		new StandardFizTest("Primitives", null, list(OUTPUT0)).testFiz();
	}

	@Test
	public void testTheOnesThatPissMeOff() {
		new StandardFizTest("AllPrimitives", null, list(OUTPUT1)).testFiz();
	}

}
