package com.clarity.java;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class ChildComponentsTest {

	@Test
    public void testClassHasMethodChild() throws Exception {

        final String code = "class Test { void method(){} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").children().get(0).equals("Test.method()"));
    }
	
	@Test
    public void testClassHasFieldVarChild() throws Exception {

        final String code = "class Test { String fieldVar; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").children().get(0).equals("Test.fieldVar"));
    }
	
	@Test
    public void testIntefaceHasMethodChild() throws Exception {

        final String code = "interface Test { void method(); }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").children().get(0).equals("Test.method()"));
    }
	
	@Test
    public void testIntefaceHasConstantFieldChild() throws Exception {

        final String code = "interface Test { String NEAR_TO_QUERY; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").children().get(0).equals("Test.NEAR_TO_QUERY"));
    }
	
	@Test
    public void testClassHasNestedIntefaceChild() throws Exception {

        final String code = "class TestA { interface TestB { }}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("TestA").children().get(0).equals("TestA.TestB"));
    }
	
	@Test
    public void testClassHasNestedEnumChild() throws Exception {

        final String code = "class TestA { enum TestB { }}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("TestA").children().get(0).equals("TestA.TestB"));
    }
	
	@Test
    public void testEnumHasNestedConstantsChild() throws Exception {

        final String code = " enum TestA { A,B,C; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("TestA").children().get(0).equals("TestA.A"));
        assertTrue(generatedSourceModel.getComponent("TestA").children().get(1).equals("TestA.B"));
        assertTrue(generatedSourceModel.getComponent("TestA").children().get(2).equals("TestA.C"));
    }
	
	@Test
    public void testClassWithMultipleChildren() throws Exception {

        final String code = " class TestA { String fieldVar; String method(){} interface TestB {}}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("TestA").children().get(0).equals("TestA.fieldVar"));
        assertTrue(generatedSourceModel.getComponent("TestA").children().get(1).equals("TestA.method()"));
        assertTrue(generatedSourceModel.getComponent("TestA").children().get(2).equals("TestA.TestB"));
    }
}
