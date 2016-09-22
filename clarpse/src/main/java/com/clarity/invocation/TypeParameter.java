package com.clarity.invocation;

import java.io.Serializable;
import java.util.List;

public class TypeParameter extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = -6321838812633289752L;
    public final String type = "typeparameter";

    public TypeParameter(String invokedClass, int lineNumber) {
        super(invokedClass, lineNumber);
    }

    public TypeParameter() {
        super();
    }

    public TypeParameter(String invokedComponent, List<Integer> lines) {
        super(invokedComponent, lines);
    }

    @Override
    public Object clone() {
        return new TypeParameter(invokedComponent(), lines());
    }
}