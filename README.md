# Clarpse

[![Clarity Views Label](http://clarityviews.ca/badge)](http://clarityviews.ca/github/clarity-team/clarpse?projectName=clarpse)
[![Build Status](https://travis-ci.org/Zir0-93/clarpse.svg?branch=master)](https://travis-ci.org/Zir0-93/clarpse)
[![Coverage Status](https://coveralls.io/repos/github/clarity-team/clarpse/badge.svg?branch=master)](https://coveralls.io/github/clarity-team/clarpse?branch=master)

Clarpse is a lightweight polyglot source code analysis tool built using ANTLRv4. Clarpse breaks down a programming language into components representing common source code constructs such as classes, methods, and fields which can be accessed in an object oriented manner. To build the project with maven, run the goal "clean package assembly:single".

If you have any questions or are interested in adding new functionality, feel free to create an issue to discuss your thoughts/plans.

### Terminology

#### OOPSourceCodeModel
A representation of a codebase through a collection of Component objects.

#### Component
A source unit of the code that can represent a class, method, interface, field variable, local variable, enum, etc ..

##### Component Invocation
An invocation of an external component found in a source file, possibly through type declaration, instantiation, extension, implementation, method invocations and so forth.


### Getting Started

#### Basic Usage
```java
   // Create a new ParseRequestContent Object representing a codebase
   final String code =                       " package com.foo;  "
                                               +  " public class SampleClass extends AnInterface {                                                 "
                                               +  "     /** Sample Doc Comment */                                              "
                                               +  "     @SampleAnnotation                                                      "
                                               +  "     public void sampleMethod(String sampleMethodParam) throws AnException {"   
                                               +  "     SampleClassB.fooMethod();
                                               +  "     }                                                                      "
                                               +  " }                                                                          ";";
    final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
    // insert a sample source file
    rawData.insertFile(new RawFile("file2", code));
    final ClarpseProject parseService = new ClarpseProject(rawData);
    // get the parsed result.
    OOPSourceCodeModel generatedSourceModel = parseService.result();
    // extract data from the OOPSourceCodeModel's components
    // get the main class component
    Component mainClassComponent = generatedSourceCodeModel.get("com.foo.java.SampleClass");
    mainclassComponent.name();           // --> "SampleClass"
    mainClassComponent.type();  // --> CLASS
    mainClassComponent.annotations();    // --> 
    mainClassComponent.modifiers();      // --> ["public"]
    mainClassComponent.children();       // --> ["foo.java.SampleClass.void_sampleMethod(String)"]
    mainClassComponent.start(); --> 1
    mainClassComponent.sourceFile(); // --> "foo.java"
    mainClassComponent.componentInvocations(ComponentInvocations.EXTENSION).get(0); // --> "com.foo.AnInterface"
    // get the inner method component
    methodComponent = generatedSourceCodeModel.get(mainClassComponent.getChildren().get(0));
    methodComponent.name();              // --> "sampleMethod"
    methodComponent.type();     // --> METHOD
    methodComponent.annotations();       // --> ["SampleAnnotation=''"]
    methodComponent.modifiers();         // --> ["public"]
    methodComponent.children();          // --> ["com.foo.java.SampleClass.void_sampleMethod(String).sampleMethodParam"]
    methodComponent.start();         // --> 5
    methodComponent.sourceFile(); // --> "foo.java"
    methodComponent.componentInvocations(ComponentInvocations.METHOD).get(0); // --> "com.foo.SampleClassB.fooMethod()"
```
#### Component Invocation Retrieval

### Architecture

The Component class is the heart of clarpse and represents any given construct of a source file (eg: Class, Interface, Method, Field, etc..). Furthermore, the OOPSourceCodeModel class represents a collection of such Components to represent a typical codebase. One can interact with these components in a OOPSourceCodeModel object in an object oriented manner to retrieve key information about the source code. The main challenge for Clarpse exists in parsing a given source file from any programming language and populating the polyglot OOPSourceCodeModel object. The benefit in this tool lies in the fact that this Object can be operated on in the same manner regarding of the original programming language used, check out [Clarity Views](http://clarityviews.ca) to see Clarpse in action.

The following diagram represents the core workings of Clarpse. A Parser should implement the ClarpseParser interface which requires the implementing class to be able to convert a ParseRequestContent Object (A collection of source files of a given programming language) and return an OOPSourceCodeModel object representing the given codebase.

![Clarity Views Diagram](http://clarityviews.ca/embed/clarity-team/clarpse/master/diagram/clarpse-master/clarpse/src/main/java/com/clarity/parser/AntlrParser.java?projectName=clarpse)

###Contributing A Patch

   -  Submit an issue describing your proposed change to the repo in question.
    The repo owner will respond to your issue promptly.
   - Fork the desired repo, develop and test your code changes.
   - Run a local maven build using "clean package assembly:single" to ensure all tests pass and the jar is produced
   -  Submit a pull request.


