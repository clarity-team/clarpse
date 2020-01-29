package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.reference.ComponentReference;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.TypeReferences;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class SimpleTypeReferenceTest {

    @Test
    public void testFieldVarTypeDeclaration() throws Exception {

        final String code = "class Test { String fieldVar; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentReference invocation = (generatedSourceModel.getComponent("Test.fieldVar")
                .get().references(TypeReferences.SIMPLE).get(0));
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
                .get().references(TypeReferences.SIMPLE).size() == 1);
    }

    @Test
    public void testClassExpressionsNotCountedAsTypeDeclaration() throws Exception {

        final String code = "class Test { Logger log = new Logger(Lol.class); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.log").get().references(TypeReferences.SIMPLE)
                .size() == 1);
    }

    @Test
    public void testMethodParamTypeDeclaration() throws Exception {

        final String code = "class Test { void method(String s1, int s2){} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.method(String, int).s1")
                .get().references(TypeReferences.SIMPLE).get(0).invokedComponent()
                .equals("java.lang.String"));
        assertTrue(generatedSourceModel.getComponent("Test.method(String, int).s2")
                .get().references(TypeReferences.SIMPLE).get(0).invokedComponent()
                .equals("java.lang.Integer"));
    }

    @Test
    public void testMethodParamTypeDeclarationListSize() throws Exception {

        final String code = "class Test { void method(String s1, int s2){} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.method(String, int).s1")
                .get().references(TypeReferences.SIMPLE).size() == 1);
        assertTrue(generatedSourceModel.getComponent("Test.method(String, int).s2")
                .get().references(TypeReferences.SIMPLE).size() == 1);
    }

    @Test
    public void testMethodLocalVarTypeDeclaration() throws Exception {

        final String code = "class Test { void method(){ String s; } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.method().s")
                .get().references(TypeReferences.SIMPLE).get(0).invokedComponent()
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
                .get().references(TypeReferences.SIMPLE).size() == 1);
    }

    @Test
    public void TypeDeclarationArrayList() throws Exception {
        final RawFile file = new RawFile("ClassA.java", "package com.sample;" + "import java.util.ArrayList; import java.util.Map;"
                + "public class ClassA {  private Map<String,ArrayList<ClassB>> b;}");
        final SourceFiles reqCon = new SourceFiles(Lang.JAVA);
        final ArrayList<SourceFiles> reqCons = new ArrayList<SourceFiles>();
        reqCon.insertFile(file);
        reqCons.add(reqCon);
        final OOPSourceCodeModel codeModel = new ClarpseProject(reqCon).result();
        assertTrue(codeModel.getComponent("com.sample.ClassA.b").get().references().size() == 4);
    }
}
