package com.hadii.clarpse.sourcemodel;

import com.hadii.clarpse.reference.ComponentReference;
import com.hadii.clarpse.reference.SimpleTypeReference;
import com.hadii.clarpse.reference.TypeExtensionReference;
import com.hadii.clarpse.reference.TypeImplementationReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class OOPSourceModelConstants {

    static final Map<String, String> JAVA_COLLECTIONS = new HashMap<String, String>();

    static final Map<String, String> JAVA_ANNOTATIONS = new HashMap<String, String>();
    private static final Map<AccessModifiers, String> JAVA_ACCESS_MODIFIER_MAP = new HashMap<AccessModifiers, String>();
    private static final Map<ComponentType, String> COMPONENT_TYPES = new HashMap<ComponentType, String>();

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

    static {
        getJavaComponentTypes().put(ComponentType.INTERFACE, "interface");
        getJavaComponentTypes().put(ComponentType.STRUCT, "struct");
        getJavaComponentTypes().put(ComponentType.ENUM, "enum");
        getJavaComponentTypes().put(ComponentType.ENUM_CONSTANT, "enumConstant");
        getJavaComponentTypes().put(ComponentType.INTERFACE_CONSTANT, "interfaceConstant");
        getJavaComponentTypes().put(ComponentType.ANNOTATION, "annotation");
        getJavaComponentTypes().put(ComponentType.METHOD, "method");
        getJavaComponentTypes().put(ComponentType.CONSTRUCTOR, "constructor");
        getJavaComponentTypes().put(ComponentType.FIELD, "field");
        getJavaComponentTypes().put(ComponentType.LOCAL, "localVar");
        getJavaComponentTypes().put(ComponentType.CLASS, "class");
    }

    private OOPSourceModelConstants() {
    }

    public static Map<AccessModifiers, String> getJavaAccessModifierMap() {
        return JAVA_ACCESS_MODIFIER_MAP;
    }

    public static Map<ComponentType, String> getJavaComponentTypes() {
        return COMPONENT_TYPES;
    }

    public enum AccessModifiers {
        FINAL(""), ABSTRACT(""), INTERFACE(""), NATIVE(""), PRIVATE("-"), PROTECTED("#"), PUBLIC("+"), STATIC(
                ""), STRICTFP(""), SYNCHRONIZED(""), TRANSIENT(""), NONE("~"), VOLATILE("");

        private String umlClassDigramSymbol = null;

        AccessModifiers(final String uMLClassDigramSymbol) {
            umlClassDigramSymbol = uMLClassDigramSymbol;
        }

        public String getUMLClassDigramSymbol() {
            return umlClassDigramSymbol;
        }
    }

    public enum TypeReferences {

        SIMPLE(SimpleTypeReference.class),
        EXTENSION(TypeExtensionReference.class),
        IMPLEMENTATION(TypeImplementationReference.class);

        private Class<? extends ComponentReference> matchingClass = null;

        TypeReferences(final Class<? extends ComponentReference> matchingClass) {
            this.matchingClass = matchingClass;
        }

        public Class<? extends ComponentReference> getMatchingClass() {
            return matchingClass;
        }
    }

    public enum ComponentType implements Serializable {

        CLASS("class", true, false, false), STRUCT("class", true, false, false), INTERFACE("interface", true, false,
                false), INTERFACE_CONSTANT("interface_constant", false, false, true), ENUM("enum", true, false,
                false), ANNOTATION("annotation", false, false, false), METHOD("method", false, true,
                false), CONSTRUCTOR("method", false, true, false), ENUM_CONSTANT("enum_constant", false,
                false,
                true), FIELD("field_variable", false, false, true), METHOD_PARAMETER_COMPONENT(
                "method_parameter", false, false,
                true), CONSTRUCTOR_PARAMETER_COMPONENT("constructor_parameter", false,
                false, true), LOCAL("local_variable", false, false, true);

        private final boolean isBaseComponent;
        private final boolean isMethodComponent;
        private final boolean isVariableComponent;
        private final String value;

        ComponentType(final String value, final boolean isBaseComponent, final boolean isMethodComponent,
                      final boolean isVariableComponent) {
            this.isBaseComponent = isBaseComponent;
            this.isMethodComponent = isMethodComponent;
            this.isVariableComponent = isVariableComponent;
            this.value = value;
        }

        public boolean isBaseComponent() {
            return isBaseComponent;
        }

        public boolean isMethodComponent() {
            return isMethodComponent;
        }

        public boolean isVariableComponent() {
            return isVariableComponent;
        }

        public String getValue() {
            return value;
        }
    }
}
