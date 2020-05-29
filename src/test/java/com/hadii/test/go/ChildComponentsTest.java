package com.hadii.test.go;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.File;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.SourceFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChildComponentsTest {

    @Test
    public void testGoStructMethodSingleParamComponentIsChildOfMethod() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int)").get().children().get(0).equals("main.person.lol(int).x"));
    }

    @Test
    public void testInterfaceAnonymousTypeMethodParamsIsChildOfMethod() throws Exception {
        final String code = "package main \n type plain interface \n{ testMethodv2(x value, h int) (value, uintptr) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
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
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().children().contains("main.person.x() : (int)"));
    }

    @Test
    public void testParseGoStructMethodWithUnnamedParameters() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x(string) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x(string)").get().children().size() == 1);
    }

    @Test
    public void testParseGoStructMethodWithEmptyReturnParenthesis() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() () {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person.x() : ()"));
    }

    @Test
    public void testParseGoStructWithMultipleFields() throws Exception {
        final String code = "package main\n type accountFile struct {\n" +
                "        PrivateKeyId string `json:\"private_key_id\"`\n" +
                "        PrivateKey   string `json:\"private_key\"`\n" +
                "        ClientEmail  string `json:\"client_email\"`\n" +
                "        ClientId     string `json:\"client_id\"`";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("accountFile.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.accountFile").get().children().size() == 4);
    }


    @Test
    public void testGoStructFIeldVarIsChildOfStruct() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().children().get(0).equals("main.person.mathObj"));
    }

    @Test
    public void testGoStructLocalVarIsChildOfMethod() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() {\nvar b, c int = 1, 2\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
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
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().children().get(0).equals("main.person.testMethod() : (int)"));
    }
}
