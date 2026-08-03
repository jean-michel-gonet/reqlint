# Introduction

Reqlint is a Doc-as-Code Maven plugin for SWAL3 requirements validation. 
ReqLint parses LaTeX specs and automated test outputs to perform graph static analysis, 
ensuring zero orphan requirements or unverified test cases. 
Keep your regulatory traceability matrices structurally sound and directly tied to your Java build pipeline.

# Why LaTeX?
Because it is a text based language, reasonably easy to learn,
that compiles beautiful PDF files.
Being text based, it is version control friendly.
This is an important feature for a requirement specification, 
because it allows to keep a detailed track of all changes, 
and also to have several authors.
Producing PDF documents is another important feature, 
as this format is standard, 
ready to be printed, 
most likely to be compatible with your electronic document management system.
Also, LaTeX has never stopped being used to produce documents since 1985.
By now it has hundreds or thousands of different solutions to produce any kind of printable artifact.
This includes tables, diagrams, equations, tables of content, bibliography...

# What does _Reqlint_ ?
_Reqlint_ reads two different sources:
- Your software requirement specification, provided it is written in LaTeX and you use the 
  expected vocabulary to surround your specification items.
- The surefire test reports.

From these two sources, it produces two different types of documents, and two subtypes each:
- The traceability matrix, showing the relations between _Equipment Requirement_, _Software Requirement_ and _Test Case_.
  The traceability matrix is split in two parts:
  - The traceability matrix itself.
  - The traceability matrix warnings, a list of inconsistencies found in the specification.
    Inconsistencies include orphans, broken links, duplicated identifiers, etc.
- The test report, showing the relations between _Test Cases_ and the actual logs of the automated tests.
  The test report is split in two parts:
  - The list of _Test Cases_ that have automated test scenarios associated, including
    the execution log and the final outcome (success / failure).
  - The list of _Test Cases_ warnings - test cases without automated test scenarios, 
    in failure, or their description does not match the automated scenario.

Additionally, it has a convenient tool to compile LaTeX documents into PDF documents. 

# Prerequisites
_Reqlint_ expects LaTeX to be installed in the host system, and present in the path.

When using Windows, you can choose from different distributions.
We've tested MikTex with success, so we recommend it.
When using macOS, you can install _texlive_ using _Homebrew_.

The expectation of _Reqlint_ is that you can open a command prompt, type the following commands,
and at least one of them should return no error:

```bash
lualatex -version
pdflatex -version
```

Additionally, you may want to install _PlantUML_, because it is the most common tool to write diagrams.
When using Windows, download the native *.zip from the official website,
unpack it,
and add it to the path.
When using macOS, install it using _Homebrew_.
In both cases, verify that you can type:

```bash
plantuml -version
```

# How to use the plugin

Start by having a Software Requirement Specification document written in LaTeX.
Then you write automated tests for your application that match the test cases defined in your specification.
Finally, you configure the plugin in your `pom.xml` file.

As _Reqlint_ is a _Maven_ plugin, it has the usual expectations on what folders you should use.
A software requirement specification is a documentation made of text files.
You must save them in a `/src/main/resources` folder or subfolder, within either a project or module.

You can check out the `sandbox` module for inspiration.

## Writing the specification
The specification has to be written in LaTeX.
To split the LaTeX document into more manageable files, you can use
the commands `\import`, `\subimport`, `\input` and `\include`.
The plugin only requires a small set of environment and commands.
Those need to be defined in your template file.

Only the names and arguments of the commands need to be verbatim.
You are at liberty to design the actual printable representation of the commands.

### The `equipmentrequirement` environment.

This environment takes two arguments:
- The identifier, that can be any string - but keep in mind is an identifier.
- The title, that can be any text - but don't make it too long.

There are no limits to what you can include inside the environment, except other _Reqlint_ environments.

For example:
```latex
\begin{equipentrequirement}{identifier}{Title}
    You can write here freely.
    You must not nest any of the other reqlint environments or commands.
\end{equipentrequirement}
```

### The `softwarerequirement` environment.

This environment takes two arguments:
- The identifier, that can be any string - but keep in mind is an identifier.
- The title, that can be any text - but don't make it too long.

There are no limits to what you can include inside the environment, except other _Reqlint_ environments.
You should add a `\childof` element, to identify the parent equipment requirement.  

For example:
```latex
\begin{softwarerequirement}{identifier}{Title}
    \childof{ER-100} % The parent equipment requirement.
    
    You can write here freely.
    You must not nest any of the other reqlint environments or commands.
\end{softwarerequirement}
```

### The `testcase` environment.

This environment takes two arguments:
- The identifier, that can be any string - but keep in mind is an identifier.
- The title, that can be any text - but don't make it too long.

There are no limits to what you can include inside the environment, except other _Reqlint_ environments.
You should add a `\childof` element, to identify the parent software requirement.
Optionally, you can nest a special `\testprocedure` environment,
where you can list a number of intermediary `\stage` to split long test procedures.

For example:

```latex
\begin{testcase}{identifier}{Title}
    \childof{ER-100} % The parent software requirement.
    
    You can write here freely.
    You must not nest any of the other reqlint environments or commands, except:
    \begin{testprocedure}
        \stage Given a couple of items
        \stage When smashing the items together
        \stage Then they're not broken
    \end{testprocedure}
\end{testcase}
```

### The `testprocedure` environment
Use this environment only inside the `testcase` environment.
It allows to place a list of `\stage` commands.

### The `\stage` command
Use this command only inside the `testprocedure` environment.
Each `\stage` is one test step.

### The `\childof` command.
Use this command only inside the `softwarerequirement` and `testcase` environments.
It identifies the parent specification item.
The parent of a `softwarerequirement` is an `equipmentrequirement`, 
and the parent of a `testcase` is a `softwarerequirement`.

### Add placeholders to your documentation

Because the plugin follows the inclusion commands (`\import`, `\input`, etc.)
you can subdivide your document in as many files as you want.
This is also helpful to prepare the placeholders, one for each of the LaTeX fragments 
that the plugin can produce:
1. The traceability matrix.
2. The traceability warnings.
3. The test report.
4. The test warnings.

To prepare a placeholder, decide where you want the fragment in the document, and then use `\input` to include it.
To avoid error messages while you type, create a dummy content for the fragment.
_Reqlint_ will replace it during the build.

For example, if you want a section of the document containing the test report
and the test warnings, you can create a folder called `testresults`. 
In the folder you write a file with a content similar to the example below,
and call it `testresults.tex`:

```latex
\section{Test results}
\label{sec:testresults}

\subsection{Warnings found while building test result report}
\label{subsec:testresults-warning}

\input{testresults-warning}

\subsection{Test results report}
\label{subsec:testresults-report}

\input{testresults-report}
```

Then create one `testresults-warning.tex` and one `testresults-report.tex`,
both empty or with some dummy content like:

```latex
THIS SECTION IS GENERATED AT BUILD TIME
```

Later on, you can configure the plugin to write the fragments
over the files with dummy content.

### Produce multiple PDF files
Depending on your quality system, you may be required to split the different parts into separated documents.
When this is the case, create one main LaTeX file per final document.
_Reqlint_ only requires that one main LaTeX document includes all equipment requirements, software requirements and test cases.
You are free to have as many additional root documents as you need.

## Automatizing the test scenarios
You can automatize your test scenarios as you would do with any Java project.
You only need to tag them appropriately so _Reqlint_ can recognize which test belongs to which test case.

### Tagging _Cucumber_ tests
To tag a _Cucumber_ test, use `@TC_IDENTIFIER` in the `Scenario` line.
For example

```cucumber
Scenario: @TC_101 Items can endure being smashed
    Given: XX
    When: YY
    Then: ZZ
```

If your scenario is too long, you may want to improve readability by splitting it into stages.
For that you must create a _Cucumber_ command that logs using the following pattern:

```pattern
   # Stage [n] - [Describe the stage]
```

Where `[n]` is an integer (without brackets), and the stage description is any string,
without new line, also without brackets.

When reading the surefire test reports, _Reqlint_ recognizes the pattern, and intersects
a space between logs in that exact place.

Additionally, if the corresponding `testcase` has a `testprocedure`, _Reqlint_ tries to match
the `\stages` in the test procedure with the stages in the test report.
If it can't match the stages, it outputs a test case warning. 

### Tagging _JUnit_ tests

To tag a _JUnit_ test, you can add the test case identifier at any part of the method that provides the test.
For this to work without problems, you must use test case identifiers that only have characters legal in _Java_ methods.

To split the test into stages, you can directly log in the method.

```java
@Test
public void TC_101_can_do_something_nice() {

    // ...
    LOGGER.info("# Stage 1 - This is going to be nice");

    // ...
}
```

## Configuring the plugin

_Reqlint_ is designed to integrate itself with the rest of the usual _Maven_ plugins.
There are several steps you want to consider, some of them using other plugins.
The elements below are just an example of how you can set up the production of documentation.

You can see in action all examples below in the `sandbox` module.

### Preprocessing the LaTeX files
At the very beginning of the `mvn` compilation, _Maven_ copies all resource files into 
the `target/classes` folder. While it does that, you can activate the resource filtering,
which allows to replace some of the maven variables by their actual value.

This is how

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <version>3.0.2</version>
    <configuration>
        <resource>
            <includes>
                <include>*.tex</include>
            </includes>
            <filtering>true</filtering>
        </resource>
    </configuration>
</plugin>
```

When you enable filtering as shown above, you can use `@xxx@` to let _Maven_ replace
the literals by the dynamic values of the variables.
For example, this inserts the project version number in the `*.tex` file:

```latex
This is software requirements specification for version @project.version@. 
```

### Compiling diagrams into image resources
Not all resources in a LaTeX document are `*.tex` files.
Typically, diagrams are built with some other language.
Below we show how to use _PlantUml_, but there are other packages that do a similar job.
Adapt the procedure to your own case.

To compile images using _PlantUml_, you can use the _AntRun_ plugin.
The best phase to process the images is `process-resources`, 
because for this you just need the resources to be present.

In the example below, the `parallel` attribute creates a list of files matching the `fileset`,
and then passes the list as last argument to the specified `executable`.
The argument `--eps` makes _PlantUml_ to compile diagrams into `*.eps` files,
which are particularly suitable for LaTeX.

```xml
<plugin>
    <!-- Uses PlantUml to convert *.puml files into *.eps files -->
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-antrun-plugin</artifactId>
    <executions>
        <execution>
            <id>batch-process-files</id>
            <phase>process-resources</phase>
            <goals>
                <goal>run</goal>
            </goals>
            <configuration>
                <target>
                    <apply executable="plantuml" parallel="true">
                        <arg value="--eps" />
                        <fileset dir="${project.build.directory}/classes/documentation" includes="**/*.puml" />
                    </apply>
                </target>
            </configuration>
        </execution>
    </executions>
</plugin>
```

In the LaTeX document, you can use the following snippet to include the diagram:

```latex
\begin{figure}[htbp]
    \centering
    \includegraphics[
      scale=0.5,
      max width=0.8\textwidth,    % Never exceeds 80% of text area width
      max height=0.8\textheight,  % Never exceeds 80% of text area height
      keepaspectratio             % Preserves aspect ratio without distortion
    ]{srs-elements}
    \caption{Software requirement specification relationship} 
    \label{fig:traceability}
\end{figure}
```

The usual is to place the `*.puml` file in the same folder as the `*.tex` file,
and let _PlantUml_ to compile it into the corresponding `*.eps`.
LaTeX's `includegraphics` looks for `*.eps` and `*.png` extensions when
not provided with an extension.

### Producing the traceability matrix

The goal name to produce the traceability matrix is `traceability-matrix`.
The most appropriate _Maven_ phase to produce the traceability matrix is `process-resources`,
because you only need the project resources, and no compilation is required.

The `traceability-matrix` goal takes the following parameters:
- `specificationLatexDocument`: The main LaTeX file that contains the software requirements specification.
- `traceabilityMatrixOutput`: The name of the LaTeX fragment where to output the traceability matrix. 
- `traceabilityWarningsOutput`: The name of the LaTeX fragment where to output the traceability warnings.

The two output files should point to the placeholders you've prepared in advance (see above).
This is an example illustrating how to configure this goal when
all documentation is in the `resources/documentation` folder:

```xml
<plugin>
    <groupId>com.reqlint</groupId>
    <artifactId>reqlint-maven-plugin</artifactId>
    <configuration>
      <specificationLatexDocument>${project.build.outputDirectory}/documentation/root.tex</specificationLatexDocument>
      <traceabilityMatrixOutput>${project.build.outputDirectory}/documentation/traceability/traceability-matrix.tex</traceabilityMatrixOutput>
      <traceabilityWarningsOutput>${project.build.outputDirectory}/documentation/traceability/traceability-warnings.tex</traceabilityWarningsOutput>
    </configuration>
    <executions>
        <!-- The traceability matrix -->
        <execution>
            <id>traceability-matrix</id>
            <!-- To be executed when resources are present in the target folder -->
            <phase>process-resources</phase>
            <goals>
                <goal>traceability-matrix</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Producing the test report

The goal name to produce the test report is `test-report`.
The most appropriate _Maven_ phase to produce the test report is `post-integration-test`,
because you need the _Surefire_ test reports to be present.

The `test-report` goal takes the following parameters:
- `specificationLatexDocument`: The main LaTeX file that contains the software requirements specification.
- `testResultsOutput`: The name of the LaTeX fragment where to output the test report.
- `testWarningsOutput`: The name of the LaTeX fragment where to output the test warnings.

All three files have paths relative to the project `resources` folder.
The two output files should point to the placeholders you've prepared in advance (see above).
This is an example illustrating how to configure this goal when
all documentation is in the `resources/documentation` folder:

```xml
<plugin>
    <groupId>com.reqlint</groupId>
    <artifactId>reqlint-maven-plugin</artifactId>
    <configuration>
      <specificationLatexDocument>${project.build.outputDirectory}/documentation/root.tex</specificationLatexDocument>
      <testResultsOutput>${project.build.outputDirectory}/documentation/testresults/testresults-report.tex</testResultsOutput>
      <testWarningsOutput>${project.build.outputDirectory}/documentation/testresults/testresults-warning.tex</testWarningsOutput>
    </configuration>
    <executions>
        <!-- The test results -->
        <execution>
            <id>test-results</id>
            <!-- To be executed after all integration tests -->
            <phase>post-integration-test</phase>
            <goals>
                <goal>test-results</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Compiling the LaTeZ files

You may have already set up a LaTeX compilation process, in which case you don't need to use
this plugin. 

The _Reqlint_ goal to compile the LaTeX files into PDF is `compile-latex`.
The most appropriate _Maven_ phase to run it is `post-integration-test`,
just after the `test-result` execution.

The `compile-latex` plugin takes the following parameters:
- `specificationLatexDocument`: The main LaTeX file that contains the software requirements specification.
- `latexTool`: The command to execute LaTeX. This is going to be either `lualatex` or `pdflatex`. 
   Most of the distributions offer both, so probably both are working. 
- `bibTool`: The command to execute the bibliography tool in LaTeX.
  It depends on your distribution, it can be `biber` or `bibtex`.
  If you don't use bibliography, then you can specify `none`.
- `additionalLatexDocuments`: An optional list of additional latex documents to compile into PDF files.

The LaTeX compilation produces a `*.pdf` file with the same name and path as `specificationLatexDocument`
and, if you specified additional files,
one `*.pdf` per each.

This is an example:
```xml

<plugin>
    <groupId>com.reqlint</groupId>
    <artifactId>reqlint-maven-plugin</artifactId>
    <configuration>
        <specificationLatexDocument>${project.build.outputDirectory}/documentation/root.tex</specificationLatexDocument>
    </configuration>
    <executions>
        <execution>
            <id>compile-latex</id>
            <!-- To be executed after all integration tests, and after test-results -->
            <phase>post-integration-test</phase>
            <goals>
                <goal>compile-latex</goal>
            </goals>
            <configuration>
                <latexTool>lualatex</latexTool> <!-- Depends on your latex distribution -->
                <bibTool>bibtex</bibTool>
                <additionalLatexDocuments>
                    <additionalLatexDocument>${project.build.outputDirectory}/documentation/root-testresults.tex</additionalLatexDocument>
                    <additionalLatexDocument>${project.build.outputDirectory}/documentation/root-traceability.tex</additionalLatexDocument>
                </additionalLatexDocuments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Deploying the PDF files

Once you've produced the XML files, you may want to distribute them with your executable.
For that you can use the _Build Helper_ plugin. 
It contains various small independent goals to assist with the Maven build lifecycle.
In particular, it provides the `attach-artifact` goal, 
which attaches additional artifacts to be installed and deployed.   

Configure `attach-artifact` goal during the `package` phase by specifying
the PDF file in the artifacts.
This is an example:

The artifacts `file` are relative to project build directory, so
you need to keep it in mind when configuring it.

This is an example:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>attach-pdf-documentation</id>
            <phase>package</phase>
            <goals>
                <goal>attach-artifact</goal>
            </goals>
            <configuration>
                <artifacts>
                    <artifact>
                        <!-- Path to the PDF generated by the compile-latex Mojo -->
                        <file>${project.build.directory}/classes/documentation/root.pdf</file>
                        <!-- Type/Extension -->
                        <type>pdf</type>
                        <!-- Classifier to distinguish it from the primary jar/war/pom -->
                        <classifier>srs</classifier>
                    </artifact>
                </artifacts>
            </configuration>
        </execution>
    </executions>
</plugin>
```

# Debugging

If you want to execute the plugin code in a debugger, you can 
use `mvnDebug` instead of `mvn`:

```bash
  mvnDbg reqlint:test-results@test-results-config
```

In your IDE, create a remote debug configuration with:
- port number: 8000.
- Host: localhost

Place a breakpoint somewhere appropriate and click on Debug/Attach.