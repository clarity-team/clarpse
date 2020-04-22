package com.hadii.test.java;

import com.hadii.clarpse.ClarpseUtil;
import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.File;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.SourceFiles;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

/**
 * Tests to ensure component type attribute of parsed components are accurate.
 */
public class ComponentTypeTest {

    private static OOPSourceCodeModel generatedSourceModel;
    private static String sampleJavaClassComponentName;
    private static OOPSourceModelConstants.ComponentType sampleJavaClassComponentType;
    private static String sampleJavaClassFieldComponentName;
    private static OOPSourceModelConstants.ComponentType sampleJavaClassFieldComponentType;
    private static String sampleJavaMethodComponentName;
    private static String sampleJavaMethodComponentKeyName;
    private static OOPSourceModelConstants.ComponentType sampleJavaMethodComponentType;
    private static String sampleJavaConstructorComponentName;
    private static String sampleJavaConstructorComponentKeyName;
    private static OOPSourceModelConstants.ComponentType sampleJavaConstructorComponentType;
    private static String sampleJavaMethodParamComponentName;
    private static OOPSourceModelConstants.ComponentType sampleJavaMethodParamComponentNameType;
    private static String sampleJavaMethodParamComponent2Name;
    private static OOPSourceModelConstants.ComponentType sampleJavaMethodParamComponent2NameType;
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
        sampleJavaClassComponentType = OOPSourceModelConstants.ComponentType.CLASS;
        sampleJavaClassFieldComponentName = "sampleJavaClassField";
        sampleJavaClassFieldComponentType = OOPSourceModelConstants.ComponentType.FIELD;
        sampleJavaMethodComponentName = "sampleJavaMethod";
        sampleJavaMethodComponentKeyName = "sampleJavaMethod(String, Object)";
        sampleJavaMethodComponentType = OOPSourceModelConstants.ComponentType.METHOD;
        sampleJavaConstructorComponentName = "sampleJavaClass";
        sampleJavaConstructorComponentKeyName = "sampleJavaClass()";
        sampleJavaConstructorComponentType = OOPSourceModelConstants.ComponentType.CONSTRUCTOR;
        sampleJavaMethodParamComponentName = "sampleJavaMethodParam";
        sampleJavaMethodParamComponentNameType = OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT;
        sampleJavaMethodParamComponent2Name = "sampleJavaMethodParam2";
        sampleJavaMethodParamComponent2NameType = OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT;
        sampleJavaInterfaceComponentName = "SampleJavaInterface";
        sampleJavaInterfaceComponentType = OOPSourceModelConstants.ComponentType.INTERFACE;
        sampleJavaInterfaceMethodComponentName = "sampleJavaInterfaceMethod";
        sampleJavaInterfaceMethodComponentKeyName = "sampleJavaInterfaceMethod(String)";
        sampleJavaInterfaceMethodComponentType = OOPSourceModelConstants.ComponentType.METHOD;
        sampleJavaInterfaceMethodParamComponentName = "sampleJavaInterfaceMethodParamComponent";
        sampleJavaInterfaceMethodParamComponentType = OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT;
        sampleJavaEnumComponent = "SampleJavaEnumClass";
        sampleJavaEnumComponentType = OOPSourceModelConstants.ComponentType.ENUM;
        sampleJavaEnumClassConstant = "SampleJavaEnumClassConstant";
        sampleJavaEnumClassConstantType = OOPSourceModelConstants.ComponentType.ENUM_CONSTANT;
        sampleJavaEnumClassConstructor = "sampleJavaEnumClass";
        sampleJavaEnumClassConstructorKey = "sampleJavaEnumClass(String)";
        sampleJavaEnumClassConstructorType = OOPSourceModelConstants.ComponentType.CONSTRUCTOR;
        sampleJavaEnumMethodParam = "enumMethodParam";
        sampleJavaEnumMethodParamType = OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT;
        sampleJavaPackageName = "SampleJavaPackage";
        codeString = "package " + sampleJavaPackageName + "; " + "class " + sampleJavaClassComponentName + " {"
                + "  private String " + sampleJavaClassFieldComponentName + ";" + "  private String "
                + sampleJavaMethodComponentName + " (final String " + sampleJavaMethodParamComponentName + ", Object..."
                + sampleJavaMethodParamComponent2Name + ") { " + "  } " + "  public "
                + sampleJavaConstructorComponentName + " () { " + "} " + "  interface "
                + sampleJavaInterfaceComponentName + " { " + "  	public void "
                + sampleJavaInterfaceMethodComponentName + "(String " + sampleJavaInterfaceMethodParamComponentName
                + " );" + "  }" + "  public enum " + sampleJavaEnumComponent + " { " + sampleJavaEnumClassConstant
                + "(\"\");" + sampleJavaEnumClassConstructor + "(final String " + sampleJavaEnumMethodParam + ") {}"
                + "  }" + "}";
    }

    @BeforeClass
    public static final void parseJavaSourceFile() throws Exception {
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        System.out.println(ClarpseUtil.fromJavaToJson(generatedSourceModel));
    }

    @Test
    public final void testSampleJavaClassMethodParamComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentKeyName + "." + sampleJavaMethodParamComponentName);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaMethodParamComponentNameType.toString()));
    }

    @Test
    public final void testSampleJavaClassMethodParam2ComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaMethodComponentKeyName + "." + sampleJavaMethodParamComponent2Name);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaMethodParamComponent2NameType.toString()));
    }

    @Test
    public final void testSampleJavaClassConstructorComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "."
                + sampleJavaClassComponentName + "." + sampleJavaConstructorComponentKeyName);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaConstructorComponentType.toString()));
    }

    @Test
    public final void testSampleJavaClassMethodComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "."
                + sampleJavaClassComponentName + "." + sampleJavaMethodComponentKeyName);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaMethodComponentType.toString()));
    }

    @Test
    public final void testSampleJavaClassFieldComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "."
                + sampleJavaClassComponentName + "." + sampleJavaClassFieldComponentName);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaClassFieldComponentType.toString()));
    }

    @Test
    public final void testSampleJavaClassComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaClassComponentType.toString()));
    }

    @Test
    public final void testSampleJavaInterfaceMethodParamComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentKeyName + "."
                        + sampleJavaInterfaceMethodParamComponentName);
        Assert.assertTrue(tmp.get().componentType().toString().equals(

                sampleJavaInterfaceMethodParamComponentType.toString()));
    }

    @Test
    public final void testSampleJavaInterfaceMethodComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaInterfaceComponentName + "." + sampleJavaInterfaceMethodComponentKeyName);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaInterfaceMethodComponentType.toString()));
    }

    @Test
    public final void testSampleJavaInterfaceComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "."
                + sampleJavaClassComponentName + "." + sampleJavaInterfaceComponentName);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaInterfaceComponentType.toString()));
    }

    @Test
    public final void testSampleJavaEnumClassComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "."
                + sampleJavaClassComponentName + "." + sampleJavaEnumComponent);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaEnumComponentType.toString()));
    }

    @Test
    public final void testSampleJavaEnumClassConstantComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "."
                + sampleJavaClassComponentName + "." + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstant);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaEnumClassConstantType.toString()));
    }

    @Test
    public final void testSampleJavaEnumClassMethodComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructorKey);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaEnumClassConstructorType.toString()));
    }

    @Test
    public final void testSampleJavaEnumClassMethodParamComponentType() throws Exception {
        final Optional<Component> tmp = generatedSourceModel.getComponent(String.valueOf(sampleJavaPackageName) + "." + sampleJavaClassComponentName + "."
                        + sampleJavaEnumComponent + "." + sampleJavaEnumClassConstructorKey + "."
                        + sampleJavaEnumMethodParam);
        Assert.assertTrue(tmp.get().componentType().toString().equals(sampleJavaEnumMethodParamType.toString()));
    }
}
