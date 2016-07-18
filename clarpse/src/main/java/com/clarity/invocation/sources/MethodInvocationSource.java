package com.clarity.invocation.sources;



public abstract class MethodInvocationSource implements InvocationSource {

    private final int numParams;
    private String containingClassComponentName;
    private final String methodName;
    private final int lineNum;

    public MethodInvocationSource(String containingClassName, String methodName, int lineNum,
            int numParams) {
        this.numParams = numParams;
        this.lineNum = lineNum;
        containingClassComponentName = containingClassName;
        this.methodName = methodName;
    }

    public int numParams() {
        return numParams;
    }

    public String containingClassComponentName() {
        return containingClassComponentName;
    }

    public void setContainingClassComponentName(String name) {
        containingClassComponentName = name;
    }

    public String methodName() {
        return methodName;
    }

    public int lineNum() {
        return lineNum;
    }
}