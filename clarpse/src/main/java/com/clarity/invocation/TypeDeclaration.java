package com.clarity.invocation;

import java.io.Serializable;
import java.util.Set;

public class TypeDeclaration extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 7304258760520469246L;
    public final String       type             = "declaration";

    public TypeDeclaration(final String invocationComponentName, final int lineNum) {
        super(invocationComponentName, lineNum);
    }

    public TypeDeclaration() {
        super();
    }

    public TypeDeclaration(String invokedComponent, Set<Integer> lines) {
        super(invokedComponent, lines);
    }

    @Override
    public Object clone() {
        return new TypeDeclaration(invokedComponent(), lines());
    }
}
