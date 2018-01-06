# :rocket: Clarpse 

[![Build Status](https://travis-ci.org/Zir0-93/clarpse.svg?branch=master)](https://travis-ci.org/Zir0-93/clarpse)
[![codecov](https://codecov.io/gh/Zir0-93/clarpse/branch/master/graph/badge.svg)](https://codecov.io/gh/Zir0-93/clarpse)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9c74dfe9ee2f42d2a0e1ee85d0d83c60)](https://www.codacy.com/app/fadhelm/clarpse?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Zir0-93/clarpse&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[Clarpse](http://mfadhel.com/2016/clarpse/) is a lightweight polyglot source code analysis tool. Clarpse breaks down a codebase into programming language agnostic components representing common source code constructs such as classes, methods, and fields which can be accessed in an object oriented manner. Check out the [releases](https://github.com/Zir0-93/clarpse/releases) page for the latest stable release.

If you have any questions or are interested in adding new functionality, feel free to create an issue to discuss your thoughts/plans.

# Features

 - Supports **Java** and **GoLang**. 
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
| Component Invocation | A relationship between two Components, typically through type declaration, instantiation, extension, implementation, etc.. |

# Getting Started
An excellent way to get started is to look at Clarpe's unit tests.
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
    mainClassComponent.annotations();    // --> SampleAnnotation
    mainClassComponent.comment();        // --> "Sample Doc Comment"
    mainClassComponent.modifiers();      // --> ["public"]
    mainClassComponent.children();       // --> ["foo.java.SampleClass.sampleMethod(java.lang.String)"]
    mainClassComponent.line();           // --> 1
    mainClassComponent.sourceFile();     // --> "foo.java"
    mainClassComponent.componentInvocations(ComponentInvocations.EXTENSION).get(0); // --> "com.foo.AbstractClass"
    // get the inner method component
    methodComponent = generatedSourceCodeModel.get(mainClassComponent.getChildren().get(0));
    methodComponent.name();              // --> "sampleMethod"
    methodComponent.type();              // --> METHOD
    methodComponent.annotations();       // --> ["SampleAnnotation=''"]
    methodComponent.modifiers();         // --> ["public"]
    methodComponent.children();          // --> ["com.foo.java.SampleClass.sampleMethod(String).sampleMethodParam"]
    methodComponent.line() ;             // --> 5
    methodComopnent.codeFragment();      // --> "sampleMethod(String)"
    methodComponent.sourceFile();        // --> "foo.java"
    methodComponent.componentInvocations(ComponentInvocations.METHOD).get(0); // --> "com.foo.SampleClassB.fooMethod()"
```

# Compile Sources
If you have checkout the project from GitHub you can build the project with maven using:

    mvn clean package assembly:single

# Latest Version 
Clarpse can be pulled in via maven, it is hosted on a public Artifactory Server using AWS.
```
<repositories>
	<repository>
		<id>central</id>
		<name>ip-172-31-28-11-releases</name>
		<url>http://ec2-35-163-144-148.us-west-2.compute.amazonaws.com/artifactory/libs-release</url>
	</repository>
</repositories>
 
 ...
 
<dependencies>	 
  	<dependency>
		<groupId>com.clarityviews</groupId>
		<artifactId>clarpse</artifactId>
		<version>3.7.5</version>
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


