package com.clarity.listener;

import com.clarity.antlr.golang.GolangBaseListener;
import com.clarity.antlr.golang.GolangParser;
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
import com.clarity.compiler.File;
import com.clarity.reference.ComponentReference;
import com.clarity.reference.SimpleTypeReference;
import com.clarity.reference.TypeExtensionReference;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

@SuppressWarnings("unchecked")
public class GoLangTreeListener extends GolangBaseListener {

    private final Stack<Component> componentStack = new Stack<Component>();
    private final ArrayList<String> currentImports = new ArrayList<String>();
    private String currentPkg = "";
    private final OOPSourceCodeModel srcModel;
    private final Map<String, String> currentImportsMap = new HashMap<String, String>();
    private final File file;
    private String lastParsedTypeIdentifier = null;
    /**
     * List of all type names in the project.
     */
    private List<String> projectFileTypes;
    private boolean inReceiverContext = false;
    private boolean inResultContext = false;
    private List<Map.Entry<String, Component>> structWaitingList;
    private int currCyclomaticComplexity = 0;

    public GoLangTreeListener(final OOPSourceCodeModel srcModel, List<String> projectFileTypes, File filetoProcess, List<Map.Entry<String, Component>> structWaitingList) {
        this.srcModel = srcModel;
        this.file = filetoProcess;
        this.projectFileTypes = projectFileTypes;
        this.structWaitingList = structWaitingList;
    }

    private void completeComponent(Component completedComponent) {
        // update cyclomatic complexity if component is a method
        if (completedComponent.componentType().isMethodComponent()
                && !ParseUtil.componentStackContainsInterface(componentStack)) {
            completedComponent.setCyclo(currCyclomaticComplexity);
        }

        // To handle the case where struct methods are parsed before their parent struct were,
        // check if the completed component is such a parent and update accordingly.
        structWaitingList.forEach(entry -> {
            if (entry.getKey().equals(completedComponent.uniqueName())) {
                udpateStructChild(completedComponent, entry.getValue());
            }
        });
        if (completedComponent.componentType().isMethodComponent()
                && srcModel.containsComponent(completedComponent.parentUniqueName())) {
            Component parentCmp = srcModel.getComponent(completedComponent.parentUniqueName()).get();
            final Iterator<ComponentReference> invocationIterator = completedComponent.references().iterator();
            while (invocationIterator.hasNext()) {
                parentCmp.insertComponentRef(invocationIterator.next());
            }
        }
        ParseUtil.copyRefsToParents(completedComponent, componentStack);
        srcModel.insertComponent(completedComponent);
    }

    private void popAndCompleteComponent() {
        if (!componentStack.isEmpty()) {
            completeComponent(componentStack.pop());
        }
    }

    /**
     * Creates a new component based on the given ParseRuleContext.
     */
    private Component createComponent(ComponentType componentType, int line, ParserRuleContext ctx) {
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
                Component cmp = createComponent(ComponentType.STRUCT, ctx.getStart().getLine(), ctx);
                String comments = ParseUtil.goLangComments(ctx.getStart().getLine(),
                        Arrays.asList(file.content().split("\n")));
                cmp.setComment(comments);
                cmp.setName(lastParsedTypeIdentifier);
                cmp.setComponentName(ParseUtil.generateComponentName(lastParsedTypeIdentifier, componentStack));
                cmp.setImports(currentImports);
                ParseUtil.pointParentsToGivenChild(cmp, componentStack);
                cmp.insertAccessModifier(visibility(cmp.name()));
                componentStack.push(cmp);
            }
        }
    }

    @Override
    public final void enterInterfaceType(InterfaceTypeContext ctx) {
        if (!componentStackContainsMethod()) {
            if (lastParsedTypeIdentifier != null) {
                Component cmp = createComponent(ComponentType.INTERFACE, ctx.getStart().getLine(), ctx);
                String comments = ParseUtil.goLangComments(ctx.getStart().getLine(),
                        Arrays.asList(file.content().split("\n")));
                cmp.setComment(comments);
                cmp.setName(lastParsedTypeIdentifier);
                cmp.setComponentName(ParseUtil.generateComponentName(lastParsedTypeIdentifier, componentStack));
                cmp.setImports(currentImports);
                ParseUtil.pointParentsToGivenChild(cmp, componentStack);
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
            Component cmp = createComponent(ComponentType.METHOD, ctx.getStart().getLine(), ctx);
            String comments = ParseUtil.goLangComments(ctx.getStart().getLine(),
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
            cmp.setComponentName(ParseUtil.generateComponentName(cmp.name(), componentStack));
            ParseUtil.pointParentsToGivenChild(cmp, componentStack);
            componentStack.push(cmp);
            processParameters(ctx.signature().parameters(), cmp);

        } else if (ctx.typeName() != null) {
            insertExtensionIntoStackBaseComponent(ctx.typeName().getText());
        }
    }

    /**
     * Searches the children of the given context for a TypeNameContext
     * and returns its Text Value. If there are multiple relevant child nodes,
     * we will return the text value for one of them at random.
     *
     * @param ctx Given context in which to search
     * @return Text corresponding to the type names found.
     */
    public String getChildTypeNameContextText(RuleContext ctx) {

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
                    String t = getChildTypeNameContextText((RuleContext) ctx.getChild(i));
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
            Component cmp = createComponent(ComponentType.METHOD, ctx.getStart().getLine(), ctx);
            String comments = ParseUtil.goLangComments(ctx.getStart().getLine(),
                    Arrays.asList(file.content().split("\n")));
            cmp.setComment(comments);
            cmp.setName(ctx.IDENTIFIER().getText());
            ParseUtil.pointParentsToGivenChild(cmp, componentStack);
            cmp.setCodeFragment(cmp.name() + "(");
            cmp.insertAccessModifier(visibility(cmp.name()));
            if (ctx.function().signature().parameters() != null) {
                setCodeFragmentFromParameters(ctx.function().signature().parameters(), cmp);
            }
            if (ctx.function().signature().result() != null) {
                processResult(ctx.function().signature().result(), cmp);
            }
            cmp.setName(cmp.codeFragment());
            cmp.setComponentName(ParseUtil.generateComponentName(cmp.name(), componentStack));
            currCyclomaticComplexity = 1 + countLogicalBinaryOperators(ctx);
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
        String origText = ParseUtil.originalText(ctx);
        if (origText != null) {
        currCyclomaticComplexity += StringUtils.countMatches(origText, " && ");
        currCyclomaticComplexity += StringUtils.countMatches(origText, " || ");
        exitExpression(ctx);
        }
    }

    private void setCodeFragmentFromParameters(ParametersContext ctx, Component currMethodCmp) {

        if (ctx.parameterList() != null && ctx.parameterList().parameterDecl() != null) {
            for (ParameterDeclContext paramCtx : ctx.parameterList().parameterDecl()) {
                String type = ParseUtil.originalText(paramCtx.type());
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
                        String type = getChildTypeNameContextText(paramCtx);
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
                        Component cmp = createComponent(ComponentType.METHOD_PARAMETER_COMPONENT, ctx.getStart().getLine(), ctx);
                        cmp.setName(methodArgName);
                        cmp.setComponentName(ParseUtil.generateComponentName(cmp.name(), componentStack));
                        if (!componentStack.isEmpty()) {
                            final Component completedCmp = componentStack.peek();
                            cmp.setPackageName(completedCmp.packageName());
                        }
                        ParseUtil.pointParentsToGivenChild(cmp, componentStack);
                        for (int h = 0; h < types.length; h++) {
                            cmp.insertComponentRef(new SimpleTypeReference(types[h]));
                            paramCmps.add(cmp);
                        }
                    }
                }
            }
            paramCmps.forEach(this::completeComponent);
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

    private int countLogicalBinaryOperators(ParserRuleContext ctx) {
        int logicalBinaryOperators = 0;
        String[] codeLines = ParseUtil.originalText(ctx).split("\\r?\\n");
        for (String codeLine : codeLines) {
            if (!codeLine.trim().startsWith("/")) {
                logicalBinaryOperators += StringUtils.countMatches(codeLine, " && ");
                logicalBinaryOperators += StringUtils.countMatches(codeLine, " || ");
            }
        }
        return logicalBinaryOperators;
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
                    Optional<Component> structCmp = srcModel.getComponent(resolvedType);
                    structCmp.ifPresent(component -> udpateStructChild(component, cmp));
                } else {
                    structWaitingList.add(new AbstractMap.SimpleEntry<String, Component>(resolvedType, cmp));
                }
            }
            componentStack.push(cmp);
            VarSpecContext tmpContext = findParentVarSpecContext(ctx);
            if (tmpContext != null) {
                for (TerminalNode identifier : tmpContext.identifierList().IDENTIFIER()) {
                    Component localVarCmp = createComponent(ComponentType.LOCAL, ctx.getStart().getLine(), ctx);
                    localVarCmp.setName(identifier.getText());
                    localVarCmp.setComponentName(ParseUtil.generateComponentName(identifier.getText(), componentStack));
                    localVarCmp.insertComponentRef(new SimpleTypeReference(resolvedType));
                    ParseUtil.pointParentsToGivenChild(localVarCmp, componentStack);
                    completeComponent(localVarCmp);
                }
            }
        }
    }

    private void udpateStructChild(Component structCmp, Component structChildCmp) {
        if (srcModel.containsComponent(structChildCmp.uniqueName())) {
            srcModel.removeComponent(structChildCmp.uniqueName());
        }
        structChildCmp.setComponentName(structCmp.componentName() + "." + structChildCmp.codeFragment());
        structChildCmp.setPackageName(structCmp.packageName());
        srcModel.insertComponent(structChildCmp);
        List<String> childrenToBeRemoved = new ArrayList<>();
        List<String> childrenToBeAdded = new ArrayList<>();
        for (String child : structChildCmp.children()) {
            Optional<Component> childCmp = srcModel.getComponent(child);
            if (childCmp.isPresent()) {
                childCmp.get().setComponentName(structChildCmp.componentName() + "." + childCmp.get().name());
                childCmp.get().setPackageName(structChildCmp.packageName());
                childrenToBeAdded.add(childCmp.get().uniqueName());
            }
            if (child != childCmp.get().uniqueName()) {
                childrenToBeRemoved.add(child);
                srcModel.removeComponent(child);
                srcModel.insertComponent(childCmp.get());
            }
        }
        childrenToBeRemoved.forEach(item -> structChildCmp.children().remove(item));
        childrenToBeAdded.forEach(structChildCmp::insertChildComponent);
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
                        String paramType = ParseUtil.originalText(paramCtx.type());
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
                    String type = ParseUtil.originalText(ctx.type());
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

    @Override
    public final void enterForStmt(GolangParser.ForStmtContext ctx) {
        currCyclomaticComplexity += 1;
    }

    @Override
    public final void enterExprSwitchCase(GolangParser.ExprSwitchCaseContext ctx) {
        if (ctx.children != null && ctx.children.size() > 0 && ctx.children.get(0).toString().equals("case")) {
            currCyclomaticComplexity += 1;
        }
    }

    @Override
    public final void enterTypeSwitchCase(GolangParser.TypeSwitchCaseContext ctx) {
        if (ctx.children != null && ctx.children.size() > 0 && ctx.children.get(0).toString().equals("case")) {
            currCyclomaticComplexity += 1;
        }
    }

    @Override
    public final void enterCommCase(GolangParser.CommCaseContext ctx) {
        if (ctx.children != null && ctx.children.size() > 0 && ctx.children.get(0).toString().equals("case")) {
            currCyclomaticComplexity += 1;
        }
    }

    @Override
    public final void enterIfStmt(GolangParser.IfStmtContext ctx) {
        currCyclomaticComplexity += 1;
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
                    Component cmp = createComponent(ComponentType.FIELD, ctx.getStart().getLine(), ctx);
                    cmp.setName(token.getText());
                    cmp.setComment(
                            ParseUtil.goLangComments(ctx.getStart().getLine(), Arrays.asList(file.content().split("\n"))));
                    cmp.setComponentName(ParseUtil.generateComponentName(token.getText(), componentStack));
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
                    ParseUtil.pointParentsToGivenChild(cmp, componentStack);
                    String[] types = getChildTypeNameContextText(ctx.type()).split(",");
                    for (String type : types) {
                        cmp.insertComponentRef(new SimpleTypeReference(resolveType(type)));
                    }
                    fieldVars.add(cmp);
                }
                fieldVars.forEach(this::completeComponent);
            } else if (ctx.anonymousField() != null) {
                String[] types = getChildTypeNameContextText(ctx.anonymousField()).split(",");
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
                stackCmp.insertComponentRef(new TypeExtensionReference(resolveType(extendsComponent)));
                break;
            }
        }
        tmp.forEach(item -> componentStack.push(item));
    }

    /**
     * Tries to return the full, unique type name of the given type.
     */
    private String resolveType(String type) {

        type = type.replace("*", "");

        if (currentImportsMap.containsKey(type)) {
            return currentImportsMap.get(type).replaceAll("/", ".");
        }

        final Iterator<?> it = currentImportsMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes") final Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
            if (type.startsWith(pair.getKey())) {
                return ((pair.getValue()).replaceAll("/", ".")) + "." + type.replace(pair.getKey() + ".", "");
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