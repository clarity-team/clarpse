package com.clarity.test.go;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChildComponentsTest {

    @Test
    public void testGoStructMethodSingleParamComponentIsChildOfMethod() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int)").get().children().get(0).equals("main.person.lol(int).x"));
    }

    @Test
    public void testInterfaceAnonymousTypeMethodParamsIsChildOfMethod() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2(x value, h int) (value, uintptr) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(value, int) : (value, uintptr)").get().children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(value, int) : (value, uintptr).x")
                .get().componentType() == OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void testGoStructMethodIsChildofStruct() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().children().contains("main.person.x() : (int)"));
    }

    @Test
    public void testParseGoStructMethodWithUnnamedParameters() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x(string) () {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x(string)").get().children().size() == 1);
    }

    @Test
    public void testGoStructFIeldVarIsChildOfStruct() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().children().get(0).equals("main.person.mathObj"));
    }

    @Test
    public void testGoStructLocalVarIsChildOfMethod() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() {\nvar b, c int = 1, 2\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x()").get().children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("main.person.x()").get().children().contains(
                "main.person.x().b"));
        assertTrue(generatedSourceModel.getComponent("main.person.x()").get().children().contains(
                "main.person.x().c"));
    }

    @Test
    public void testInterfaceMethodSpecComponentIsChildOfParentInterface() throws Exception {
        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().children().get(0).equals("main.person.testMethod() : (int)"));
    }
}
