package com.clarity.reference;

import java.io.Serializable;

public class SimpleTypeReference extends ComponentReference implements Serializable {

    private static final long serialVersionUID = 7304258760520469246L;
    public final String type = "simple";

    public SimpleTypeReference(final String invocationComponentName) {
        super(invocationComponentName);
    }

    public SimpleTypeReference() {
        super();
    }

    @Override
    public Object clone() {
        return new SimpleTypeReference(invokedComponent());
    }
}
