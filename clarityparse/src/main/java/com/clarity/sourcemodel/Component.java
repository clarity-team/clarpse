package com.clarity.sourcemodel;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.clarity.ClarityUtil;
import com.clarity.sourcemodel.OOPSourceModelConstants.JavaComponentTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Polyglot representation of an individual code unit (classes,
 * methodComponents, fieldComponents, etc..) that is typically used to build a
 * piece of code.
 *
 * @author Muntazir Fadhel
 */
@JsonInclude(Include.NON_NULL)
public class Component implements Serializable {

    /**
     * Clone a component.
     *
     * @param component
     *            component to be cloned.
     */
    public Component(final Component component) {
        modifiers = component.getModifiers();
        annotations = component.getAnnotations();
        code = component.getCode();
        componentType = component.getComponentType();
        declarationTypeSnippet = component.getDeclarationTypeSnippet();
        exceptions = component.getExceptions();
        externalTypeReferences = component.getExternalClassTypeReferences();
        implementedClasses = component.getImplementedClasses();
        imports = component.getImports();
        componentName = component.getComponentName();
        packageName = component.getPackageName();
        superClasses = component.getSuperClasses();
        value = component.getValue();
        uniqueName = component.getUniqueName();
        comment = component.getComment();
        startLine = component.getStartLine();
        endLine = component.getEndLine();
    }

    /**
     * Default Constructor.
     */
    public Component() {

    }

    private static final long serialVersionUID = 1L;

    private String uniqueName = null;

    public void setUniqueName() {
        uniqueName = getUniqueName();
    }

    /**
     * Value of the component.
     */
    private String value = null;
    /**
     * Component package componentName.
     */

    /**
     * Component Java Doc Comment.
     */
    private String comment;

    private String packageName = null;
    /**
     * Exceptions.
     */
    private ArrayList<String> exceptions = new ArrayList<String>();
    /**
     * list of classes the current component implements.
     */
    @JsonProperty("implements")
    private ArrayList<String> implementedClasses = new ArrayList<String>();

    @JsonProperty("start")
    private String startLine;

    @JsonProperty("end")
    private String endLine;

    /**
     * Array list of imported resources for the class.
     */
    private ArrayList<String> imports = new ArrayList<String>();
    /**
     * Super classes.
     */
    @JsonProperty("extends")
    private ArrayList<String> superClasses = new ArrayList<String>();
    /**
     * Component's access modifiers.
     */
    private ArrayList<String> modifiers = new ArrayList<String>();
    /**
     * Type of code piece the current component represents.
     */
    @JsonProperty("component")
    private String componentType = null;
    /**
     * List of annotations for the current component.
     */
    private ArrayList<Map.Entry<String, HashMap<String, String>>> annotations = new ArrayList<Map.Entry<String, HashMap<String, String>>>();

    /**
     * references to other class components in the project.
     */
    @JsonProperty("refs")
    private ArrayList<TypeReference> externalTypeReferences = new ArrayList<TypeReference>();
    /**
     * componentName of the current component.
     */

    private String componentName = null;
    /**
     * code of the current component.
     */
    private String code = null;
    /*
     * This is different from the type in the following way... eg) 'public
     * ArrayList<String> tempList; type -> String declarationTypeSnippet ->
     * ArrayList<String>
     */
    private String declarationTypeSnippet = null;

    @JsonProperty("children")
    private final ArrayList<String> childComponents = new ArrayList<String>();

    public ArrayList<String> getChildComponents() {
        return childComponents;
    }

    /**
     * @return the startLine
     */
    public String getStartLine() {
        return startLine;
    }

    /**
     * @param a
     *            the startLine to set
     */
    public void setStartLine(final int a) {
        startLine = String.valueOf(a);
    }

    /**
     * @return the endLine
     */
    public String getEndLine() {
        return endLine;
    }

    /**
     * @param b
     *            the endLine to set
     */
    public void setEndLine(final int b) {
        endLine = String.valueOf(b);
    }

    /**
     * @param exception
     *            insert exception for current component.
     */
    public final void insertException(final String exception) {
        exceptions.add(exception);
    }

    /**
     * @param implemented
     *            implemented class
     */
    public final void addImplementedClass(final String implemented) {
        implementedClasses.add(implemented);
    }

    /**
     * @return component unique name
     */
    public String getUniqueName() {
        return packageName + "." + componentName;
    }

    /**
     * Returns the short version name of the current component. eg) if component
     * componentName = AClass.aMethod.classField, then the short version
     * componentName = classField
     *
     * @return short version componentName of current component.
     */
    @JsonInclude(Include.NON_NULL)
    public final String getName() {

        final String[] bits = this.getComponentName().split(Pattern.quote("."));
        return bits[bits.length - 1];
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Sets the child component to the given component and inherits all the
     * child component's external type references.
     *
     * @param childComponent
     *            the child component of the current component
     */
    public final void insertChildComponent(final Component childComponent) {
        childComponents.add(childComponent.getUniqueName());

        for (final TypeReference ref : childComponent.getExternalClassTypeReferences()) {
            final TypeReference temp = new TypeReference(ref);
            insertExternalClassType(temp);
        }
    }

    /**
     * Retuns the short version name of the current component's parent class.
     * eg) if componentName = AClass.ANestedInterface.Amethod.variable1 then
     * parentClassName = ANestedInterface
     *
     * @param components
     *            list of all the components.
     * @return name of the current component's parent class.
     */
    public String getParentClassUniqueName(final LinkedHashMap<String, Component> components) {

        String currParentClassName = uniqueName;
        if (this.isBaseComponent()) {
            return currParentClassName;
        } else {
            final int numberOfParentCmps = StringUtils.countMatches(componentName, ".");
            for (int i = numberOfParentCmps; i > 0; i--) {
                currParentClassName = ClarityUtil.getParentComponentUniqueName(currParentClassName);
                if (components.containsKey(currParentClassName)
                        && components.get(currParentClassName).isBaseComponent()) {
                    break;
                }
            }
            return currParentClassName;
        }
    }

    /**
     * @param type
     *            type of parent component, returns the first one found
     * @param components
     *            list of all components
     * @return the first parent component of the given type found
     */
    public final Component getParentComponent(final OOPSourceModelConstants.JavaComponentTypes type,
            final Map<String, Component> components) {

        String currParentClassName = uniqueName;
        final int numberOfParentCmps = StringUtils.countMatches(componentName, ".");
        for (int i = numberOfParentCmps; i > 0; i--) {
            currParentClassName = ClarityUtil.getParentComponentUniqueName(currParentClassName);
            if (components.containsKey(currParentClassName)
                    && (ClarityUtil.getObjectFromStringObjectKeyValueMap(components.get(currParentClassName)
                            .getComponentType(), OOPSourceModelConstants.getJavaComponentTypes()) == type)) {
                return components.get(currParentClassName);
            }
        }
        return null;
    }

    @JsonIgnore
    public boolean isBaseComponent() {
        final JavaComponentTypes temp = (JavaComponentTypes) ClarityUtil.getObjectFromStringObjectKeyValueMap(
                componentType, OOPSourceModelConstants.getJavaComponentTypes());
        if (temp == null) {
            return false;
        } else {
            return temp.isBaseComponent();
        }
    }

    @JsonIgnore
    public boolean isMethodomponent() {
        final JavaComponentTypes temp = (JavaComponentTypes) ClarityUtil.getObjectFromStringObjectKeyValueMap(
                componentType, OOPSourceModelConstants.getJavaComponentTypes());
        if (temp == null) {
            return false;
        } else {
            return temp.isMethodComponent();
        }
    }

    /**
     * @param importStmt
     *            import statement
     */
    public final void addImports(final String importStmt) {
        imports.add(importStmt);
    }

    /**
     * @param superClass
     *            super class
     */
    public final void addSuperClass(final String superClass) {
        superClasses.add(superClass);
    }

    /**
     * @return the code for the class.
     */
    public final String getCode() {
        return code;
    }

    /**
     * @return the declaration type fragment
     */
    public final String getDeclarationTypeSnippet() {
        return declarationTypeSnippet;
    }

    /**
     * @return string representing external class type
     */
    public final ArrayList<TypeReference> getExternalClassTypeReferences() {
        return externalTypeReferences;
    }

    /**
     * @return implemented class
     */
    public final ArrayList<String> getImplementedClasses() {
        return implementedClasses;
    }

    /**
     * @return import statements
     */
    public final ArrayList<String> getImports() {
        return imports;
    }

    /**
     * @return componentName of the class.
     */
    public final String getComponentName() {
        return componentName;
    }

    /**
     * @return super classes
     */
    public final ArrayList<String> getSuperClasses() {
        return superClasses;
    }

    /**
     * @param ref
     *            the type reference to be inserted for the current component.
     */
    public final void insertExternalClassType(TypeReference ref) {

        boolean typeRefAlreadyExists = false;
        for (final TypeReference currRefs : externalTypeReferences) {
            if (currRefs.getExternalTypeName().equals(ref.getExternalTypeName())) {
                typeRefAlreadyExists = true;
                for (final int lineNums : ref.getReferenceLineNums()) {
                    if (!currRefs.getReferenceLineNums().contains(lineNums)) {
                        currRefs.insertReferenceLineNum(lineNums);
                    }
                }
            }
        }
        if (!typeRefAlreadyExists) {
            externalTypeReferences.add(ref);
        }
    }

    /**
     * @param componentCode
     *            component code
     */
    public final void setCode(final String componentCode) {
        code = componentCode;
    }

    /**
     * @param componentDeclarationTypeFragment
     *            fragment to be set.
     */
    public final void setDeclarationTypeSnippet(final String componentDeclarationTypeFragment) {
        declarationTypeSnippet = componentDeclarationTypeFragment;
    }

    /**
     * @param implClasses
     *            implemented classes
     */
    public final void setImplementedClasses(final ArrayList<String> implClasses) {
        implementedClasses = implClasses;
    }

    /**
     * @param importStatements
     *            import statements
     */
    public final void setImports(final ArrayList<String> importStatements) {
        imports = importStatements;
    }

    /**
     * @param componentName
     *            component Name
     */
    public final void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

    /**
     * @param superClass
     *            super class
     */
    public final void setSuperClasses(final ArrayList<String> superClass) {
        superClasses = superClass;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(final String componentType) {
        this.componentType = componentType;
    }

    public ArrayList<String> getModifiers() {
        return modifiers;
    }

    public void insertAccessModifier(final String modifier) {
        if (OOPSourceModelConstants.getJavaAccessModifierMap().containsValue(modifier)) {
            modifiers.add(modifier);
        }
    }

    public ArrayList<String> getExceptions() {
        return exceptions;
    }

    public ArrayList<Map.Entry<String, HashMap<String, String>>> getAnnotations() {
        return annotations;
    }

    public void insertAnnotation(final AbstractMap.SimpleEntry<String, HashMap<String, String>> newAnnotation) {
        annotations.add(newAnnotation);
    }

    public void setAnnotations(final ArrayList<Map.Entry<String, HashMap<String, String>>> annotations) {
        this.annotations = annotations;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
