package com.clarity.invocation.sources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Chain of dependent method source invocations.
 *
 * @author Muntazir Fadhel
 */
public final class MethodInvocationSourceChain extends InvocationSourceChain {

    private final OOPSourceCodeModel srcModel;
    private final Map<String, List<InvocationSourceChain>> blockedInvocations;

    public MethodInvocationSourceChain(List<InvocationSource> invocationSources, OOPSourceCodeModel srcModel,
            Map<String, List<InvocationSourceChain>> blockedInvocationSources) {
        super(invocationSources);
        blockedInvocations = blockedInvocationSources;
        this.srcModel = srcModel;
    }

    @Override
    void prepareInvocationSource(InvocationSource invocationSource) {

        final String requiredComponentName = invocationSource.componentInvocationClassName();
        List<InvocationSourceChain> blockedSources = blockedInvocations.get(requiredComponentName);
        if (blockedSources == null) {
            blockedSources = new ArrayList<InvocationSourceChain>();
        }
        blockedSources.add(this);
        blockedInvocations.put(requiredComponentName, blockedSources);
    }

    @Override
    void updateDependantInvocationSource(ComponentInvocation createdInvocation, InvocationSource dependantSource) {

        final Component methodCmp = srcModel.getComponent(createdInvocation.invokedComponent());
        final String containingClassCmpName = methodCmp.value();
        dependantSource.update(containingClassCmpName);
    }
}
