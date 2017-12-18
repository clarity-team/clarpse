package com.clarity.test.java;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.compiler.RawFile;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;

public class TypeDeclarationTest {

    @Test
    public void testFieldVarTypeDeclaration() throws Exception {

        final String code = "class Test { String fieldVar; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("Test.fieldVar")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0));
        assertTrue(invocation.invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testFieldVarTypeDeclarationListSize() throws Exception {

        final String code = "class Test { String fieldVar; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar")
                .componentInvocations(ComponentInvocations.DECLARATION).size() == 1);
    }

    @Test
    public void testClassExpressionsNotCountedAsTypeDeclaration() throws Exception {

        final String code = "class Test { Logger log = new Logger(Lol.class); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.log").componentInvocations(ComponentInvocations.DECLARATION)
                .size() == 1);
    }

    @Test
    public void testMethodParamTypeDeclaration() throws Exception {

        final String code = "class Test { void method(String s1, int s2){} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.method(java.lang.String,java.lang.Integer).s1")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("java.lang.String"));
        assertTrue(generatedSourceModel.getComponent("Test.method(java.lang.String,java.lang.Integer).s2")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("java.lang.Integer"));
    }

    @Test
    public void testMethodParamTypeDeclarationListSize() throws Exception {

        final String code = "class Test { void method(String s1, int s2){} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.method(java.lang.String,java.lang.Integer).s1")
                .componentInvocations(ComponentInvocations.DECLARATION).size() == 1);
        assertTrue(generatedSourceModel.getComponent("Test.method(java.lang.String,java.lang.Integer).s2")
                .componentInvocations(ComponentInvocations.DECLARATION).size() == 1);
    }

    @Test
    public void testMethodLocalVarTypeDeclaration() throws Exception {

        final String code = "class Test { void method(){ String s; } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.method().s")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("java.lang.String"));
    }

    @Test
    public void testMethodLocalVarTypeDeclarationListSize() throws Exception {

        final String code = "class Test { void method(){ String s; } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.method().s")
                .componentInvocations(ComponentInvocations.DECLARATION).size() == 1);
    }
}
