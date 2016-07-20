package com.clarity.invocation.sources;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.sourcemodel.Component;

/**
 * Binds the created component invocation to a component.
 *
 * @author Muntazir Fadhel
 */
public class BindedInvocationSource implements InvocationSource {

    private final InvocationSource original;
    private final Component bindingComponent;

    public BindedInvocationSource(InvocationSource original, Component bindingComponent) {

        this.original = original;
        this.bindingComponent = bindingComponent;
    }

    @Override
    public ComponentInvocation createComponentInvocation() throws Exception {

        final ComponentInvocation invocation = original.createComponentInvocation();
        if (!invocation.empty()) {
            bindingComponent.insertComponentInvocation(invocation);
        }
        return invocation;
    }

    @Override
    public void update(Object updateData) {
        original.update(updateData);
    }

    @Override
    public String componentInvocationClassName() {
        return original.componentInvocationClassName();
    }
}
