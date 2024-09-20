package test.java;

import org.junit.jupiter.api.Test;
import xyz.cliserkad.util.BestList;

public class IsEvenTest {

	public static final int[] NUMS = { -50, -30, -15, -5, -3, -1, 0, 1, 3, 5, 15, 30, 40, 45, 50, 99, 22 };

	@Test
	public void testIsEven() {
		BestList<String> arguments = new BestList<String>();
		for(int n : NUMS)
			arguments.add("" + n);
		BestList<String> outputs = new BestList<String>();
		for(int n : NUMS)
			outputs.add(isEven(n));

		new StandardFizTest("IsEven", arguments, outputs).testFiz();
	}

	public String isEven(int n) {
		if(n % 2 == 0)
			return "true";
		else
			return "false";
	}

}
