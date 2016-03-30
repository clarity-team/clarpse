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
import com.clarity.sourcemodel.OOPSourceModelConstants;

/**
 * Tests to ensure component type attribute of parsed components are accurate.
 *
 * @author Muntazir Fadhel
 */
public class JavaComponentTypeTest {

    private static OOPSourceCodeModel generatedSourceModel;
    private static String sampleJavaClassComponentName;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaClassComponentType;
    private static String sampleJavaClassFieldComponentName;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaClassFieldComponentType;
    private static String sampleJavaMethodComponentName;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaMethodComponentType;
    private static String sampleJavaMethodParamComponentName;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaMethodParamComponentNameType;
    private static String sampleJavaInterfaceComponentName;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaInterfaceComponentType;
    private static String sampleJavaInterfaceMethodComponentName;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaInterfaceMethodComponentType;
    private static String sampleJavaInterfaceMethodParamComponentName;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaInterfaceMethodParamComponentType;
    private static String sampleJavaEnumComponent;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaEnumComponentType;
    private static String sampleJavaEnumClassConstant;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaEnumClassConstantType;
    private static String sampleJavaEnumClassConstructor;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaEnumClassConstructorType;
    private static String sampleJavaEnumMethodParam;
    private static OOPSourceModelConstants.ComponentTypes sampleJavaEnumMethodParamType;
    private static String sampleJavaPackageName;
    private static String codeString;

    static {
        sampleJavaClassComponentName = "SampleJavaClass";
        sampleJavaClassComponentType = OOPSourceModelConstants.ComponentTypes.CLASS_COMPONENT;
        sampleJavaClassFieldComponentName = "sampleJavaClassField";
        sampleJavaClassFieldComponentType = OOPSourceModelConstants.ComponentTypes.FIELD_COMPONENT;
        sampleJavaMethodComponentName = "sampleJavaMethod";
        sampleJavaMethodComponentType = OOPSourceModelConstants.ComponentTypes.METHOD_COMPONENT;
        sampleJavaMethodParamComponentName = "sampleJavaMethodParam";
        sampleJavaMethodParamComponentNameType = OOPSourceModelConstants.ComponentTypes.METHOD_PARAMETER_COMPONENT;
        sampleJavaInterfaceComponentName = "SampleJavaInterface";
        sampleJavaInterfaceComponentType = OOPSourceModelConstants.ComponentTypes.INTERFACE_COMPONENT;
        sampleJavaInterfaceMethodComponentName = "sampleJavaInterfaceMethod";
        sampleJavaInterfaceMethodComponentType = OOPSourceModelConstants.ComponentTypes.METHOD_COMPONENT;
        sampleJavaInterfaceMethodParamComponentName = "sampleJavaInterfaceMethodParamComponent";
        sampleJavaInterfaceMethodParamComponentType = OOPSourceModelConstants.ComponentTypes.METHOD_PARAMETER_COMPONENT;
        sampleJavaEnumComponent = "SampleJavaEnumClass";
        sampleJavaEnumComponentType = OOPSourceModelConstants.ComponentTypes.ENUM_COMPONENT;
        sampleJavaEnumClassConstant = "SampleJavaEnumClassConstant";
        sampleJavaEnumClassConstantType = OOPSourceModelConstants.ComponentTypes.ENUM_CONSTANT_COMPONENT;
        sampleJavaEnumClassConstructor = "sampleJavaEnumClass";
        sampleJavaEnumClassConstructorType = OOPSourceModelConstants.ComponentTypes.CONSTRUCTOR_COMPONENT;
        sampleJavaEnumMethodParam = "enumMethodParam";
        sampleJavaEnumMethodParamType = OOPSourceModelConstants.ComponentTypes.CONSTRUCTOR_PARAMETER_COMPONENT;
        sampleJavaPackageName = "SampleJavaPackage";
        codeString = "package "
        + sampleJavaPackageName + "; "
                + "class " + sampleJavaClassComponentName + " {"
                + "  private String " + sampleJavaClassFieldComponentName + ";"
                + "  private void "  + sampleJavaMethodComponentName + " (final String " + sampleJavaMethodParamComponentName + ") { "
                     + "} "
           + "  " + "interface " + sampleJavaInterfaceComponentName + " { "
                        + "  public void "
                        + sampleJavaInterfaceMethodComponentName + "(String " + sampleJavaInterfaceMethodParamComponentName   + " );"
                        + "  }"
                + "  public enum " + sampleJavaEnumComponent + " { " + "  "
                +        sampleJavaEnumClassConstant + "(\"\");" + "  "
                        + "" + sampleJavaEnumClassConstructor + "(final String "  + sampleJavaEnumMethodParam + ") {}"
                + "  }"
        + "  }";
    }

    @BeforeClass
    public static final void parseJavaSourceFile() throws Exception {
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ParseService parseService = new ParseService();
        generatedSourceModel = parseService.parseProject(rawData);
        System.out.println(ClarpseUtil.fromJavaToJson(generatedSourceModel, true));
    }

    @Test
    public final void testSampleJavaClassMethodParamComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentName + "." + sampleJavaMethodParamComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaMethodParamComponentNameType)));
    }

    @Test
    public final void testSampleJavaClassMethodComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaMethodComponentType)));
    }

    @Test
    public final void testSampleJavaClassFieldComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaClassFieldComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaClassFieldComponentType)));
    }

    @Test
    public final void testSampleJavaClassComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaClassComponentType)));
    }

    @Test
    public final void testSampleJavaInterfaceMethodParamComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentName + "."
                        + sampleJavaInterfaceMethodParamComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(
                        sampleJavaInterfaceMethodParamComponentType)));
    }

    @Test
    public final void testSampleJavaInterfaceMethodComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaInterfaceMethodComponentType)));
    }

    @Test
    public final void testSampleJavaInterfaceComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaInterfaceComponentType)));
    }

    @Test
    public final void testSampleJavaEnumClassComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaEnumComponentType)));
    }

    @Test
    public final void testSampleJavaEnumClassConstantComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstant);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaEnumClassConstantType)));
    }

    @Test
    public final void testSampleJavaEnumClassMethodComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructor);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaEnumClassConstructorType)));
    }

    @Test
    public final void testSampleJavaEnumClassMethodParamComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructor + "."
                        + sampleJavaEnumMethodParam);
        Assert.assertTrue(tmp.getComponentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(sampleJavaEnumMethodParamType)));
    }
}
