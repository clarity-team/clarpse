package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
public class ParseComponentTypeTest {

    private static OOPSourceCodeModel generatedSourceModel;
    private static String sampleJavaClassComponentName;
    private static OOPSourceModelConstants.ComponentType sampleJavaClassComponentType;
    private static String sampleJavaClassFieldComponentName;
    private static OOPSourceModelConstants.ComponentType sampleJavaClassFieldComponentType;
    private static String sampleJavaMethodComponentName;
    private static String sampleJavaMethodComponentKeyName;
    private static OOPSourceModelConstants.ComponentType sampleJavaMethodComponentType;
    private static String sampleJavaMethodParamComponentName;
    private static OOPSourceModelConstants.ComponentType sampleJavaMethodParamComponentNameType;
    private static String sampleJavaInterfaceComponentName;
    private static OOPSourceModelConstants.ComponentType sampleJavaInterfaceComponentType;
    private static String sampleJavaInterfaceMethodComponentName;
    private static String sampleJavaInterfaceMethodComponentKeyName;
    private static OOPSourceModelConstants.ComponentType sampleJavaInterfaceMethodComponentType;
    private static String sampleJavaInterfaceMethodParamComponentName;
    private static OOPSourceModelConstants.ComponentType sampleJavaInterfaceMethodParamComponentType;
    private static String sampleJavaEnumComponent;
    private static OOPSourceModelConstants.ComponentType sampleJavaEnumComponentType;
    private static String sampleJavaEnumClassConstant;
    private static OOPSourceModelConstants.ComponentType sampleJavaEnumClassConstantType;
    private static String sampleJavaEnumClassConstructor;
    private static String sampleJavaEnumClassConstructorKey;
    private static OOPSourceModelConstants.ComponentType sampleJavaEnumClassConstructorType;
    private static String sampleJavaEnumMethodParam;
    private static OOPSourceModelConstants.ComponentType sampleJavaEnumMethodParamType;
    private static String sampleJavaPackageName;
    private static String codeString;

    static {
        sampleJavaClassComponentName = "SampleJavaClass";
        sampleJavaClassComponentType = OOPSourceModelConstants.ComponentType.CLASS_COMPONENT;
        sampleJavaClassFieldComponentName = "sampleJavaClassField";
        sampleJavaClassFieldComponentType = OOPSourceModelConstants.ComponentType.FIELD_COMPONENT;
        sampleJavaMethodComponentName = "sampleJavaMethod";
        sampleJavaMethodComponentKeyName = "java.lang.String_sampleJavaMethod(java.lang.String)";
        sampleJavaMethodComponentType = OOPSourceModelConstants.ComponentType.METHOD_COMPONENT;
        sampleJavaMethodParamComponentName = "sampleJavaMethodParam";
        sampleJavaMethodParamComponentNameType = OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT;
        sampleJavaInterfaceComponentName = "SampleJavaInterface";
        sampleJavaInterfaceComponentType = OOPSourceModelConstants.ComponentType.INTERFACE_COMPONENT;
        sampleJavaInterfaceMethodComponentName = "sampleJavaInterfaceMethod";
        sampleJavaInterfaceMethodComponentKeyName = "void_sampleJavaInterfaceMethod(java.lang.String)";
        sampleJavaInterfaceMethodComponentType = OOPSourceModelConstants.ComponentType.METHOD_COMPONENT;
        sampleJavaInterfaceMethodParamComponentName = "sampleJavaInterfaceMethodParamComponent";
        sampleJavaInterfaceMethodParamComponentType = OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT;
        sampleJavaEnumComponent = "SampleJavaEnumClass";
        sampleJavaEnumComponentType = OOPSourceModelConstants.ComponentType.ENUM_COMPONENT;
        sampleJavaEnumClassConstant = "SampleJavaEnumClassConstant";
        sampleJavaEnumClassConstantType = OOPSourceModelConstants.ComponentType.ENUM_CONSTANT_COMPONENT;
        sampleJavaEnumClassConstructor = "sampleJavaEnumClass";
        sampleJavaEnumClassConstructorKey = "void_sampleJavaEnumClass(java.lang.String)";
        sampleJavaEnumClassConstructorType = OOPSourceModelConstants.ComponentType.CONSTRUCTOR_COMPONENT;
        sampleJavaEnumMethodParam = "enumMethodParam";
        sampleJavaEnumMethodParamType = OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT;
        sampleJavaPackageName = "SampleJavaPackage";
        codeString = "package "
                + sampleJavaPackageName + "; "
                + "class " + sampleJavaClassComponentName + " {"
                + "  private String " + sampleJavaClassFieldComponentName + ";"
                + "  private String "
                + sampleJavaMethodComponentName + " (final String " + sampleJavaMethodParamComponentName + ") { "
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
    }

    @Test
    public final void testSampleJavaClassMethodParamComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentKeyName + "." + sampleJavaMethodParamComponentName);
        Assert.assertTrue(tmp.getComponentType().toString().equals(sampleJavaMethodParamComponentNameType.toString()));
    }

    @Test
    public final void testSampleJavaClassMethodComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentKeyName);
        Assert.assertTrue(tmp.getComponentType().toString()
                .equals(
                        sampleJavaMethodComponentType.toString()));
    }

    @Test
    public final void testSampleJavaClassFieldComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaClassFieldComponentName);
        Assert.assertTrue(tmp.getComponentType().toString()
                .equals(
                        sampleJavaClassFieldComponentType.toString()));
    }

    @Test
    public final void testSampleJavaClassComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName);
        Assert.assertTrue(tmp.getComponentType().toString().equals(sampleJavaClassComponentType.toString()));
    }

    @Test
    public final void testSampleJavaInterfaceMethodParamComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentKeyName + "."
                        + sampleJavaInterfaceMethodParamComponentName);
        Assert.assertTrue(tmp.getComponentType().toString().equals(

                sampleJavaInterfaceMethodParamComponentType.toString()));
    }

    @Test
    public final void testSampleJavaInterfaceMethodComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentKeyName);
        Assert.assertTrue(tmp.getComponentType().toString().equals(
                sampleJavaInterfaceMethodComponentType.toString()));
    }

    @Test
    public final void testSampleJavaInterfaceComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName);
        Assert.assertTrue(tmp.getComponentType().toString().equals(sampleJavaInterfaceComponentType.toString()));
    }

    @Test
    public final void testSampleJavaEnumClassComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent);
        Assert.assertTrue(tmp.getComponentType().toString().equals(sampleJavaEnumComponentType.toString()));
    }

    @Test
    public final void testSampleJavaEnumClassConstantComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstant);
        Assert.assertTrue(tmp.getComponentType().toString().equals(sampleJavaEnumClassConstantType.toString()));
    }

    @Test
    public final void testSampleJavaEnumClassMethodComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructorKey);
        Assert.assertTrue(tmp.getComponentType().toString().equals(sampleJavaEnumClassConstructorType.toString()));
    }

    @Test
    public final void testSampleJavaEnumClassMethodParamComponentType() throws Exception {
        final Component tmp = generatedSourceModel.getComponents().get(
                String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructorKey + "."
                        + sampleJavaEnumMethodParam);
        Assert.assertTrue(tmp.getComponentType().toString()
                .equals(
                        sampleJavaEnumMethodParamType.toString()));
    }
}
