package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.File;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.reference.ComponentReference;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure component invocations for a given component are inherited by its
 * parents.
 */
public class InvocationReferenceTest {

    @Test
    public void testClassInheritsFieldInvocations() throws Exception {
        final String code = "class Test { String fieldVar; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("Test").get().references().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassInheritsMethodInvocations() throws Exception {
        final String code = "class Test { public String aMethod(){} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("Test").get().references().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassInheritsLocalVarsInvocations() throws Exception {
        final String code = "class Test { public void fieldVar(){String test;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("Test").get().references().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassInheritsMethodParamsInvocations() throws Exception {
        final String code = "class Test { public void fieldVar(String test){} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("Test").get().references().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassInheritsNestedClassInvocations() throws Exception {
        final String code = "class Test { class NestedClass { public void fieldVar(String test){} } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("Test").get().references().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassDoesNotInheritExtendsAndImplementsInvocations() throws Exception {
        final String code = "class Test { class NestedClass extends String implements Integer {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").get().references().isEmpty());
    }

    @Test
    public void testInterfaceInheritsFieldInvocations() throws Exception {
        final String code = "interface Test { String localVar; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("Test").get().references().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testInterfaceInheritsMethodInvocations() throws Exception {
        final String code = "interface Test { abstract String aMethod(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("Test").get().references().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testInterfaceInheritsMethodParamsInvocations() throws Exception {
        final String code = "interface Test { abstract void aMethod(String test); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("Test").get().references().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testMethodInheritsLocalVarsInvocations() throws Exception {
        final String code = "class Test { public void aMethod(){String test;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(
                ((ComponentReference) generatedSourceModel.getComponent("Test.aMethod()").get().references().toArray()[0])
                        .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testMethodInheritsMethodParamsInvocations() throws Exception {
        final String code = "class Test { public void aMethod(String test){} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("Test.aMethod(String)")
                .get().references().toArray()[0]).invokedComponent().equals("java.lang.String"));
    }
}
