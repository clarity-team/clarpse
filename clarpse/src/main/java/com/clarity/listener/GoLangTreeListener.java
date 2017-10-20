package com.clarity.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.clarity.antlr.golang.GolangBaseListener;
import com.clarity.antlr.golang.GolangParser.FieldDeclContext;
import com.clarity.antlr.golang.GolangParser.ImportSpecContext;
import com.clarity.antlr.golang.GolangParser.PackageClauseContext;
import com.clarity.antlr.golang.GolangParser.SourceFileContext;
import com.clarity.antlr.golang.GolangParser.StructTypeContext;
import com.clarity.antlr.golang.GolangParser.TypeSpecContext;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.TypeDeclaration;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;

import edu.emory.mathcs.backport.java.util.Arrays;

public class GoLangTreeListener extends GolangBaseListener {

    private final Stack<Component> componentStack = new Stack<Component>();
    private final ArrayList<String> currentImports = new ArrayList<String>();
    private String currentPkg = "";
    private final OOPSourceCodeModel srcModel;
    private final Map<String, String> currentImportsMap = new HashMap<String, String>();
    private final RawFile file;
    private String lastParsedTypeIdentifier = null;

    /**
     * @param srcModel
     *            Source model to populate from the parsing of the given code base.
     * @param file
     *            The path of the source file being parsed.
     */
    public GoLangTreeListener(final OOPSourceCodeModel srcModel, final RawFile file) {
        this.srcModel = srcModel;
        this.file = file;
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
    private Component createComponent(ComponentType componentType) {
        final Component newCmp = new Component();
        newCmp.setPackageName(currentPkg);
        newCmp.setComponentType(componentType);
        newCmp.setSourceFilePath(file.name());
        return newCmp;
    }

    @Override
    public void enterSourceFile(SourceFileContext ctx) {
        super.enterSourceFile(ctx);
    }

    @Override
    public final void enterPackageClause(PackageClauseContext ctx) {
        currentPkg = ctx.IDENTIFIER().getText();
        currentImports.clear();
        if (!componentStack.isEmpty()) {
            System.out.println(
                    "Clarpse GoLang Listener found new package declaration while component stack not empty! component stack size is: "
                            + componentStack.size());
        }
    }

    @Override
    public final void enterImportSpec(ImportSpecContext ctx) {
        final String fullImportName = ctx.importPath().getText().replaceAll("/", ".").replaceAll("\"", "");
        currentImports.add(fullImportName);
        final String shortImportName;
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getText() != null) {
            shortImportName = ctx.IDENTIFIER().getText();
        } else {
            if (ctx.importPath().getText().contains("/")) {
                shortImportName = ctx.importPath().getText().substring(ctx.importPath().getText().lastIndexOf("/"));
            } else {
                shortImportName = ctx.importPath().getText().replaceAll("\"", "");
            }
        }
        currentImportsMap.put(shortImportName, fullImportName);
        if (currentPkg.isEmpty()) {
            currentPkg = "";
        }
    }

    @Override
    public final void enterStructType(StructTypeContext ctx) {
        Component cmp = createComponent(ComponentType.CLASS);
        String comments = AntlrUtil.goLangComments(ctx.getStart().getLine(), Arrays.asList(file.content().split("\n")));
        cmp.setComment(comments);
        cmp.setName(lastParsedTypeIdentifier);
        cmp.setComponentName(generateComponentName(lastParsedTypeIdentifier));
        cmp.setImports(currentImports);
        pointParentsToGivenChild(cmp);
        cmp.insertAccessModifier(visibility(cmp.name()));
        componentStack.push(cmp);
    }

    private String visibility(String goLangComponentName) {
        return Character.isUpperCase(goLangComponentName.charAt(0)) ? "public" : "private";
    }

    @Override
    public final void exitStructType(StructTypeContext ctx) {
        completeComponent();
    }

    @Override
    public final void enterFieldDecl(FieldDeclContext ctx) {
        if (ctx.identifierList() != null && !ctx.identifierList().isEmpty()) {
            List<Component> fieldVars = new ArrayList<Component>();
            for (TerminalNode token : ctx.identifierList().IDENTIFIER()) {
                Component cmp = createComponent(ComponentType.FIELD);
                cmp.setName(token.getText());
                cmp.setComponentName(generateComponentName(token.getText()));
                cmp.insertAccessModifier(visibility(cmp.name()));
                pointParentsToGivenChild(cmp);
                cmp.insertComponentInvocation(new TypeDeclaration(ctx.type().getText()));
                fieldVars.add(cmp);
            }
        }
    }

    /**
     * Tries to return the full, unique type name of the given type.
     */

    private String resolveType(final String type) {

        if (currentImportsMap.containsKey(type)) {
            return currentImportsMap.get(type);
        }
        if (type.contains(".")) {
            return type;
        }
        if (!currentPkg.isEmpty()) {
            return currentPkg + "." + type;
        } else {
            return type;
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

    @Override
    public final void enterTypeSpec(TypeSpecContext ctx) {
        lastParsedTypeIdentifier = ctx.IDENTIFIER().getText();
    }
}
