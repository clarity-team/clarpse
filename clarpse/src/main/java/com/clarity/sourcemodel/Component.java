package com.clarity.sourcemodel;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.clarity.ClarpseUtil;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of the individual code level components (classes,
 * methodComponents, fieldComponents, etc..) that are used to create a code
 * base.
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
        externalClassTypeReferences = component.getExternalClassTypeReferences();
        implementedClasses = component.getImplementedClasses();
        imports = component.getImports();
        componentName = component.getComponentName();
        packageName = component.getPackageName();
        superClasses = component.getSuperClasses();
        value = component.getValue();
        startLine = component.getStartLine();
        endLine = component.getEndLine();
    }

    @JsonIgnore
    public void copy(final Component component) {

        modifiers = component.getModifiers();
        annotations = component.getAnnotations();
        code = component.getCode();
        componentType = component.getComponentType();
        declarationTypeSnippet = component.getDeclarationTypeSnippet();
        exceptions = component.getExceptions();
        externalClassTypeReferences = component.getExternalClassTypeReferences();
        implementedClasses = component.getImplementedClasses();
        imports = component.getImports();
        componentName = component.getComponentName();
        packageName = component.getPackageName();
        superClasses = component.getSuperClasses();
        value = component.getValue();
        startLine = component.getStartLine();
        endLine = component.getEndLine();
    }

    /**
     * Default Constructor.
     */
    public Component() {

    }

    @JsonProperty("start")
    private String startLine;

    @JsonProperty("end")
    private String endLine;

    private static final long serialVersionUID = 1L;
    /**
     * If the component is invoked Using an array.
     */

    /**
     * Value of the component.
     */
    private String value;
    /**
     * Component package componentName.
     */
    private String packageName;
    /**
     * Exceptions.
     */

    /**
     * Component Java Doc Comment.
     */
    private String comment;

    private ArrayList<String> exceptions = new ArrayList<String>();
    /**
     * list of classes the current component implements.
     */
    @JsonProperty("implements")
    private ArrayList<String> implementedClasses = new ArrayList<String>();
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
    private String componentType;
    /**
     * List of annotations for the current component.
     */
    private ArrayList<Map.Entry<String, HashMap<String, String>>> annotations = new ArrayList<Map.Entry<String, HashMap<String, String>>>();

    /**
     * references to other class components in the project.
     */
    @JsonProperty("refs")
    private ArrayList<TypeReference> externalClassTypeReferences = new ArrayList<TypeReference>();
    /**
     * componentName of the current component.
     */

    private String componentName;
    /**
     * code of the current component.
     */
    private String code;
    /*
     * This is different from the type in the following way... eg) 'public
     * ArrayList<String> tempList; type -> String declarationTypeSnippet ->
     * ArrayList<String>
     */
    private String declarationTypeSnippet;

    @JsonProperty("children")
    private final ArrayList<String> childComponents = new ArrayList<String>();

    public ArrayList<String> getChildComponents() {
        return childComponents;
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
     * @return the startLine
     */
    public String getStartLine() {
        return startLine;
    }

    /**
     * @param startLine
     *            the startLine to set
     */
    public void setStartLine(final String startLine) {
        this.startLine = startLine;
    }

    /**
     * @return the endLine
     */
    public String getEndLine() {
        return endLine;
    }

    /**
     * @param endLine
     *            the endLine to set
     */
    public void setEndLine(final String endLine) {
        this.endLine = endLine;
    }

    public String getUniqueName() {
        if (!packageName.isEmpty()) {
            return packageName + "." + componentName;
        } else {
            return componentName;
        }
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
     * @param childComponentName
     *            name of the child component of the current component
     */
    public final void insertChildComponent(final String childComponentName) {
        childComponents.add(childComponentName);
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
        return externalClassTypeReferences;
    }

    /**
     * Inserts a external class type reference.
     */
    @JsonIgnore
    public final void insertTypeReference(final TypeReference ref) {

        boolean alreadyExists = false;
        // If an external type reference to the same class already exists,
        // update the line numbers if needed...
        for (final TypeReference currentRef : externalClassTypeReferences) {
            if (currentRef.getExternalTypeName().equals(ref.getExternalTypeName())) {
                alreadyExists = true;
                for (final int lineNum : ref.getReferenceLineNums()) {
                    if (!currentRef.getReferenceLineNums().contains(lineNum)) {
                        currentRef.insertReferenceLineNum(lineNum);
                    }
                }
            }
        }
        // doesn't already exist, simply add it to the list
        if (!alreadyExists) {
            final TypeReference newRef = new TypeReference(ref);
            externalClassTypeReferences.add(newRef);
        }
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
     * @param externalReferences
     *            arraylist of external classes referenced.
     */
    public final void setExternalTypeReferences(final ArrayList<TypeReference> externalReferences) {
        externalClassTypeReferences = externalReferences;
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
     * Returns the short version name of the current component's parent class.
     * eg) if componentName = AClass.ANestedInterface.Amethod.variable1 then
     * parentClassName = ANestedInterface
     *
     * @param map
     *            list of all the components.
     * @return name of the current component's parent class.
     */
    @JsonIgnore
    public Component getParentBaseComponent(final Map<String, Component> map) {

        String currParentClassName = getUniqueName();
        final int numberOfParentCmps = StringUtils.countMatches(getComponentName(), ".");
        for (int i = numberOfParentCmps; i > 0; i--) {
            currParentClassName = getParentComponentUniqueName(currParentClassName);
            if (map.containsKey(currParentClassName)
                    && map.get(currParentClassName).isBaseComponent()) {
                break;
            }
        }
        return map.get(currParentClassName);
    }

    /**
     * @param uniqueComponentName
     *            Component package name + containing hierarchical name.
     * @return parent component name
     */
    @JsonIgnore
    public String getParentComponentUniqueName(final String componentFullName) {

        final int lastPeriod = componentFullName.lastIndexOf(".");
        final String currParentClassName = getUniqueName().substring(0, lastPeriod);
        return currParentClassName;
    }

    @JsonIgnore
    public Component getParentMethodComponent(final Map<String, Component> components) {

        String currParentClassName = getUniqueName();
        final int numberOfParentCmps = StringUtils.countMatches(getComponentName(), ".");
        for (int i = numberOfParentCmps; i > 0; i--) {
            currParentClassName = getParentComponentUniqueName(currParentClassName);
            if (components.containsKey(currParentClassName)
                    && components.get(currParentClassName).isMethodComponent()) {
                break;
            }
        }
        return components.get(currParentClassName);
    }

    @JsonIgnore
    public boolean isBaseComponent() {
        final ComponentTypes temp = (ComponentTypes) ClarpseUtil.getObjectFromStringObjectKeyValueMap(
                componentType, OOPSourceModelConstants.getJavaComponentTypes());
        if (temp == null) {
            return false;
        } else {
            return temp.isBaseComponent();
        }
    }

    @JsonIgnore
    public boolean isMethodComponent() {
        final ComponentTypes temp = (ComponentTypes) ClarpseUtil.getObjectFromStringObjectKeyValueMap(
                componentType, OOPSourceModelConstants.getJavaComponentTypes());
        if (temp == null) {
            return false;
        } else {
            return temp.isMethodComponent();
        }
    }

    @JsonIgnore
    public boolean isVariableComponent() {
        final ComponentTypes temp = (ComponentTypes) ClarpseUtil.getObjectFromStringObjectKeyValueMap(
                componentType, OOPSourceModelConstants.getJavaComponentTypes());
        if (temp == null) {
            return false;
        } else {
            return temp.isVariableComponent();
        }
    }

    @JsonIgnore
    public boolean isTestRelatedComponent() {

        return getAnnotations().toString().toLowerCase().contains("Test".toLowerCase());
    }

    /**
     * @param componentName
     *            Component hierarchical name.
     * @return parent component name
     */
    @JsonIgnore
    public String getParentComponentUniqueName() {

        final int lastPeriod = getUniqueName().lastIndexOf(".");
        final String currParentClassName = getUniqueName().substring(0, lastPeriod);
        return currParentClassName;
    }

    public void insertTypeReferences(final ArrayList<TypeReference> externalClassTypeReferenceList) {
        for (final TypeReference typeRef : externalClassTypeReferenceList) {
            insertTypeReference(typeRef);
        }
    }

    @Override
    public int hashCode() {
        return getUniqueName().hashCode();
    }
}
