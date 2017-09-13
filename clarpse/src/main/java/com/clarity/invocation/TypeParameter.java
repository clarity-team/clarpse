package com.clarity.invocation;

import java.io.Serializable;

public class TypeParameter extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = -6321838812633289752L;
    public final String type = "typeparameter";

    public TypeParameter() {
        super();
    }

    public TypeParameter(String invokedComponent) {
        super(invokedComponent);
    }

    @Override
    public Object clone() {
        return new TypeParameter(invokedComponent());
    }
}