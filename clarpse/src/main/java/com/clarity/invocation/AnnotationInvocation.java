package com.clarity.invocation;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AnnotationInvocation extends ComponentInvocation {

    private final List<Entry<String, HashMap<String, String>>> annotations = new ArrayList<Map.Entry<String, HashMap<String, String>>>();

    public List<Entry<String, HashMap<String, String>>> annotations() {
        return annotations;
    }

    public AnnotationInvocation(final String invocationComponentName, final int lineNum,
            final SimpleEntry<String, HashMap<String, String>> annotation) {
        super(invocationComponentName, lineNum);
        annotations.add(annotation);
    }

    @Override
    public boolean empty() {
        return (super.empty() && annotations.isEmpty());
    }
}
