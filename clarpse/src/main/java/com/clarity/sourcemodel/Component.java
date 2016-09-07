package com.clarity.sourcemodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Representation of the individual code level components (classes,
 * methodComponents, fieldComponents, etc..) that are used to create a code
 * base.
 *
 * @author Muntazir Fadhel
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder(alphabetic = true)
public final class Component implements Serializable {

    private static final long serialVersionUID = 1L;

    private String start;
    private String end;
    /**
     * Value of the component if applicable.
     */
    private String value;
    private String packageName;
    /**
     * Short name.
     */
    private String name;
    private String comment = "";
    private String code;
    /**
     * Source file path from which the component was derived.
     */
    private String sourceFile;
    private List<String> imports = new ArrayList<String>();
    private List<String> modifiers = new ArrayList<String>();
    private ComponentType type;
    private List<ComponentInvocation> invocations = new ArrayList<ComponentInvocation>();
    /**
     * Formed by chaining parent components' names separated by a period.
     *
     * Eg) ClassA -> MethodB -> varC = "ClassA.MethodB.varC"
     */
    private String componentName;
    /**
     * List of all child components.
     */
    private final ArrayList<String> children = new ArrayList<String>();
    private String declarationTypeSnippet;

    public Component(final Component component) {
        modifiers = component.modifiers();
        type = component.componentType();
        declarationTypeSnippet = component.declarationTypeSnippet();
        invocations = component.invocations();
        imports = component.imports();
        componentName = component.componentName();
        packageName = component.packageName();
        value = component.value();
        start = component.startLine();
        end = component.endLine();
        sourceFile = component.sourceFile();
        code = component.code();
        comment = component.comment();
    }

    public Component() {
    }

    public ArrayList<String> children() {
        return children;
    }

    public String startLine() {
        return start;
    }

    public void setStartLine(final String startLine) {
        start = startLine;
    }

    public String endLine() {
        return end;
    }

    public void setEndLine(final String endLine) {
        end = endLine;
    }

    public String uniqueName() {
        if (packageName != null && !packageName.isEmpty()) {
            return packageName + "." + componentName;
        } else {
            return componentName;
        }
    }

    public String name() {

        return name;
    }

    public void insertChildComponent(final String childComponentName) {
        if (!children.contains(childComponentName)) {
            children.add(childComponentName);
        }
    }

    public void addImports(final String importStmt) {
        imports.add(importStmt);
    }

    public String declarationTypeSnippet() {
        return declarationTypeSnippet;
    }

    public List<ComponentInvocation> invocations() {
        return invocations;
    }

    public void insertComponentInvocation(final ComponentInvocation ref) {
        for (final ComponentInvocation invocation : invocations) {
            if (invocation.invokedComponent().equals(ref.invokedComponent()) && invocation.getClass().isInstance(ref)) {
                invocation.insertLineNums(ref.lines());
            }
        }
        invocations.add(ref);
    }

    public List<String> imports() {
        return imports;
    }

    public String componentName() {
        return componentName;
    }

    public void setDeclarationTypeSnippet(
            final String componentDeclarationTypeFragment) {
        declarationTypeSnippet = componentDeclarationTypeFragment;
    }

    public void setExternalTypeReferences(
            final ArrayList<ComponentInvocation> externalReferences) {
        invocations = externalReferences;
    }

    public void setImports(final ArrayList<String> importStatements) {
        imports = importStatements;
    }

    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

    public List<String> modifiers() {
        return modifiers;
    }

    public void insertAccessModifier(final String modifier) {
        if (OOPSourceModelConstants.getJavaAccessModifierMap().containsValue(
                modifier)) {
            modifiers.add(modifier.toLowerCase());
        }
    }

    public ComponentType componentType() {
        return type;
    }

    public void setComponentType(final ComponentType componentType) {
        type = componentType;
    }

    public String packageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String value() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String comment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String parentUniqueName() {

        if (!type.isMethodComponent()) {
            if (uniqueName().contains(".")) {
                final int lastPeriod = uniqueName().lastIndexOf(".");
                final String currParentClassName = uniqueName().substring(0,
                        lastPeriod);
                return currParentClassName;
            } else {
                throw new IllegalArgumentException(
                        "Cannot get parent of component: " + uniqueName());
            }
        } else {
            final int lastOpeningBracket = uniqueName().lastIndexOf("(");
            final String methodComponentUniqueNameMinusParamters = uniqueName()
                    .substring(0, lastOpeningBracket);
            final int lastPeriod = methodComponentUniqueNameMinusParamters
                    .lastIndexOf(".");
            final String currParentClassName = methodComponentUniqueNameMinusParamters
                    .substring(0, lastPeriod);
            return currParentClassName;
        }
    }

    public void insertComponentInvocations(
            final ArrayList<ComponentInvocation> externalClassTypeReferenceList) {
        for (final ComponentInvocation typeRef : externalClassTypeReferenceList) {
            insertComponentInvocation(typeRef);
        }
    }

    public List<ComponentInvocation> componentInvocations(
            final ComponentInvocations type) {
        final List<ComponentInvocation> tmpInvocations = new ArrayList<ComponentInvocation>();
        for (final ComponentInvocation compInvocation : invocations) {
            if (type.getMatchingClass().isAssignableFrom(
                    compInvocation.getClass())) {
                tmpInvocations.add(compInvocation);
            }
        }
        return tmpInvocations;
    }

    public List<ComponentInvocation> componentInvocations() {
        return invocations;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public String sourceFile() {
        return sourceFile;
    }

    public void setSourceFilePath(final String sourceFilePath) {
        sourceFile = sourceFilePath;
    }

    public void setAccessModifiers(List<String> list) {
        for (final String modifier : list) {
            modifiers.add(modifier.toLowerCase());
        }

    }

    public String code() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
