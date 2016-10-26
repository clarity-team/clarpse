package com.clarity.invocation.sources;

import java.util.List;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.sourcemodel.Component;

/**
 * Binds the created component invocation to a component.
 *
 * @author Muntazir Fadhel
 */
public class BindedInvocationSource implements InvocationSource {

    private final InvocationSource original;
    private final List<Component>  bindingComponents;

    public BindedInvocationSource(InvocationSource original, List<Component> bindingComponents) {

        this.original = original;
        this.bindingComponents = bindingComponents;
    }

    @Override
    public ComponentInvocation createComponentInvocation() throws Exception {

        ComponentInvocation originalInvocation = original.createComponentInvocation();
        bindingComponents.forEach(bindingComponent -> bindingComponent.insertComponentInvocation(originalInvocation));
        return originalInvocation;
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
