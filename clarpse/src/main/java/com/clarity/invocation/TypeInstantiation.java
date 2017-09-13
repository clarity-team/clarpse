package com.clarity.invocation;

import java.io.Serializable;

public class TypeInstantiation extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 7253490170234016131L;
    public final String type = "instantiation";

    public TypeInstantiation() {
        super();
    }

    public TypeInstantiation(String invokedComponent) {
        super(invokedComponent);
    }

    @Override
    public Object clone() {
        return new TypeInstantiation(invokedComponent());
    }

}
