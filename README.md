# :rocket: Clarpse 

[![Clarity Views Label](http://clarityviews.io/badge)](http://clarityviews.io/github/Zir0-93/clarpse?projectName=clarpse)
[![Build Status](https://travis-ci.org/Zir0-93/clarpse.svg?branch=master)](https://travis-ci.org/Zir0-93/clarpse)
[![codecov](https://codecov.io/gh/Zir0-93/clarpse/branch/master/graph/badge.svg)](https://codecov.io/gh/Zir0-93/clarpse)

[Clarpse](http://mfadhel.com/tech/clarpse) is a lightweight polyglot source code analysis tool built using ANTLRv4. Clarpse breaks down a codebase into programming language agnostic components representing common source code constructs such as classes, methods, and fields which can be accessed in an object oriented manner. Check out the [releases](https://github.com/Zir0-93/clarpse/releases) page for the latest stable release.

If you have any questions or are interested in adding new functionality, feel free to create an issue to discuss your thoughts/plans.

# Features

 - Light weight
 - Performant
 - Easy to use
 - Clean API built on top of AST
 - Support of comments

# Terminology
| Term                 | Definition                                                                                                                                                                  |
|----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Component            | A language independant source unit of the code, typically represented by a class, method, interface, field variable, local variable, enum, etc ..                                                       |
|  OOPSourceCodeModel  |                                                  A representation of a codebase through a collection of Component objects.                                                  |
| Component Invocation | An invocation of an external component found in a source file, typically through type declaration, instantiation, extension, implementation, method invocations and so forth. |

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
    final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
    // insert a sample source file
    rawData.insertFile(new RawFile("file2", code));
    final ClarpseProject project = new ClarpseProject(rawData);
    // get the parsed result.
    OOPSourceCodeModel generatedSourceModel = project.result();
    // extract data from the OOPSourceCodeModel's components
    // get the main class component
    Component mainClassComponent = generatedSourceCodeModel.get("com.foo.java.SampleClass");
    mainclassComponent.name();           // --> "SampleClass"
    mainClassComponent.type();           // --> CLASS
    mainClassComponent.annotations();    // --> SampleAnnotation
    mainClassComponent.comment();        // --> "Sample Doc Comment"
    mainClassComponent.modifiers();      // --> ["public"]
    mainClassComponent.children();       // --> ["foo.java.SampleClass.sampleMethod(java.lang.String)"]
    mainClassComponent.start();          // --> 1
    mainClassComponent.sourceFile();     // --> "foo.java"
    mainClassComponent.componentInvocations(ComponentInvocations.EXTENSION).get(0); // --> "com.foo.AbstractClass"
    // get the inner method component
    methodComponent = generatedSourceCodeModel.get(mainClassComponent.getChildren().get(0));
    methodComponent.name();              // --> "sampleMethod"
    methodComponent.type();              // --> METHOD
    methodComponent.annotations();       // --> ["SampleAnnotation=''"]
    methodComponent.modifiers();         // --> ["public"]
    methodComponent.children();          // --> ["com.foo.java.SampleClass.sampleMethod(java.lang.String).sampleMethodParam"]
    methodComponent.start();             // --> 5
    methodComponent.sourceFile();        // --> "foo.java"
    methodComponent.componentInvocations(ComponentInvocations.METHOD).get(0); // --> "com.foo.SampleClassB.fooMethod()"
```
# Design

The Component class is the heart of clarpse and represents any given construct of a source file (eg: Class, Interface, Method, Field, etc..). Furthermore, the OOPSourceCodeModel class represents a collection of such Components to represent a typical codebase. One can interact with these components in a OOPSourceCodeModel object in an object oriented manner to retrieve key information about the source code. The main challenge for Clarpse exists in parsing a given source file from any programming language and populating the polyglot OOPSourceCodeModel object. The benefit in this tool lies in the fact that this Object can be operated on in the same manner regarding of the original programming language used, check out [Clarity Views](http://clarityviews.io) to see Clarpse in action.

The following diagram represents the core workings of Clarpse. A Parser should implement the ClarpseParser interface which requires the implementing class to be able to convert a ParseRequestContent Object (A collection of source files of a given programming language) and return an OOPSourceCodeModel object representing the given codebase.

![Clarity Views Diagram](http://api.clarityviews.io/v1/github/Zir0-93/clarpse/master?filename=clarpse-master/clarpse/src/main/java/com/clarity/parser/ClarpseJavaParser.java&size=6)

# Compile Sources
If you have checkout the project from GitHub you can build the project with maven using:

    mvn clean package assembly:single

# Contributing A Patch

   -  Submit an issue describing your proposed change to the repo in question.
    The repo owner will respond to your issue promptly.
   - Fork the desired repo, develop and test your code changes.
   - Run a local maven build using "clean package assembly:single" to ensure all tests pass and the jar is produced
   - Update the versioning using the x.y.z scheme:
     - x = main version number, Increase if introducing API breaking changes.
     - y = feature number, Increase this number if the change contains new features with or without bug fixes.
     - z = hotfix number, Increase this number if the change only contains bug fixes.
   -  Submit a pull request.


