package com.clarity.listener;

import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.NodeTraversal.AbstractPreOrderCallback;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

/**
 * Listener for JavaScript ES6+ source files, based on google's closure
 * compiler.
 */
public class JavaScriptListener extends AbstractPreOrderCallback {

    private OOPSourceCodeModel srcModel;
    private RawFile file;

    public JavaScriptListener(final OOPSourceCodeModel srcModel, final RawFile file) {
        this.srcModel = srcModel;
        this.file = file;
    }

    @Override
    public void visit(NodeTraversal t, Node n, Node parent) {

    }

    @Override
    public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
        System.out.println(Token.name(n.getType()));
        if (n.isMemberFunctionDef()) {
            System.out.println("Found member function def: " + n.getString());
        }
        if (n.isName()) {
            System.out.println("Found name token: " + n.getString());
        }
        if (n.isAssign()) {
            // System.out.println(n.getString());
        }
        return true;
    }
}
