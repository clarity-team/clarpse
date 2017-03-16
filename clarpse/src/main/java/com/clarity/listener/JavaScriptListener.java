package com.clarity.listener;

import java.util.List;
import java.util.Map;

import com.clarity.invocation.sources.InvocationSourceChain;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.NodeTraversal.AbstractPostOrderCallback;
import com.google.javascript.rhino.Node;

/**
 * Supports ES6+, based on google's closure compiler.
 *
 */
public class JavaScriptListener extends AbstractPostOrderCallback {

    private OOPSourceCodeModel srcModel;
    private RawFile file;
    private Map<String, List<InvocationSourceChain>> blockedInvocationSources;

    public JavaScriptListener(final OOPSourceCodeModel srcModel, final RawFile file,
            Map<String, List<InvocationSourceChain>> blockedInvocationSources) {
        this.srcModel = srcModel;
        this.file = file;
        this.blockedInvocationSources = blockedInvocationSources;
    }

    @Override
    public void visit(NodeTraversal t, Node n, Node parent) {
        if (n.isClass()) {
            System.out.println(n.getFirstChild().getString());
        }
        if (n.isMemberFunctionDef() || n.isGetterDef() || n.isSetterDef()) {
            System.out.println(n.getString());
        }
        if (n.isFunction()) {
            System.out.println(n.getFirstChild().getString());
        }
    }
}
