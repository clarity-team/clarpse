package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class CodeTest {

    @Test
    public void testMethodCode() throws Exception {

        final String code = "@Deprecated public class test { \n static void aMethod (String a) { \n int x = 1; \n a = null; \n } \n }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.aMethod(String)").get().code().equals(" static void aMethod (String a) { \n" +
                " int x = 1; \n" +
                " a = null; \n" +
                " } "));
    }

    @Test
    public void testClassCode() throws Exception {

        final String code = "@Deprecated public class test { \n static void aMethod (String a) { \n int x = 1; \n a = null; \n } \n }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test").get().code().equals("@Deprecated public class test { \n" +
                " static void aMethod (String a) { \n" +
                " int x = 1; \n" +
                " a = null; \n" +
                " } \n" +
                " }"));
    }
}
