package com.clarity.test.go;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.File;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure component invocations for a given component are inherited by its
 * parents.
 */
public class InheritanceReferenceTest {

    @Test
    public void StructInheritsFieldVarInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }

    @Test
    public void StructMethodInheritsLocalVarInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {} func (p person) x() int {var mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }

    @Test
    public void StructMethodInheritsMethodParamInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {} func (p person) x(mathObj math.Person) int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x(math.Person) : (int)")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }

    @Test
    public void StructInheritsLocalVarInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {} func (p person) x() int {var mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }

    @Test
    public void StructInheritsMethodParamInvocations() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {} func (p person) x(mathObj math.Person) int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("test.math.Person"));
    }
}
