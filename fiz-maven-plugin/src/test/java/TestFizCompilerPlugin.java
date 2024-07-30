package test.java;

import dev.fiz.plugin.FizCompilerPlugin;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TestFizCompilerPlugin {

	/**
	 * Test the execute method
	 */
	@Test
	public void testExecute() {
		final File currentWorkingDirectory = new File(System.getProperty("user.dir"));
		final File mavenOutput = new File(currentWorkingDirectory, "/target/classes/");
		FizCompilerPlugin plugin = new FizCompilerPlugin(currentWorkingDirectory, mavenOutput);
		assertDoesNotThrow(plugin::execute);
	}

}
