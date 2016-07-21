package com.clarity.invocation.sources;

import com.clarity.DynamicResource;
import com.clarity.invocation.ComponentInvocation;

/**
 * Used to create ComponentInvocation Objects.
 *
 * @author Muntazir Fadhel
 */
public interface InvocationSource extends DynamicResource {

    /**
     * Generates a component invocation.
     */
    ComponentInvocation createComponentInvocation() throws Exception;

    String componentInvocationClassName();
}
