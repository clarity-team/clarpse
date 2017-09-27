package com.clarity.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.TypeDeclaration;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.invocation.TypeInstantiation;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.NodeTraversal.Callback;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

/**
 * Listener for JavaScript ES6+ source files, based on google's closure
 * compiler.
 */
public class JavaScriptListener implements Callback {

    private final Stack<Component> componentStack = new Stack<Component>();
    private OOPSourceCodeModel srcModel;
    private RawFile file;

    public JavaScriptListener(final OOPSourceCodeModel srcModel, final RawFile file) {
        this.srcModel = srcModel;
        this.file = file;
        System.out.println("\nParsing New JS File: " + file.name() + "\n");
    }

    @Override
    public void visit(NodeTraversal t, Node n, Node parent) {

        String baseCmpName = null;

        if (n.isClass()) {
            baseCmpName = n.getFirstC hild().getString();
        } else if (n.isMemberFunctionDef()) {
            baseCmpName = n.getString();
        } else if (n.isGetterDef()) {
            baseCmpName = "get_" + n.getString();
        } else if (n.isSetterDef()) {
            baseCmpName = "set_" + n.getString();
        }

        if (baseCmpName != null) {
            while (!componentStack.isEmpty() && (componentStack.peek().componentName().contains(baseCmpName + ".")
                    || componentStack.peek().componentName().endsWith(baseCmpName))) {
                completeComponent();
            }
        }
    }

    @Override
    public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
        try {
            return shouldTraverse(n, parent);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean shouldTraverse(Node n, Node parent) throws Exception {

        Component cmp;

        if (n.isClass()) {
            System.out.println("Entering the " + n.getFirstChild().getString() + " class");
            cmp = createComponent(ComponentType.CLASS, n);
            cmp.setComponentName(generateComponentName(n.getFirstChild().getString()));
            cmp.setName(generateComponentName(n.getFirstChild().getString()));
            pointParentsToGivenChild(cmp);
            if (n.getSecondChild().isName()) {
                // this class extends another class
                System.out.println("this class extends " + n.getSecondChild().getString());
                cmp.insertComponentInvocation(new TypeExtension(n.getSecondChild().getString()));
            }
            componentStack.push(cmp);
        } else if (n.isMemberFunctionDef()) {
            if (n.getString() != null && n.getString().equals("constructor")) {
                System.out.println("Found constructor");
                cmp = createComponent(ComponentType.CONSTRUCTOR, n);
                cmp.setComponentName(generateComponentName("constructor"));
                cmp.setName("constructor");
                pointParentsToGivenChild(cmp);
                componentStack.push(cmp);
            } else {
                System.out.println("Found instance method: " + n.getString());
                cmp = createComponent(ComponentType.METHOD, n);
                cmp.setComponentName(generateComponentName(n.getString()));
                cmp.setName(n.getString());
                if (n.isStaticMember()) {
                    cmp.insertAccessModifier("static");
                }
                cmp.insertAccessModifier("public");
                pointParentsToGivenChild(cmp);
                componentStack.push(cmp);
            }
        } else if (n.isGetterDef()) {
            String cmpName = "get_" + n.getString();
            System.out.println("Found getter definition: " + cmpName);
            cmp = createComponent(ComponentType.METHOD, n);
            cmp.setComponentName(generateComponentName(cmpName));
            cmp.setName(cmpName);
            pointParentsToGivenChild(cmp);
            if (n.isStaticMember()) {
                cmp.insertAccessModifier("static");
            }
            cmp.insertAccessModifier("public");
            componentStack.push(cmp);
        } else if (n.isSetterDef()) {
            String cmpName = "set_" + n.getString();
            System.out.println("Found setter definition: " + cmpName);
            cmp = createComponent(ComponentType.METHOD, n);
            cmp.setComponentName(generateComponentName(cmpName));
            cmp.setName(cmpName);
            cmp.insertAccessModifier("public");
            if (n.isStaticMember()) {
                cmp.insertAccessModifier("static");
            }
            pointParentsToGivenChild(cmp);
            componentStack.push(cmp);
        } else if (n.isParamList()) {
            List<Component> generatedParamComponents = new ArrayList<Component>();
            // determine if the top of the component stack is a method
            // or a constructor...
            boolean isConstructor = true;
            if (componentStack.peek().componentType() != ComponentType.CONSTRUCTOR) {
                isConstructor = false;
            }
            for (Node param : n.children()) {
                String paramName = param.getString();
                System.out.println("Found Parameter: " + paramName);
                if (isConstructor) {
                    cmp = createComponent(ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT, n);
                } else {
                    cmp = createComponent(ComponentType.METHOD_PARAMETER_COMPONENT, n);
                }
                cmp.setComponentName(generateComponentName(paramName));
                cmp.setName(paramName);
                pointParentsToGivenChild(cmp);
                generatedParamComponents.add(cmp);
            }
            for (Component paramCmp : generatedParamComponents) {
                componentStack.push(paramCmp);
                completeComponent();
            }
        } else if (n.isAssign() && n.getFirstChild().hasChildren() && n.getFirstChild().getFirstChild().isThis()
                && newestMethodComponent().componentType() == ComponentType.CONSTRUCTOR) {
            String fieldVarname = n.getFirstChild().getSecondChild().getString();
            System.out.println("Found field variable: " + fieldVarname);
            cmp = createComponent(ComponentType.FIELD, n);
            cmp.setComponentName(generateComponentName(fieldVarname, ComponentType.FIELD));
            cmp.setName(fieldVarname);
            cmp.insertAccessModifier("private");
            processVariableAssignment(cmp, n.getSecondChild());
            try {
                pointParentsToGivenChild(cmp);
            } catch (Exception e) {
                e.printStackTrace();
                // don't add this component to the stack..
                return true;
            }
            componentStack.push(cmp);
            completeComponent();

        } else if (!componentStack.isEmpty() && (n.isVar() || n.isLet())
                && (newestMethodComponent().componentType() == ComponentType.METHOD
                        || newestMethodComponent().componentType() == ComponentType.CONSTRUCTOR)) {
            String localVarName = n.getFirstChild().getString();
            System.out.println("Found local variable: " + localVarName);
            cmp = createComponent(ComponentType.LOCAL, n);
            cmp.setComponentName(generateComponentName(localVarName));
            cmp.setName(localVarName);
            processVariableAssignment(cmp, n.getFirstChild().getFirstChild());
            pointParentsToGivenChild(cmp);
            componentStack.push(cmp);
            completeComponent();
        } else if (!componentStack.isEmpty() && n.isNew()) {
            processVariableAssignment(newestMethodComponent(), n);
        } else if (n.isName() && !n.getString().isEmpty()
                && (NodeUtil.isImportedName(n) || Character.isUpperCase(n.getString().codePointAt(0)))
                && !componentStack.isEmpty()) {
            Component latestCmp = componentStack.pop();
            latestCmp.insertComponentInvocation(new TypeDeclaration(n.getString()));
            componentStack.push(latestCmp);
        } else if (n.isGetProp() && n.getFirstChild().isName() && !n.getFirstChild().getString().isEmpty()
                && (NodeUtil.isImportedName(n.getFirstChild())
                        || Character.isUpperCase(n.getFirstChild().getString().codePointAt(0)))
                && !componentStack.isEmpty()) {
            Component latestCmp = componentStack.pop();
            latestCmp.insertComponentInvocation(new TypeDeclaration(n.getFirstChild().getString()));
            componentStack.push(latestCmp);
        }
        return true;
    }

    private void processVariableAssignment(Component cmp, Node assignmentNode) {
        if (NodeUtil.isLiteralValue(assignmentNode, false)) {
            cmp.setValue(NodeUtil.getStringValue(assignmentNode));
            cmp.setDeclarationTypeSnippet(declarationSnippet(assignmentNode.getToken()));
        } else if (assignmentNode.hasChildren() && assignmentNode.isNew()
                && (assignmentNode.getFirstChild().isName() || assignmentNode.getFirstChild().isGetProp())) {
            String invokedType;
            if (assignmentNode.getFirstChild().isGetProp()) {
                invokedType = assignmentNode.getFirstChild().getFirstChild().getString();
                cmp.insertComponentInvocation(new TypeDeclaration(invokedType));
            } else {
                invokedType = assignmentNode.getFirstChild().getString();
                cmp.insertComponentInvocation(new TypeInstantiation(invokedType));
            }
            cmp.setDeclarationTypeSnippet(invokedType);
        }
    }

    private void completeComponent() {
        if (!componentStack.isEmpty()) {
            final Component completedCmp = componentStack.pop();
            System.out.println("Completing component: " + completedCmp.uniqueName());
            // bubble up the completing component's invocations to it's parent
            // components
            // that are currently on the stack
            for (final Component parentCmp : componentStack) {
                final Iterator<ComponentInvocation> invocationIterator = completedCmp.invocations().iterator();
                while (invocationIterator.hasNext()) {
                    ComponentInvocation invocation = invocationIterator.next();
                    if (!(invocation instanceof TypeExtension || invocation instanceof TypeImplementation)) {
                        // if the invocation is not a class extension or
                        // implementation,
                        // bubble it up!
                        parentCmp.insertComponentInvocation(invocation);
                    }
                }
            }
            srcModel.insertComponent(completedCmp);
        }
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

    private String generateComponentName(final String identifier, ComponentType componentType) throws Exception {

        if (componentType == ComponentType.FIELD) {
            return newestBaseComponent().componentName() + "." + identifier;
        } else {
            return generateComponentName(identifier);
        }
    }

    /**
     * Creates a new component representing the given node object, see
     * {@link Component}.
     */
    private Component createComponent(ComponentType componentType, Node n) {
        final Component newCmp = new Component();
        newCmp.setComponentType(componentType);
        newCmp.setSourceFilePath(file.name());
        if (NodeUtil.getBestJSDocInfo(n) != null) {
            String doc = NodeUtil.getBestJSDocInfo(n).getOriginalCommentString();
            if (doc != null) {
                newCmp.setComment(doc);
            }
        }
        return newCmp;
    }

    private void pointParentsToGivenChild(Component childCmp) throws Exception {

        if (childCmp.componentType() == ComponentType.FIELD) {
            newestBaseComponent().insertChildComponent(childCmp.componentName());
        } else {
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

    /**
     * Retrieves the most recently inserted base component on the stack.
     */
    private Component newestBaseComponent() throws Exception {

        Component latestBaseCmp = null;
        for (Component cmp : componentStack) {
            if (cmp.componentType().isBaseComponent()) {
                latestBaseCmp = cmp;
            }
        }

        if (latestBaseCmp != null) {
            return latestBaseCmp;
        } else {
            throw new Exception("There are no base components on the stack right now!");
        }
    }

    /**
     * Retrieves the most recently inserted base component on the stack.
     */
    private Component newestMethodComponent() throws Exception {

        Component latestMethodCmp = null;
        for (Component cmp : componentStack) {
            if (cmp.componentType().isMethodComponent()) {
                latestMethodCmp = cmp;
            }
        }

        if (latestMethodCmp != null) {
            return latestMethodCmp;
        } else {
            throw new Exception("There are no method components on the stack right now!");
        }
    }

    static String declarationSnippet(Token token) {
        switch (token) {
        case TRUE:
        case FALSE:
            return "Boolean";
        case STRING:
        case STRING_TYPE:
        case STRING_KEY:
            return "String";
        case NUMBER:
            return "Number";
        case ARRAYLIT:
        case ARRAY_PATTERN:
        case ARRAY_TYPE:
            return "Array";
        case OBJECTLIT:
        case OBJECT_PATTERN:
            return "Object";
        default:
            break;
        }
        return null;
    }
}
