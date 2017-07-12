package com.clarity.listener;

import java.util.Iterator;
import java.util.Stack;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.NodeTraversal.AbstractPreOrderCallback;
import com.google.javascript.rhino.Node;

/**
 * Listener for JavaScript ES6+ source files, based on google's closure
 * compiler.
 */
public class JavaScriptListener extends AbstractPreOrderCallback {

    private final Stack<Component> componentStack = new Stack<Component>();
    private OOPSourceCodeModel srcModel;
    private RawFile file;

    public JavaScriptListener(final OOPSourceCodeModel srcModel, final RawFile file) {
        this.srcModel = srcModel;
        this.file = file;
        System.out.println("\nParsing New File: " + file.name() + "\n");
    }

    @Override
    public void visit(NodeTraversal t, Node n, Node parent) {
        if (n.isMemberFunctionDef()) {
            System.out.println("Found member function def: " + n.getString());
        }
        if (n.isClass()) {
            System.out.println("Exiting " + n.getFirstChild().getString());
            completeComponent();
        }
    }

    private void completeComponent() {
        if (!componentStack.isEmpty()) {
            final Component completedCmp = componentStack.pop();
            // include the processed component's invocations into its parent
            // components
            for (final Component parentCmp : componentStack) {

                final Iterator<ComponentInvocation> invocationIterator = completedCmp.invocations().iterator();
                while (invocationIterator.hasNext()) {

                    // We do not want to bubble up type implementations and
                    // extensions
                    // to the parent component because a child class for example
                    // could
                    // extend its containing class component. Without this check
                    // this would
                    // cause the parent class to have a type extension to itself
                    // which will
                    // cause problems down the line.
                    ComponentInvocation invocation = invocationIterator.next();
                    if (!(invocation instanceof TypeExtension || invocation instanceof TypeImplementation)) {
                        parentCmp.insertComponentInvocation(invocation);
                    }
                }
            }
            srcModel.insertComponent(completedCmp);
        }
    }

    @Override
    public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
        final Component cmp;

        if (n.isClass()) {
            System.out.println("Entering the " + n.getFirstChild().getString() + " class");
            cmp = createComponent(n, ComponentType.CLASS);
            cmp.setComponentName(generateComponentName(n.getFirstChild().getString()));
            cmp.setName(n.getFirstChild().getString());
            pointParentsToGivenChild(cmp);
            if (n.hasMoreThanOneChild()) {
                // this class extends another class
                System.out.println("this class extends " + n.getSecondChild().getString());
                cmp.insertComponentInvocation(new TypeExtension(n.getSecondChild().getString()));
            }
            componentStack.push(cmp);
        }
        return true;
    }

    /**
     * Generates appropriate name for the component. Uses the current stack of
     * parents components as prefixes to the name.
     */
    private String generateComponentName(final String identifier) {
        String componentName = "";

        if (!componentStack.isEmpty()) {
            final Component completedCmp = componentStack.peek();
            componentName = completedCmp.componentName() + "." + identifier;
        } else {
            componentName = identifier;
        }
        return componentName;
    }

    /**
     * Creates a new component.
     */
    private Component createComponent(Node node, ComponentType componentType) {
        final Component newCmp = new Component();
        newCmp.setComponentType(componentType);
        if (node.getJSDocInfo() != null) {
            newCmp.setComment(node.getJSDocInfo().getBlockDescription());
        }
        newCmp.setStartLine(String.valueOf(node.getLineno()));
        newCmp.setSourceFilePath(file.name());
        return newCmp;
    }

    private void pointParentsToGivenChild(Component childCmp) {

        if (!componentStack.isEmpty()) {
            final String parentName = childCmp.parentUniqueName();
            for (int i = componentStack.size() - 1; i >= 0; i--) {
                if (componentStack.get(i).uniqueName().equals(parentName)) {
                    componentStack.get(i).insertChildComponent(childCmp.uniqueName());
                }
            }
        }
    }
}
