package com.clarity.invocation.sources;

import com.clarity.DynamicResource;
import com.clarity.invocation.ComponentInvocation;

/**
 * A template used to create Component Invocation Objects.
 *
 * @author Muntazir Fadhel
 */
public interface InvocationSource extends DynamicResource {

    /**
     * Generates a component invocation.
     */
    ComponentInvocation createComponentInvocation() throws Exception;

    /**
     * Returns the name of the class corresponding to the component invocation.
     */
    String componentInvocationClassName();
}
