package com.clarity.java;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class CommentsParsingTest {

    @Test
    public void testClassLevelComment() throws Exception {

        final String code = "package test; /** Licensing */ import lol; /**A \n comment*/ public class Test { }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**Acomment*/"));
    }

    @Test
    public void testInterfaceLevelComment() throws Exception {

        final String code = "package test;  import lol; /**A \n comment*/ public class Test { }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**Acomment*/"));
    }

    @Test
    public void testEnumLevelComment() throws Exception {

        final String code = "package test;  import lol; /**A \n comment*/ public enum Test { }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**Acomment*/"));
    }

    @Test
    public void testNestedClassLevelComment() throws Exception {

        final String code = "package test; /** Licensing */ import lol; public class Test { /**A \n comment*/  class Base{} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Test.Base").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**Acomment*/"));
    }

    @Test
    public void testMethodLevelComment() throws Exception {

        final String code = "public class Test { String fieldVar; /**lolcakes*/ void test() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.test()").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**lolcakes*/"));
    }

    @Test
    public void testInterfaceMethodLevelComment() throws Exception {

        final String code = "public interface Test { /**lolcakes*/abstract void test();}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.test()").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**lolcakes*/"));
    }

    @Test
    public void testFieldVarLevelComment() throws Exception {

        final String code = "/*lolcakesv2*/ public class Test { /**lolcakes*/ String fieldVar;}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").comment().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**lolcakes*/"));
    }

    @Test
    public void testMethodParamLevelComment() throws Exception {

        final String code = "public class Test { void aMethod(/**lolcakes*/ String methodParam){}}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.aMethod(java.lang.String).methodParam").comment()
                .replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase("/**lolcakes*/"));
    }
}
