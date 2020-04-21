# :rocket: Clarpse 
[![maintained-by](https://img.shields.io/badge/Maintained%20by-Hadii%20Technologies-violet.svg)](https://hadii.ca) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.hadii-tech/clarpse/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.hadii-tech/clarpse) [![Build Status](https://travis-ci.com/hadii-tech/clarpse.svg?branch=master)](https://travis-ci.com/hadii-tech/clarpse) [![codecov](https://codecov.io/gh/clarity-org/clarpse/branch/master/graph/badge.svg)](https://codecov.io/gh/clarity-org/clarpse)  [![Codacy Badge](https://api.codacy.com/project/badge/Grade/2685a1fc39474c11bd882cd4bd738115)](https://www.codacy.com/app/clarity-bot-admin/clarpse?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=clarity-org/clarpse&amp;utm_campaign=Badge_Grade)  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

Clarpse is a lightweight polyglot source code analysis tool designed to represent a codebase as a collection of language agnostic components representing common source code constructs such as classes, methods, and fields. Clarpse exposes these objects via an easy to use, object oriented API.

If you have any questions or are interested in adding new functionality, feel free to create an issue to discuss your thoughts/plans.

# Features

 - Supports **Java**, **GoLang** and **JavaScript(ES6 Syntax)**. 
 - Light weight
 - Performant
 - Easy to use
 - Clean API built on top of AST
 - Support of comments

# Terminology
| Term                 | Definition                                                                                                                                                                  |
|----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Component            | A language independent source unit of the code, typically represented by a class, method, interface, field variable, local variable, enum, etc ..                                                       |
|  OOPSourceCodeModel  |                                                  A representation of a codebase through a collection of Component objects.                                                  |
| Component Reference | Any given component may contain a number of "Component References" which simply represent its dependency on those referenced components. These references typically exist through import statements, variable declarations, method calls, and so on. |

# Getting Started
First execute `mvn generate-resources` to generate neccessary Antlr files. An excellent way to get familiar with Clarpse is to check out some unit tests.
```java
   // Create a new ParseRequestContent Object representing a codebase
   final String code =                       " package com.foo;  "
                                               +  " public class SampleClass extends AbstractClass {                                                 "
                                               +  "     /** Sample Doc Comment */                                              "
                                               +  "     @SampleAnnotation                                                      "
                                               +  "     public void sampleMethod(String sampleMethodParam) throws AnException {"   
                                               +  "     SampleClassB.fooMethod();
                                               +  "     }                                                                      "
                                               +  " }                                                                          ";";
    final SourceFiles rawData = new SourceFiles(Lang.JAVA);
    // insert a sample source file
    rawData.insertFile(new RawFile("file2", code));
    final ClarpseProject project = new ClarpseProject(rawData);
    // get the parsed result.
    OOPSourceCodeModel generatedSourceModel = project.result();
   ```
   The compiled `OOPSourceCodeModel` is the polygot representation of our project through a collection of `Component` objects.
   ```java
    // extract data from the OOPSourceCodeModel's components
    // get the main class component
    Component mainClassComponent = generatedSourceCodeModel.get("com.foo.java.SampleClass");
    mainclassComponent.name();           // --> "SampleClass"
    mainClassComponent.type();           // --> CLASS
    mainClassComponent.comment();        // --> "Sample Doc Comment"
    mainClassComponent.modifiers();      // --> ["public"]
    mainClassComponent.children();       // --> ["foo.java.SampleClass.sampleMethod(java.lang.String)"]
    mainClassComponent.sourceFile();     // --> "foo.java"
    // get the inner method component
    methodComponent = generatedSourceCodeModel.get(mainClassComponent.getChildren().get(0));
    methodComponent.name();              // --> "sampleMethod"
    methodComponent.type();              // --> METHOD
    methodComponent.modifiers();         // --> ["public"]
    methodComponent.children();          // --> ["com.foo.java.SampleClass.sampleMethod(String).sampleMethodParam"]
    methodComopnent.codeFragment();      // --> "sampleMethod(String)"
    methodComponent.sourceFile();        // --> "foo.java"
```

# Compile Sources
If you have checkout the project from GitHub you can build the project with maven using:

    mvn clean package assembly:single

# Latest Version 
Clarpse can be pulled in via gradle or maven, and is served using [jitpack](https://jitpack.io/).
```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
 
 ...
<dependencies>	 
  	<dependency>
		<groupId>com.github.clarity-org</groupId>
		<artifactId>clarpse</artifactId>
		<version>master</version>
	</dependency>
</dependencies> 
 ```
 
# Contributing A Patch

   -  Submit an issue describing your proposed change to the repo in question.
    The repo owner will respond to your issue promptly.
   - Fork the desired repo, develop and test your code changes.
   - Run a local maven build using "clean package assembly:single" to ensure all tests pass and the jar is produced
   - Update the versioning in the pom.xml and README.md using the x.y.z scheme:
     - x = main version number, Increase if introducing API breaking changes.
     - y = feature number, Increase this number if the change contains new features with or without bug fixes.
     - z = hotfix number, Increase this number if the change only contains bug fixes.
   -  Submit a pull request.


