package com.clarity.listener;

import com.clarity.antlr.php.PHP;
import com.clarity.antlr.php.PHPBaseListener;
import com.clarity.compiler.RawFile;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.*;


public class PHPTreeListener extends PHPBaseListener {

    private final Stack<Component> componentStack = new Stack<Component>();
    private final ArrayList<String> currentImports = new ArrayList<String>();
    private String currentPkg = "";
    private final OOPSourceCodeModel srcModel;
    private final Map<String, String> currentImportsMap = new HashMap<String, String>();
    private final RawFile file;

    public PHPTreeListener(final OOPSourceCodeModel srcModel, RawFile filetoProcess) {
        this.srcModel = srcModel;
        this.file = filetoProcess;
    }

    private void completeComponent(Component completedComponent) {
        // include the processed component's invocations into its parent
        // components
        for (final Component parentCmp : componentStack) {
            final Iterator<ComponentInvocation> invocationIterator = completedComponent.invocations().iterator();
            while (invocationIterator.hasNext()) {

                // Bubble up component invocations (except type implementations and
                // extensions) to the parent components on the stack
                ComponentInvocation invocation = invocationIterator.next();
                if (!(invocation instanceof TypeExtension || invocation instanceof TypeImplementation)) {
                    parentCmp.insertComponentInvocation(invocation);
                }
            }
        }
        srcModel.insertComponent(completedComponent);
    }

    private void popAndCompleteComponent() {
        if (!componentStack.isEmpty()) {
            completeComponent(componentStack.pop());
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

    /**
     * Creates a new component based on the given ParseRuleContext.
     */
    private Component createComponent(ComponentType componentType, int line) {
        final Component newCmp = new Component();
        newCmp.setPackageName(currentPkg);
        newCmp.setComponentType(componentType);
        newCmp.setSourceFilePath(file.name());
        newCmp.setLine(line);
        return newCmp;
    }

    @Override
    public final void enterNamespaceDeclaration(PHP.NamespaceDeclarationContext ctx) {
        if (ctx.namespaceNameList() != null) {
            currentPkg = ctx.namespaceNameList().getText().replaceAll("\\\\", ".");
            System.out.println(currentPkg);
        }
    }

    @Override
    public final void enterClassDeclaration(PHP.ClassDeclarationContext ctx) {
        if (componentStackContainsMethod()) {
            // skip over structs defined within methods.
            exitClassDeclaration(ctx);
        } else {

            if (ctx.Interface() != null) {
                Component cmp = createComponent(ComponentType.STRUCT, ctx.getStart().getLine());
                String comments = AntlrUtil.goLangComments(ctx.getStart().getLine(),
                        Arrays.asList(file.content().split("\n")));
                cmp.setComment(comments);
                cmp.setName(ctx.identifier().getText());
                cmp.setComponentName(generateComponentName(cmp.name()));
                cmp.setImports(currentImports);
                pointParentsToGivenChild(cmp);
                componentStack.push(cmp);
            } else {
                Component cmp = createComponent(ComponentType.INTERFACE, ctx.getStart().getLine());
                String comments = AntlrUtil.goLangComments(ctx.getStart().getLine(),
                        Arrays.asList(file.content().split("\n")));
                cmp.setComment(comments);
                cmp.setName(ctx.identifier().getText());
                cmp.setComponentName(generateComponentName(ctx.identifier().getText()));
                cmp.setImports(currentImports);
                pointParentsToGivenChild(cmp);
                componentStack.push(cmp);
            }
        }
    }

    private boolean componentStackContainsMethod() {
        for (Component cmp : componentStack) {
            if (cmp.componentType().isMethodComponent()) {
                return true;
            }
        }
        return false;
    }
    /**
     * Tries to return the full, unique type name of the given type.
     */
    private String resolveType(final String type) {

        if (currentImportsMap.containsKey(type)) {
            return currentImportsMap.get(type).replaceAll("/", ".");
        }

        final Iterator<?> it = currentImportsMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes") final Map.Entry pair = (Map.Entry) it.next();
            if (type.startsWith((String) pair.getKey())) {
                return (((String) pair.getValue()).replaceAll("/", ".")) + "." + type.replace(pair.getKey() + ".", "");
            }
        }
        if (type.contains(".")) {
            return type;
        } else {
            // must be a local type...
            return this.currentPkg + "." + type;
        }
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