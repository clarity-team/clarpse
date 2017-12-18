package com.clarity.test.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.ClarpseUtil;
import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.compiler.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Basic tests to ensure components are being recognized and parsed.
 *
 * @author Muntazir Fadhel
 */
public class ComponentExistTest {

    private static String sampleJavaClassComponentName = "SampleJavaClass";
    private static String sampleJavaClassFieldComponentName = "sampleJavaClassField";
    private static String sampleJavaInterfaceMethodParamComponentName = "sampleJavaInterfaceMethodParamComponent";
    private static String sampleJavaEnumComponent = "SampleJavaEnumClass";
    private static String sampleJavaEnumClassConstant = "SampleJavaEnumClassConstant";
    private static String sampleJavaEnumMethodParam = "enumMethodParam";
    private static String sampleJavaPackageName = "SampleJavaPackage";
    private static String sampleJavaMethodParamComponentName = "sampleJavaMethodParam";
    private static String sampleJavaInterfaceComponentName = "SampleJavaInterface";

    private static String sampleJavaMethodComponentName = "sampleJavaMethod";
    private static String sampleJavaMethodComponentKeyName = "sampleJavaMethod(java.lang.String)";

    private static String sampleJavaInterfaceMethodComponentName = "sampleJavaInterfaceMethod";
    private static String sampleJavaInterfaceMethodComponentKeyName = "sampleJavaInterfaceMethod(java.lang.String)";

    private static String sampleJavaEnumClassConstructor = "sampleJavaEnumClass";
    private static String sampleJavaEnumClassConstructurKey = "sampleJavaEnumClass(java.lang.String)";

    private static String codeString = "package " + sampleJavaPackageName + ";"
            + "                              class " + sampleJavaClassComponentName  + " {"
            + "                                 private String " + sampleJavaClassFieldComponentName + ";"
            + "                                 private void " + sampleJavaMethodComponentName + " (final String " + sampleJavaMethodParamComponentName+ ") { "
            + "                                     String cakes = localVar(" + sampleJavaMethodParamComponentName + ");"
            + "                                 } "
            + "                                 public void testMethod(String test) { } "
            + "                                 interface " + sampleJavaInterfaceComponentName + " { "
            + "                                     public void " + sampleJavaInterfaceMethodComponentName + "(String " + sampleJavaInterfaceMethodParamComponentName + " );"
            + "                                 }"
            + "                                 public enum " + sampleJavaEnumComponent + " { " + "  "
            +                                       sampleJavaEnumClassConstant + "(\"\");" + "  "
            +                                       sampleJavaEnumClassConstructor + "(final String " + sampleJavaEnumMethodParam + ") {}"
            + "                                 }"
            + "                              }";

    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static final void parseJavaSourceFile() throws Exception {
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        System.out.println(ClarpseUtil.fromJavaToJson(generatedSourceModel));
    }

    @Test
    public final void testSampleJavaEnumClassMethodParamComponent() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructurKey + "."
                        + sampleJavaEnumMethodParam));
    }

    @Test
    public final void testSampleJavaEnumClassConstructorComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructurKey));
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
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentKeyName + "."
                        + sampleJavaInterfaceMethodParamComponentName));
    }

    @Test
    public final void testSampleJavaInterfaceMethodComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentKeyName));
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
                        + sampleJavaMethodComponentKeyName + "." + sampleJavaMethodParamComponentName));
    }

    @Test
    public final void testSampleJavaClassMethodComponentExists() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponents().containsKey(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentKeyName));
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
