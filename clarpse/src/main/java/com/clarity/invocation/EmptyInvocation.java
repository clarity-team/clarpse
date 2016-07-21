package com.clarity.invocation;

public class EmptyInvocation extends ComponentInvocation {

    public EmptyInvocation(String invocationComponentName, int lineNum) {
        super(invocationComponentName, lineNum);
    }

    @Override
    public boolean empty() {
        return true;
    }
}
