# Clarpse

[![Clarity View](http://clarity.mybluemix.net/badge)](http://clarity.mybluemix.net/github/clarity-team/clarpse)
[![Build Status](https://travis-ci.org/clarity-team/clarpse.svg?branch=master)](https://travis-ci.org/clarity-team/clarpse)
[![Coverage Status](https://coveralls.io/repos/github/clarity-team/clarpse/badge.svg?branch=master)](https://coveralls.io/github/clarity-team/clarpse?branch=master)

A lightweight polyglot source code analysis tool built using ANTLRv4. Clarpse breaks down a programming language into components representing common source code constructs such as classes, methods, and fields which can be accessed in an object oriented manner. To build the project with maven, run the goal "clean package assembly:single" to run the tests and produce the updated clarpse jar.

If you have any questions or are interested in adding new functionality, feel free to create an issue to discuss your thoughts/plans.

### Supported Languages
- Java
- C++    (Coming Soon)
- C      (Coming Soon)
- PHP    (Coming Soon)
- Python (Coming Soon)

### Getting Started
```java
    // Create a new ParseRequestContent Object representing a codebase
    ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
    // insert a sample source file
    rawData.insertFile(new RawFile("foo.java", "package com.foo;                                                                "
                                               +  " public class SampleClass {                                                 "
                                               +  "     /** Sample Doc Comment */                                              "
                                               +  "     @SampleAnnotation                                                      "
                                               +  "     public void sampleMethod(String sampleMethodParam) throws AnException {"
                                               +  "     }                                                                      "
                                               +  " }                                                                          ";
    final ParseService = new ParseService();
    OOPSourceCodeModel generatedSourceCodeModel = parseService.parseProject(rawData);
    // get the main class component
    Component mainClassComponent = generatedSourceCodeModel.get("com.foo.java.SampleClass");
    mainclassComponent.getName(); // --> SampleClass
    mainClassComponent.getComponentType(); // --> CLASS
    mainClassComponent.getAnnotations(); // --> 
    mainClassComponent.getModifiers(); // --> ["public"]
    mainClassComponent.getChildren(); // --> ["foo.java.SampleClass.void_sampleMethod(String)"]
    mainClassComponent.getStartLine(); // --> 1
    // get the inner method component
    methodComponent = generatedSourceCodeModel.get(mainClassComponent.getChildren().get(0));
    methodComponent.getName(); // --> SampleClass
    methodComponent.getComponentType(); // --> CLASS
    methodComponent.getAnnotations(); // --> ["SampleAnnotation=''"]
    methodComponent.getModifiers(); // --> ["public"]
    methodComponent.getChildren(); // --> ["com.foo.java.SampleClass.void_sampleMethod(String).sampleMethodParam"]
    methodComponent.getStartLine(); // --> 5
```

The OOPSourceCodeModel class contains components from which key properties of the code base may be extracted. These components can be of the following type: Class, Inteface, Method, MethodParam, Enum, EnumConstant, Annotation, InterfaceConstant, LocalVar and Field (See OOPSourceCodeModelConstants.ComponentTypes). As expected, the format remains the same irregardless of the original input language specified. The JSON representation of the generatedSourceCodeModel for this example is provided below:


![Diagram: clarpse-master^clarpse^src^main^java^com^clarity^parser^AntlrParser.java](http://clarityviews.ca/github/clarity-team/clarpse/diagram/clarpse-master^clarpse^src^main^java^com^clarity^parser^AntlrParser.java/)
