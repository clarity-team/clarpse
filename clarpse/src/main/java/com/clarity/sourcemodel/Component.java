package com.clarity.sourcemodel;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Representation of the individual code level components (classes,
 * methodComponents, fieldComponents, etc..) that are used to create a code
 * base.
 *
 * @author Muntazir Fadhel
 */

public final class Component implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Value of the component if applicable.
     */
    private String value;
    private String packageName;
    /**
     * Short name.
     */
    private String name;
    private int line;
    private Component parent;
    private String comment = "";
    /**
     * Source file path from which the component was derived.
     */
    private String sourceFile;
    @JsonInclude(Include.NON_EMPTY)
    private List<String> imports = new ArrayList<String>();
    @JsonInclude(Include.NON_EMPTY)
    private Set<String> modifiers = new LinkedHashSet<String>();
    private ComponentType type;
    @JsonInclude(Include.NON_EMPTY)
    private Set<ComponentInvocation> invocations = new LinkedHashSet<ComponentInvocation>();
    /**
     * Formed by chaining parent components' names separated by a period.
     * <p>
     * Eg) ClassA -> MethodB -> varC = "ClassA.MethodB.varC"
     */
    private String componentName;
    /**
     * List of all child components.
     */
    @JsonInclude(Include.NON_EMPTY)
    private final ArrayList<String> children = new ArrayList<String>();
    private String codeFragment;

    public Component(final Component component) {
        modifiers = component.modifiers();
        type = component.componentType();
        codeFragment = component.codeFragment();
        invocations = component.invocations();
        imports = component.imports();
        componentName = component.componentName();
        packageName = component.packageName();
        value = component.value();
        parent = component.parent();
        sourceFile = component.sourceFile();
        comment = component.comment();
        line = component.line();
    }

    public Component() {
    }

    public ArrayList<String> children() {
        return children;
    }

    public String uniqueName() {
        if (packageName != null && !packageName.isEmpty()) {
            return packageName + "." + componentName;
        } else {
            return componentName;
        }
    }

    public Component parent() {
        return (this.parent);
    }

    public void setParent(Component parent) {
        this.parent = parent;
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

    public String codeFragment() {
        return codeFragment;
    }

    public Set<ComponentInvocation> invocations() {
        return invocations;
    }

    public void insertComponentInvocation(final ComponentInvocation ref) {
        for (final ComponentInvocation invocation : invocations) {
            if (invocation.invokedComponent().equals(ref.invokedComponent()) && invocation.getClass().isInstance(ref)) {
                return;
            }
        }
        invocations.add(ref);
    }

    public int line() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public List<String> imports() {
        return imports;
    }

    public String componentName() {
        return componentName;
    }

    public void setCodeFragment(final String componentDeclarationTypeFragment) {
        codeFragment = componentDeclarationTypeFragment;
    }

    public void setExternalTypeReferences(final Set<ComponentInvocation> externalReferences) {
        invocations = externalReferences;
    }

    public void setImports(final ArrayList<String> currentImports) {
        imports = currentImports;
    }

    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

    public Set<String> modifiers() {
        return modifiers;
    }

    public void insertAccessModifier(final String modifier) {
        if (OOPSourceModelConstants.getJavaAccessModifierMap().containsValue(modifier)) {
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

        final int lastOpeningBracket = uniqueName().indexOf("(");

        if (lastOpeningBracket == -1 || !type.isMethodComponent()) {
            if (uniqueName().contains(".")) {
                final int lastPeriod = uniqueName().lastIndexOf(".");
                final String currParentClassName = uniqueName().substring(0, lastPeriod);
                return currParentClassName;
            } else {
                throw new IllegalArgumentException("Cannot get parent of component: " + uniqueName());
            }
        } else {
            final String methodComponentUniqueNameMinusParamters = uniqueName().substring(0, lastOpeningBracket);
            final int lastPeriod = methodComponentUniqueNameMinusParamters.lastIndexOf(".");
            final String currParentClassName = methodComponentUniqueNameMinusParamters.substring(0, lastPeriod);
            return currParentClassName;
        }
    }

    public void insertComponentInvocations(final ArrayList<ComponentInvocation> externalClassTypeReferenceList) {
        for (final ComponentInvocation typeRef : externalClassTypeReferenceList) {
            insertComponentInvocation(typeRef);
        }
    }

    public List<ComponentInvocation> componentInvocations(final ComponentInvocations type) {
        final List<ComponentInvocation> tmpInvocations = new ArrayList<ComponentInvocation>();
        for (final ComponentInvocation compInvocation : invocations) {
            if (type.getMatchingClass().isAssignableFrom(compInvocation.getClass())) {
                tmpInvocations.add(compInvocation);
            }
        }
        return tmpInvocations;
    }

    public Set<ComponentInvocation> componentInvocations() {
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

    @Override
    public boolean equals(Object o) {
        return (((Component) o).uniqueName().equals(this.uniqueName()));
    }

    @Override
    public int hashCode() {
        return this.uniqueName().hashCode();
    }
}
