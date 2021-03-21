package com.hadii.clarpse.reference;

import java.io.Serializable;

public final class TypeExtensionReference extends ComponentReference implements Serializable {

    private static final long serialVersionUID = 6641497827060470449L;
    public final String type = "extension";

    @Override
    public int priority() {
        return 1;
    }

    public TypeExtensionReference() {
        super();
    }

    public TypeExtensionReference(String invokedComponent) {
        super(invokedComponent);
    }

    @Override
    public Object clone() {
        return new TypeExtensionReference(invokedComponent());
    }
}
