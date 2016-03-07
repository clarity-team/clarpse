# Clarpse
A lightweight polyglot source code analysis tool. Clarpse breaks down a programming language into components representing common source code constructs such as classes, methods, and fields which can be accessed in an object oriented manner.

### Supported Languages
- Java
- C++    (Coming Soon)
- C      (Coming Soon)
- PHP    (Coming Soon)
- Python (Coming Soon)

### Getting Started
```java
    // Create a new ParseRequestContent Object representing a codebase
    ParseRequestContent rawData = new ParseRequestContent();
    // set the language type, see Supported Languages section below
    rawData.setLanguage("java");
    // insert a sample source file
    rawData.insertFile(new RawFile("foo.java", "package com.foo                                                                "
                                               +  " public class SampleClass {                                                 "
                                               +  "     /** Sample Doc Comment */                                              "
                                               +  "     @SampleAnnotation                                                      "
                                               +  "     public void sampleMethod(String sampleMethodParam) throws AnException {"
                                               +  "     }                                                                      "
                                               +  " }                                                                          ";
    final ParseService = new ParseService();
    OOPSourceCodeModel generatedSourceCodeModel = parseService.parseProject(rawData);
```

The OOPSourceCodeModel contains contains components from which key properties of the code base may be extracted. As expected, the format remains the same irregardless of the original input language specified. The JSON representation of the generatedSourceCodeModel for this example is provided below:

```json
        "components": {
        "com.foo.SampleClass.sampleMethod.sampleMethodParam": {
            "uniqueName": "com.foo.SampleClass.sampleMethod.sampleMethodParam",
            "comment": "",
            "packageName": "com.foo",
            "exceptions": [

            ],
            "imports": [

            ],
            "modifiers": [

            ],
            "annotations": [

            ],
            "componentName": "SampleClass.sampleMethod.sampleMethodParam",
            "code": "String sampleMethodParam",
            "declarationTypeSnippet": "String",
            "implements": [

            ],
            "start": "1",
            "end": "1",
            "extends": [

            ],
            "component": "methodParam",
            "refs": [{
                "type": "java.lang.String",
                "lines": [
                    1
                ]
            }],
            "children": [

            ]
        },
        "com.foo.SampleClass.sampleMethod": {
            "uniqueName": "com.foo.SampleClass.sampleMethod",
            "comment": "/** Sample Doc Comment */",
            "packageName": "com.foo",
            "exceptions": [
                "com.foo.AnException"
            ],
            "imports": [

            ],
            "modifiers": [
                "public"
            ],
            "annotations": [{
                "SampleAnnotation": {

                }
            }],
            "componentName": "SampleClass.sampleMethod",
            "code": "@SampleAnnotation                                                           public void sampleMethod(String sampleMethodParam) throws AnException {     }",
            "implements": [

            ],
            "start": "1",
            "end": "1",
            "extends": [

            ],
            "component": "method",
            "refs": [{
                "type": "java.lang.String",
                "lines": [
                    1
                ]
            }, {
                "type": "com.foo.AnException",
                "lines": [
                    1
                ]
            }],
            "children": [
                "com.foo.SampleClass.sampleMethod.sampleMethodParam"
            ]
        },
        "com.foo.SampleClass": {
            "uniqueName": "com.foo.SampleClass",
            "comment": "",
            "packageName": "com.foo",
            "exceptions": [

            ],
            "imports": [

            ],
            "modifiers": [
                "public"
            ],
            "annotations": [

            ],
            "componentName": "SampleClass",
            "code": "package com.foo                                                                 public class SampleClass {                                                      /** Sample Doc Comment */                                                   @SampleAnnotation                                                           public void sampleMethod(String sampleMethodParam) throws AnException {     }                                                                       }",
            "implements": [

            ],
            "start": "1",
            "end": "1",
            "extends": [

            ],
            "component": "class",
            "refs": [{
                "type": "java.lang.String",
                "lines": [
                    1
                ]
            }, {
                "type": "com.foo.AnException",
                "lines": [
                    1
                ]
            }],
            "children": [
                "com.foo.SampleClass.sampleMethod"
            ]
        }
    }
    }
```


