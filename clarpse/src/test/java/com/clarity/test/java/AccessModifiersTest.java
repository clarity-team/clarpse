package com.clarity.test.java;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.compiler.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class AccessModifiersTest {

    @Test
    public void testClassLevelModifier() throws Exception {

        final String code = " public class Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) generatedSourceModel.getComponent("Test").modifiers().toArray()[0])
                .equalsIgnoreCase("public"));
    }

    @Test
    public void testInterfaceLevelModifier() throws Exception {

        final String code = " public interface Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) generatedSourceModel.getComponent("Test").modifiers().toArray()[0])
                .equalsIgnoreCase("public"));
    }

    @Test
    public void testEnumLevelModifier() throws Exception {

        final String code = " private enum Test { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) ((Set<String>) generatedSourceModel.getComponent("Test").modifiers()).toArray()[0])
                .equalsIgnoreCase("private"));
    }

    @Test
    public void testClassMethodLevelModifier() throws Exception {

        final String code = " private class Test { static boolean lolcakes(){} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) generatedSourceModel.getComponent("Test.lolcakes()").modifiers().toArray()[0])
                .equalsIgnoreCase("static"));
    }

    @Test
    public void testClassConstructorLevelModifier() throws Exception {

        final String code = " private class Test { test(){} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.test()").modifiers().isEmpty());
    }

    @Test
    public void testInterfaceMethodLevelModifier() throws Exception {

        final String code = " private interface Test { abstract boolean lolcakes(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((String) generatedSourceModel.getComponent("Test.lolcakes()").modifiers().toArray()[0])
                .equalsIgnoreCase("abstract"));
    }

    @Test
    public void testFieldVarLevelModifier() throws Exception {

        final String code = " public class Test { public static int fieldVar; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
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
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(
                ((String) generatedSourceModel.getComponent("Test.Test(java.lang.String).str").modifiers().toArray()[0])
                        .equalsIgnoreCase("final"));
    }
}
