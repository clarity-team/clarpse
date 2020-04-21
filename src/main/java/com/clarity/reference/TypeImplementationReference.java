package com.clarity.reference;

import java.io.Serializable;

public class TypeImplementationReference extends ComponentReference implements Serializable {

    private static final long serialVersionUID = 7807962152246261233L;
    public final String type = "implementation";

    public TypeImplementationReference() {
        super();
    }

    public TypeImplementationReference(String invokedComponent) {
        super(invokedComponent);
    }

    @Override
    public Object clone() {
        return new TypeImplementationReference(invokedComponent());
    }

}
