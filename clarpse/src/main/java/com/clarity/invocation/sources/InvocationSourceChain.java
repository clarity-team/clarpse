package com.clarity.invocation.sources;

import java.util.List;

import com.clarity.invocation.ComponentInvocation;

/**
 * Chain of invocation sources that depend on one another. Each invocation
 * source depends on the one before it to complete before it can process.
 *
 * @author Muntazir Fadhel
 */
public abstract class InvocationSourceChain {

    /**
     * List of invocation sources in the chain.
     */
    private final List<InvocationSource> invocationSources;
    private int                          currentSourceIndex = 0;

    public InvocationSourceChain(List<InvocationSource> invocationSources) {
        this.invocationSources = invocationSources;
        if (invocationSources.size() < 1) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Process the invocation source in the chain. Note it may not be possible
     * to process a given invocation source at the moment, either because the
     * required class has not been parsed yet or the invocation source is not
     * satisfiable.
     */
    public void process() {
        if (currentSourceIndex < (invocationSources.size())) {
            try {
                final ComponentInvocation invocation = invocationSources.get(currentSourceIndex)
                        .createComponentInvocation();
                if (!invocation.empty()) {

                    currentSourceIndex++;
                    if (currentSourceIndex < (invocationSources.size())) {
                        updateDependantInvocationSource(invocation, invocationSources.get(currentSourceIndex));
                        process();
                    }
                } else {
                    prepareInvocationSource(invocationSources.get(currentSourceIndex));
                }
            } catch (final Exception e) {
                // ignore...
                e.printStackTrace();
            }
        }
    }

    abstract void prepareInvocationSource(InvocationSource invocationSource);

    abstract void updateDependantInvocationSource(ComponentInvocation createdInvocation,
            InvocationSource dependantSource);

}