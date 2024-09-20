package test.java;

import org.junit.jupiter.api.Test;
import xyz.cliserkad.util.Union3;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUnion3 {

	public Union3<String, Integer, Double> data;

	public String lambdaMatch() {
		return data.match((str -> {
			return str;
		}), (i -> {
			return "Integers get multiplied: " + i * 5;
		}), (dbl -> {
			return "Doubles get addition: " + (dbl + 3.14);
		}));
	}

	public String switchMatch() {
		return switch(data) {
			case Union3.A<String, ?, ?> a -> a.getValue();
			case Union3.B<?, Integer, ?> b -> "Integers get multiplied: " + b.getValue() * 5;
			case Union3.C<?, ?, Double> c -> "Doubles get addition: " + (c.getValue() + 3.14);
		};
	}

	@Test
	public void testWithString() {
		TestUnion3 example = new TestUnion3();
		example.data = new Union3.A<>("Hello, World!");
		assertEquals(example.lambdaMatch(), example.switchMatch());
		assertEquals(example.lambdaMatch(), "Hello, World!");
		assertEquals(example.switchMatch(), "Hello, World!");
	}

	@Test
	public void testWithInteger() {
		TestUnion3 example = new TestUnion3();
		example.data = new Union3.B<>(2);
		assertEquals(example.lambdaMatch(), example.switchMatch());
		assertEquals(example.lambdaMatch(), "Integers get multiplied: 10");
		assertEquals(example.switchMatch(), "Integers get multiplied: 10");
	}

	@Test
	public void testWithDouble() {
		TestUnion3 example = new TestUnion3();
		example.data = new Union3.C<>(3.14);
		assertEquals(example.lambdaMatch(), example.switchMatch());
		assertEquals(example.lambdaMatch(), "Doubles get addition: 6.28");
		assertEquals(example.switchMatch(), "Doubles get addition: 6.28");
	}

}
