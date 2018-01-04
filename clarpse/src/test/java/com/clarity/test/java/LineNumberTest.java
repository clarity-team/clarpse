package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LineNumberTest {

    @Test
    public void testClassComponentLineNumber() throws Exception {

        final String code = " public class Test {\n public Test(String test) {\n String tester;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").line() == 1);
    }

    @Test
    public void testMethodComponentLineNumber() throws Exception {

        final String code = "public class Test {\n public Test(String test) {\n String tester;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.Test(java.lang.String)").line() == 2);
    }

    @Test
    public void testFieldVarComponentLineNumber() throws Exception {

        final String code = "public class Test {\n private String fieldVar;\n }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").line() == 2);
    }


    @Test
    public void testMethodParamComponentLineNumber() throws Exception {

        final String code = "public class Test {\n public Test(\nString test) {\n String tester;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.Test(java.lang.String).test").line() == 3);
    }

    @Test
    public void testLocalVarComponentLineNumber() throws Exception {

        final String code = "public class Test {\n public Test(\nString test) {\nString tester;\n}\n}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.Test(java.lang.String).tester").line() == 4);
    }
}
