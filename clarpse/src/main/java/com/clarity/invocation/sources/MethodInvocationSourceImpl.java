package com.clarity.invocation.sources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.EmptyInvocation;
import com.clarity.invocation.MethodInvocation;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;

public class MethodInvocationSourceImpl extends MethodInvocationSource {

    private final OOPSourceCodeModel srcModel;

    public MethodInvocationSourceImpl(String containingClassName, String methodName, int lineNum, int numParams,
            OOPSourceCodeModel srcModel, Map<String, List<InvocationSourceChain>> blockedInvocationSources) {
        super(containingClassName, methodName, lineNum, numParams);
        this.srcModel = srcModel;
    }

    @Override
    public ComponentInvocation createComponentInvocation() throws Exception {

        // check the current containing class for a method matching the current
        // invocation source
        Component cmp = srcModel.getComponent(containingClassComponentName());
        List<Component> methodComponentMatches = new ArrayList<Component>();
        List<ComponentInvocation> extendedTypes;
        if (cmp != null) {
            // get the method components that match the current invocation
            // source
            methodComponentMatches = getMethodComponentMatches(cmp, methodName(), numParams());
            extendedTypes = cmp.componentInvocations(ComponentInvocations.EXTENSION);
            // continue looping through parent classes untill a match is found
            while (methodComponentMatches.isEmpty() && !extendedTypes.isEmpty()) {
                setContainingClassComponentName(extendedTypes.get(0).invokedComponent());
                // check for method in super class if available right now
                cmp = srcModel.getComponent(containingClassComponentName());
                if (cmp != null) {
                    methodComponentMatches = getMethodComponentMatches(cmp, methodName(), numParams());
                    extendedTypes = cmp.componentInvocations(ComponentInvocations.EXTENSION);
                } else {
                    // no more super classes left, exit
                    extendedTypes.clear();
                }
            }
        }
        // Could not find the component that corresponds to the component
        // invocation,
        // return an empty invocation...
        if (methodComponentMatches.isEmpty() || methodComponentMatches.size() > 1) {
            return new EmptyInvocation("", 0);
        } else {
            return new MethodInvocation(methodComponentMatches.get(0).uniqueName(), lineNum());
        }
    }

    /**
     * Given a method name and the number of params, this method returns a list
     * of matching method components in the given component.
     */
    private List<Component> getMethodComponentMatches(Component containingClassCmp, String methodName, int numParams) {

        final List<Component> componentMatches = new ArrayList<Component>();
        for (final String child : containingClassCmp.children()) {
            final Component methodCmp = srcModel.getComponent(child);
            if (methodCmp != null) {
                if (methodCmp.componentType() == ComponentType.CONSTRUCTOR
                        || methodCmp.componentType() == ComponentType.METHOD) {
                    if (methodCmp.name().equals(methodName)) {
                        // figure out the number of parameters for this method..
                        int methodParams = 0;
                        for (final String methodChildCmp : methodCmp.children()) {
                            final Component methodChildComponent = srcModel.getComponent(methodChildCmp);
                            if (methodChildComponent != null && methodChildComponent
                                    .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT) {
                                methodParams++;
                            }
                        }
                        if (methodParams == numParams) {
                            componentMatches.add(methodCmp);
                        }
                    }

                }
            }
        }
        return componentMatches;
    }

    @Override
    public String toString() {
        return ("containingClassName: " + containingClassComponentName() + ", methodName: " + methodName()
                + ", lineNumber: " + lineNum() + ", numParams: " + numParams());
    }

    @Override
    public void update(Object updateData) {
        final String containingClassName = (String) updateData;
        setContainingClassComponentName(containingClassName);
    }

    @Override
    public String componentInvocationClassName() {
        return containingClassComponentName();
    }
}
