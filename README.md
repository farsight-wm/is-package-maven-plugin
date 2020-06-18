# farsight:is-package-maven-plugin
[![License: MIT](https://img.shields.io/badge/License-MIT-silver.svg)](https://opensource.org/licenses/MIT)

A small maven-plugin assisting in building IS-packages that depend on jar files.

## Features 
 - copy jar files from compile or runtime dependencies into the `code/jars (/static)` directory of the is package.
 
## Usage
 1. Create the sources for the IS package(s) (eg. in the folder `src/is/<name-of-IS-package>`
 1. Configure your build to use the plugin:

```xml
<plugin>
	<groupId>farsight-wm</groupId>
	<artifactId>is-package-maven-plugin</artifactId>
	<version>1.0.0</version>
	<executions>
		<execution>
			<goals>
				<goal>is-package</goal>
			</goals>
			<configuration>
				<packages>
					<package>
						<name>name-of-IS-package</name>
						<jars>
							<jar>
								<id><!-- jar dependency --></id>
							</jar>
						</jars>
					</package>
				</packages>
			</configuration>
		</execution>
	</executions>
</plugin>
```

You can add multiple packages with multiple jars. Each jar entry can have the keys:

| key       | required | type       | description           |
| --------- | -------- | ---------- | --------------------- |
| id        | yes      | `String`  | Id that defines the dependency (Format `<groupId>:<artifactId>`) |
| staticJar | no       | `Boolean` | If true the jar will be placed in `code/jars/static`. Default is false. |
| filename  | no       | `String`  | If provided the jar will get the defined filename. If not the jar is named `<artifactId>-<version>.jar` |

The output will be generated in `target/is/<name-of-IS-package>`
The input and output directory can be modified by setting `source` and `target` plugin parameter. E.g:
```xml
<!-- ... -->
<configuration>
	<source>src/main/is</source>
	<target>target/is-packages</source>
</configuration>
```
Default source is `src/is`
Default target is `target/is` 

## Nice-to-Have Features / TODO
 - [ ] compile java services
 - [ ] create java service nodes from annotations in the source code
 - [ ] create importable zip archive
 - [ ] create/update entries in mainfest.v3
