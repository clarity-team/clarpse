package com.clarity.test.java;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Ensure component invocations for a given component are inherited by its
 * parents.
 */
public class InvocationInheritanceTest {

    @Test
    public void testClassInheritsFieldInvocations() throws Exception {
        final String code = "class Test { String fieldVar; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("Test").invocations().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassInheritsMethodInvocations() throws Exception {
        final String code = "class Test { public String aMethod(){} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("Test").invocations().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassInheritsLocalVarsInvocations() throws Exception {
        final String code = "class Test { public void fieldVar(){String test;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("Test").invocations().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassInheritsMethodParamsInvocations() throws Exception {
        final String code = "class Test { public void fieldVar(String test){} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("Test").invocations().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassInheritsNestedClassInvocations() throws Exception {
        final String code = "class Test { class NestedClass { public void fieldVar(String test){} } }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("Test").invocations().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testClassDoesNotInheritExtendsAndImplementsInvocations() throws Exception {
        final String code = "class Test { class NestedClass extends String implements Integer {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test").invocations().isEmpty());
    }

    @Test
    public void testInterfaceInheritsFieldInvocations() throws Exception {
        final String code = "interface Test { String localVar; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("Test").invocations().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testInterfaceInheritsMethodInvocations() throws Exception {
        final String code = "interface Test { abstract String aMethod(); }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("Test").invocations().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testInterfaceInheritsMethodParamsInvocations() throws Exception {
        final String code = "interface Test { abstract void aMethod(String test); }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("Test").invocations().toArray()[0])
                .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testMethodInheritsLocalVarsInvocations() throws Exception {
        final String code = "class Test { public void aMethod(){String test;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(
                ((ComponentInvocation) generatedSourceModel.getComponent("Test.aMethod()").invocations().toArray()[0])
                        .invokedComponent().equals("java.lang.String"));
    }

    @Test
    public void testMethodInheritsMethodParamsInvocations() throws Exception {
        final String code = "class Test { public void aMethod(String test){} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("Test.aMethod(java.lang.String)")
                .invocations().toArray()[0]).invokedComponent().equals("java.lang.String"));
    }
}
