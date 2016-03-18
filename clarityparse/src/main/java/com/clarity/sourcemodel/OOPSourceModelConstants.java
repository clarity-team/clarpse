package com.clarity.sourcemodel;

import java.util.HashMap;
import java.util.Map;

/**
 * Constants related to source code model entities.
 * @author Muntazir Fadhel
 *
 */
public final class OOPSourceModelConstants {

    /**
     * private constructor.
     */
    private OOPSourceModelConstants() { };

    static final Map<String, String> JAVA_COLLECTIONS
    =
    new HashMap<String, String>();

    /**
     * @return the javaCollections
     */
    public static
    Map<String, String> getJavaCollections() {
        return JAVA_COLLECTIONS;
    }
    static {
        JAVA_COLLECTIONS.put("java.util.ArrayList", "java.util.ArrayList");
    }

    public static final String JAVA_DEFAULT_PKG = "java.lang.";

    static final Map<String, String> JAVA_DEFAULT_CLASSES
    =
    new HashMap<String, String>();
    static {
        JAVA_DEFAULT_CLASSES.put("Appendable", JAVA_DEFAULT_PKG + "Appendable");
        JAVA_DEFAULT_CLASSES.put("AutoCloseable", JAVA_DEFAULT_PKG + "AutoCloseable");
        JAVA_DEFAULT_CLASSES.put("CharSequence", JAVA_DEFAULT_PKG + "CharSequence");
        JAVA_DEFAULT_CLASSES.put("Cloneable", JAVA_DEFAULT_PKG + "Cloneable");
        JAVA_DEFAULT_CLASSES.put("Comparable", JAVA_DEFAULT_PKG + "Comparable");
        JAVA_DEFAULT_CLASSES.put("Iterable", JAVA_DEFAULT_PKG + "Iterable");
        JAVA_DEFAULT_CLASSES.put("Readable", JAVA_DEFAULT_PKG + "Readable");
        JAVA_DEFAULT_CLASSES.put("ArrayList", JAVA_DEFAULT_PKG + "ArrayList");
        JAVA_DEFAULT_CLASSES.put("Runnable", JAVA_DEFAULT_PKG + "Runnable");
        JAVA_DEFAULT_CLASSES.put("UncaughtExceptionHandler", JAVA_DEFAULT_PKG + "UncaughtExceptionHandler");
        JAVA_DEFAULT_CLASSES.put("Boolean", JAVA_DEFAULT_PKG + "Boolean");
        JAVA_DEFAULT_CLASSES.put("Byte", JAVA_DEFAULT_PKG + "Byte");
        JAVA_DEFAULT_CLASSES.put("Character", JAVA_DEFAULT_PKG + "Character");
        JAVA_DEFAULT_CLASSES.put("Subset", JAVA_DEFAULT_PKG + "Character.Subset");
        JAVA_DEFAULT_CLASSES.put("UnicodeBlock", JAVA_DEFAULT_PKG + "Character.UnicodeBlock");
        JAVA_DEFAULT_CLASSES.put("Class", JAVA_DEFAULT_PKG + "Class");
        JAVA_DEFAULT_CLASSES.put("ClassLoader", JAVA_DEFAULT_PKG + "ClassLoader");
        JAVA_DEFAULT_CLASSES.put("ClassValue", JAVA_DEFAULT_PKG + "ClassValue");
        JAVA_DEFAULT_CLASSES.put("Compiler", JAVA_DEFAULT_PKG + "Compiler");
        JAVA_DEFAULT_CLASSES.put("Double", JAVA_DEFAULT_PKG + "Double");
        JAVA_DEFAULT_CLASSES.put("Enum", JAVA_DEFAULT_PKG + "Enum");
        JAVA_DEFAULT_CLASSES.put("Float", JAVA_DEFAULT_PKG + "Float");
        JAVA_DEFAULT_CLASSES.put("InheritableThreadLocal", JAVA_DEFAULT_PKG + "InheritableThreadLocal");
        JAVA_DEFAULT_CLASSES.put("Integer", JAVA_DEFAULT_PKG + "Integer");
        JAVA_DEFAULT_CLASSES.put("Long", JAVA_DEFAULT_PKG + "Long");
        JAVA_DEFAULT_CLASSES.put("Math", JAVA_DEFAULT_PKG + "Math");
        JAVA_DEFAULT_CLASSES.put("Number", JAVA_DEFAULT_PKG + "Number");
        JAVA_DEFAULT_CLASSES.put("Object", JAVA_DEFAULT_PKG + "Object");
        JAVA_DEFAULT_CLASSES.put("Package", JAVA_DEFAULT_PKG + "Package");
        JAVA_DEFAULT_CLASSES.put("Process", JAVA_DEFAULT_PKG + "Process");
        JAVA_DEFAULT_CLASSES.put("ProcessBuilder", JAVA_DEFAULT_PKG + "ProcessBuilder");
        JAVA_DEFAULT_CLASSES.put("Redirect", JAVA_DEFAULT_PKG + "ProcessBuilder.Redirect");
        JAVA_DEFAULT_CLASSES.put("Runtime", JAVA_DEFAULT_PKG + "Runtime");
        JAVA_DEFAULT_CLASSES.put("RuntimePermission", JAVA_DEFAULT_PKG + "RuntimePermission");
        JAVA_DEFAULT_CLASSES.put("SecurityManager", JAVA_DEFAULT_PKG + "SecurityManager");
        JAVA_DEFAULT_CLASSES.put("Short", JAVA_DEFAULT_PKG + "Short");
        JAVA_DEFAULT_CLASSES.put("StackTraceElement", JAVA_DEFAULT_PKG + "StackTraceElement");
        JAVA_DEFAULT_CLASSES.put("StrictMath", JAVA_DEFAULT_PKG + "StrictMath");
        JAVA_DEFAULT_CLASSES.put("String", JAVA_DEFAULT_PKG + "String");
        JAVA_DEFAULT_CLASSES.put("StringBuffer", JAVA_DEFAULT_PKG + "StringBuffer");
        JAVA_DEFAULT_CLASSES.put("StringBuilder", JAVA_DEFAULT_PKG + "StringBuilder");
        JAVA_DEFAULT_CLASSES.put("System", JAVA_DEFAULT_PKG + "System");
        JAVA_DEFAULT_CLASSES.put("Thread", JAVA_DEFAULT_PKG + "Thread");
        JAVA_DEFAULT_CLASSES.put("ThreadGroup", JAVA_DEFAULT_PKG + "ThreadGroup");
        JAVA_DEFAULT_CLASSES.put("ThreadLocal", JAVA_DEFAULT_PKG + "ThreadLocal");
        JAVA_DEFAULT_CLASSES.put("Throwable", JAVA_DEFAULT_PKG + "Throwable");
        JAVA_DEFAULT_CLASSES.put("UnicodeScript", JAVA_DEFAULT_PKG + "Character.UnicodeScript");
        JAVA_DEFAULT_CLASSES.put("Type", JAVA_DEFAULT_PKG + "ProcessBuilder.Redirect.Type");
        JAVA_DEFAULT_CLASSES.put("State", JAVA_DEFAULT_PKG + "Thread.State");
        JAVA_DEFAULT_CLASSES.put("ArithmeticException", JAVA_DEFAULT_PKG + "ArithmeticException");
        JAVA_DEFAULT_CLASSES.put("ArrayIndexOutOfBoundsException", JAVA_DEFAULT_PKG + "ArrayIndexOutOfBoundsException");
        JAVA_DEFAULT_CLASSES.put("ArrayStoreException", JAVA_DEFAULT_PKG + "ArrayStoreException");
        JAVA_DEFAULT_CLASSES.put("ClassCastException", JAVA_DEFAULT_PKG + "ClassCastException");
        JAVA_DEFAULT_CLASSES.put("ClassNotFoundException", JAVA_DEFAULT_PKG + "ClassNotFoundException");
        JAVA_DEFAULT_CLASSES.put("CloneNotSupportedException", JAVA_DEFAULT_PKG + "CloneNotSupportedException");
        JAVA_DEFAULT_CLASSES.put("EnumConstantNotPresentException", JAVA_DEFAULT_PKG +  "EnumConstantNotPresentException  ");
        JAVA_DEFAULT_CLASSES.put("Exception", JAVA_DEFAULT_PKG + "Exception");
        JAVA_DEFAULT_CLASSES.put("IllegalAccessException", JAVA_DEFAULT_PKG + "IllegalAccessException");
        JAVA_DEFAULT_CLASSES.put("IllegalArgumentException", JAVA_DEFAULT_PKG + "IllegalArgumentException");
        JAVA_DEFAULT_CLASSES.put("IllegalMonitorStateException", JAVA_DEFAULT_PKG + "IllegalMonitorStateException");
        JAVA_DEFAULT_CLASSES.put("IllegalStateException", JAVA_DEFAULT_PKG + "IllegalStateException");
        JAVA_DEFAULT_CLASSES.put("IllegalThreadStateException", JAVA_DEFAULT_PKG + "IllegalThreadStateException");
        JAVA_DEFAULT_CLASSES.put("IndexOutOfBoundsException", JAVA_DEFAULT_PKG + "IndexOutOfBoundsException");
        JAVA_DEFAULT_CLASSES.put("InstantiationException", JAVA_DEFAULT_PKG + "InstantiationException");
        JAVA_DEFAULT_CLASSES.put("InterruptedException", JAVA_DEFAULT_PKG + "InterruptedException");
        JAVA_DEFAULT_CLASSES.put("NegativeArraySizeException", JAVA_DEFAULT_PKG + "NegativeArraySizeException");
        JAVA_DEFAULT_CLASSES.put("NoSuchFieldException", JAVA_DEFAULT_PKG + "NoSuchFieldException");
        JAVA_DEFAULT_CLASSES.put("NoSuchMethodException", JAVA_DEFAULT_PKG + "NoSuchMethodException");
        JAVA_DEFAULT_CLASSES.put("NullPointerException", JAVA_DEFAULT_PKG + "NullPointerException");
        JAVA_DEFAULT_CLASSES.put("NumberFormatException", JAVA_DEFAULT_PKG + "NumberFormatException");
        JAVA_DEFAULT_CLASSES.put("ReflectiveOperationException", JAVA_DEFAULT_PKG + "ReflectiveOperationException");
        JAVA_DEFAULT_CLASSES.put("RuntimeException", JAVA_DEFAULT_PKG + "RuntimeException");
        JAVA_DEFAULT_CLASSES.put("SecurityException", JAVA_DEFAULT_PKG + "SecurityException");
        JAVA_DEFAULT_CLASSES.put("StringIndexOutOfBoundsException", JAVA_DEFAULT_PKG + "StringIndexOutOfBoundsException");
        JAVA_DEFAULT_CLASSES.put("TypeNotPresentException", JAVA_DEFAULT_PKG + "TypeNotPresentException");
        JAVA_DEFAULT_CLASSES.put("UnsupportedOperationException", JAVA_DEFAULT_PKG + "UnsupportedOperationException");
        JAVA_DEFAULT_CLASSES.put("AbstractMethodError", JAVA_DEFAULT_PKG + "AbstractMethodError");
        JAVA_DEFAULT_CLASSES.put("AssertionError", JAVA_DEFAULT_PKG + "AssertionError");
        JAVA_DEFAULT_CLASSES.put("BootstrapMethodError", JAVA_DEFAULT_PKG + "BootstrapMethodError");
        JAVA_DEFAULT_CLASSES.put("ClassCircularityError", JAVA_DEFAULT_PKG + "ClassCircularityError");
        JAVA_DEFAULT_CLASSES.put("ClassFormatError", JAVA_DEFAULT_PKG + "ClassFormatError");
        JAVA_DEFAULT_CLASSES.put("Error", JAVA_DEFAULT_PKG + "Error");
        JAVA_DEFAULT_CLASSES.put("ExceptionInInitializerError", JAVA_DEFAULT_PKG + "ExceptionInInitializerError");
        JAVA_DEFAULT_CLASSES.put("IllegalAccessError", JAVA_DEFAULT_PKG + "IllegalAccessError");
        JAVA_DEFAULT_CLASSES.put("IncompatibleClassChangeError", JAVA_DEFAULT_PKG + "IncompatibleClassChangeError");
        JAVA_DEFAULT_CLASSES.put("InstantiationError", JAVA_DEFAULT_PKG + "InstantiationError");
        JAVA_DEFAULT_CLASSES.put("InternalError", JAVA_DEFAULT_PKG + "InternalError");
        JAVA_DEFAULT_CLASSES.put("LinkageError", JAVA_DEFAULT_PKG + "LinkageError");
        JAVA_DEFAULT_CLASSES.put("NoClassDefFoundError", JAVA_DEFAULT_PKG + "NoClassDefFoundError");
        JAVA_DEFAULT_CLASSES.put("NoSuchFieldError", JAVA_DEFAULT_PKG + "NoSuchFieldError");
        JAVA_DEFAULT_CLASSES.put("NoSuchMethodError", JAVA_DEFAULT_PKG + "NoSuchMethodError");
        JAVA_DEFAULT_CLASSES.put("OutOfMemoryError", JAVA_DEFAULT_PKG + "OutOfMemoryError");
        JAVA_DEFAULT_CLASSES.put("StackOverflowError", JAVA_DEFAULT_PKG + "StackOverflowError");
        JAVA_DEFAULT_CLASSES.put("ThreadDeath", JAVA_DEFAULT_PKG + "ThreadDeath");
        JAVA_DEFAULT_CLASSES.put("UnknownError", JAVA_DEFAULT_PKG + "UnknownError");
        JAVA_DEFAULT_CLASSES.put("UnsatisfiedLinkError", JAVA_DEFAULT_PKG + "UnsatisfiedLinkError");
        JAVA_DEFAULT_CLASSES.put("UnsupportedClassVersionError", JAVA_DEFAULT_PKG + "UnsupportedClassVersionError");
        JAVA_DEFAULT_CLASSES.put("InternalError", JAVA_DEFAULT_PKG + "InternalError");
        JAVA_DEFAULT_CLASSES.put("VerifyError", JAVA_DEFAULT_PKG + "VerifyError");
        JAVA_DEFAULT_CLASSES.put("VirtualMachineError", JAVA_DEFAULT_PKG + "VirtualMachineError");
    }

    /**
     * @return the javaDefaultClasses
     */
    public static
    Map<String, String> getJavaDefaultClasses() {
        return JAVA_DEFAULT_CLASSES;
    }

    /**
     *
     * @author Muntazir Fadhel
     *
     */
    public enum AccessModifiers {

        FINAL(""),
        ABSTRACT(""),
        INTERFACE(""),
        NATIVE(""),
        PRIVATE("-"),
        PROTECTED("#"),
        PUBLIC("+"),
        STATIC(""),
        STRICTFP(""),
        SYNCHRONIZED(""),
        TRANSIENT(""),
        NONE("~"),
        VOLATILE("");

        private String                 umlClassDigramSymbol = null;

        /**
         *
         * @param uMLClassDigramSymbol symbol
         */
        AccessModifiers(
                final String uMLClassDigramSymbol) {
            umlClassDigramSymbol = uMLClassDigramSymbol;
        }

        /**
         *
         * @return diagram symbol string.
         */
        public
        String getUMLClassDigramSymbol() {
            return umlClassDigramSymbol;
        }
    }

    /**
     * Enum constants representing types of component found in java code source model.
     * @author Muntazir Fadhel
     *
     */
    public  enum JavaComponentTypes {

        CLASS_COMPONENT(true, false, false),
        INTERFACE_COMPONENT(true, false, false),
        INTERFACE_CONSTANT_COMPONENT(false, false, true),
        ENUM_COMPONENT(true, false, false),
        ANNOTATION_COMPONENT(false, false, false),
        METHOD_COMPONENT(false, true, false),
        CONSTRUCTOR_COMPONENT(false, true, false),
        ENUM_CONSTANT_COMPONENT(false, false, true),
        FIELD_COMPONENT(false, false, true),
        METHOD_PARAMETER_COMPONENT(false, false, true),
        CONSTRUCTOR_PARAMETER_COMPONENT(false, false, true),
        LOCAL_VARIABLE_COMPONENT(false, false, true);

        private final boolean isBaseComponent;
        private final boolean isMethodComponent;
        private final boolean isVariableComonent;

        /**
         * Constructor.
         * @param isBaseComponent true when component is base component.
         * @param isMethodComponent true when method is method component.
         * @param isVariableComponent true when component is a variable type component.
         */
        JavaComponentTypes(final boolean isBaseComponent, final boolean isMethodComponent,
                final boolean isVariableComponent) {
            this.isBaseComponent = isBaseComponent;
            this.isMethodComponent = isMethodComponent;
            isVariableComonent = isVariableComponent;
        }

        /**
         *
         * @return true when component is a class/interface/enum/ etc..
         */
        public boolean isBaseComponent() {
            return isBaseComponent;
        }

        /**
         *
         * @return true when component is a method type component.
         */
        public boolean isMethodComponent() {
            return isMethodComponent;
        }

        /**
         *
         * @return true when component is a variable type component.
         */
        public boolean isVariableComponent() {
            return isVariableComonent;
        }
    }

    /**
     *
     * @author Muntazir Fadhel
     *
     */
    public enum InvocationSiteProperty {

        /**
         *
         */
        FIELD,
        LOCAL,
        NONE,
        METHOD_PARAMETER,
        CONSTRUCTOR_PARAMETER;
    }


    private static final Map<AccessModifiers, String> JAVA_ACCESS_MODIFIER_MAP
    =
    new HashMap<AccessModifiers, String>();

    /**
     * @return the javaAccessModifierMap
     */

    public static
    Map<AccessModifiers, String> getJavaAccessModifierMap() {
        return JAVA_ACCESS_MODIFIER_MAP;
    }

    /**
     * @return the javaComponentTypes
     */
    public static
    Map<JavaComponentTypes, String> getJavaComponentTypes() {
        return JAVA_COMPONENT_TYPES;
    }
    static {
        getJavaAccessModifierMap().put(AccessModifiers.PRIVATE, "private");
        getJavaAccessModifierMap().put(AccessModifiers.PROTECTED, "protected");
        getJavaAccessModifierMap().put(AccessModifiers.PUBLIC, "public");
        getJavaAccessModifierMap().put(AccessModifiers.VOLATILE, "volatile");
        getJavaAccessModifierMap().put(AccessModifiers.TRANSIENT, "transient");
        getJavaAccessModifierMap().put(AccessModifiers.SYNCHRONIZED, "synchronized");
        getJavaAccessModifierMap().put(AccessModifiers.STRICTFP, "strictfp");
        getJavaAccessModifierMap().put(AccessModifiers.STATIC, "static");
        getJavaAccessModifierMap().put(AccessModifiers.NATIVE, "native");
        getJavaAccessModifierMap().put(AccessModifiers.ABSTRACT, "abstract");
        getJavaAccessModifierMap().put(AccessModifiers.INTERFACE, "interface");
        getJavaAccessModifierMap().put(AccessModifiers.FINAL, "final");

    }

    /**
     * Map of all the possible java component types..
     */
    private static final Map<JavaComponentTypes, String> JAVA_COMPONENT_TYPES  =  new HashMap<JavaComponentTypes, String>();
    static {
        getJavaComponentTypes().put(JavaComponentTypes.INTERFACE_COMPONENT, "interface");
        getJavaComponentTypes().put(JavaComponentTypes.ENUM_COMPONENT, "enum");
        getJavaComponentTypes().put(JavaComponentTypes.ENUM_CONSTANT_COMPONENT, "enumConstant");
        getJavaComponentTypes().put(JavaComponentTypes.INTERFACE_CONSTANT_COMPONENT, "interfaceConstant");
        getJavaComponentTypes().put(JavaComponentTypes.ANNOTATION_COMPONENT, "annotation");
        getJavaComponentTypes().put(JavaComponentTypes.METHOD_COMPONENT, "method");
        getJavaComponentTypes().put(JavaComponentTypes.CONSTRUCTOR_COMPONENT, "constructor");
        getJavaComponentTypes().put(JavaComponentTypes.CONSTRUCTOR_PARAMETER_COMPONENT, "constructorParam");
        getJavaComponentTypes().put(JavaComponentTypes.FIELD_COMPONENT, "field");
        getJavaComponentTypes().put(JavaComponentTypes.METHOD_PARAMETER_COMPONENT, "methodParam");
        getJavaComponentTypes().put(JavaComponentTypes.LOCAL_VARIABLE_COMPONENT, "localVar");
        getJavaComponentTypes().put(JavaComponentTypes.CLASS_COMPONENT, "class");
    }

}
