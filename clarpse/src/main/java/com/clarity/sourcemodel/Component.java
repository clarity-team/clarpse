package com.clarity.sourcemodel;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    @JsonProperty("start")
    private String startLine;
    @JsonProperty("end")
    private String endLine;
    private static final long serialVersionUID = 1L;
    private String value;
    private String packageName;
    private String name;
    private String comment;
    private String sourceFilePath;
    private ArrayList<String> exceptions = new ArrayList<String>();
    @JsonProperty("implements")
    private ArrayList<String> implementedClasses = new ArrayList<String>();
    private ArrayList<String> imports = new ArrayList<String>();
    @JsonProperty("extends")
    private ArrayList<String> superClasses = new ArrayList<String>();
    private ArrayList<String> modifiers = new ArrayList<String>();
    @JsonProperty("component")
    private String componentType;
    private ArrayList<Map.Entry<String, HashMap<String, String>>> annotations = new ArrayList<Map.Entry<String, HashMap<String, String>>>();
    @JsonProperty("refs")
    private ArrayList<TypeReference> externalClassTypeReferences = new ArrayList<TypeReference>();
    private String componentName;
    private String code;
    private String declarationTypeSnippet;

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
        sourceFilePath = component.getSourceFilePath();
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

    public Component() {
    }

    @JsonProperty("children")
    private final ArrayList<String> childComponents = new ArrayList<String>();

    public ArrayList<String> getChildComponents() {
        return childComponents;
    }

    public final void insertException(final String exception) {
        exceptions.add(exception);
    }

    public final void addImplementedClass(final String implemented) {
        implementedClasses.add(implemented);
    }

    /**
     * Returns the line number corresponding to the signature of the component.
     */
    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(final String startLine) {
        this.startLine = startLine;
    }

    public String getEndLine() {
        return endLine;
    }

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

    @JsonInclude(Include.NON_NULL)
    public final String getName() {

        return name;
    }

    public final void insertChildComponent(final String childComponentName) {
        childComponents.add(childComponentName);
    }

    public final void addImports(final String importStmt) {
        imports.add(importStmt);
    }

    public final void addSuperClass(final String superClass) {
        superClasses.add(superClass);
    }

    public final String getCode() {
        return code;
    }

    public final String getDeclarationTypeSnippet() {
        return declarationTypeSnippet;
    }

    public final ArrayList<TypeReference> getExternalClassTypeReferences() {
        return externalClassTypeReferences;
    }

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

    public final ArrayList<String> getImplementedClasses() {
        return implementedClasses;
    }

    public final ArrayList<String> getImports() {
        return imports;
    }

    public final String getComponentName() {
        return componentName;
    }

    public final ArrayList<String> getSuperClasses() {
        return superClasses;
    }

    public final void setCode(final String componentCode) {
        code = componentCode;
    }

    public final void setDeclarationTypeSnippet(final String componentDeclarationTypeFragment) {
        declarationTypeSnippet = componentDeclarationTypeFragment;
    }

    public final void setExternalTypeReferences(final ArrayList<TypeReference> externalReferences) {
        externalClassTypeReferences = externalReferences;
    }

    public final void setImplementedClasses(final ArrayList<String> implClasses) {
        implementedClasses = implClasses;
    }

    public final void setImports(final ArrayList<String> importStatements) {
        imports = importStatements;
    }

    public final void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

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

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

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

        return getAnnotations().toString().toLowerCase().contains("Test".toLowerCase())
                || this.getName().endsWith("Test");
    }

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

    @JsonIgnore
    public boolean isMethodParamComponentType() {
        final ComponentTypes temp = (ComponentTypes) ClarpseUtil.getObjectFromStringObjectKeyValueMap(componentType,
                OOPSourceModelConstants.getJavaComponentTypes());
        return temp == ComponentTypes.METHOD_PARAMETER_COMPONENT;
    }


    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(final String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }
}
