package dev.fiz.plugin;

import dev.fiz.bootstrap.CompilationDispatcher;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;

import java.io.File;

/**
 * Compiles fiz files in a Maven project
 */
@Mojo(
	name = "fiz",
	defaultPhase = LifecyclePhase.COMPILE,
	requiresDependencyResolution = ResolutionScope.TEST,
	requiresOnline = false,
	requiresProject = true
)
@Execute(
	goal = "fiz",
	phase = LifecyclePhase.COMPILE
)
public class FizCompilerPlugin extends AbstractMojo {

	/**
	 * Used via injection for Maven Plugin
	 */
	public FizCompilerPlugin() {

	}

	/**
	 * For unit testing
	 */
	public FizCompilerPlugin(final File sourceDirectory, final File outputDirectory) {
		this.sourceDirectory = sourceDirectory;
		this.outputDirectory = outputDirectory;
	}

	/**
	 * Location of compiled (output) .class files
	 */
	@Parameter(
		name = "outputDirectory",
		property = "project.build.directory",
		required = true,
		readonly = false
	)
	private File outputDirectory;

	/**
	 * Location of source (input) .fiz files
	 */
	@Parameter(
		name = "sourceDirectory",
		property = "project.build.sourceDirectory",
		required = true,
		readonly = false
	)
	private File sourceDirectory;

	/**
	 * Compile files
	 *
	 * @throws MojoFailureException Forwarded exceptions from compiler
	 */
	public void execute() throws MojoFailureException {
		// append a slash to the file path if it isn't a directory
		if(!outputDirectory.isDirectory())
			outputDirectory = new File(outputDirectory.getPath() + "/");
		getLog().info("Input Directory: " + sourceDirectory);
		getLog().info("Output Directory: " + outputDirectory);
		// dispatch compilation
		final CompilationDispatcher dispatcher = new CompilationDispatcher(sourceDirectory, CompilationDispatcher.FIZ_FILTER, outputDirectory);
		try {
			dispatcher.dispatchQuietly();
		} catch(Exception e) {
			throw new MojoFailureException(e.getMessage());
		}
	}

}
