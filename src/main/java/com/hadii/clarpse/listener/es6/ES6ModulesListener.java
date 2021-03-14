package com.hadii.clarpse.listener.es6;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.NodeTraversal.Callback;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.rhino.Node;
import com.hadii.clarpse.compiler.ClarpseES6Compiler;
import com.hadii.clarpse.compiler.ProjectFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Listener for JavaScript ES6+ import, export and class declaration statements.
 */
public class ES6ModulesListener implements Callback {

    private static final Logger logger = LogManager.getLogger(ES6ModulesListener.class);
    private final ES6Module module;

    public ES6ModulesListener(final ProjectFile projectFile, ModulesMap modulesMap) throws Exception {
        this.module = new ES6Module(projectFile.path());
        modulesMap.insertModule(module);
        logger.info("Parsing module: " + this.module.modulePath());
    }

    @Override
    public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
        try {
            return shouldTraverse(n);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void visit(NodeTraversal nodeTraversal, Node node, Node node1) {
    }

    private boolean shouldTraverse(Node n) {
        if (n.isExport()) {
            this.module.insertModuleExportNode(n);
        } else if (n.isImport()) {
            this.module.insertModuleImportNode(n);
        }  else if (n.isClass()) {
            if (NodeUtil.isNameDeclaration(n.getParent().getParent())) {
                if (n.getParent().isName()) {
                    this.module.insertDeclaredClass(n.getParent().getString());
                }
            } else if (n.hasChildren() && n.getFirstChild().isName()) {
                this.module.insertDeclaredClass(n.getFirstChild().getString());
            }
        }
        return true;
    }
}
