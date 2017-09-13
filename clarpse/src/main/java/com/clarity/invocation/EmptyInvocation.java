package com.clarity.invocation;

import java.io.Serializable;

public class EmptyInvocation extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = -3058881761749807208L;
    public final String type = "empty";

    public EmptyInvocation(String invocationComponentName) {
        super(invocationComponentName);
    }

    @Override
    public boolean empty() {
        return true;
    }

    public EmptyInvocation() {
        super();
    }

    @Override
    public Object clone() {
        return new EmptyInvocation(invokedComponent());
    }
}
