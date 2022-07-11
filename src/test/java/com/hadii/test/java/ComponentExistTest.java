package com.hadii.test.java;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Basic tests to ensure components are being recognized and parsed.
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
    private static String sampleJavaMethodComponentKeyName = "sampleJavaMethod(String)";

    private static String sampleJavaInterfaceMethodComponentName = "sampleJavaInterfaceMethod";
    private static String sampleJavaInterfaceMethodComponentKeyName = "sampleJavaInterfaceMethod(String)";

    private static String sampleJavaEnumClassConstructor = "sampleJavaEnumClass";
    private static String sampleJavaEnumClassConstructurKey = "sampleJavaEnumClass(String)";

    private static String codeString =
            "package " + sampleJavaPackageName + ";"
                    + "class " + sampleJavaClassComponentName  + " {"
                    + "  private String " + sampleJavaClassFieldComponentName + ";"
                    + "  private void " + sampleJavaMethodComponentName + " (final String " + sampleJavaMethodParamComponentName+ ") { "
                    + "      String cakes = testMethod(" + sampleJavaMethodParamComponentName +
                    ");"
                    + "  } "
                    + "  public void testMethod(String test) { } "
                    + "  interface " + sampleJavaInterfaceComponentName + " { "
                    + "      public void " + sampleJavaInterfaceMethodComponentName + "(String " + sampleJavaInterfaceMethodParamComponentName + " );"
                    + "  }"
                    + "  public enum " + sampleJavaEnumComponent + " { " + "  "
                    +      sampleJavaEnumClassConstant + "(\"\");" + "  "
                    +       sampleJavaEnumClassConstructor + "(final String " + sampleJavaEnumMethodParam + ") {}"
                    + "  }"
                    + " }";


    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static final void parseJavaSourceFile() throws Exception {
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result().model();
    }

    @Test
    public void noJavaFilesParsedTest() throws Exception {
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.components().count() == 0);
    }

    @Test
    public final void testSampleJavaEnumClassMethodParamComponent() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructurKey + "."
                        + sampleJavaEnumMethodParam));
    }

    @Test
    public final void testSampleJavaEnumClassConstructorComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructurKey));
    }

    @Test
    public final void testSampleJavaEnumClassConstantComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstant));
    }

    @Test
    public final void testSampleJavaEnumClassComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "."  + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent));
    }

    @Test
    public final void testSampleJavaInterfaceMethodParamComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentKeyName + "."
                        + sampleJavaInterfaceMethodParamComponentName));
    }

    @Test
    public final void testSampleJavaInterfaceMethodComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentKeyName));
    }

    @Test
    public final void testSampleJavaInterfaceComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName));
    }

    @Test
    public final void testSampleJavaClassMethodParamComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentKeyName + "." + sampleJavaMethodParamComponentName));
    }

    @Test
    public final void testSampleJavaClassMethodComponentExists() throws Exception {

        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentKeyName));
    }

    @Test
    public final void testSampleJavaClassFieldComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaClassFieldComponentName));
    }

    @Test
    public final void testSampleJavaClassComponentExists() throws Exception {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName));
    }
}
