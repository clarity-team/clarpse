package com.hadii.test.go;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ComponentReferenceTest {

    @Test
    public void testResolveTypesComplex() throws Exception {
        final String code = "package main\n import \"com/html/template\"\n import temp " +
            "\"com/text/template\"\n type berry struct {\n person template.Person}";
        final String codeB = "package template\n type Person struct {}";
        final String codeC = "package template\n type Person struct {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/github/com/main/person.go", code));
        rawData.insertFile(new ProjectFile("/github/com/html/template/person.go", codeB));
        rawData.insertFile(new ProjectFile("/github/com/text/template/person.go", codeC));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("com.main.berry.person")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent()
                .equals("com.html.template.Person"));
    }

    @Test
    public void testTwoGoStructsReferenceEachOther() throws Exception {
        final String code = "package test \n type person struct {teacher Teacher} \n type Teacher struct{}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("src/github/test/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.person.teacher")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent()
                .equals("test.Teacher"));
    }

    @Test
    public void methodReferenceExternalStruct() throws Exception {
        final String code = "package test \n type person struct {} \n type Teacher struct{} \n" +
            " func (t person) testMethod () {\n var i Teacher\n}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("src/github/test/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.person.testMethod()")
                                       .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent()
                                       .equals("test.Teacher"));
    }


    @Test
    public void localVarComponentReference() throws Exception {
        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2().i")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent().equals("int"));
    }

    @Test
    public void testInterfaceAnonymousTypeMethodParamDeclaration() throws Exception {
        final String code = "package main \n type plain interface \n{ testMethodv2(x string, h int) (string, uintptr) {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).x")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).size() == 1);
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).x")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent().equals("string"));
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).h")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent().equals("int"));
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(string, int) : (string, uintptr).x")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).size() == 1);
    }

    @Test
    public void testParseGoStructImplementsInterface() throws Exception {
        final String codeA = "package main\n import \"github\"\n type person struct {}\n func (p person) someMethod() {}";
        final String codeB = "package github\n \n type anInterface interface { someMethod();}";

        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/person.go", codeA));
        rawData.insertFile(new ProjectFile("/src/lol/github/aninterface.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION).get(0).invokedComponent()
                .equals("lol.github.anInterface"));
    }

    @Test
    public void testParseGoStructImplementsInterfaceWithDifferentPkgName() throws Exception {
        final String codeA = "package main\n import \"../lol/github\"\n type person struct {}\n " +
            "func (p person) someMethod() {}";
        final String codeB = "package complex\n type anInterface interface { someMethod();}";

        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/person.go", codeA));
        rawData.insertFile(new ProjectFile("/src/lol/github/aninterface.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("lol.github.anInterface").get().pkg().name().equals("complex"));
        assertTrue(generatedSourceModel.getComponent("main.person")
                                       .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION).get(0).invokedComponent()
                                       .equals("lol.github.anInterface"));
    }

    @Test
    public void testParseGoInterfaceDoesNotImplementItself() throws Exception {
        final String codeA = "package main\n import \"github\"\n type person struct {}\n func (p person) someMethod() {}";
        final String codeB = "package github\n \n type anInterface interface { someMethod();}";

        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/person.go", codeA));
        rawData.insertFile(new ProjectFile("/src/lol/github/aninterface.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("lol.github.anInterface")
                .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION).size() == 0);
    }

    @Test
    public void testParseGoStructImplementsInterfaceComplex() throws Exception {
        final String codeA = "package main\n type person struct {}\n func (p person) someMethod() {}\n"
                + "func (p* person) methodA() {}\n func (p person) methodB(int, y int, z string) (f,d string) {}";
        final String codeB = "package github\n \n type anInterface interface { aSecondInterface \n someMethod();}";
        final String codeC = "package github\n \n type aSecondInterface interface { methodA();\n methodB(x,y int, z string) (f string, d string);";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/person.go", codeA));
        rawData.insertFile(new ProjectFile("/src/lol/github/aninterface.go", codeB));
        rawData.insertFile(new ProjectFile("/src/lol/github/aSecondinterface.go", codeC));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION).size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION).stream()
                                       .anyMatch(reference -> reference.invokedComponent()
                .equals("lol.github.anInterface")));
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION)
                                       .stream().anyMatch(reference -> reference.invokedComponent()
                .equals("lol.github.aSecondInterface")));
    }

    @Test
    public void testParseGoStructDoesNotImplementSimilarInterfaceByName() throws Exception {
        final String codeA = "package main\n type person struct {}\n func (p person) someMethods() {}\n"
                + "func (p* person) methodA() {}\n func (p person) methodB(x int, y int, z string) (f,d string) {}";
        final String codeB = "package github\n \n type anInterface interface { aSecondInterface \n someMethod();}";
        final String codeC = "package github\n \n type aSecondInterface interface { methodA();\n methodB(x,y int, z string) (f string, d string);}";

        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/person.go", codeA));
        rawData.insertFile(new ProjectFile("/src/lol/github/aninterface.go", codeB));
        rawData.insertFile(new ProjectFile("/src/lol/github/aSecondinterface.go", codeC));

        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION).size() == 1);
    }

    @Test
    public void testParseGoStructDoesImplementsTwoSeparateInterfaces() throws Exception {
        final String codeA = "package main\n type person struct {}\n func (p person) someMethod() {}\n"
                + "func (p* person) methodA() {}\n func (p person) methodB(x int, y int, z string) (f,d string) {}";
        final String codeB = "package github\n \n type anInterface interface {  someMethod();}";
        final String codeC = "package github\n \n type aSecondInterface interface { methodA();\n methodB(x,y int, z string) (f string, d string);";

        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/person.go", codeA));
        rawData.insertFile(new ProjectFile("/src/lol/github/aninterface.go", codeB));
        rawData.insertFile(new ProjectFile("/src/lol/github/aSecondinterface.go", codeC));

        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION).size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION).stream().anyMatch(reference -> reference.invokedComponent()
                .equals("lol.github.anInterface")));
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION).stream().anyMatch(reference -> reference.invokedComponent()
                .equals("lol.github.aSecondInterface")));
    }

    @Test
    public void testParseGoStructExtensionThroughAnonymousType() throws Exception {
        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {fmt.Math}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .get(0).invokedComponent().equals("fmt.Math"));
    }

    @Test
    public void testParseGoMethodVarWithNoType() throws Exception {
        final String code = "package main\n type person struct {} \n func (p person) x(cancel <-chan struct{}) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x(<-chan struct{}).cancel").get().references().size() == 0);
    }

    @Test
    public void testParseGoStructMultipleTypesInFieldVar() throws Exception {
        final String code = "package main\n import \"fmt\"\n type person struct {aField map[*fmt.Node]bool}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.aField")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.person.aField")
                                       .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE)
                                           .stream().anyMatch(reference -> reference.invokedComponent()
                                                                                    .equals("fmt.Node")));
        assertTrue(generatedSourceModel.getComponent("main.person.aField")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).stream()
                                       .anyMatch(reference -> reference.invokedComponent().equals("bool")));
    }

    @Test
    public void testPackageImportResolveFunction() throws Exception {
        final String code = "package main\n import \"package/http\"\n type person struct {http.Object}";
        final String codeB = "package http\n type Object struct{}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/custom/package/http/person.go", codeB));
        rawData.insertFile(new ProjectFile("/custom/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.EXTENSION).stream().anyMatch(reference -> reference.invokedComponent()
                .equals("package.http.Object")));
    }

    @Test
    public void testPackageExtensionOfStructWithDifferentPkgName() throws Exception {
        final String code = "package main\n import \"package/http\"\n type person struct {complex.Object}";
        final String codeB = "package complex\n type Object struct{}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/custom/package/http/person.go", codeB));
        rawData.insertFile(new ProjectFile("/custom/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                                       .get().references(OOPSourceModelConstants.TypeReferences.EXTENSION).stream().anyMatch(reference -> reference.invokedComponent()
                                                                                                                                                   .equals("package.http.Object")));
    }

    @Test
    public void testPackageImportResolveStructField() throws Exception {
        final String code = "package main\n import zed \"custom/package/http\"\n type person " +
            "struct {x zed.Object}";
        final String codeB = "package http\n type Object struct{}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("src/custom/package/http/person.go", codeB));
        rawData.insertFile(new ProjectFile("/src/custom/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("custom.main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).stream().anyMatch(reference -> reference.invokedComponent()
                .equals("custom.package.http.Object")));
    }

    @Test
    public void testParseGoStructExtensionThroughAnonymousTypePointer() throws Exception {
        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {*fmt.Math}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .get(0).invokedComponent().equals("fmt.Math"));
    }

    @Test
    public void testParseGoStructExtensionThroughLocalStruct() throws Exception {
        final String code = "package main\n type Importable struct { \n Import string \n In " +
            "[]interface{} \n } \n type person struct { Importable }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/main.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                                       .get(0).invokedComponent().equals("main.Importable"));
    }

    @Test
    public void goStructFieldExtensionAfterMapInterface() throws Exception {
        final String code = "package main\n type Importable struct { } \n type person struct { "
            + "\n test  map[string]interface{} \n Importable }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/main.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                                       .get(0).invokedComponent().equals("main.Importable"));
    }

    @Test
    public void testInterfaceAnonymousTypeExtends() throws Exception {
        final String code = "package main \n type plain interface \n{testMethod() int\n Person\n testMethodv2() (string, uintptr) {} }";
        final String codeB = "package main\n type Person struct {}";

        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/plain.go", code));
        rawData.insertFile(new ProjectFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .get(0).invokedComponent().equals("main.Person"));
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2() : (string, uintptr)").get().codeFragment().equals("testMethodv2() : (string, uintptr)"));
    }

    @Test
    public void testGoStructFieldVarReference() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).stream().anyMatch(reference -> reference.invokedComponent()
                .equals("test.math.Person")));
    }

    @Test
    public void testGoStructMethodSingleParamComponentReference() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int).x")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent().equals("int"));
    }

    @Test
    public void testGoStructMethodTripleParamComponentReference() throws Exception {
        final String codeB = "package http\ntype httpcakes struct {}";
        final String code = "package main\nimport \"github/http\"\ntype person struct {} \n func " +
            "(p person) lol(x,y int, z *http.httpcakes) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("src/main/person.go", code));
        rawData.insertFile(new ProjectFile("src/github/http/http.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int, int, *http.httpcakes).x")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).stream().anyMatch(reference -> reference.invokedComponent().equals("int")));
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int, int, *http.httpcakes).y")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).stream().anyMatch(reference -> reference.invokedComponent().equals("int")));
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int, int, *http.httpcakes).z")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).stream().anyMatch(reference -> reference.invokedComponent()
                .equals("github.http.httpcakes")));
    }

    @Test
    public void testGoStructMethodSingleParamUniqueNameComplex() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"test/main\" \n func (p tester.Person) x(v1,v2 tester.Person) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/test/main/cherry.go", code));
        rawData.insertFile(new ProjectFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x(tester.Person, tester" +
                                                         ".Person).v1").get().pkg().ellipsisSeparatedPkgPath().equals(
                                                             "test.main"));
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x(tester.Person, tester.Person).v2")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).stream().anyMatch(reference -> reference.invokedComponent()
                .equals("test.main.Person")));
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x(tester.Person, tester.Person)").get().children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x(tester.Person, tester.Person)").get().children().get(0)
                .equals("test.main.Person.x(tester.Person, tester.Person).v1"));
    }

    @Test
    public void simpleReferenceWithAsterisk() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"test/main\" \n func (p tester.Person) x(v1,v2 *tester.Person) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/test/main/cherry.go", code));
        rawData.insertFile(new ProjectFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.main.Person.x(*tester.Person, *tester.Person).v2")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).stream().anyMatch(reference -> reference.invokedComponent()
                .equals("test.main.Person")));
    }

    @Test
    public void simpleGitHubPkgRef() throws Exception {
        final String codeB = "package main\n import log \"github.com/sirupsen/logrus\" \n type Person struct {} \n" +
                "func (p Person)  x(v1 log.Tester) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x(log.Tester).v1")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).stream().anyMatch(reference -> reference.invokedComponent()
                .equals("github.com.sirupsen.logrus.Tester")));
    }
}
