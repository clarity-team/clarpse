package com.clarity.invocation;

import java.util.ArrayList;

import com.clarity.EmptyResource;

/**
 * Represents an invocation of another component in the code base.
 *
 * @author Muntazir Fadhel
 */
public abstract class ComponentInvocation implements EmptyResource {

    private final String invokedComponent;
    private final ArrayList<Integer> invocationLines = new ArrayList<Integer>();

    public ComponentInvocation(final String invocationComponentName, final int lineNum) {
        invokedComponent = invocationComponentName;
        invocationLines.add(lineNum);
    }

    public ComponentInvocation(final ComponentInvocation invocation) {
        for (final Integer lineNum : invocation.lines()) {
            invocationLines.add(lineNum);
        }
        invokedComponent = invocation.invokedComponent();
    }

    public String invokedComponent() {
        return invokedComponent;
    }

    public void insertLineNum(final int invocationLineNums) {
        if (!invocationLines.contains(invocationLineNums)) {
            invocationLines.add(invocationLineNums);
        }
    }

    public ArrayList<Integer> lines() {
        return invocationLines;
    }

    @Override
    public
    boolean empty() {
        return (invokedComponent.isEmpty() && invocationLines.isEmpty());
    }
}
