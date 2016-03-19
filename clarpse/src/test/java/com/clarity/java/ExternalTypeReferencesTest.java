package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.ClarpseUtil;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Tests to ensure external class type references of parsed components are
 * accurate.
 *
 * @author Muntazir Fadhel
 */
public class ExternalTypeReferencesTest {

    private static String sampleJavaClassComponentName = "SampleJavaClass";
    private static String sampleJavaClassComponentTypeRefsJSON = "[{ \"type\" : \"java.lang.String\",\"lines\" : [ 2, 3, 5 ]}]";
    private static String sampleJavaClassFieldComponentName = "sampleJavaClassField";
    private static String sampleJavaClassFieldComponentRefsJSON = "[ {  \"type\" : \"java.lang.String\",  \"lines\" : [ 2 ]  } ]";
    private static String sampleJavaMethodComponentName = "sampleJavaMethod";
    private static String sampleJavaMethodComponentRefsJSON = "[ { \"type\" : \"java.lang.String\",  \"lines\" : [ 3 ]  } ]";
    private static String sampleJavaMethodParamComponentName = "sampleJavaMethodParam";
    private static String sampleJavaMethodParamComponentRefsJSON = "[ {\"type\" : \"java.lang.String\",\"lines\" : [ 3 ] } ]";
    private static String sampleJavaInterfaceComponentName = "SampleJavaInterface";
    private static String sampleJavaInterfaceComponentRefsJSON = "[ {\"type\" : \"java.lang.String\", \"lines\" : [ 5 ] } ]";
    private static String sampleJavaInterfaceMethodComponentName = "sampleJavaInterfaceMethod";
    private static String sampleJavaInterfaceMethodComponentRefsJSON = "[ {\"type\" : \"java.lang.String\",  \"lines\" : [ 5 ] } ]";
    private static String sampleJavaInterfaceMethodParamComponentName = "sampleJavaInterfaceMethodParamComponent";
    private static String sampleJavaInterfaceMethodParamComponentRefsJSON = "[ { \"type\" : \"java.lang.String\", \"lines\" : [ 5 ]} ]";
    private static String sampleJavaPackageName = "SampleJavaPackage";
    private static String codeString = "package " + sampleJavaPackageName + "; class " + sampleJavaClassComponentName
            + " { \n" + "  private String " + sampleJavaClassFieldComponentName + "; \n" + "  private void "
            + sampleJavaMethodComponentName + " (final String " + sampleJavaMethodParamComponentName + ") { } \n"
            + "  " + "interface " + sampleJavaInterfaceComponentName + " { \n " + "  public void "
            + sampleJavaInterfaceMethodComponentName + "(String " + sampleJavaInterfaceMethodParamComponentName
            + " ); \n" + "  } }";
    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static final void parseJavaSourceFile() throws Exception {
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ParseService parseService = new ParseService();
        generatedSourceModel = parseService.parseProject(rawData);
        System.out.println(ClarpseUtil.fromJavaToJson(generatedSourceModel, true));
    }

    @Test
    public final void testSampleJavaInterfaceMethodParamComponentExternalTypeRefs() throws Exception {
        final Component temp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentName + "."
                        + sampleJavaInterfaceMethodParamComponentName);
        Assert.assertEquals(ClarpseUtil.fromJavaToJson(temp.getExternalClassTypeReferences()),
                sampleJavaInterfaceMethodParamComponentRefsJSON.replace(" ", ""));
    }

    @Test
    public final void testSampleJavaInterfaceMethodComponentExternalTypeRefs() throws Exception {
        final Component temp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentName);
        Assert.assertEquals(ClarpseUtil.fromJavaToJson(temp.getExternalClassTypeReferences()),
                sampleJavaInterfaceMethodComponentRefsJSON.replace(" ", ""));
    }

    @Test
    public final void testSampleJavaInterfaceComponentExternalTypeRefs() throws Exception {
        final Component temp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName);
        Assert.assertEquals(ClarpseUtil.fromJavaToJson(temp.getExternalClassTypeReferences()),
                sampleJavaInterfaceComponentRefsJSON.replace(" ", ""));
    }

    @Test
    public final void testSampleJavaMethodParamComponentExternalTypeRefs() throws Exception {
        final Component temp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentName + "." + sampleJavaMethodParamComponentName);
        Assert.assertEquals(ClarpseUtil.fromJavaToJson(temp.getExternalClassTypeReferences()),
                sampleJavaMethodParamComponentRefsJSON.replace(" ", ""));
    }

    @Test
    public final void testSampleJavaMethodComponentExternalTypeRefs() throws Exception {
        final Component temp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentName);
        Assert.assertEquals(ClarpseUtil.fromJavaToJson(temp.getExternalClassTypeReferences()),
                sampleJavaMethodComponentRefsJSON.replace(" ", ""));
    }

    @Test
    public final void testSampleJavaClassFieldComponentExternalTypeRefs() throws Exception {
        final Component temp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaClassFieldComponentName);
        Assert.assertEquals(ClarpseUtil.fromJavaToJson(temp.getExternalClassTypeReferences()),
                sampleJavaClassFieldComponentRefsJSON.replace(" ", ""));
    }

    @Test
    public final void testSampleJavaClassComponentExternalTypeRefs() throws Exception {
        final Component temp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName);
        Assert.assertEquals(ClarpseUtil.fromJavaToJson(temp.getExternalClassTypeReferences()),
                sampleJavaClassComponentTypeRefsJSON.replace(" ", ""));
    }
}
