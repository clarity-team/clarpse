package com.clarity.invocation.sources;

import java.util.ArrayList;
import java.util.List;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.parser.AntlrParser;
import com.clarity.sourcemodel.Component;

/**
 * Chain of dependent method source invocations.
 *
 * @author Muntazir Fadhel
 */
public final class MethodInvocationSourceChain extends InvocationSourceChain {

    public MethodInvocationSourceChain(List<InvocationSource> invocationSources) {
        super(invocationSources);
    }

    @Override
    void prepareInvocationSource(InvocationSource invocationSource) {

        final String requiredComponentName = invocationSource.componentInvocationClassName();
        List<InvocationSourceChain> blockedSources = AntlrParser.getBlockedInvocationSources().get(
                requiredComponentName);
        if (blockedSources == null) {
            blockedSources = new ArrayList<InvocationSourceChain>();
        }
        blockedSources.add(this);
        AntlrParser.getBlockedInvocationSources().put(requiredComponentName, blockedSources);
    }

    @Override
    void updateDependantInvocationSource(ComponentInvocation createdInvocation, InvocationSource dependantSource) {

        final Component methodCmp = AntlrParser.getSrcModel().getComponent(createdInvocation.invokedComponent());
        final String containingClassCmpName = methodCmp.value();
        dependantSource.update(containingClassCmpName);
    }

}
