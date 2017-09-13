package com.clarity.invocation;

import java.io.Serializable;

public class InvalidInvocation extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = -3058881761749807208L;
    public final String type = "empty";

    public InvalidInvocation(String invocationComponentName) {
        super(invocationComponentName);
    }

    @Override
    public boolean empty() {
        return true;
    }

    public InvalidInvocation() {
        super();
    }

    @Override
    public Object clone() {
        return new InvalidInvocation(invokedComponent());
    }
}
