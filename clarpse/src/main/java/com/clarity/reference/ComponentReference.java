package com.clarity.reference;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Represents a reference to another component in the code base.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = SimpleTypeReference.class, name = "simple"),
        @Type(value = TypeExtensionReference.class, name = "extension"),
        @Type(value = TypeImplementationReference.class, name = "implementation")})
public abstract class ComponentReference implements Serializable, Cloneable {

    private static final long serialVersionUID = -242718695900611890L;
    private String invokedComponent = "";

    public ComponentReference(final String invocationComponentName, final int lineNum) {
        invokedComponent = invocationComponentName;
    }

    public ComponentReference(final ComponentReference invocation) {
        invokedComponent = invocation.invokedComponent();
    }

    public ComponentReference() {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + invokedComponent;
    }

    public ComponentReference(String invokedComponent2) {
        invokedComponent = invokedComponent2;
    }

    public String invokedComponent() {
        return invokedComponent;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
