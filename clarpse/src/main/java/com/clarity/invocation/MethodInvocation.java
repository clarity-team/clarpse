package com.clarity.invocation;

import java.io.Serializable;
import java.util.Set;

public class MethodInvocation extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 6518046913196400190L;
    public final String       type             = "method";

    public MethodInvocation(final String invocationComponentName, final int lineNum) {

        super(invocationComponentName, lineNum);
    }

    public MethodInvocation() {
        super();
    }

    public MethodInvocation(String invokedComponent, Set<Integer> lines) {
        super(invokedComponent, lines);
    }

    @Override
    public Object clone() {
        return new MethodInvocation(invokedComponent(), lines());
    }
}
