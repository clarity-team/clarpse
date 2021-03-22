package com.hadii.clarpse.sourcemodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hadii.clarpse.reference.ComponentReference;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants.ComponentType;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants.TypeReferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Representation of the individual code level components (classes,
 * methodComponents, fieldComponents, etc..) that are used to create a code
 * base.
 */
public final class Component implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonInclude(Include.NON_EMPTY)
    private final ArrayList<String> children = new ArrayList<String>();
    private String value;
    private String packageName;
    private int cyclo;
    private String name;
    private String comment = "";
    private String sourceFile;
    @JsonInclude(Include.NON_EMPTY)
    private List<String> imports = new ArrayList<String>();
    @JsonInclude(Include.NON_EMPTY)
    private Set<String> modifiers = new LinkedHashSet<String>();
    private ComponentType type;
    @JsonInclude(Include.NON_EMPTY)
    private Set<ComponentReference> references = new LinkedHashSet<ComponentReference>();
    private String componentName;
    private int codeHash;
    private String codeFragment;

    public Component(final Component component) throws Exception {
        modifiers = component.modifiers();
        type = component.componentType();
        codeFragment = component.codeFragment();
        imports = component.imports();
        componentName = component.componentName();
        packageName = component.packageName();
        value = component.value();
        sourceFile = component.sourceFile();
        comment = component.comment();
        codeHash = component.codeHash();
        children.addAll(component.children);
        for (final ComponentReference ref : component.references()) {
            references.add((ComponentReference) ref.clone());
        }
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

    public int cyclo() {
        return cyclo;
    }

    public void setCyclo(final int cyclo) {
        this.cyclo = cyclo;
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

    public Set<ComponentReference> references() {
        return references;
    }

    public void insertComponentRef(final ComponentReference ref) {
        references.add(ref);
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

    public void setExternalTypeReferences(final Set<ComponentReference> externalReferences) {
        references = externalReferences;
    }

    public void setImports(final List<String> currentImports) {
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
            final String methodComponentName = uniqueName().substring(0, lastOpeningBracket);
            final int lastPeriod = methodComponentName.lastIndexOf(".");
            final String currParentClassName = methodComponentName.substring(0, lastPeriod);
            return currParentClassName;
        }
    }

    public void insertComponentReferences(final List<ComponentReference> externalClassTypeReferenceList) {
        for (final ComponentReference typeRef : externalClassTypeReferenceList) {
            insertComponentRef(typeRef);
        }
    }

    public List<ComponentReference> references(final TypeReferences type) {
        final List<ComponentReference> tmpReferences = new ArrayList<ComponentReference>();
        for (final ComponentReference compReference : references) {
            if (type.getMatchingClass().isAssignableFrom(compReference.getClass())) {
                tmpReferences.add(compReference);
            }
        }
        return tmpReferences;
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

    public void setAccessModifiers(final List<String> list) {
        for (final String modifier : list) {
            modifiers.add(modifier.toLowerCase());
        }
    }

    @Override
    public boolean equals(final Object o) {
        return (((Component) o).uniqueName().equals(uniqueName()));
    }

    @Override
    public int hashCode() {
        return uniqueName().hashCode();
    }

    public int codeHash() {
        return codeHash;
    }

    public void setCodeHash(int codeHash) {
        this.codeHash = codeHash;
    }
}
