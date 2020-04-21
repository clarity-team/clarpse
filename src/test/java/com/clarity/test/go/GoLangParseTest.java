package com.clarity.test.go;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.File;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GoLangParseTest {

    @Test
    public void assertNoMethodParameters() throws Exception {
        final String code = "package main\n import\"flag\"\n type Command struct {}\n func (c *Command) LocalFlags() *flag.FlagSet {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Command.LocalFlags() : (*flag.FlagSet)").get().children().size() == 0);
    }

    @Test
    public void testStructWithinMethodIgnored() throws Exception {
        final String code = "package main\n import\"fmt\"\n func SomeFunc(b []byte) error {\n" +
                "  var inside struct {\n" +
                "    Foo value`json:\"foo\"`\n" +
                "  }" +
                "}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertFalse(generatedSourceModel.getComponent("main.SomeFunc.inside").isPresent());
        assertTrue(generatedSourceModel.size() == 0);
    }



    @Test
    public void testParseGoStruct() throws Exception {
        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
    }

    @Test
    public void testParseGoStructs() throws Exception {
        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
        assertTrue(generatedSourceModel.containsComponent("main.teacher"));
    }

    @Test
    public void testParseGoInterface() throws Exception {
        final String code = "package main\n import \"fmt\"\n /*test*/ type person interface {} type teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
        assertTrue(generatedSourceModel.containsComponent("main.teacher"));
    }
    @Test
    public void localVarWithoutTypeDoesNotExist() throws Exception {
        final String code = "package main \n type plain struct \n{ func (t plain) testMethodv2(x value, h int) (value, uintptr) {\n a:=\"test\"} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertFalse(generatedSourceModel.getComponent("main.plain.testMethodv2.a").isPresent());
    }

    @Test
    public void localVarExists() throws Exception {
        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.plain.testMethodv2().i"));
    }

    @Test
    public void localVarName() throws Exception {
        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2().i").get().name().equals("i"));
    }

    @Test
    public void localVarUniqueName() throws Exception {
        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2().i").get().uniqueName()
                .equals("main.plain.testMethodv2().i"));
    }


    @Test
    public void testInterfaceMethodSpecExists() throws Exception {
        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.testMethod() : (int)"));
    }
    @Test
    public void testGoStructFieldVarExists() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.mathObj"));
    }

    @Test
    public void testGoStructFieldVarComponentName() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").get().componentName().equals("person.mathObj"));
    }

    @Test
    public void testGoStructFieldVarName() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").get().name().equals("mathObj"));
    }

    @Test
    public void testGoStructSideBySideFieldVars() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj , secondObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.mathObj"));
        assertTrue(generatedSourceModel.containsComponent("main.person.secondObj"));
    }

    @Test
    public void testGoStructSideBySideFieldVarsInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj , secondObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.mathObj"));
        assertTrue(generatedSourceModel.containsComponent("main.person.secondObj"));
    }

    @Test
    public void testGoStructMethodExists() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.x() : (int)"));
    }

    @Test
    public void testGoStructMethodComponentName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get().componentName().equals("person.x() : (int)"));
    }

    @Test
    public void testGoStructMethodName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get().name().equals("x"));
    }

    @Test
    public void testGoStructMethodSingleParamExists() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x,y int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.lol(int, int).x"));
        assertTrue(generatedSourceModel.containsComponent("main.person.lol(int, int).y"));
    }

    @Test
    public void testGoStructMethodExistsInAnotherSourceFile() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package cakes\n import \"main\" \n func (p main.Person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/person.go", code));
        rawData.insertFile(new File("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.Person.x() : (int)"));
    }

    @Test
    public void testGoStructMethodExistsInAnotherSourceFilev2() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package cakes\n import main \"main\" \n func (p main.Person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/cherry.go", code));
        rawData.insertFile(new File("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.Person.x() : (int)"));
        assertTrue(generatedSourceModel.containsComponent("main.Person"));
    }

    @Test
    public void structMethodInDifferentSourceFileInSamePackage() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n func (p *Person) x(y value) []value {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/test.go", codeB));
        rawData.insertFile(new File("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.Person.x(value) : ([]value)"));
        assertTrue(generatedSourceModel.containsComponent("main.Person.x(value) : ([]value).y"));
    }
}
