package com.clarity.invocation.sources;

import java.util.ArrayList;
import java.util.List;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.EmptyInvocation;
import com.clarity.invocation.MethodInvocation;
import com.clarity.invocation.TypeExtension;
import com.clarity.parser.AntlrParser;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;

public class MethodInvocationSourceImpl extends MethodInvocationSource {

    public MethodInvocationSourceImpl(String containingClassName, String methodName, int lineNum, int numParams) {
        super(containingClassName, methodName, lineNum, numParams);
    }

    @Override
    public ComponentInvocation createComponentInvocation() throws Exception {

        // check class for method
        Component cmp = AntlrParser.getSrcModel().getComponent(containingClassComponentName());
        List<Component> methodComponentMatches = new ArrayList<Component>();
        List<ComponentInvocation> extendedTypes;
        if (cmp != null) {
            methodComponentMatches = getMethodComponentMatches(cmp, methodName(), numParams());
            extendedTypes = cmp.componentInvocations(TypeExtension.class);
            while (methodComponentMatches.isEmpty() && !extendedTypes.isEmpty()) {
                setContainingClassComponentName(extendedTypes.get(0).invokedComponent());
                // check for method in super class if available right now
                cmp = AntlrParser.getSrcModel().getComponent(containingClassComponentName());
                if (cmp != null) {
                    methodComponentMatches = getMethodComponentMatches(cmp, methodName(), numParams());

                    extendedTypes = cmp.componentInvocations(TypeExtension.class);
                } else {
                    extendedTypes.clear();
                }
            }
        }
        // don't support method overloading yet..
        if (methodComponentMatches.isEmpty() || methodComponentMatches.size() > 1) {
            return new EmptyInvocation("", 0);
        } else {
            return new MethodInvocation(methodComponentMatches.get(0).getUniqueName(), lineNum());
        }
    }

    private List<Component> getMethodComponentMatches(Component containingClassCmp, String methodName, int numParams) {

        final List<Component> componentMatches = new ArrayList<Component>();
        for (final String child : containingClassCmp.children()) {
            final Component methodCmp = AntlrParser.getSrcModel().getComponent(child);
            if (methodCmp.getComponentType() == ComponentType.CONSTRUCTOR_COMPONENT
                    || methodCmp.getComponentType() == ComponentType.METHOD_COMPONENT) {
                if (methodCmp.getName().equals(methodName)) {
                    int methodParams = 0;
                    for (final String methodChildCmp : methodCmp.children()) {
                        if (AntlrParser.getSrcModel().getComponent(methodChildCmp).getComponentType() == ComponentType.METHOD_PARAMETER_COMPONENT) {
                            methodParams++;
                        }
                    }
                    if (methodParams == numParams) {
                        componentMatches.add(methodCmp);
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
