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
        assertTrue(generatedSourceModel.getComponent("test.Test").comment().equals("/**\n" +
                " * A comment\n" +
                " */\n"));
    }

    @Test
    public void testInterfaceLevelComment() throws Exception {

        final String code = "package test;  import lol; /**A \n comment*/ public class Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").comment().equals("/**\n" +
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
        assertTrue(generatedSourceModel.getComponent("test.Test").comment().equals("/**\n" +
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
        assertTrue(generatedSourceModel.getComponent("test.Test.Base").comment().equals("/**\n" +
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
        assertTrue(generatedSourceModel.getComponent("Test.test()").comment().equals("/**\n" +
                " * lolcakes\n" +
                " */\n"));
    }

    @Test
    public void testInterfaceMethodLevelComment() throws Exception {

        final String code = "public interface Test { /**lol \n cakes \n */abstract void test();}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.test()").comment().equals("/**\n" +
                " * lol\n" +
                " * cakes\n" +
                " */\n"));
    }

    @Test
    public void testFieldVarLevelComment() throws Exception {

        final String code = "/*lolcakesv2*/ public class Test { /**lolcakes*/ String fieldVar;}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").comment().equals("/**\n" +
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
        assertTrue(generatedSourceModel.getComponent("Test.aMethod(java.lang.String).methodParam").comment().equals("Optional[/**\n" +
                " * lolcakes\n" +
                " */\n" +
                "]"));
    }
}
