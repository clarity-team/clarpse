package com.clarity.invocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.clarity.EmptyResource;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents an invocation of another component in the code base.
 *
 * @author Muntazir Fadhel
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
    @Type(value = AnnotationInvocation.class, name = "annotation"),
    @Type(value = EmptyInvocation.class, name = "empty"),
    @Type(value = MethodInvocation.class, name = "method"),
    @Type(value = ThrownException.class, name = "exception"),
    @Type(value = TypeDeclaration.class, name = "declaration"),
    @Type(value = TypeExtension.class, name = "extension"),
    @Type(value = TypeImplementation.class, name = "implementation"),
    @Type(value = TypeParameter.class, name = "typeparameter") })
public abstract class ComponentInvocation implements EmptyResource, Serializable, Cloneable {

    private static final long serialVersionUID = -242718695900611890L;
    private String invokedComponent = "";
    private List<Integer> invocationLines = new ArrayList<Integer>();

    public ComponentInvocation(final String invocationComponentName, final int lineNum) {
        invokedComponent = invocationComponentName;
        invocationLines.add(lineNum);
    }

    public ComponentInvocation(final ComponentInvocation invocation) {
        for (final Integer lineNum : invocation.lines()) {
            invocationLines.add(lineNum);
        }
        invokedComponent = invocation.invokedComponent();
    }

    public ComponentInvocation() {
    }

    public ComponentInvocation(String invokedComponent2, List<Integer> lines) {
        invokedComponent = invokedComponent2;
        invocationLines = lines;
    }

    public String invokedComponent() {
        return invokedComponent;
    }

    public void insertLineNums(final List<Integer> list) {
        for (final Integer lineNum : list) {
            if (!this.lines().contains(lineNum)) {
                this.lines().add(lineNum);
            }
        }
    }

    public List<Integer> lines() {
        return invocationLines;
    }

    @Override
    public
    boolean empty() {
        return (invokedComponent.isEmpty() && invocationLines.isEmpty());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        return super.clone();
    }
}
