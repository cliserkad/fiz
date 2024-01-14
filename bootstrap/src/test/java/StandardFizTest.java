package test.java;

import dev.fiz.bootstrap.BestList;
import dev.fiz.bootstrap.CompilationDispatcher;
import org.apache.commons.io.filefilter.RegexFileFilter;

import static dev.fiz.bootstrap.BestList.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StandardFizTest {

	public static final String JAVA_CMD = "java -cp target/classes ";

	public final String clazz;
	private final BestList<String> arguments;
	private final BestList<String> expectedOutputs;

	/**
	 * Makes a StandardFizTest
	 *
	 * @param clazz           name of class
	 * @param arguments       a list of sets of command line arguments
	 * @param expectedOutputs a list of expected outputs for each set of arguments
	 */
	public StandardFizTest(final String clazz, final BestList<String> arguments, final BestList<String> expectedOutputs) {
		this.clazz = clazz;

		if(arguments != null)
			this.arguments = arguments;
		else
			this.arguments = list("");

		if(expectedOutputs != null)
			this.expectedOutputs = expectedOutputs;
		else
			this.expectedOutputs = list("");

		if(this.arguments.size() != this.expectedOutputs.size())
			throw new IllegalStateException("arguments and expectedOutputs must have the same length. Check both inputs on " + getClass().getName() + " constructor");
	}

	/**
	 * Makes a StandardFizTest that has no arguments and no output. Use this
	 * constructor for .fiz files that use assert instead of printing.
	 *
	 * @param clazz name of class
	 */
	public StandardFizTest(final String clazz) {
		this(clazz, null, null);
	}

	public void testFiz() {
		try {
			// compile .fiz file
			new CompilationDispatcher(null, new RegexFileFilter(fileName()), null).dispatchQuietly();
			// run .class file
			for(int i = 0; i < arguments.size(); i++) {
				ProcessOutput process = ProcessOutput.runProcess(JAVA_CMD + clazz + " " + arguments.get(i));
				if(!process.getErrors().isEmpty()) {
					System.err.println("Encountered error running " + process.getCommand() + " @ " + System.getProperty("user.dir"));
					for(String s : process.getErrors())
						System.err.println(s);
				}
				assertTrue(process.getErrors().isEmpty());
				assertEquals(0, process.getExitValue());
				assertEquals(expectedOutputs.get(i), process.getOutput().squish());
			}
		} catch(Exception e) {
			e.printStackTrace();
			assert false;
		}
	}

	/**
	 * @return className + ".fiz"
	 */
	public String fileName() {
		if(clazz.contains("."))
			return ".*" + clazz.substring(clazz.lastIndexOf('.') + 1) + ".fiz";
		else
			return clazz + ".fiz";
	}

}
