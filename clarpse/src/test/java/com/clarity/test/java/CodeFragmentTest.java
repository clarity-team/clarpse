package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CodeFragmentTest {

    @Test
    public void classGenericsCodeFragmentTest() throws Exception {

        final String code = "class Test<List> {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").get().codeFragment().equals("<List>"));
    }

    @Test
    public void classGenericsCodeFragmentTestv2() throws Exception {

        final String code = "class Test<T extends List> {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").get().codeFragment().equals("<T extends List>"));
    }

    @Test
    public void fieldVarCodeFragmentTest() throws Exception {

        final String code = "class Test {List<Integer, String> fieldVar, x;}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").get().codeFragment().equals("fieldVar : List<Integer, String>"));
        assertTrue(generatedSourceModel.getComponent("Test.x").get().codeFragment().equals("x : List<Integer, String>"));
    }

    @Test
    public void fieldVarCodeFragmentTestComplex() throws Exception {

        final String code = "class Test {Map<String, List<Integer, String>> fieldVar, x;}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").get().codeFragment().equals("fieldVar : Map<String, List<Integer, String>>"));
        assertTrue(generatedSourceModel.getComponent("Test.x").get().codeFragment().equals("x : Map<String, List<Integer, String>>"));
    }

    @Test
    public void simpleMethodCodeFragmentTest() throws Exception {

        final String code = "class Test {Map<String, List<Integer, String>> sMethod() {}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.sMethod()").get().codeFragment().equals("sMethod() : Map<String, List<Integer, String>>"));
    }

    @Test
    public void interfaceMethodCodeFragmentTest() throws Exception {

        final String code = "interface Test { Map<String, List<Integer, String>> sMethod();}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.sMethod()").get().codeFragment().equals("sMethod() : Map<String, List<Integer, String>>"));
    }

    @Test
    public void complexMethodCodeFragmentTest() throws Exception {

        final String code = "class Test {Map<List<String>, String[]> sMethod(String s, int t) {}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("Test.sMethod(String, int)").get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("Test.sMethod(String, int)").get().codeFragment().equals("sMethod(String, int) : Map<List<String>, String[]>"));
    }


}
