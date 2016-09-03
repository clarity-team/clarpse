package com.clarity.invocation;

import java.io.Serializable;
import java.util.List;

public class ThrownException extends ComponentInvocation  implements Serializable{

    private static final long serialVersionUID = 3346563314076095662L;
    public final String type = "exception";
    public ThrownException(String invocationComponentName, int lineNum) {
        super(invocationComponentName, lineNum);
    }

    public ThrownException() {
        super();
    }

    public ThrownException(String invokedComponent, List<Integer> lines) {
        super(invokedComponent, lines);
    }

    @Override
    public Object clone() {
        return new ThrownException(invokedComponent(), lines());
    }

}
