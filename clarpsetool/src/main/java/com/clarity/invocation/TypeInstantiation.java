package com.clarity.invocation;

import java.io.Serializable;
import java.util.List;

public class TypeInstantiation extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 7253490170234016131L;
    public final String type = "instantiation";

    public TypeInstantiation(String invocationComponentName, int lineNum) {
        super(invocationComponentName, lineNum);
    }

    public TypeInstantiation() {
        super();
    }

    public TypeInstantiation(String invokedComponent, List<Integer> lines) {
        super(invokedComponent, lines);
    }

    @Override
    public Object clone() {
        return new TypeInstantiation(invokedComponent(), lines());
    }

}
