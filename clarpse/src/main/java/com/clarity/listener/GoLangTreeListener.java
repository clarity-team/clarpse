package com.clarity.listener;

import com.clarity.antlr.golang.GolangBaseListener;
import com.clarity.antlr.golang.GolangParser.ExpressionContext;
import com.clarity.antlr.golang.GolangParser.FieldDeclContext;
import com.clarity.antlr.golang.GolangParser.FunctionDeclContext;
import com.clarity.antlr.golang.GolangParser.ImportSpecContext;
import com.clarity.antlr.golang.GolangParser.InterfaceTypeContext;
import com.clarity.antlr.golang.GolangParser.MethodDeclContext;
import com.clarity.antlr.golang.GolangParser.MethodSpecContext;
import com.clarity.antlr.golang.GolangParser.PackageClauseContext;
import com.clarity.antlr.golang.GolangParser.ParameterDeclContext;
import com.clarity.antlr.golang.GolangParser.ParametersContext;
import com.clarity.antlr.golang.GolangParser.ReceiverContext;
import com.clarity.antlr.golang.GolangParser.ResultContext;
import com.clarity.antlr.golang.GolangParser.SourceFileContext;
import com.clarity.antlr.golang.GolangParser.StructTypeContext;
import com.clarity.antlr.golang.GolangParser.TypeNameContext;
import com.clarity.antlr.golang.GolangParser.TypeSpecContext;
import com.clarity.antlr.golang.GolangParser.VarSpecContext;
import com.clarity.compiler.RawFile;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.TypeDeclaration;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GoLangTreeListener extends GolangBaseListener {

    private final Stack<Component> componentStack = new Stack<Component>();
    private final ArrayList<String> currentImports = new ArrayList<String>();
    private String currentPkg = "";
    private final OOPSourceCodeModel srcModel;
    private final Map<String, String> currentImportsMap = new HashMap<String, String>();
    private final RawFile file;
    private String lastParsedTypeIdentifier = null;
    /**
     * List of all type names in the project.
     */
    private List<String> projectFileTypes;
    private boolean inReceiverContext = false;
    private boolean inResultContext = false;
    private List<Map.Entry<String, Component>> structWaitingList;

    public GoLangTreeListener(final OOPSourceCodeModel srcModel, List<String> projectFileTypes, RawFile filetoProcess, List<Map.Entry<String, Component>> structWaitingList) {
        this.srcModel = srcModel;
        this.file = filetoProcess;
        this.projectFileTypes = projectFileTypes;
        this.structWaitingList = structWaitingList;
    }

    private void completeComponent(Component completedComponent) {


        // To handle the case where struct methods are parsed before their parent struct were,
        // check if the completed component is such a parent and update accordingly.
        structWaitingList.forEach(entry -> {
            if (entry.getKey().equals(completedComponent.uniqueName())) {
                udpateStructChild(completedComponent, entry.getValue());
            }
        });

        // include the processed component's invocations into its parent
        // components
        for (final Component parentCmp : componentStack) {
            final Iterator<ComponentInvocation> invocationIterator = completedComponent.invocations().iterator();
            while (invocationIterator.hasNext()) {

                // We do not want to bubble up type implementations and
                // extensions to the parent component because a child
                // class for example could extend its containing class
                // component. Without this check this would
                // cause the parent class to have a type extension to itself
                // which will cause problems down the line.
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
    public void enterSourceFile(SourceFileContext ctx) {
        super.enterSourceFile(ctx);
    }

    @Override
    public final void enterPackageClause(PackageClauseContext ctx) {
        currentPkg = ctx.IDENTIFIER().getText();
        if (this.file.name().contains("/")) {
            String modFileName = this.file.name().substring(0, this.file.name().lastIndexOf("/"));
            for (String s : projectFileTypes) {
                if (modFileName.endsWith(s)) {
                    currentPkg = s;
                    break;
                }
            }
        }
        currentPkg = currentPkg.replaceAll("/", ".");
        currentImports.clear();
        if (!componentStack.isEmpty()) {
            System.out.println(
                    "Clarpse GoLang Listener found new package declaration while component stack not empty! component stack size is: "
                            + componentStack.size());
        }
    }

    @Override
    public final void enterImportSpec(ImportSpecContext ctx) {
        String fullImportName = ctx.importPath().getText().replaceAll("\"", "");
        for (String s : projectFileTypes) {
            if (this.file.name().contains("app_only_env")) {
                System.out.println("");
            }
            if (s.endsWith(fullImportName) || fullImportName.endsWith(s)) {
                fullImportName = s;
                break;
            }
        }

        currentImports.add(fullImportName.replaceAll("/", "."));
        final String shortImportName;
        if (ctx.IDENTIFIER() != null && ctx.IDENTIFIER().getText() != null) {
            shortImportName = ctx.IDENTIFIER().getText();
        } else {
            if (ctx.importPath().getText().contains("/")) {
                shortImportName = ctx.importPath().getText().substring(ctx.importPath().getText().lastIndexOf("/") + 1)
                        .replace("\"", "");
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
        if (lastParsedTypeIdentifier != null) {
            if (componentStackContainsMethod()) {
                // skip over structs defined within methods.
                exitStructType(ctx);
            } else {
                Component cmp = createComponent(ComponentType.STRUCT, ctx.getStart().getLine());
                String comments = AntlrUtil.goLangComments(ctx.getStart().getLine(),
                        Arrays.asList(file.content().split("\n")));
                cmp.setComment(comments);
                cmp.setName(lastParsedTypeIdentifier);
                cmp.setComponentName(generateComponentName(lastParsedTypeIdentifier));
                cmp.setImports(currentImports);
                pointParentsToGivenChild(cmp);
                cmp.insertAccessModifier(visibility(cmp.name()));
                componentStack.push(cmp);
            }
        }
    }

    @Override
    public final void enterInterfaceType(InterfaceTypeContext ctx) {
        if (!componentStackContainsMethod()) {
            if (lastParsedTypeIdentifier != null) {
                Component cmp = createComponent(ComponentType.INTERFACE, ctx.getStart().getLine());
                String comments = AntlrUtil.goLangComments(ctx.getStart().getLine(),
                        Arrays.asList(file.content().split("\n")));
                cmp.setComment(comments);
                cmp.setName(lastParsedTypeIdentifier);
                cmp.setComponentName(generateComponentName(lastParsedTypeIdentifier));
                cmp.setImports(currentImports);
                pointParentsToGivenChild(cmp);
                cmp.insertAccessModifier(visibility(cmp.name()));
                componentStack.push(cmp);
            }
        } else {
            exitInterfaceType(ctx);
        }
    }

    @Override
    public final void enterMethodSpec(MethodSpecContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            Component cmp = createComponent(ComponentType.METHOD, ctx.getStart().getLine());
            String comments = AntlrUtil.goLangComments(ctx.getStart().getLine(),
                    Arrays.asList(file.content().split("\n")));
            cmp.setComment(comments);
            cmp.setName(ctx.IDENTIFIER().getText());
            cmp.setCodeFragment(cmp.name() + "(");
            cmp.insertAccessModifier(visibility(cmp.name()));
            if (ctx.signature().parameters() != null) {
                setCodeFragmentFromParameters(ctx.signature().parameters(), cmp);
            }
            if (ctx.signature().result() != null) {
                processResult(ctx.signature().result(), cmp);
            }
            cmp.setName(cmp.codeFragment());
            cmp.setComponentName(generateComponentName(cmp.name()));
            pointParentsToGivenChild(cmp);
            componentStack.push(cmp);
            processParameters(ctx.signature().parameters(), cmp);

        } else if (ctx.typeName() != null) {
            insertExtensionIntoStackBaseComponent(ctx.typeName().getText());
        }
    }

    /**
     * Searches the children of the given context for a context of the given clazz
     * type and returns its Text Value. If there are multiple relevant child nodes,
     * we will return the text value for one of them at random.
     */
    public String getChildContextText(RuleContext ctx) {

        if (ctx == null) {
            return "";
        }
        if (ctx instanceof TypeNameContext) {
            return ctx.getText();
        }

        String s = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!(ctx.getChild(i) instanceof TerminalNodeImpl)) {
                try {
                    String t = getChildContextText((RuleContext) ctx.getChild(i));
                    if (!t.isEmpty()) {
                        s += t + ",";
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        return s.replaceAll(",$", "");
    }

    private VarSpecContext findParentVarSpecContext(RuleContext ctx) {
        if (ctx instanceof VarSpecContext) {
            return (VarSpecContext) ctx;
        } else if (ctx.getParent() != null) {
            return findParentVarSpecContext(ctx.getParent());
        } else {
            return null;
        }
    }

    @Override
    public final void enterMethodDecl(MethodDeclContext ctx) {
        if (ctx.IDENTIFIER() != null && ctx.function() != null) {
            Component cmp = createComponent(ComponentType.METHOD, ctx.getStart().getLine());
            String comments = AntlrUtil.goLangComments(ctx.getStart().getLine(),
                    Arrays.asList(file.content().split("\n")));
            cmp.setComment(comments);
            cmp.setName(ctx.IDENTIFIER().getText());
            pointParentsToGivenChild(cmp);
            cmp.setCodeFragment(cmp.name() + "(");
            cmp.insertAccessModifier(visibility(cmp.name()));
            if (ctx.function().signature().parameters() != null) {
                setCodeFragmentFromParameters(ctx.function().signature().parameters(), cmp);
            }
            if (ctx.function().signature().result() != null) {
                processResult(ctx.function().signature().result(), cmp);
            }
            cmp.setName(cmp.codeFragment());
            cmp.setComponentName(generateComponentName(cmp.name()));
            componentStack.push(cmp);
            processParameters(ctx.function().signature().parameters(), cmp);
        }
    }

    @Override
    public final void enterFunctionDecl(FunctionDeclContext ctx) {
        exitFunctionDecl(ctx);
    }

    @Override
    public final void enterExpression(ExpressionContext ctx) {
        exitExpression(ctx);
    }

    private void setCodeFragmentFromParameters(ParametersContext ctx, Component currMethodCmp) {

        if (ctx.parameterList() != null && ctx.parameterList().parameterDecl() != null) {
            for (ParameterDeclContext paramCtx : ctx.parameterList().parameterDecl()) {
                String type = AntlrUtil.originalText(paramCtx.type());
                int interval = 1;
                if (paramCtx.identifierList() != null) {
                    interval = paramCtx.identifierList().IDENTIFIER().size();
                }
                for (int i = 0; i < interval; i++) {
                    if (currMethodCmp.codeFragment().endsWith("(")) {
                        currMethodCmp.setCodeFragment(currMethodCmp.codeFragment() + type);
                    } else {
                        currMethodCmp.setCodeFragment(currMethodCmp.codeFragment() + ", " + type);
                    }
                }
            }
        }
        currMethodCmp.setCodeFragment(currMethodCmp.codeFragment() + ")");
    }

    private void processParameters(ParametersContext ctx, Component currMethodCmp) {
        if (ctx.parameterList() != null && ctx.parameterList().parameterDecl() != null) {
            LetterProvider letterProvider = new LetterProvider();
            List<Component> paramCmps = new ArrayList<Component>();
            if (!inReceiverContext && !inResultContext) {
                for (ParameterDeclContext paramCtx : ctx.parameterList().parameterDecl()) {
                    String[] types = {};
                    for (int j = 0; j < paramCtx.children.size(); j++) {
                        String type = getChildContextText(paramCtx);
                        types = type.split(",");

                        if (types.length < 1) {
                            // without a type we really can't continue...
                            System.out.println(
                                    "Error! Did not find TypeNameContext for ParamDeclContext: " + paramCtx.getText());
                            return;
                        } else {
                            for (int g = 0; g < types.length; g++) {
                                types[g] = resolveType(types[g]);
                            }
                        }
                    }
                    List<String> argumentNames = new ArrayList<String>();
                    if (paramCtx.identifierList() == null) {
                        // no name provided for method arg, we have to name it ourselves.
                        argumentNames.add(letterProvider.getLetter());
                    } else {
                        paramCtx.identifierList().IDENTIFIER().forEach(nameCtx -> argumentNames.add(nameCtx.getText()));
                    }
                    for (String methodArgName : argumentNames) {
                        Component cmp = createComponent(ComponentType.METHOD_PARAMETER_COMPONENT, ctx.getStart().getLine());
                        cmp.setName(methodArgName);
                        cmp.setComponentName(generateComponentName(cmp.name()));
                        if (!componentStack.isEmpty()) {
                            final Component completedCmp = componentStack.peek();
                            cmp.setPackageName(completedCmp.packageName());
                        }
                        pointParentsToGivenChild(cmp);
                        for (int h = 0; h < types.length; h++) {
                            cmp.insertComponentInvocation(new TypeDeclaration(types[h]));
                            paramCmps.add(cmp);
                        }
                    }
                }
            }
            paramCmps.forEach(item -> completeComponent(item));
        }
    }

    @Override
    public final void exitMethodDecl(MethodDeclContext ctx) {
        if (!componentStack.isEmpty()) {
            Component cmp = componentStack.peek();
            if (cmp.componentType().isMethodComponent()) {
                if (cmp.codeFragment().endsWith("(")) {
                    cmp.setCodeFragment(cmp.codeFragment() + ")");
                }
                cmp.setName(ctx.IDENTIFIER().getText());
                popAndCompleteComponent();
            }
        }
    }

    @Override
    public final void exitMethodSpec(MethodSpecContext ctx) {
        if (!componentStack.isEmpty()) {
            Component cmp = componentStack.peek();
            if (cmp.componentType().isMethodComponent()) {
                if (cmp.codeFragment().endsWith("(")) {
                    cmp.setCodeFragment(cmp.codeFragment() + ")");
                }
                cmp.setName(ctx.IDENTIFIER().getText());
                popAndCompleteComponent();
            }
        }
    }

    @Override
    public final void enterResult(ResultContext ctx) {
        inResultContext = true;
    }

    @Override
    public final void enterReceiver(ReceiverContext ctx) {
        inReceiverContext = true;
    }

    @Override
    public final void enterTypeName(TypeNameContext ctx) {
        String resolvedType = resolveType(ctx.getText());
        if (!componentStack.isEmpty() && componentStack.peek().componentType().isMethodComponent()) {
            Component cmp = componentStack.pop();
            if (inResultContext) {
                int vars = 1;
                if (ctx.getParent().getParent() instanceof ParameterDeclContext) {
                    ParameterDeclContext pctx = (ParameterDeclContext) ctx.getParent().getParent();
                    if (pctx.identifierList() != null) {
                        vars = pctx.identifierList().IDENTIFIER().size();
                    }
                }
                for (int i = 0; i < vars; i++) {
                    if (cmp.value() == null || cmp.value().isEmpty()) {
                        cmp.setValue(resolvedType);
                    } else {
                        cmp.setValue(cmp.value() + ", " + resolvedType);
                    }
                }
            }
            if (inReceiverContext) {
                if (srcModel.containsComponent(resolvedType)) {
                    Component structCmp = srcModel.getComponent(resolvedType);
                    udpateStructChild(structCmp, cmp);
                } else {
                    structWaitingList.add(new AbstractMap.SimpleEntry<String, Component>(resolvedType, cmp));
                }
            }
            componentStack.push(cmp);
            VarSpecContext tmpContext = findParentVarSpecContext(ctx);
            if (tmpContext != null) {
                for (TerminalNode identifier : tmpContext.identifierList().IDENTIFIER()) {
                    Component localVarCmp = createComponent(ComponentType.LOCAL, ctx.getStart().getLine());
                    localVarCmp.setName(identifier.getText());
                    localVarCmp.setComponentName(generateComponentName(identifier.getText()));
                    localVarCmp.insertComponentInvocation(new TypeDeclaration(resolvedType));
                    completeComponent(localVarCmp);
                }
            }
        }
    }

    private void udpateStructChild(Component structCmp, Component structChildCmp) {
        if (srcModel.containsComponent(structChildCmp.uniqueName())) {
            srcModel.getComponents().remove(structChildCmp.uniqueName());
        }
        structChildCmp.setComponentName(structCmp.componentName() + "." + structChildCmp.codeFragment());
        structChildCmp.setPackageName(structCmp.packageName());
        srcModel.insertComponent(structChildCmp);
        List<String> childrenToBeRemoved = new ArrayList<>();
        List<String> childrenToBeAdded = new ArrayList<>();
        for (String child : structChildCmp.children()) {
            Component childCmp = srcModel.getComponent(child);
            if (childCmp != null) {
                childCmp.setComponentName(structChildCmp.componentName() + "." + childCmp.name());
                childCmp.setPackageName(structChildCmp.packageName());
                childrenToBeAdded.add(childCmp.uniqueName());
            }
            if (child != childCmp.uniqueName()) {
                childrenToBeRemoved.add(child);
                srcModel.getComponents().remove(child);
                srcModel.insertComponent(childCmp);
            }
        }
        childrenToBeRemoved.forEach(item -> structChildCmp.children().remove(item));
        childrenToBeAdded.forEach(item -> structChildCmp.insertChildComponent(item));
        structCmp.insertChildComponent(structChildCmp.uniqueName());
    }

    @Override
    public final void exitResult(ResultContext ctx) {
        inResultContext = false;
    }

    private void processResult(ResultContext ctx, Component methodCmp) {
        if ((ctx.parameters() != null && !ctx.parameters().isEmpty() && ctx.parameters().parameterList() != null) || ctx.type() != null && !ctx.type().getText().isEmpty()) {
            if (!methodCmp.codeFragment().contains(":")) {
                if (!methodCmp.codeFragment().endsWith(")") && !methodCmp.codeFragment().contains(":")) {
                    methodCmp.setCodeFragment(methodCmp.codeFragment() + ") : (");
                } else {
                    methodCmp.setCodeFragment(methodCmp.codeFragment() + " : (");
                }
                if (ctx.parameters() != null && ctx.parameters().parameterList() != null) {
                    for (ParameterDeclContext paramCtx : ctx.parameters().parameterList().parameterDecl()) {
                        String paramType = AntlrUtil.originalText(paramCtx.type());
                        int iterations = 1;
                        if (paramCtx.identifierList() != null) {
                            iterations = paramCtx.identifierList().IDENTIFIER().size();
                        }
                        for (int i = 0; i < iterations; i++) {
                            if (methodCmp.codeFragment().trim().endsWith("(")) {
                                methodCmp.setCodeFragment(methodCmp.codeFragment() + paramType);
                            } else {
                                methodCmp.setCodeFragment(methodCmp.codeFragment() + ", " + paramType);
                            }
                        }
                    }
                } else if (ctx.type() != null) {
                    String type = AntlrUtil.originalText(ctx.type());
                    if (methodCmp.codeFragment().trim().endsWith("(")) {
                        methodCmp.setCodeFragment(methodCmp.codeFragment() + type);
                    } else {
                        methodCmp.setCodeFragment(methodCmp.codeFragment() + ", " + type);
                    }
                }
                methodCmp.setCodeFragment(methodCmp.codeFragment() + ")");
            }
        }
    }

    @Override
    public final void exitReceiver(ReceiverContext ctx) {
        inReceiverContext = false;
    }

    private String visibility(String goLangComponentName) {
        if (Character.isUpperCase(goLangComponentName.charAt(0))) {
            return "public";
        } else {
            return "private";
        }
    }

    @Override
    public final void exitStructType(StructTypeContext ctx) {
        if (!componentStack.isEmpty() && componentStack.peek().componentType().isBaseComponent()) {
            popAndCompleteComponent();
        }
    }

    @Override
    public final void exitInterfaceType(InterfaceTypeContext ctx) {
        if (!componentStack.isEmpty() && componentStack.peek().componentType().isBaseComponent()) {
            popAndCompleteComponent();
        }
    }

    @Override
    public final void enterFieldDecl(FieldDeclContext ctx) {
        if (!componentStack.isEmpty() && componentStack.peek().componentType().isBaseComponent()) {
            if (ctx.identifierList() != null && !ctx.identifierList().isEmpty()) {
                List<Component> fieldVars = new ArrayList<Component>();
                for (TerminalNode token : ctx.identifierList().IDENTIFIER()) {
                    Component cmp = createComponent(ComponentType.FIELD, ctx.getStart().getLine());
                    cmp.setName(token.getText());
                    cmp.setComment(
                            AntlrUtil.goLangComments(ctx.getStart().getLine(), Arrays.asList(file.content().split("\n"))));
                    cmp.setComponentName(generateComponentName(token.getText()));
                    if (ctx.type().getText().contains("func")) {
                        String line = file.content().split("\n")[ctx.type().start.getLine() - 1];
                        if (line.trim().endsWith("}")) {
                            line = line.substring(0, line.indexOf("}")).trim();
                        }
                        if (line.contains("//")) {
                            line = line.substring(0, line.lastIndexOf("//"));
                        }
                        cmp.setCodeFragment(cmp.name() + " : " + line.substring(line.indexOf("func")).trim());
                    } else {
                        cmp.setCodeFragment(cmp.name() + " : " + ctx.type().getText());
                    }
                    cmp.insertAccessModifier(visibility(cmp.name()));
                    pointParentsToGivenChild(cmp);
                    String[] types = getChildContextText(ctx.type()).split(",");
                    for (String type : types) {
                        cmp.insertComponentInvocation(new TypeDeclaration(resolveType(type)));
                    }
                    fieldVars.add(cmp);
                }
                fieldVars.forEach(item -> completeComponent(item));
            } else if (ctx.anonymousField() != null) {
                String[] types = getChildContextText(ctx.anonymousField()).split(",");
                for (String type : types) {
                    insertExtensionIntoStackBaseComponent(type);
                }
            }
        } else {
            exitFieldDecl(ctx);
        }
    }

    private void insertExtensionIntoStackBaseComponent(String extendsComponent) {
        List<Component> tmp = new ArrayList<Component>();
        while (!componentStack.isEmpty()) {
            Component stackCmp = componentStack.pop();
            tmp.add(stackCmp);
            if (stackCmp.componentType().isBaseComponent()) {
                stackCmp.insertComponentInvocation(new TypeExtension(resolveType(extendsComponent)));
                break;
            }
        }
        tmp.forEach(item -> componentStack.push(item));
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
        } else if (baseType(type)) {
            return type;
        } else {
            // must be a local type...
            return this.currentPkg + "." + type;
        }
    }

    private boolean baseType(String type) {
        return (type.equals("string") || type.equals("int") || type.equals("int8") || type.equals("int16")
                || type.equals("int32") || type.equals("int64") || type.equals("uint") || type.equals("uint8")
                || type.equals("uint16") || type.equals("uint32") || type.equals("uint64") || type.equals("uintptr")
                || type.equals("byte") || type.equals("rune") || type.equals("float32") || type.equals("float64")
                || type.equals("complex64") || type.equals("complex128") || type.equals("bool"));
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

    private boolean componentStackContainsMethod() {
        for (Component cmp : componentStack) {
            if (cmp.componentType().isMethodComponent()) {
                return true;
            }
        }
        return false;
    }


    class LetterProvider {
        private int count = -1;
        private String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        /**
         * Will hit array index out of bounds after 26 letters, but that should never happen.
         */
        public String getLetter() {
            count += 1;
            return letters[count];
        }
    }
}