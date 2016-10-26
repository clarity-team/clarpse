package com.clarity.java;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class AccessModifiersTest {

    @Test
    public void testClassLevelModifier() throws Exception {

        final String code = " public class Test { }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) generatedSourceModel.getComponent("Test").modifiers().toArray()[0])
                .equalsIgnoreCase("public"));
    }

    @Test
    public void testInterfaceLevelModifier() throws Exception {

        final String code = " public interface Test { }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) generatedSourceModel.getComponent("Test").modifiers().toArray()[0])
                .equalsIgnoreCase("public"));
    }

    @Test
    public void testEnumLevelModifier() throws Exception {

        final String code = " private enum Test { }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) ((Set<String>) generatedSourceModel.getComponent("Test").modifiers()).toArray()[0])
                .equalsIgnoreCase("private"));
    }

    @Test
    public void testClassMethodLevelModifier() throws Exception {

        final String code = " private class Test { static boolean lolcakes(){} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) generatedSourceModel.getComponent("Test.lolcakes()").modifiers().toArray()[0])
                .equalsIgnoreCase("static"));
    }

    @Test
    public void testClassConstructorLevelModifier() throws Exception {

        final String code = " private class Test { test(){} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.test()").modifiers().isEmpty());
    }

    @Test
    public void testInterfaceMethodLevelModifier() throws Exception {

        final String code = " private interface Test { abstract boolean lolcakes(); }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) generatedSourceModel.getComponent("Test.lolcakes()").modifiers().toArray()[0])
                .equalsIgnoreCase("abstract"));
    }

    @Test
    public void testFieldVarLevelModifier() throws Exception {

        final String code = " public class Test { public static int fieldVar; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) generatedSourceModel.getComponent("Test.fieldVar").modifiers().toArray()[0])
                .equalsIgnoreCase("public"));
        assertTrue(((String) generatedSourceModel.getComponent("Test.fieldVar").modifiers().toArray()[1])
                .equalsIgnoreCase("static"));
    }

    @Test
    public void testLocalVarLevelModifier() throws Exception {

        final String code = " public class Test { Test(final String str){} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(
                ((String) generatedSourceModel.getComponent("Test.Test(java.lang.String).str").modifiers().toArray()[0])
                        .equalsIgnoreCase("final"));
    }
}
