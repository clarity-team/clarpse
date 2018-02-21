package com.clarity.test.go;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GoLangParseTest {

    @Test
    public void testPackageGroup() throws Exception {

        final String code = "package main\ntype person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").packageName().equals("main"));
    }

    @Test
    public void testShortImportType() throws Exception {

        final String code = "package main\n import\"fmt\"\n type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("fmt"));
    }

    @Test
    public void assertNoMethodParameters() throws Exception {

        final String code = "package main\n import\"flag\"\n type Command struct {}\n func (c *Command) LocalFlags() *flag.FlagSet {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Command.LocalFlags() : (*flag.FlagSet)").children().size() == 0);
    }

    @Test
    public void fieldVarCodeFragment() throws Exception {

        final String code = "package main\n import\"fmt\"\n type person struct {SuggestFor []string}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.SuggestFor").codeFragment().equals("SuggestFor : []string"));
    }

    @Test
    public void structMethodCodeFragment() throws Exception {

        final String code = "package main\n import\"fmt\"\n type Command struct {} func (c *Command) SetHelpCommand(cmd *Command) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Command.SetHelpCommand(*Command)").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Command.SetHelpCommand(*Command)").codeFragment().equals("SetHelpCommand(*Command)"));
    }


    @Test
    public void fieldVarCodeFragmentWithComment() throws Exception {

        final String code = "package main\n import\"fmt\"\n type person struct {usageFunc func(*Command) error // Usage can be defined by application\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.usageFunc").codeFragment().equals("usageFunc : func(*Command) error"));
    }

    @Test
    public void fieldVarFuncTypeCodeFragment() throws Exception {

        final String code = "package main\n type person struct { PersistentPreRun func(cmd *Command, args []string) }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.person.PersistentPreRun").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.person.PersistentPreRun").codeFragment().equals("PersistentPreRun : func(cmd *Command, args []string)"));
    }

    @Test
    public void fieldVarFuncTypeCodeFragmentWithComment() throws Exception {

        final String code = "package main\n type person struct { PersistentPreRun func(cmd *Command, args []string)//test \n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.person.PersistentPreRun").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.person.PersistentPreRun").codeFragment().equals("PersistentPreRun : func(cmd *Command, args []string)"));
    }

    @Test
    public void fieldVarFuncTypeCodeFragmentv2() throws Exception {

        final String code = "package main\n type person struct { globNormFunc func(f *flag.FlagSet, name string) flag.NormalizedName }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.person.globNormFunc").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.person.globNormFunc").codeFragment().equals("globNormFunc : func(f *flag.FlagSet, name string) flag.NormalizedName"));
    }

    @Test
    public void lineNumber() throws Exception {

        final String code = "package main\n import\"fmt\"\n type person struct {\nSuggestFor []string}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.SuggestFor").line() == 4);
        assertTrue(generatedSourceModel.getComponent("main.person").line() == 3);

    }

    @Test
    public void testStructWithinMethodIgnored() throws Exception {

        final String code = "package main\n import\"fmt\"\n func SomeFunc(b []byte) error {\n" +
                "  var inside struct {\n" +
                "    Foo string`json:\"foo\"`\n" +
                "  }" +
                "}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.SomeFunc.inside") == null);
        assertTrue(generatedSourceModel.getComponents().size() == 0);
    }

    @Test
    public void testLongImportType() throws Exception {

        final String code = "package main\n import m \"fmt\"\n type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/main.go", code));
        rawData.insertFile(new RawFile("/src/http/cakes/github/person.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("http.cakes.github"));
    }

    @Test
    public void testDotImportType() throws Exception {

        final String code = "package main\n import . \"fmt\"\n type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("fmt"));
    }

    @Test
    public void testParseGoStruct() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
    }

    @Test
    public void testParseGoStructs() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
        assertTrue(generatedSourceModel.containsComponent("main.teacher"));
    }

    @Test
    public void testParseGoInterface() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person interface {} type teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
        assertTrue(generatedSourceModel.containsComponent("main.teacher"));
    }

    @Test
    public void testParseGoStructPrivateVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type Teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").modifiers().contains("private"));
    }

    @Test
    public void testParseGoInterfacePrivateVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person interface {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").modifiers().contains("private"));
    }

    @Test
    public void testTwoGoStructsReferenceEachOther() throws Exception {

        final String code = "package test \n type person struct {teacher Teacher} \n type Teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Teacher").modifiers().contains("public"));
    }

    @Test
    public void testParseGoInterfacePublicVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person interface {} type Teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Teacher").modifiers().contains("public"));
    }

    @Test
    public void testInterfaceAnonymousTypeMethods() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2() (string, uintptr) {} }";
        final String codeB = "package main\n type Person struct {}";

        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        rawData.insertFile(new RawFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2() : (string, uintptr)").codeFragment().equals("testMethodv2() : (string, uintptr)"));
    }

    @Test
    public void testInterfaceAnonymousTypeMethodParamType() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2(x string, h int) (string, uintptr) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).x")
                .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void localVarWithoutTypeDoesNotExist() throws Exception {

        final String code = "package main \n type plain struct \n{ func (t plain) testMethodv2(x string, h int) (string, uintptr) {\n a:=\"test\"} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2.a") == null);
    }

    @Test
    public void localVarExists() throws Exception {

        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.plain.testMethodv2().i"));
    }

    @Test
    public void localVarName() throws Exception {

        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2().i").name().equals("i"));
    }

    @Test
    public void localVarUniqueName() throws Exception {

        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2().i").uniqueName()
                .equals("main.plain.testMethodv2().i"));
    }

    @Test
    public void localVarComponentType() throws Exception {

        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(
                generatedSourceModel.getComponent("main.plain.testMethodv2().i").componentType() == ComponentType.LOCAL);
    }

    @Test
    public void localVarComponentInvocation() throws Exception {

        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2().i")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("int"));
    }

    @Test
    public void testInterfaceAnonymousTypeMethodParamDeclaration() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2(x string, h int) (string, uintptr) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).x")
                .componentInvocations(ComponentInvocations.DECLARATION).size() == 1);
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).x")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("string"));
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).h")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("int"));
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).x")
                .componentInvocations(ComponentInvocations.DECLARATION).size() == 1);
    }

    @Test
    public void testInterfaceAnonymousTypeMethodParamsIsChildOfMethod() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2(x string, h int) (string, uintptr) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr)").children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).x")
                .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void testParseGoStructImplementsInterface() throws Exception {

        final String codeA = "package main\n import \"github\"\n type person struct {}\n func (p person) someMethod() {}";
        final String codeB = "package github\n \n type anInterface interface { someMethod();}";

        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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

        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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

        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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

        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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

        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").componentInvocations(ComponentInvocations.EXTENSION)
                .get(0).invokedComponent().equals("fmt.Math"));
    }

    @Test
    public void testParseGoStructMultipleTypesInFieldVar() throws Exception {

        final String code = "package main\n import \"fmt\"\n type person struct {aField map[*fmt.Node]bool}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").componentInvocations(ComponentInvocations.EXTENSION)
                .get(0).invokedComponent().equals("fmt.Math"));
    }

    @Test
    public void testParsedSingleLineStructDoc() throws Exception {

        final String code = "package main\n //test struct doc\n type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParsMultiLineStructDoc() throws Exception {

        final String code = "package main\n //test struct\n// doc\n type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParseMultiLineInterfaceDoc() throws Exception {

        final String code = "package main\n //test interface\n// doc\n type person interface {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test interface doc"));
    }

    @Test
    public void testParseMultiLineStructDocAfterAnotherStruct() throws Exception {

        final String code = "package main\n type animal struct {}\n//test struct\n// doc\n type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParseMultiLineStructDocSeparatedByEmptyLines() throws Exception {

        final String code = "package main\n//test struct\n// doc\n\n\ntype person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParseMultiLineStructDocForInterfaceMethodSpece() throws Exception {

        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.testMethod() : (int)").comment().equals("test"));
    }

    @Test
    public void testInterfaceMethodSpecExists() throws Exception {

        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.testMethod() : (int)"));
    }

    @Test
    public void testInterfaceMethodSpecCodeFragment() throws Exception {
        final String code = "package main\ntype person interface { testMethod() int}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.testMethod() : (int)").codeFragment().equals("testMethod() : (int)"));
    }

    @Test
    public void testInterfaceComplexMethodSpecParamsExist() throws Exception {
        final String code = "package go\n import \"org\" \n type person interface { testMethod() org.Cake}";
        final String codeB = "package org\n type Cake struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("src/github/go/person.go", code));
        rawData.insertFile(new RawFile("src/github/game/org/cakes.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.go.person.testMethod() : (org.Cake)").codeFragment()
                .equals("testMethod() : (org.Cake)"));
    }

    @Test
    public void testInterfaceMethodSpecComponentType() throws Exception {

        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.testMethod() : (int)").componentType() == ComponentType.METHOD);
    }

    @Test
    public void testInterfaceMethodSpecComponentIsChildOfParentInterface() throws Exception {

        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").children().get(0).equals("main.person.testMethod() : (int)"));
    }

    @Test
    public void testInterfaceAnonymousTypeExtends() throws Exception {

        final String code = "package main \n type plain interface \n{testMethod() int\n Person\n testMethodv2() (string, uintptr) {} }";
        final String codeB = "package main\n type Person struct {}";

        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        rawData.insertFile(new RawFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain").componentInvocations(ComponentInvocations.EXTENSION)
                .get(0).invokedComponent().equals("main.Person"));
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2() : (string, uintptr)").codeFragment().equals("testMethodv2() : (string, uintptr)"));
    }

    @Test
    public void testParseSingleLineStructDocSeparatedByEmptyLines() throws Exception {

        final String code = "package main\n//test struct doc\n\n\n\ntype person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testGoStructHasCorrectSourceFileAttr() throws Exception {
        final String code = "package main\ntype person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").sourceFile().equals("person.go"));
    }

    @Test
    public void testGoStructHasCorrectComponentType() throws Exception {
        final String code = "package main\ntype person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").componentType() == ComponentType.STRUCT);
    }

    @Test
    public void testGoStructFieldVarExists() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.mathObj"));
    }

    @Test
    public void testGoStructFieldVarInvocation() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").componentName().equals("person.mathObj"));
    }

    @Test
    public void testGoStructFieldVarComponenType() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").componentType() == ComponentType.FIELD);
    }

    @Test
    public void testGoStructFieldVarPrivateVisibility() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").modifiers().contains("private"));
    }

    @Test
    public void testGoStructFieldVarPublicVisibility() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {MathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.MathObj").modifiers().contains("public"));
    }

    @Test
    public void testGoStructFieldVarName() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").name().equals("mathObj"));
    }

    @Test
    public void testGoStructSideBySideFieldVars() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj , secondObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.mathObj"));
        assertTrue(generatedSourceModel.containsComponent("main.person.secondObj"));
    }

    @Test
    public void testGoStructSideBySideFieldVarsInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj , secondObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.mathObj"));
        assertTrue(generatedSourceModel.containsComponent("main.person.secondObj"));
    }

    @Test
    public void testGoStructFIeldVarIsChildOfStruct() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").children().get(0).equals("main.person.mathObj"));
    }

    @Test
    public void testGoStructMethodExists() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.x() : (int)"));
    }

    @Test
    public void testParseGoStructMethodWithUnnamedParameters() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x(string) () {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x(string)").children().size() == 1);
    }

    @Test
    public void testGoStructMethodComponentType() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").componentType() == ComponentType.METHOD);
    }

    @Test
    public void testGoStructMethodComponentName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").componentName().equals("person.x() : (int)"));
    }

    @Test
    public void testGoStructMethodName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").name().equals("x"));
    }

    @Test
    public void testGoStructMethodComment() throws Exception {
        final String code = "package main\ntype person struct {}\n\n //test \n //test\n\nfunc (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").comment().equals("test test"));
    }

    @Test
    public void testGoStructMethodDocComment() throws Exception {
        final String code = "package main\ntype person struct {}\n\n //test \n //test\n\nfunc (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").comment().equals("test test"));
    }

    @Test
    public void testGoStructMethodIsChildofStruct() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").children().contains("main.person.x() : (int)"));
    }

    @Test
    public void testMethodCode() throws Exception {
        final String code = "package main\n import (\n\"math\"\n)\n type person struct {} \n func (p person) x(i uint64) int { math.MaxUint32 \n }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x(uint64) : (int)").code().equals("func (p person) x(i uint64) int { math.MaxUint32 \n" +
                " }"));
    }

    @Test
    public void testGoStructMethodPackageNameEqualsParentsPackageName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").packageName()
                .equals(generatedSourceModel.getComponent("main.person.x() : (int)").packageName()));
    }

    @Test
    public void testGoStructMethodSingleParamExists() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x,y int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.lol(int, int).x"));
        assertTrue(generatedSourceModel.containsComponent("main.person.lol(int, int).y"));
    }

    @Test
    public void testGoStructMethodSingleParamComponentType() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int).x")
                .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void testGoStructMethodSingleParamComponentIsChildOfMethod() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int)").children().get(0).equals("main.person.lol(int).x"));
    }

    @Test
    public void testGoStructMethodSingleParamComponentInvocation() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int).x")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("int"));
    }

    @Test
    public void testGoStructMethodTripleParamComponentInvocation() throws Exception {
        final String codeB = "package http\ntype httpcakes struct {}";
        final String code = "package main\nimport \"http\"\ntype person struct {} \n func (p person) lol(x,y int, z *http.httpcakes) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("src/main/person.go", code));
        rawData.insertFile(new RawFile("src/github/http/http.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int, int, *http.httpcakes).x")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("int"));
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int, int, *http.httpcakes).y")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("int"));
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int, int, *http.httpcakes).z")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("github.http.httpcakes"));
    }

    @Test
    public void testGoStructMethodSingleParamUniqueNameComplex() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"test/main\" \n func (p tester.Person) x(v1,v2 tester.Person) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/test/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x(tester.Person, tester.Person).v1").packageName().equals("test.main"));
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x(tester.Person, tester.Person).v2")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("test.main.Person"));
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x(tester.Person, tester.Person)").children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x(tester.Person, tester.Person)").children().get(0)
                .equals("test.main.Person.x(tester.Person, tester.Person).v1"));
    }

    @Test
    public void testGoStructMethodExistsInAnotherSourceFile() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package cakes\n import \"main\" \n func (p main.Person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/person.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.Person.x() : (int)"));
    }

    @Test
    public void testGoStructMethodExistsInAnotherSourceFilev2() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package cakes\n import main \"main\" \n func (p main.Person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.Person.x() : (int)"));
        assertTrue(generatedSourceModel.containsComponent("main.Person"));
    }

    @Test
    public void testGoStructMethodCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.x() : (int)").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (int)").codeFragment().equals("x() : (int)"));
    }

    @Test
    public void testGoStructMethodSpecCodeFragment() throws Exception {
        final String code = "package main\ntype Person interface {  Get(key interface{}) (value interface{}, ok bool) }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.Get(interface{}) : (interface{}, bool)").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.Get(interface{}) : (interface{}, bool)").codeFragment().equals("Get(interface{}) : (interface{}, bool)"));
    }

    @Test
    public void testGoStructMethodWithFuncAsParamCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {  func (c *Person) SetGlobalNormalizationFunc(n func(f *flag.FlagSet, name string)) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name string))").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name string))").codeFragment()
                .equals("SetGlobalNormalizationFunc(func(f *flag.FlagSet, name string))"));
    }

    @Test
    public void testGoInterfaceMethodWithFuncAsParamCodeFragment() throws Exception {
        final String code = "package main\ntype Person interface {  func SetGlobalNormalizationFunc(n func(f *flag.FlagSet, name string)) }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name string))").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name string))").codeFragment()
                .equals("SetGlobalNormalizationFunc(func(f *flag.FlagSet, name string))"));

        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name string))").parentUniqueName()
                .equals("main.Person"));
    }

    @Test
    public void testGoStructMethodWithFuncAsReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {  func (c *Person) SetGlobalNormalizationFunc(n int) (func(f *flag.FlagSet, name string)) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(int) : (func(f *flag.FlagSet, name string))").codeFragment().equals(
                "SetGlobalNormalizationFunc(int) : (func(f *flag.FlagSet, name string))"));
    }

    @Test
    public void testGoStructMethodWithFuncAsPartOfReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {  func (c *Person) SetGlobalNormalizationFunc(n int) (x int, func(f *flag.FlagSet, name string)) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(int) : (int, func(f *flag.FlagSet, name string))").codeFragment().equals(
                "SetGlobalNormalizationFunc(int) : (int, func(f *flag.FlagSet, name string))"));
    }

    @Test
    public void testGoStructMethodMultipleDeclarationReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (x,y int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (int, int)").codeFragment().equals("x() : (int, int)"));
    }

    @Test
    public void testGoStructMethodMultipleIndividualDeclarationReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (x uint8,y int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.x() : (uint8, int)").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (uint8, int)").codeFragment().equals("x() : (uint8, int)"));
    }

    @Test
    public void testGoStructMethodMultipleComplexDeclarationReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (x,z uint8, y int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (uint8, uint8, int)").codeFragment().equals("x() : (uint8, uint8, int)"));
    }

    @Test
    public void testGoStructMethodMultipleReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (string, int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (string, int)").codeFragment().equals("x() : (string, int)"));
    }

    @Test
    public void testGoNoReturnStructMethodCodeFragmentIsNull() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p main.Person) x() {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.x()").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.x()").codeFragment().equals("x()"));
    }

    @Test
    public void structMethodInDifferentSourceFileInSamePackage() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n func (p *Person) x(y string) []string {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/test.go", codeB));
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.Person.x(string) : ([]string)"));
        assertTrue(generatedSourceModel.containsComponent("main.Person.x(string) : ([]string).y"));
    }

    @Test
    public void testGoReturnStructMethodComplexCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p main.Person) x(args []string, x,y map[string]string, v, u string) (j,i []string, map[string]string.test)  {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/cherry.go", code));
        rawData.insertFile(new RawFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.x([]string, map[string]string, map[string]string, string, string) : ([]string, []string, map[string]string.test)").codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.x([]string, map[string]string, map[string]string, string, string) : ([]string, []string, map[string]string.test)").codeFragment().equals("x([]string, map[string]string, map[string]string, string, string) : ([]string, []string, map[string]string.test)"));
        assertTrue(generatedSourceModel.getComponent("main.Person.x([]string, map[string]string, map[string]string, string, string) : ([]string, []string, map[string]string.test).u")
                .parentUniqueName().equals("main.Person.x([]string, map[string]string, map[string]string, string, string) : ([]string, []string, map[string]string.test)"));
        assertTrue(generatedSourceModel.getComponent("main.Person.x([]string, map[string]string, map[string]string, string, string) : ([]string, []string, map[string]string.test)")
                .parentUniqueName().equals("main.Person"));
    }
}
