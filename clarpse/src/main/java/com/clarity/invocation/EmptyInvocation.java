package com.clarity.invocation;

import java.io.Serializable;
import java.util.Set;

public class EmptyInvocation extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = -3058881761749807208L;
    public final String       type             = "empty";

    public EmptyInvocation(String invocationComponentName, int lineNum) {
        super(invocationComponentName, lineNum);
    }

    @Override
    public boolean empty() {
        return true;
    }

    public EmptyInvocation() {
        super();
    }

    public EmptyInvocation(String invokedComponent, Set<Integer> lines) {
        super(invokedComponent, lines);
    }

    @Override
    public Object clone() {
        return new EmptyInvocation(invokedComponent(), lines());
    }
}
