package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Basic tests to ensure components are being recognized and parsed.
 *
 * @author Muntazir Fadhel
 */
public class ComponentExistTest {

    private static String sampleJavaClassComponentName = "SampleJavaClass";
    private static String sampleJavaClassFieldComponentName = "sampleJavaClassField";
    private static String sampleJavaMethodComponentName = "sampleJavaMethod";
    private static String sampleJavaMethodParamComponentName = "sampleJavaMethodParam";
    private static String sampleJavaInterfaceComponentName = "SampleJavaInterface";
    private static String sampleJavaInterfaceMethodComponentName = "sampleJavaInterfaceMethod";
    private static String sampleJavaInterfaceMethodParamComponentName = "sampleJavaInterfaceMethodParamComponent";
    private static String sampleJavaEnumComponent = "SampleJavaEnumClass";
    private static String sampleJavaEnumClassConstant = "SampleJavaEnumClassConstant";
    private static String sampleJavaEnumClassConstructor = "sampleJavaEnumClass";
    private static String sampleJavaEnumMethodParam = "enumMethodParam";
    private static String sampleJavaPackageName = "SampleJavaPackage";
    private static String codeString = "package " + sampleJavaPackageName + "; class " + sampleJavaClassComponentName
            + " {" + "  private String " + sampleJavaClassFieldComponentName + ";" + "  private void "
            + sampleJavaMethodComponentName + " (final String " + sampleJavaMethodParamComponentName + ") { } " + "  "
            + "interface " + sampleJavaInterfaceComponentName + " { " + "  public void "
            + sampleJavaInterfaceMethodComponentName + "(String " + sampleJavaInterfaceMethodParamComponentName + " );"
            + "  }" + "  public enum " + sampleJavaEnumComponent + " { " + "  " + sampleJavaEnumClassConstant
            + "(\"\");" + "  " + sampleJavaEnumClassConstructor + "(final String " + sampleJavaEnumMethodParam + ") {}"
            + "  }" + "  }";
    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static final void parseJavaSourceFile() throws Exception {
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ParseService parseService = new ParseService();
        generatedSourceModel = parseService.parseProject(rawData);
    }

    @Test
    public final void testSampleJavaEnumClassMethodParamComponent() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructor + "."
                        + sampleJavaEnumMethodParam));
    }

    @Test
    public final void testSampleJavaEnumClassConstructorComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructor));
    }

    @Test
    public final void testSampleJavaEnumClassConstantComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstant));
    }

    @Test
    public final void testSampleJavaEnumClassComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent));
    }

    @Test
    public final void testSampleJavaInterfaceMethodParamComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentName + "."
                        + sampleJavaInterfaceMethodParamComponentName));
    }

    @Test
    public final void testSampleJavaInterfaceMethodComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentName));
    }

    @Test
    public final void testSampleJavaInterfaceComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName));
    }

    @Test
    public final void testSampleJavaClassMethodParamComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentName + "." + sampleJavaMethodParamComponentName));
    }

    @Test
    public final void testSampleJavaClassMethodComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentName));
    }

    @Test
    public final void testSampleJavaClassFieldComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaClassFieldComponentName));
    }

    @Test
    public final void testSampleJavaClassComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName));
    }
}
