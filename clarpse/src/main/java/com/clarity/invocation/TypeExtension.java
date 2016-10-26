package com.clarity.invocation;

import java.io.Serializable;
import java.util.Set;

public final class TypeExtension extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 6641497827060470449L;
    public final String       type             = "extension";

    public TypeExtension(final String invocationComponentName, final int lineNum) {
        super(invocationComponentName, lineNum);
    }

    public TypeExtension() {
        super();
    }

    public TypeExtension(String invokedComponent, Set<Integer> lines) {
        super(invokedComponent, lines);
    }

    @Override
    public Object clone() {
        return new TypeExtension(invokedComponent(), lines());
    }
}
