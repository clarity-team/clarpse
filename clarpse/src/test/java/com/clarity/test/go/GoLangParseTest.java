package com.clarity.test.go;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;

public class GoLangParseTest {

    @Test
    public void testPackageGroup() throws Exception {

        final String code = "package main\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").packageName().equals("main"));
    }

    @Test
    public void testShortImportType() throws Exception {

        final String code = "package main\n import\"fmt\"\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("fmt"));
    }

    @Test
    public void testLongImportType() throws Exception {

        final String code = "package main\n import m \"fmt\"\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("fmt"));
    }

    @Test
    public void testResolveTypesComplex() throws Exception {

        final String code = "package main\n import \"html/template\"\n import temp \"text/template\"\n type berry struct {\n person template.Person}";
        final String codeB = "package template\n type Person struct {}";
        final String codeC = "package template\n type Person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/github/com/main/person.go", code));
        rawData.insertFile(new RawFile("/github/com/html/template/person.go", codeB));
        rawData.insertFile(new RawFile("/github/com/text/template/person.go", codeC));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.berry.person")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("html.template.Person"));
    }

    @Test
    public void testImportUsesFullUniquePathIfPossible() throws Exception {

        final String code = "package main\n import g \"github\"\n type person struct {}";
        final String codeB = "package github";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/main.go", code));
        rawData.insertFile(new RawFile("/src/http/cakes/github/person.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("http.cakes.github"));
    }

    @Test
    public void testDotImportType() throws Exception {

        final String code = "package main\n import . \"fmt\"\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("fmt"));
    }

    @Test
    public void testParseGoStruct() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
    }

    @Test
    public void testParseGoStructs() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type teacher struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
        assertTrue(generatedSourceModel.containsComponent("main.teacher"));
    }

    @Test
    public void testParseGoInterface() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person interface {} type teacher struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
        assertTrue(generatedSourceModel.containsComponent("main.teacher"));
    }

    @Test
    public void testParseGoStructPrivateVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type Teacher struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").modifiers().contains("private"));
    }

    @Test
    public void testParseGoInterfacePrivateVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person interface {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").modifiers().contains("private"));
    }

    @Test
    public void testTwoGoStructsReferenceEachOther() throws Exception {

        final String code = "package test \n type person struct {teacher Teacher} \n type Teacher struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("src/github/test/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.test.person.teacher")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("github.test.Teacher"));
    }

    @Test
    public void testParseGoStructPublicVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ \n type person struct {} type Teacher struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Teacher").modifiers().contains("public"));
    }

    @Test
    public void testParseGoInterfacePublicVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person interface {} type Teacher struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Teacher").modifiers().contains("public"));
    }

    @Test
    public void testInterfaceAnonymousTypeMethods() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2() (string, uintptr) {} }";
        final String codeB = "package main\n type Person struct {}";

        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        rawData.insertFile(new RawFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2").value().equals("string, uintptr"));
    }

    @Test
    public void testInterfaceAnonymousTypeMethodParamType() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2(x string, h int) (string, uintptr) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2.x")
                .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void testInterfaceAnonymousTypeMethodParamDeclaration() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2(x string, h int) (string, uintptr) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2.x")
                .componentInvocations(ComponentInvocations.DECLARATION).size() == 1);
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2.x")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("string"));
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2.h")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("int"));
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2.x")
                .componentInvocations(ComponentInvocations.DECLARATION).size() == 1);
    }

    @Test
    public void testInterfaceAnonymousTypeMethodParamsIsChildOfMethod() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2(x string, h int) (string, uintptr) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2").children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2.x")
                .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void testParseGoStructImplementsInterface() throws Exception {

        final String codeA = "package main\n import \"github\"\n type person struct {}\n func (p person) someMethod() {}";
        final String codeB = "package github\n \n type anInterface interface { someMethod();}";

        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/person.go", codeA));
        rawData.insertFile(new RawFile("/src/lol/github/aninterface.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).get(0).invokedComponent()
                .equals("lol.github.anInterface"));
    }

    @Test
    public void testParseGoInterfaceDoesNotImplementItself() throws Exception {

        final String codeA = "package main\n import \"github\"\n type person struct {}\n func (p person) someMethod() {}";
        final String codeB = "package github\n \n type anInterface interface { someMethod();}";

        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/person.go", codeA));
        rawData.insertFile(new RawFile("/src/lol/github/aninterface.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("lol.github.anInterface")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).size() == 0);
    }

    @Test
    public void testParseGoStructImplementsInterfaceComplex() throws Exception {

        final String codeA = "package main\n type person struct {}\n func (p person) someMethod() {}\n"
                + "func (p* person) methodA() {}\n func (p person) methodB(int, y int, z string) (f,d string) {}";
        final String codeB = "package github\n \n type anInterface interface { aSecondInterface \n someMethod();}";
        final String codeC = "package github\n \n type aSecondInterface interface { methodA();\n methodB(x,y int, z string) (f string, d string);";

        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/person.go", codeA));
        rawData.insertFile(new RawFile("/src/lol/github/aninterface.go", codeB));
        rawData.insertFile(new RawFile("/src/lol/github/aSecondinterface.go", codeC));

        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.person")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).get(0).invokedComponent()
                .equals("lol.github.anInterface"));
        assertTrue(generatedSourceModel.getComponent("main.person")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).get(1).invokedComponent()
                .equals("lol.github.aSecondInterface"));
    }

    @Test
    public void testParseGoStructDoesNotImplementSimilarInterfaceByName() throws Exception {

        final String codeA = "package main\n type person struct {}\n func (p person) someMethods() {}\n"
                + "func (p* person) methodA() {}\n func (p person) methodB(x int, y int, z string) (f,d string) {}";
        final String codeB = "package github\n \n type anInterface interface { aSecondInterface \n someMethod();}";
        final String codeC = "package github\n \n type aSecondInterface interface { methodA();\n methodB(x,y int, z string) (f string, d string);}";

        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/person.go", codeA));
        rawData.insertFile(new RawFile("/src/lol/github/aninterface.go", codeB));
        rawData.insertFile(new RawFile("/src/lol/github/aSecondinterface.go", codeC));

        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).size() == 1);
    }

    @Test
    public void testParseGoStructDoesImplementsTwoSeparateInterfaces() throws Exception {

        final String codeA = "package main\n type person struct {}\n func (p person) someMethod() {}\n"
                + "func (p* person) methodA() {}\n func (p person) methodB(x int, y int, z string) (f,d string) {}";
        final String codeB = "package github\n \n type anInterface interface {  someMethod();}";
        final String codeC = "package github\n \n type aSecondInterface interface { methodA();\n methodB(x,y int, z string) (f string, d string);";

        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/person.go", codeA));
        rawData.insertFile(new RawFile("/src/lol/github/aninterface.go", codeB));
        rawData.insertFile(new RawFile("/src/lol/github/aSecondinterface.go", codeC));

        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.person")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).get(0).invokedComponent()
                .equals("lol.github.anInterface"));
        assertTrue(generatedSourceModel.getComponent("main.person")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).get(1).invokedComponent()
                .equals("lol.github.aSecondInterface"));
    }

    @Test
    public void testParseGoStructExtensionThroughAnonymousType() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {fmt.Math}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").componentInvocations(ComponentInvocations.EXTENSION)
                .get(0).invokedComponent().equals("fmt.Math"));
    }

    @Test
    public void testParseGoStructMultipleTypesInFieldVar() throws Exception {

        final String code = "package main\n import \"fmt\"\n type person struct {aField map[*fmt.Node]bool}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.aField")
                .componentInvocations(ComponentInvocations.DECLARATION).size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.person.aField")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("fmt.Node"));
        assertTrue(generatedSourceModel.getComponent("main.person.aField")
                .componentInvocations(ComponentInvocations.DECLARATION).get(1).invokedComponent().equals("bool"));
    }

    @Test
    public void testPackageImportResolveFunction() throws Exception {

        final String code = "package main\n import \"package/http\"\n type person struct {http.Object}";
        final String codeB = "package http\n type Object struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("src/custom/package/http/person.go", codeB));
        rawData.insertFile(new RawFile("/src/custom/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("custom.main.person")
                .componentInvocations(ComponentInvocations.EXTENSION).get(0).invokedComponent()
                .equals("custom.package.http.Object"));
    }

    @Test
    public void testPackageImportResolveStructField() throws Exception {

        final String code = "package main\n import zed \"package/http\"\n type person struct {x zed.Object}";
        final String codeB = "package http\n type Object struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("src/custom/package/http/person.go", codeB));
        rawData.insertFile(new RawFile("/src/custom/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("custom.main.person")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("custom.package.http.Object"));
    }

    @Test
    public void testParseGoStructExtensionThroughAnonymousTypePointer() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {*fmt.Math}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").componentInvocations(ComponentInvocations.EXTENSION)
                .get(0).invokedComponent().equals("fmt.Math"));
    }

    @Test
    public void testParsedSingleLineStructDoc() throws Exception {

        final String code = "package main\n //test struct doc\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParsMultiLineStructDoc() throws Exception {

        final String code = "package main\n //test struct\n// doc\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParseMultiLineInterfaceDoc() throws Exception {

        final String code = "package main\n //test interface\n// doc\n type person interface {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test interface doc"));
    }

    @Test
    public void testParseMultiLineStructDocAfterAnotherStruct() throws Exception {

        final String code = "package main\n type animal struct {}\n//test struct\n// doc\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParseMultiLineStructDocSeparatedByEmptyLines() throws Exception {

        final String code = "package main\n//test struct\n// doc\n\n\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParseMultiLineStructDocForInterfaceMethodSpece() throws Exception {

        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.testMethod").comment().equals("test"));
    }

    @Test
    public void testInterfaceMethodSpecExists() throws Exception {

        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.testMethod"));
    }

    @Test
    public void testInterfaceMethodSpecParamsExist() throws Exception {
        final String code = "package main\ntype person interface { testMethod() int}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.testMethod").value().equals("int"));
    }

    @Test
    public void testInterfaceComplexMethodSpecParamsExist() throws Exception {
        final String code = "package go\n import \"org\" \n type person interface { testMethod() org.Cake}";
        final String codeB = "package org\n type Cake struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("src/github/go/person.go", code));
        rawData.insertFile(new RawFile("src/github/game/org/cakes.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.go.person.testMethod").value()
                .equals("github.game.org.Cake"));
    }

    @Test
    public void testInterfaceMethodSpecComponentType() throws Exception {

        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.testMethod").componentType() == ComponentType.METHOD);
    }

    @Test
    public void testInterfaceMethodSpecComponentIsChildOfParentInterface() throws Exception {

        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").children().get(0).equals("main.person.testMethod"));
    }

    @Test
    public void testInterfaceAnonymousTypeExtends() throws Exception {

        final String code = "package main \n type plain interface \n{testMethod() int\n Person\n testMethodv2() (string, uintptr) {} }";
        final String codeB = "package main\n type Person struct {}";

        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        rawData.insertFile(new RawFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain").componentInvocations(ComponentInvocations.EXTENSION)
                .get(0).invokedComponent().equals("main.Person"));
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2").value().equals("string, uintptr"));
    }

    @Test
    public void testParseSingleLineStructDocSeparatedByEmptyLines() throws Exception {

        final String code = "package main\n//test struct doc\n\n\n\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testGoStructHasCorrectSourceFileAttr() throws Exception {
        final String code = "package main\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").sourceFile().equals("person.go"));
    }

    @Test
    public void testGoStructHasCorrectComponentType() throws Exception {
        final String code = "package main\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").componentType() == ComponentType.STRUCT);
    }

    @Test
    public void testGoStructFieldVarExists() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.mathObj"));
    }

    @Test
    public void testGoStructFieldVarInvocation() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("test.math.Person"));
    }

    @Test
    public void testGoStructFieldVarComponentName() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").componentName().equals("person.mathObj"));
    }

    @Test
    public void testGoStructFieldVarComponenType() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").componentType() == ComponentType.FIELD);
    }

    @Test
    public void testGoStructFieldVarPrivateVisibility() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").modifiers().contains("private"));
    }

    @Test
    public void testGoStructFieldVarPublicVisibility() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {MathObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.MathObj").modifiers().contains("public"));
    }

    @Test
    public void testGoStructFieldVarName() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").name().equals("mathObj"));
    }

    @Test
    public void testGoStructSideBySideFieldVars() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj , secondObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.mathObj"));
        assertTrue(generatedSourceModel.containsComponent("main.person.secondObj"));
    }

    @Test
    public void testGoStructSideBySideFieldVarsInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj , secondObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.mathObj"));
        assertTrue(generatedSourceModel.containsComponent("main.person.secondObj"));
    }

    @Test
    public void testGoStructFIeldVarIsChildOfStruct() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").children().get(0).equals("main.person.mathObj"));
    }

    @Test
    public void testGoStructMethodExists() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.x"));
    }

    @Test
    public void testParseGoStructMethodWithUnnamedParameters() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x(string) () {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x").children().size() == 1);
    }

    @Test
    public void testGoStructMethodComponentType() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x").componentType() == ComponentType.METHOD);
    }

    @Test
    public void testGoStructMethodComponentName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x").componentName().equals("person.x"));
    }

    @Test
    public void testGoStructMethodName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x").name().equals("x"));
    }

    @Test
    public void testGoStructMethodComment() throws Exception {
        final String code = "package main\ntype person struct {}\n\n //test \n //test\n\nfunc (p person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x").comment().equals("test test"));
    }

    @Test
    public void testGoStructMethodDocComment() throws Exception {
        final String code = "package main\ntype person struct {}\n\n //test \n //test\n\nfunc (p person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x").comment().equals("test test"));
    }

    @Test
    public void testGoStructMethodIsChildofStruct() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").children().contains("main.person.x"));
    }

    @Test
    public void testGoStructMethodPackageNameEqualsParentsPackageName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").packageName()
                .equals(generatedSourceModel.getComponent("main.person.x").packageName()));
    }

    @Test
    public void testGoStructMethodSingleParamExists() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.lol.x"));
    }

    @Test
    public void testGoStructMethodSingleParamComponentType() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol.x")
                .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void testGoStructMethodSingleParamComponentIsChildOfMethod() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol").children().get(0).equals("main.person.lol.x"));
    }

    @Test
    public void testGoStructMethodSingleParamComponentInvocation() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol.x")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("int"));
    }

    @Test
    public void testGoStructMethodTripleParamComponentInvocation() throws Exception {
        final String codeB = "package http\ntype httpcakes struct {}";
        final String code = "package main\nimport \"http\"\ntype person struct {} \n func (p person) lol(x,y int, z *http.httpcakes) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("src/main/person.go", code));
        rawData.insertFile(new RawFile("src/github/http/http.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol.x")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("int"));
        assertTrue(generatedSourceModel.getComponent("main.person.lol.y")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("int"));
        assertTrue(generatedSourceModel.getComponent("main.person.lol.z")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("github.http.httpcakes"));
    }

    @Test
    public void testGoStructMethodSingleParamUniqueNameComplex() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"test/main\" \n func (p tester.Person) x(v1,v2 tester.Person) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/test/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x.v1").packageName().equals("test.main"));
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x.v2")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("test.main.Person"));
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x").children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x").children().get(0)
                .equals("test.main.Person.x.v1"));
    }

    @Test
    public void testGoStructMethodExistsInAnotherSourceFile() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package cakes\n import \"main\" \n func (p main.Person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/person.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.Person.x"));
    }

    @Test
    public void testGoStructMethodExistsInAnotherSourceFilev2() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package cakes\n import tester \"main\" \n func (p tester.Person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.Person.x"));
        assertTrue(generatedSourceModel.containsComponent("main.Person"));
    }

    @Test
    public void testGoStructMethodValue() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() int {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x").value().equals("int"));
    }

    @Test
    public void testGoStructMethodMultipleDeclarationReturnValues() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (x,y int) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x").value().equals("int, int"));
    }

    @Test
    public void testGoStructMethodMultipleIndividualDeclarationReturnValues() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (x uint8,y int) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x").value().equals("uint8, int"));
    }

    @Test
    public void testGoStructMethodMultipleComplexDeclarationReturnValues() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (x,z uint8, y int) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x").value().equals("uint8, uint8, int"));
    }

    @Test
    public void testGoStructMethodMultipleReturnValue() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (string, int) {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x").value().equals("string, int"));
    }

    @Test
    public void testGoNoReturnStructMethodValueIsNull() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x").value() == null);
    }
}
