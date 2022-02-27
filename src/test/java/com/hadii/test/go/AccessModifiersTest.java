package com.hadii.test.go;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AccessModifiersTest extends GoTestBase {

    @Test
    public void testParseGoStructPrivateVisibility() throws Exception {
        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type Teacher struct{}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().modifiers().contains("private"));
    }

    @Test
    public void testGoStructFieldVarPrivateVisibility() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").get().modifiers().contains("private"));
    }

    @Test
    public void testParseGoStructPublicVisibility() throws Exception {
        final String code = "package main\n import \"fmt\"\n /*test*/ \n type person struct {} type Teacher struct{}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Teacher").get().modifiers().contains("public"));
    }

    @Test
    public void testGoStructFieldVarPublicVisibility() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {MathObj math.Person}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.MathObj").get().modifiers().contains("public"));
    }

    @Test
    public void testParseGoInterfacePublicVisibility() throws Exception {
        final String code = "package main\n import \"fmt\"\n /*test*/ type Person interface {} type Teacher struct{}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person").get().modifiers().contains("public"));
    }

    @Test
    public void testParseGoInterfacePrivateVisibility() throws Exception {
        final String code = "package main\n import \"fmt\"\n /*test*/ type person interface {}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().modifiers().contains("private"));
    }
}
