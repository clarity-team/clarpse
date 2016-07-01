package com.clarity.sourcemodel;

import invocation.ComponentInvocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;

/**
 * Representation of the individual code level components (classes,
 * methodComponents, fieldComponents, etc..) that are used to create a code
 * base.
 *
 * @author Muntazir Fadhel
 */
public final class Component implements Serializable {

    private static final long serialVersionUID = 1L;

    private String start;
    private String end;
    private String value;
    private String packageName;
    private String name;
    private String comment;
    private String sourceFilePath;
    private ArrayList<String> imports = new ArrayList<String>();
    private ArrayList<String> modifiers = new ArrayList<String>();
    private ComponentType componentType;
    private ArrayList<ComponentInvocation> componentInvocations = new ArrayList<ComponentInvocation>();
    private String componentName;
    private String code;
    private final ArrayList<String> children = new ArrayList<String>();
    private String declarationTypeSnippet;

    public Component(final Component component) {
        modifiers = component.getModifiers();
        code = component.getCode();
        componentType = component.getComponentType();
        declarationTypeSnippet = component.getDeclarationTypeSnippet();
        componentInvocations = component.getExternalClassTypeReferences();
        imports = component.getImports();
        componentName = component.getComponentName();
        packageName = component.getPackageName();
        value = component.getValue();
        start = component.getStartLine();
        end = component.getEndLine();
        sourceFilePath = component.getSourceFilePath();
    }

    public Component() {
    }

    public void copy(final Component component) {

        modifiers = component.getModifiers();
        code = component.getCode();
        componentType = component.getComponentType();
        declarationTypeSnippet = component.getDeclarationTypeSnippet();
        componentInvocations = component.getExternalClassTypeReferences();
        imports = component.getImports();
        componentName = component.getComponentName();
        packageName = component.getPackageName();
        value = component.getValue();
        start = component.getStartLine();
        end = component.getEndLine();
    }

    public ArrayList<String> getChildComponents() {
        return children;
    }

    public String getStartLine() {
        return start;
    }

    public void setStartLine(final String startLine) {
        start = startLine;
    }

    public String getEndLine() {
        return end;
    }

    public void setEndLine(final String endLine) {
        end = endLine;
    }

    public String getUniqueName() {
        if (!packageName.isEmpty()) {
            return packageName + "." + componentName;
        } else {
            return componentName;
        }
    }

    public String getName() {

        return name;
    }

    public void insertChildComponent(final String childComponentName) {
        children.add(childComponentName);
    }

    public void addImports(final String importStmt) {
        imports.add(importStmt);
    }

    public String getCode() {
        return code;
    }

    public String getDeclarationTypeSnippet() {
        return declarationTypeSnippet;
    }

    public ArrayList<ComponentInvocation> getExternalClassTypeReferences() {
        return componentInvocations;
    }

    public void insertTypeReference(final ComponentInvocation ref) {
        componentInvocations.add(ref);
    }

    public ArrayList<String> getImports() {
        return imports;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setCode(final String componentCode) {
        code = componentCode;
    }

    public void setDeclarationTypeSnippet(final String componentDeclarationTypeFragment) {
        declarationTypeSnippet = componentDeclarationTypeFragment;
    }

    public void setExternalTypeReferences(final ArrayList<ComponentInvocation> externalReferences) {
        componentInvocations = externalReferences;
    }

    public void setImports(final ArrayList<String> importStatements) {
        imports = importStatements;
    }

    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

    public ArrayList<String> getModifiers() {
        return modifiers;
    }

    public void insertAccessModifier(final String modifier) {
        if (OOPSourceModelConstants.getJavaAccessModifierMap().containsValue(modifier)) {
            modifiers.add(modifier);
        }
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(final ComponentType componentType) {
        this.componentType = componentType;
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

    public Component getParentBaseComponent(final Map<String, Component> map) {

        String currParentClassName = getUniqueName();
        final int numberOfParentCmps = StringUtils.countMatches(getComponentName(), ".");
        for (int i = numberOfParentCmps; i > 0; i--) {
            currParentClassName = getParentComponentUniqueName(currParentClassName);
            if (map.containsKey(currParentClassName)
                    && map.get(currParentClassName).getComponentType().isBaseComponent()) {
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

    public Component getParentMethodComponent(final Map<String, Component> components) {

        String currParentClassName = getUniqueName();
        final int numberOfParentCmps = StringUtils.countMatches(getComponentName(), ".");
        for (int i = numberOfParentCmps; i > 0; i--) {
            currParentClassName = getParentComponentUniqueName(currParentClassName);
            if (components.containsKey(currParentClassName)
                    && components.get(currParentClassName).getComponentType().isMethodComponent()) {
                break;
            }
        }
        return components.get(currParentClassName);
    }

    public String getParentComponentUniqueName() {

        final int lastPeriod = getUniqueName().lastIndexOf(".");
        final String currParentClassName = getUniqueName().substring(0, lastPeriod);
        return currParentClassName;
    }

    public void insertTypeReferences(final ArrayList<ComponentInvocation> externalClassTypeReferenceList) {
        for (final ComponentInvocation typeRef : externalClassTypeReferenceList) {
            insertTypeReference(typeRef);
        }
    }

    public List<ComponentInvocation> componentInvocations(final Class<? extends ComponentInvocation> type) {

        final List<ComponentInvocation> invocations = new ArrayList<ComponentInvocation>();

        for (final ComponentInvocation compInvocation : componentInvocations) {
            if (type.isAssignableFrom(compInvocation.getClass())) {
                invocations.add(compInvocation);
            }
        }
        return invocations;
    }

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
