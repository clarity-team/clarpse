package com.hadii.test.go;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure component invocations for a given component are inherited by its
 * parents.
 */
public class InheritanceReferenceTest extends GoTestBase {

    @Test
    public void StructInheritsFieldVarInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        projectFiles = goLangProjectFilesFixture("/src");
        projectFiles.insertFile(new ProjectFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }

    @Test
    public void StructMethodInheritsLocalVarInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {} func (p person) x() int {var mathObj math.Person}";
        projectFiles = goLangProjectFilesFixture("/src");
        projectFiles.insertFile(new ProjectFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }

    @Test
    public void StructMethodInheritsMethodParamInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {} func (p person) x(mathObj math.Person) int {}";
        projectFiles = goLangProjectFilesFixture("/src");
        projectFiles.insertFile(new ProjectFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x(math.Person) : (int)")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }

    @Test
    public void StructInheritsLocalVarInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {} func (p person) x() int {var mathObj math.Person}";
        projectFiles = goLangProjectFilesFixture("/src");
        projectFiles.insertFile(new ProjectFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }

    @Test
    public void StructInheritsMethodParamInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {} func (p person) x(mathObj math.Person) int {}";
        projectFiles = goLangProjectFilesFixture("/src");
        projectFiles.insertFile(new ProjectFile("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }
}
