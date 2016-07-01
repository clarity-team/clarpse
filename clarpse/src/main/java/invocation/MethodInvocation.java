package invocation;

import java.util.List;

public class MethodInvocation extends ComponentInvocation {

    private final List<String> parameterComponents;
    private final String returnComponentName;

    public MethodInvocation(final String invocationComponentName, final String returnComponentName,
            final List<String> parameterComponents, final int lineNum) {

        super(invocationComponentName, lineNum);
        this.returnComponentName = returnComponentName;
        this.parameterComponents = parameterComponents;
    }

    public String returnComponentName() {
        return returnComponentName;
    }

    public List<String> parameterComponents() {
        return parameterComponents;
    }
}
