package farsight.ispackage.mojo;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.maven.plugins.annotations.Parameter;

public class IsPackageDefinition {
	
	public static class IsJarDependency {
		@Parameter
		public boolean staticJar = false;
		
		@Parameter(required = true)
		public String id;
		
		@Parameter(required = false)
		public String filename;
		
		public String toString() {
			return new ToStringBuilder(this)
					.append("id", id)
					.build();
		}		
	}
	
	
	@Parameter(required = true)
	public String name;
	
	@Parameter(required = false)
	public List<IsJarDependency> jars;	
	
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.append("jars", jars)
				.build();
	}

}
