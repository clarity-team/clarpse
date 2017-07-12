package com.clarity.invocation;

import java.io.Serializable;

public class MethodInvocation extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 6518046913196400190L;
    public final String type = "method";

    public MethodInvocation(final String invocationComponentName) {

        super(invocationComponentName);
    }

    public MethodInvocation() {
        super();
    }

    @Override
    public Object clone() {
        return new MethodInvocation(invokedComponent());
    }
}
