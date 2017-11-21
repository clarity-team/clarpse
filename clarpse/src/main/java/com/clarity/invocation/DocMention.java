package com.clarity.invocation;

import java.io.Serializable;

public class DocMention extends ComponentInvocation implements Serializable {

    private static final long serialVersionUID = 7807962152246261233L;
    public final String type = "implementation";

    public DocMention() {
        super();
    }

    public DocMention(String invokedComponent) {
        super(invokedComponent);
    }

    @Override
    public Object clone() {
        return new DocMention(invokedComponent());
    }

}
