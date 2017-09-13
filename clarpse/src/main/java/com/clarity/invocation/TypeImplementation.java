package com.clarity.invocation;

import java.io.Serializable;

public class TypeImplementation extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 7807962152246261233L;
    public final String type = "implementation";

    public TypeImplementation() {
        super();
    }

    public TypeImplementation(String invokedComponent) {
        super(invokedComponent);
    }

    @Override
    public Object clone() {
        return new TypeImplementation(invokedComponent());
    }

}
