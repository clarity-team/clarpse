package com.clarity.invocation;

import java.io.Serializable;
import java.util.Set;

public class TypeImplementation extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 7807962152246261233L;
    public final String       type             = "implementation";

    public TypeImplementation(String invocationComponentName, int lineNum) {
        super(invocationComponentName, lineNum);
    }

    public TypeImplementation() {
        super();
    }

    public TypeImplementation(String invokedComponent, Set<Integer> lines) {
        super(invokedComponent, lines);
    }

    @Override
    public Object clone() {
        return new TypeImplementation(invokedComponent(), lines());
    }

}
