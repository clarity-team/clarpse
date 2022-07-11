package com.hadii.test.java;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommentsParsingTest {

    @Test
    public void testClassLevelComment() throws Exception {
        final String code = "package test; /** Licensing */ import lol; /**\n*A comment \n */ public class Test { }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("test.Test").get().comment().equals("/**\n" +
                " * A comment\n" +
                " */\n"));
    }

    @Test
    public void testClassLevelNoComment() throws Exception {
        final String code = "package test; import lol; public class Test { }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("test.Test").get().comment().equals(""));
    }

    @Test
    public void testInterfaceLevelComment() throws Exception {
        final String code = "package test;  import lol; /**A \n comment*/ public class Test { }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("test.Test").get().comment().equals("/**\n" +
                " * A\n" +
                " *  comment\n" +
                " */\n"));
    }

    @Test
    public void testEnumLevelComment() throws Exception {
        final String code = "package test;  import lol; /**A \n comment*/ public enum Test { }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("test.Test").get().comment().equals("/**\n" +
                " * A\n" +
                " *  comment\n" +
                " */\n"));
    }

    @Test
    public void testNestedClassLevelComment() throws Exception {
        final String code = "package test; /** Licensing */ import lol; public class Test { /**A \n comment*/  class Base{} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("test.Test.Base").get().comment().equals("/**\n" +
                " * A\n" +
                " *  comment\n" +
                " */\n"));
    }

    @Test
    public void testMethodLevelComment() throws Exception {
        final String code = "public class Test { String fieldVar;\n /**\nlolcakes\n*/\n void test() {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Test.test()").get().comment().equals("/**\n" +
                " * lolcakes\n" +
                " */\n"));
    }

    @Test
    public void testInterfaceMethodLevelComment() throws Exception {
        final String code = "public interface Test { /**lol cakes */ void test();}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Test.test()").get().comment().equals("/**\n" +
                " * lol cakes\n" +
                " */\n"));
    }

    @Test
    public void testFieldVarLevelComment() throws Exception {
        final String code = "/*lolcakesv2*/ public class Test { /**lolcakes*/ String fieldVar;}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").get().comment().equals("/**\n" +
                " * lolcakes\n" +
                " */\n"));
    }

    @Test
    public void testMethodParamLevelComment() throws Exception {
        final String code = "public class Test { void aMethod(/**lolcakes*/ String methodParam){}}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Test.aMethod(String).methodParam").get().comment().equals("/**\n" +
                " * lolcakes\n" +
                " */\n"));
    }
}
