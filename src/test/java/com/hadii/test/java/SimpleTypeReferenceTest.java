package com.hadii.test.java;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.reference.ComponentReference;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants.TypeReferences;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class SimpleTypeReferenceTest {

    @Test
    public void testFieldVarTypeDeclaration() throws Exception {
        final String code = "class Test { String fieldVar; }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        final ComponentReference invocation = (generatedSourceModel.getComponent("Test.fieldVar")
                                                                   .get().references(TypeReferences.SIMPLE).get(0));
        Assert.assertEquals("java.lang.String", invocation.invokedComponent());
    }

    @Test
    public void testFieldVarImportTypeDeclaration() throws Exception {
        final String codeA = "package com; \n import org.ClassB; \n" +
            " class Test { \n ClassB fieldVar; \n }";
        final String codeB = "package org; \n class ClassB { }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("src/main/com/Test.java", codeA));
        rawData.insertFile(new ProjectFile("src/main/org/ClassB.java", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        final ComponentReference invocation = (generatedSourceModel.getComponent("com.Test" +
                                                                                     ".fieldVar")
                                                                   .get().references(TypeReferences.SIMPLE).get(0));
        Assert.assertEquals("org.ClassB", invocation.invokedComponent());
    }

    @Test
    public void testFieldVarTypeDeclarationListSize() throws Exception {
        final String code = "class Test { String fieldVar; }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertEquals(1, generatedSourceModel.getComponent("Test.fieldVar")
                                                   .get().references(TypeReferences.SIMPLE).size());
    }

    @Test
    public void testResolveImportInFieldType() throws Exception {
        final String codeB = "package some.maven.pkg; \n class Logger { }";
        final String code = "import some.maven.pkg.Logger; \n class Test { Logger log = new " +
            "Logger(Lol.class); }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        rawData.insertFile(new ProjectFile("Logger.java", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertEquals(
            1,generatedSourceModel.getComponent("Test.log").get().references(TypeReferences.SIMPLE).size());
    }

    @Test
    public void testMethodParamTypeDeclaration() throws Exception {
        final String code = "class Test { void method(String s1, int s2) { } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("Test.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertEquals("java.lang.String", generatedSourceModel.getComponent(
            "Test.method(String, int).s1").get().references(TypeReferences.SIMPLE).get(0).invokedComponent());
    }

    @Test
    public void testMethodThrowsExceptionReference() throws Exception {
        final String code = "class Test { \nvoid AMethod() throws Exception \n{ } \n}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("Test.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertEquals("java.lang.Exception", generatedSourceModel.getComponent(
            "Test.AMethod()").get().references(TypeReferences.SIMPLE).get(0).invokedComponent());
    }

    @Test
    public void testMethodParamTypeDeclarationListSize() throws Exception {
        final String code = "class Test { void method(String s1, int s2){} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertEquals(1, generatedSourceModel.getComponent("Test.method(String, int).s1")
                                                   .get().references(TypeReferences.SIMPLE).size());
        Assert.assertEquals(1, generatedSourceModel.getComponent("Test.method(String, int).s2")
                                                   .get().references(TypeReferences.SIMPLE).size());
    }

    @Test
    public void testMethodLocalVarTypeDeclaration() throws Exception {
        final String code = "class Test { void method(){ String s; } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertEquals("java.lang.String", generatedSourceModel.getComponent(
            "Test.method().s").get().references(TypeReferences.SIMPLE).get(0).invokedComponent());
    }

    @Test
    public void testMethodCallStaticTypeReference() throws Exception {
        final String code = "class Test { void test() { Cake.method(); } }";
        final String codeD = "import java.util.List;  public class Cake { public " +
            "static List<String> method() { return null; } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("Test.java", code));
        rawData.insertFile(new ProjectFile("Cake.java", codeD));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertEquals("Cake", generatedSourceModel.getComponent(
            "Test.test()").get().references(TypeReferences.SIMPLE).get(0).invokedComponent());
        Assert.assertEquals("Cake", generatedSourceModel.getComponent(
            "Test.test()").get().references(TypeReferences.SIMPLE).get(0).invokedComponent());
    }

    @Test
    public void testInstantiateObjectType() throws Exception {
        final String code = "class Test { void method(){ new String(\"s\"); } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("Test.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertEquals("java.lang.String", generatedSourceModel.getComponent(
            "Test.method()").get().references(TypeReferences.SIMPLE).get(0).invokedComponent());
    }

    @Test
    public void testMethodLocalVarTypeDeclarationListSize() throws Exception {
        final String code = "class Test { void method(){ String s; } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertEquals(1, generatedSourceModel.getComponent("Test.method().s")
                                                   .get().references(TypeReferences.SIMPLE).size());
    }

    @Test
    public void TypeDeclarationArrayList() throws Exception {
        final ProjectFile ProjectFile = new ProjectFile("src/com/sample/ClassA.java",
                                                        "package com.sample;"
                                                            + "import java.util.ArrayList; import" +
                                                            " java.util.Map;"
                                                            + " \n public class ClassA {  private" +
                                                            " Map<String, ArrayList<String>> b,c;" +
                                                            "}");
        final ProjectFiles reqCon = new ProjectFiles(Lang.JAVA);
        reqCon.insertFile(ProjectFile);
        final ArrayList<ProjectFiles> reqCons = new ArrayList<ProjectFiles>();
        reqCons.add(reqCon);
        final OOPSourceCodeModel codeModel = new ClarpseProject(reqCon).result().model();
        Assert.assertEquals(3, codeModel.getComponent("com.sample.ClassA.b").get()
                                        .references().size());
        Assert.assertEquals(3, codeModel.getComponent("com.sample.ClassA.c").get()
                                        .references().size());
    }
}
