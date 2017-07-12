package com.clarity.invocation;

import java.io.Serializable;

public class TypeDeclaration extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 7304258760520469246L;
    public final String type = "declaration";

    public TypeDeclaration(final String invocationComponentName) {
        super(invocationComponentName);
    }

    public TypeDeclaration() {
        super();
    }

    @Override
    public Object clone() {
        return new TypeDeclaration(invokedComponent());
    }
}
