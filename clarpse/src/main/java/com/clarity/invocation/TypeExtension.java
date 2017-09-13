package com.clarity.invocation;

import java.io.Serializable;

public final class TypeExtension extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 6641497827060470449L;
    public final String type = "extension";

    public TypeExtension() {
        super();
    }

    public TypeExtension(String invokedComponent) {
        super(invokedComponent);
    }

    @Override
    public Object clone() {
        return new TypeExtension(invokedComponent());
    }
}
