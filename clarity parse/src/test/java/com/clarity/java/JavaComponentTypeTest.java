package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.ClarityUtil;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;

public class JavaComponentTypeTest {

    private static OOPSourceCodeModel generatedSourceModel;
    private static String sampleJavaClassComponentName;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaClassComponentType;
    private static String sampleJavaClassFieldComponentName;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaClassFieldComponentType;
    private static String sampleJavaMethodComponentName;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaMethodComponentType;
    private static String sampleJavaMethodParamComponentName;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaMethodParamComponentNameType;
    private static String sampleJavaInterfaceComponentName;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaInterfaceComponentType;
    private static String sampleJavaInterfaceMethodComponentName;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaInterfaceMethodComponentType;
    private static String sampleJavaInterfaceMethodParamComponentName;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaInterfaceMethodParamComponentType;
    private static String sampleJavaEnumComponent;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaEnumComponentType;
    private static String sampleJavaEnumClassConstant;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaEnumClassConstantType;
    private static String sampleJavaEnumClassConstructor;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaEnumClassConstructorType;
    private static String sampleJavaEnumMethodParam;
    private static OOPSourceModelConstants.JavaComponentTypes sampleJavaEnumMethodParamType;
    private static String sampleJavaPackageName;
    private static String codeString;

    static {
        sampleJavaClassComponentName = "SampleJavaClass";
        sampleJavaClassComponentType = OOPSourceModelConstants.JavaComponentTypes.CLASS_COMPONENT;
        sampleJavaClassFieldComponentName = "sampleJavaClassField";
        sampleJavaClassFieldComponentType = OOPSourceModelConstants.JavaComponentTypes.FIELD_COMPONENT;
        sampleJavaMethodComponentName = "sampleJavaMethod";
        sampleJavaMethodComponentType = OOPSourceModelConstants.JavaComponentTypes.METHOD_COMPONENT;
        sampleJavaMethodParamComponentName = "sampleJavaMethodParam";
        sampleJavaMethodParamComponentNameType = OOPSourceModelConstants.JavaComponentTypes.METHOD_PARAMETER_COMPONENT;
        sampleJavaInterfaceComponentName = "SampleJavaInterface";
        sampleJavaInterfaceComponentType = OOPSourceModelConstants.JavaComponentTypes.INTERFACE_COMPONENT;
        sampleJavaInterfaceMethodComponentName = "sampleJavaInterfaceMethod";
        sampleJavaInterfaceMethodComponentType = OOPSourceModelConstants.JavaComponentTypes.METHOD_COMPONENT;
        sampleJavaInterfaceMethodParamComponentName = "sampleJavaInterfaceMethodParamComponent";
        sampleJavaInterfaceMethodParamComponentType = OOPSourceModelConstants.JavaComponentTypes.METHOD_PARAMETER_COMPONENT;
        sampleJavaEnumComponent = "SampleJavaEnumClass";
        sampleJavaEnumComponentType = OOPSourceModelConstants.JavaComponentTypes.ENUM_COMPONENT;
        sampleJavaEnumClassConstant = "SampleJavaEnumClassConstant";
        sampleJavaEnumClassConstantType = OOPSourceModelConstants.JavaComponentTypes.ENUM_CONSTANT_COMPONENT;
        sampleJavaEnumClassConstructor = "sampleJavaEnumClass";
        sampleJavaEnumClassConstructorType = OOPSourceModelConstants.JavaComponentTypes.CONSTRUCTOR_COMPONENT;
        sampleJavaEnumMethodParam = "enumMethodParam";
        sampleJavaEnumMethodParamType = OOPSourceModelConstants.JavaComponentTypes.CONSTRUCTOR_PARAMETER_COMPONENT;
        sampleJavaPackageName = "SampleJavaPackage";
        codeString = "package " + sampleJavaPackageName + "; class " + sampleJavaClassComponentName + " {"
                + "  private String " + sampleJavaClassFieldComponentName + ";" + "  private void "
                + sampleJavaMethodComponentName + " (final String " + sampleJavaMethodParamComponentName + ") { } "
                + "  " + "interface " + sampleJavaInterfaceComponentName + " { " + "  public void "
                + sampleJavaInterfaceMethodComponentName + "(String " + sampleJavaInterfaceMethodParamComponentName
                + " );" + "  }" + "  public enum " + sampleJavaEnumComponent + " { " + "  "
                + sampleJavaEnumClassConstant + "(\"\");" + "  " + sampleJavaEnumClassConstructor + "(final String "
                + sampleJavaEnumMethodParam + ") {}" + "  }" + "  }";
    }

    @BeforeClass
    public static final void parseJavaSourceFile() throws Exception {
        final ParseRequestContent rawData = new ParseRequestContent();
        rawData.setLanguage("java");
        rawData.insertFile(new RawFile("file1", codeString));
        final ParseService parseService = new ParseService();
        generatedSourceModel = parseService.parseProject(rawData);
        System.out.println(ClarityUtil.fromJavaToJson(generatedSourceModel, true));
    }

    @Test
    public final void testSampleJavaClassMethodParamComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentName + "." + sampleJavaMethodParamComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaMethodParamComponentNameType)));
    }

    @Test
    public final void testSampleJavaClassMethodComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaMethodComponentType)));
    }

    @Test
    public final void testSampleJavaClassFieldComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaClassFieldComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaClassFieldComponentType)));
    }

    @Test
    public final void testSampleJavaClassComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaClassComponentType)));
    }

    @Test
    public final void testSampleJavaInterfaceMethodParamComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentName + "."
                        + sampleJavaInterfaceMethodParamComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(
                        sampleJavaInterfaceMethodParamComponentType)));
    }

    @Test
    public final void testSampleJavaInterfaceMethodComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaInterfaceMethodComponentType)));
    }

    @Test
    public final void testSampleJavaInterfaceComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaInterfaceComponentType)));
    }

    @Test
    public final void testSampleJavaEnumClassComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaEnumComponentType)));
    }

    @Test
    public final void testSampleJavaEnumClassConstantComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstant);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaEnumClassConstantType)));
    }

    @Test
    public final void testSampleJavaEnumClassMethodComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructor);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaEnumClassConstructorType)));
    }

    @Test
    public final void testSampleJavaEnumClassMethodParamComponentType() {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructor + "."
                        + sampleJavaEnumMethodParam);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaEnumMethodParamType)));
    }
}
