package test.java;

import dev.fiz.bootstrap.CompilationDispatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FullCompileTest {

	@Test
	public void testFull() {
		try {
			new CompilationDispatcher(null, null, null).dispatchQuietly();
			assert true;
		} catch(Exception e) {
			fail(e);
		}
	}

}
