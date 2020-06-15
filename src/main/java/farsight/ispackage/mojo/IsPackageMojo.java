package farsight.ispackage.mojo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import farsight.ispackage.mojo.IsPackageDefinition.IsJarDependency;

/**
 * Mojo that copies required jar file into an is package
 */
@Mojo(name = "is-package", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class IsPackageMojo extends AbstractMojo {

	@Parameter(defaultValue = "src/is")
	public String source;

	@Parameter(defaultValue = "target/is")
	public String target;

	@Parameter(required = true)
	public List<IsPackageDefinition> packages;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	private File sourceBase;
	private File targetBase;

	public void execute() throws MojoExecutionException {
		sourceBase = new File(project.getBasedir(), source);
		targetBase = new File(project.getBasedir(), target);

		for (IsPackageDefinition isPackage : packages) {
			buildIsPackage(isPackage);
		}
	}

	private void buildIsPackage(IsPackageDefinition isPackage) throws MojoExecutionException {
		getLog().info("Building IS-Package: " + isPackage.name);

		File pkgSource = new File(sourceBase, isPackage.name);
		File pkgTarget = new File(targetBase, isPackage.name);

		if (!pkgSource.isDirectory()) {
			throw new MojoExecutionException("IS-Package source not found at: " + pkgSource);
		}

		pkgTarget.mkdirs();

		try {
			FileUtils.copyDirectoryStructureIfModified(pkgSource, pkgTarget);
		} catch (IOException e) {
			throw new MojoExecutionException("Error copying source files", e);
		}

		if (isPackage.jars != null) {
			// append jars

			File jars = new File(pkgTarget, "code/jars");
			File staticJars = new File(jars, "static");

			for (IsJarDependency jar : isPackage.jars) {
				Artifact artifact = getArtifact(jar);
				if (artifact == null)
					throw new MojoExecutionException("Artifact is not a dependency: " + jar.id);

				getLog().info("Adding jar: " + artifact);
				File outDirectory = jar.staticJar ? staticJars : jars;
				outDirectory.mkdirs();

				File jarFile = artifact.getFile();
				if (jarFile == null || !jarFile.canRead())
					throw new MojoExecutionException("Cannot read artifact file: " + jarFile);

				File output = new File(outDirectory, jar.filename == null ? jarFile.getName() : jar.filename);

				try {
					FileUtils.copyFile(jarFile, output);
				} catch (IOException e) {
					throw new MojoExecutionException("Cannot copy artifact file: " + jarFile, e);
				}
			}

		}

	}

	private Artifact getArtifact(IsJarDependency jar) {
		return jar.id.equals(project.getGroupId() + ":" + project.getArtifactId()) ? project.getArtifact()
				: (Artifact) project.getArtifactMap().get(jar.id);
	}
}
