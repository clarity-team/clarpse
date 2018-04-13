package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.invocation.AnnotationInvocation;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AnnotationInvocationTest {

    @Test
    public void testClassAnnotationRegisteredInvokedComponent() throws Exception {

        final String code = "@Deprecated public class test {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("test").get().componentInvocations(
                ComponentInvocations.ANNOTATION).get(0));
        assertTrue(invocation.invokedComponent().equals("Deprecated"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getKey().equals("Deprecated"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getValue().isEmpty());
    }

    @Test
    public void testClassSingleElementAnnotationRegisteredInvokedComponent() throws Exception {

        final String code = "@Deprecated(\"lolcakes\") public class test {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("test").get().componentInvocations(
                ComponentInvocations.ANNOTATION).get(0));
        assertTrue(invocation.invokedComponent().equals("Deprecated"));
        System.out.println(((AnnotationInvocation) invocation).annotations().get(0).getValue().get("")
                .equals("\"lolcakes\""));
    }

    @Test
    public void testInterfaceAnnotationRegisteredInvokedComponent() throws Exception {

        final String code = "@Deprecated public interface test {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("test").get().componentInvocations(
                ComponentInvocations.ANNOTATION).get(0));
        assertTrue(invocation.invokedComponent().equals("Deprecated"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getKey().equals("Deprecated"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getValue().isEmpty());
    }

    @Test
    public void testEnumAnnotationRegisteredInvokedComponent() throws Exception {

        final String code = "@Deprecated public enum test {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("test").get().componentInvocations(
                ComponentInvocations.ANNOTATION).get(0));
        assertTrue(invocation.invokedComponent().equals("Deprecated"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getKey().equals("Deprecated"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getValue().isEmpty());
    }

    @Test
    public void testFieldVarAnnotationRegisteredInvokedComponent() throws Exception {

        final String code = "import org.annotation.Autowired; public class Test { @Autowired static final String fieldVar; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("Test.fieldVar")
                .get().componentInvocations(ComponentInvocations.ANNOTATION).get(0));
        assertTrue(invocation.invokedComponent().equals("org.annotation.Autowired"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getKey().equals("org.annotation.Autowired"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getValue().isEmpty());
    }

    @Test
    public void testMethodParamAnnotationRegisteredInvokedComponent() throws Exception {

        final String code = "import org.annotation.Autowired; public class test { void aMethod(@Override String var){}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("test.aMethod(String).var")
                .get().componentInvocations(ComponentInvocations.ANNOTATION).get(0));
        assertTrue(invocation.invokedComponent().equals("Override"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getKey().equals("Override"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getValue().isEmpty());
    }

    @Test
    public void testInterfaceConstantAnnotationRegisteredInvokedComponent() throws Exception {

        final String code = "import org.annotation.Autowired; public class test { @Autowired String fieldVar; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("test.fieldVar")
                .get().componentInvocations(
                        ComponentInvocations.ANNOTATION).get(0));
        assertTrue(invocation.invokedComponent().equals("org.annotation.Autowired"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getKey().equals("org.annotation.Autowired"));
        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getValue().isEmpty());
    }

    @Test
    public void testMethodAnnotationRegisteredInvokedComponent() throws Exception {

        final String code = "import org.springframework.RequestMapping;"
                + "import org.springframework.RequestMethod;"
                + " class test { "
                + "     @RequestMapping(value = \"value1\", method = RequestMethod.GET) method(){}"
                + " }";

        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("test.method()")
                .get().componentInvocations(
                        ComponentInvocations.ANNOTATION).get(0));
        assertTrue(invocation.invokedComponent().equals("org.springframework.RequestMapping"));
    }

    @Test
    public void testMethodAnnotationRegisteredValueList() throws Exception {

        final String code = "import org.springframework.RequestMapping;"
                + "import org.springframework.RequestMethod;"
                + " public class test { "
                + "     @RequestMapping(value = \"value1\", method = RequestMethod.GET) void method(){}"
                + " }";

        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file2", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        final ComponentInvocation invocation = (generatedSourceModel.getComponent("test.method()")
                .get().componentInvocations(
                        ComponentInvocations.ANNOTATION).get(0));

        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getValue().get("value")
                .equals("\"value1\""));

        assertTrue(((AnnotationInvocation) invocation).annotations().get(0).getValue().get("method")
                .equals("RequestMethod.GET"));
    }

}
