package com.hadii.test.java;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CodeFragmentTest {

    @Test
    public void classGenericsCodeFragmentTest() throws Exception {
        final String code = "class Test<List> {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").get().codeFragment().equals("<List>"));
    }

    @Test
    public void classGenericsCodeFragmentTestv2() throws Exception {
        final String code = "class Test<T extends List> {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").get().codeFragment().equals("<T extends List>"));
    }

    @Test
    public void fieldVarCodeFragmentTest() throws Exception {
        final String code = "import java.util.Map;import java.util.List; \n  class Test {List<Integer> fieldVar, x;}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").get().codeFragment().equals("fieldVar : List<Integer>"));
        assertTrue(generatedSourceModel.getComponent("Test.x").get().codeFragment().equals("x : List<Integer>"));
    }

    @Test
    public void fieldVarCodeFragmentTestComplex() throws Exception {
        final String code = "import java.util.Map;import java.util.List; \n class Test {Map<String, List<String>> fieldVar, x;}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.fieldVar").get().codeFragment().equals("fieldVar : Map<String, List<String>>"));
        assertTrue(generatedSourceModel.getComponent("Test.x").get().codeFragment().equals("x : Map<String, List<String>>"));
    }

    @Test
    public void simpleMethodCodeFragmentTest() throws Exception {
        final String code = "import java.util.Map;import java.util.List; \n  class Test {Map<String, List<Integer>> sMethod() {}}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.sMethod()").get().codeFragment().equals("sMethod() : Map<String, List<Integer>>"));
    }

    @Test
    public void interfaceMethodCodeFragmentTest() throws Exception {
        final String code = "import java.util.Map;import java.util.List; \n  interface Test { Map<String, List<Integer>> sMethod();}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.sMethod()").get().codeFragment().equals("sMethod() : Map<String, List<Integer>>"));
    }

    @Test
    public void complexMethodCodeFragmentTest() throws Exception {
        final String code = "import java.util.Map;import java.util.List; \n  class Test {Map<List<String>, String[]> sMethod(String s, int t) {}}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("Test.sMethod(String, int)").get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("Test.sMethod(String, int)").get().codeFragment().equals("sMethod(String, int) : Map<List<String>, String[]>"));
    }
}
