package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommentsParsingTest {

    @Test
    public void testClassLevelComment() throws Exception {

        final String code = "package test; /** Licensing */ import lol; /**\n*A comment \n */ public class Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").get().comment().equals("/**\n" +
                " * A comment\n" +
                " */\n"));
    }


    @Test
    public void testClassLevelNoComment() throws Exception {

        final String code = "package test; import lol; public class Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").get().comment().equals(""));
    }

    @Test
    public void testInterfaceLevelComment() throws Exception {

        final String code = "package test;  import lol; /**A \n comment*/ public class Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").get().comment().equals("/**\n" +
                " * A\n" +
                " * comment\n" +
                " */\n"));
    }

    @Test
    public void testEnumLevelComment() throws Exception {

        final String code = "package test;  import lol; /**A \n comment*/ public enum Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").get().comment().equals("/**\n" +
                " * A\n" +
                " * comment\n" +
                " */\n"));
    }

    @Test
    public void testNestedClassLevelComment() throws Exception {

        final String code = "package test; /** Licensing */ import lol; public class Test { /**A \n comment*/  class Base{} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test.Base").get().comment().equals("/**\n" +
                " * A\n" +
                " * comment\n" +
                " */\n"));
    }

    @Test
    public void testMethodLevelComment() throws Exception {

        final String code = "public class Test { String fieldVar;\n /**\nlolcakes\n*/\n void test() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.test()").get().comment().equals("/**\n" +
                " * lolcakes\n" +
                " */\n"));
    }

    @Test
    public void testInterfaceMethodLevelComment() throws Exception {

        final String code = "public interface Test { /**lol cakes */ void test();}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.test()").get().comment().equals("/**\n" +
                " * lol cakes\n" +
                " */\n"));
    }

    @Test
    public void testFieldVarLevelComment() throws Exception {

        final String code = "/*lolcakesv2*/ public class Test { /**lolcakes*/ String fieldVar;}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").get().comment().equals("/**\n" +
                " * lolcakes\n" +
                " */\n"));
    }

    @Test
    public void testMethodParamLevelComment() throws Exception {

        final String code = "public class Test { void aMethod(/**lolcakes*/ String methodParam){}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.aMethod(String).methodParam").get().comment().equals("Optional[/**\n" +
                " * lolcakes\n" +
                " */\n" +
                "]"));
    }
}
