package com.clarity.test.java;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.compiler.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class CommentsParsingTest {

    @Test
    public void testClassLevelComment() throws Exception {

        final String code = "package test; /** Licensing */ import lol; /**A \n comment \n */ public class Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**Acomment*/"));
    }

    @Test
    public void testInterfaceLevelComment() throws Exception {

        final String code = "package test;  import lol; /**A \n comment*/ public class Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**Acomment*/"));
    }

    @Test
    public void testEnumLevelComment() throws Exception {

        final String code = "package test;  import lol; /**A \n comment*/ public enum Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**Acomment*/"));
    }

    @Test
    public void testNestedClassLevelComment() throws Exception {

        final String code = "package test; /** Licensing */ import lol; public class Test { /**A \n comment*/  class Base{} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test.Base").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**Acomment*/"));
    }

    @Test
    public void testMethodLevelComment() throws Exception {

        final String code = "public class Test { String fieldVar; /**lolcakes*/ void test() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.test()").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**lolcakes*/"));
    }

    @Test
    public void testInterfaceMethodLevelComment() throws Exception {

        final String code = "public interface Test { /**lol \n cakes \n */abstract void test();}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.test()").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**lolcakes*/"));
    }

    @Test
    public void testFieldVarLevelComment() throws Exception {

        final String code = "/*lolcakesv2*/ public class Test { /**lolcakes*/ String fieldVar;}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**lolcakes*/"));
    }

    @Test
    public void testMethodParamLevelComment() throws Exception {

        final String code = "public class Test { void aMethod(/**lolcakes*/ String methodParam){}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.aMethod(java.lang.String).methodParam").comment()
                .replaceAll("[\\n\\t\\r ]", "").equalsIgnoreCase("/**lolcakes*/"));
    }
}
