package com.clarity.invocation;

import java.io.Serializable;

public class ThrownException extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 3346563314076095662L;
    public final String type = "exception";

    public ThrownException(String invocationComponentName) {
        super(invocationComponentName);
    }

    public ThrownException() {
        super();
    }

    @Override
    public Object clone() {
        return new ThrownException(invokedComponent());
    }

}
