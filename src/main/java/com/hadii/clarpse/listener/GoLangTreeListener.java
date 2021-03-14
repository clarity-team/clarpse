package com.hadii.clarpse.listener;

import com.hadii.antlr.golang.GoParser;
import com.hadii.antlr.golang.GoParserBaseListener;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.reference.ComponentReference;
import com.hadii.clarpse.reference.SimpleTypeReference;
import com.hadii.clarpse.reference.TypeExtensionReference;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

@SuppressWarnings("unchecked")
public class GoLangTreeListener extends GoParserBaseListener {

    private static final Logger logger = LogManager.getLogger(GoLangTreeListener.class);
    private final Stack<Component> componentStack = new Stack<>();
    private final ArrayList<String> currentImports = new ArrayList<>();
    private String currentPkg = "";
    private final OOPSourceCodeModel srcModel;
    private final Map<String, String> currentImportsMap = new HashMap<>();
    private final ProjectFile ProjectFile;
    private String lastParsedTypeIdentifier = null;
    /**
     * List of all type names in the project.
     */
    private final List<String> projectFileTypes;
    private boolean inReceiverContext = false;
    private boolean inResultContext = false;
    private final List<Map.Entry<String, Component>> structWaitingList;
    private int currCyclomaticComplexity = 0;

    public GoLangTreeListener(final OOPSourceCodeModel srcModel, List<String> projectFileTypes, ProjectFile filetoProcess, List<Map.Entry<String, Component>> structWaitingList) {
        this.srcModel = srcModel;
        this.ProjectFile = filetoProcess;
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
                updateStructChild(completedComponent, entry.getValue());
            }
        });
        if (completedComponent.componentType().isMethodComponent()
                && srcModel.containsComponent(completedComponent.parentUniqueName())) {
            Component parentCmp = srcModel.getComponent(completedComponent.parentUniqueName()).get();
            for (ComponentReference componentReference : completedComponent.references()) {
                parentCmp.insertComponentRef(componentReference);
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
    private Component createComponent(OOPSourceModelConstants.ComponentType componentType) {
        final Component newCmp = new Component();
        newCmp.setPackageName(currentPkg);
        newCmp.setComponentType(componentType);
        newCmp.setSourceFilePath(ProjectFile.path());
        return newCmp;
    }

    @Override
    public void enterSourceFile(GoParser.SourceFileContext ctx) {
        super.enterSourceFile(ctx);
    }

    @Override
    public void enterPackageClause(GoParser.PackageClauseContext ctx) {
        currentPkg = ctx.IDENTIFIER().getText();
        if (this.ProjectFile.path().contains("/")) {
            String modFileName = this.ProjectFile.path().substring(0, this.ProjectFile.path().lastIndexOf("/"));
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
            logger.info(
                    "Clarpse GoLang Listener found new package declaration while component stack not empty! component stack size is: "
                            + componentStack.size());
        }
    }

    @Override
    public final void enterImportSpec(GoParser.ImportSpecContext ctx) {
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
    public final void enterStructType(GoParser.StructTypeContext ctx) {
        if (lastParsedTypeIdentifier != null) {
            if (componentStackContainsMethod()) {
                // skip over structs defined within methods.
                exitStructType(ctx);
            } else {
                Component cmp = createComponent(OOPSourceModelConstants.ComponentType.STRUCT);
                String comments = ParseUtil.goLangComments(ctx.getStart().getLine(),
                                                           Arrays.asList(ProjectFile.content().split("\n")));
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
    public final void enterInterfaceType(GoParser.InterfaceTypeContext ctx) {
        if (!componentStackContainsMethod()) {
            if (lastParsedTypeIdentifier != null) {
                Component cmp = createComponent(OOPSourceModelConstants.ComponentType.INTERFACE);
                String comments = ParseUtil.goLangComments(ctx.getStart().getLine(),
                        Arrays.asList(ProjectFile.content().split("\n")));
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
    public final void enterMethodSpec(GoParser.MethodSpecContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            Component cmp = createComponent(OOPSourceModelConstants.ComponentType.METHOD);
            String comments = ParseUtil.goLangComments(ctx.getStart().getLine(),
                    Arrays.asList(ProjectFile.content().split("\n")));
            cmp.setComment(comments);
            cmp.setName(ctx.IDENTIFIER().getText());
            cmp.setCodeFragment(cmp.name() + "(");
            cmp.insertAccessModifier(visibility(cmp.name()));
            if (ctx.parameters() != null) {
                setCodeFragmentFromParameters(ctx.parameters(), cmp);
            }
            if (ctx.result() != null) {
                processResult(ctx.result(), cmp);
            }
            cmp.setName(cmp.codeFragment());
            cmp.setComponentName(ParseUtil.generateComponentName(cmp.name(), componentStack));
            ParseUtil.pointParentsToGivenChild(cmp, componentStack);
            componentStack.push(cmp);
            processParameters(ctx.parameters());

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
        if (ctx instanceof GoParser.TypeNameContext) {
            return ctx.getText();
        }

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!(ctx.getChild(i) instanceof TerminalNodeImpl)) {
                try {
                    String t = getChildTypeNameContextText((RuleContext) ctx.getChild(i));
                    if (!t.isEmpty()) {
                        s.append(t).append(",");
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        return s.toString().replaceAll(",$", "");
    }

    private GoParser.VarSpecContext findParentVarSpecContext(RuleContext ctx) {
        if (ctx instanceof GoParser.VarSpecContext) {
            return (GoParser.VarSpecContext) ctx;
        } else if (ctx.getParent() != null) {
            return findParentVarSpecContext(ctx.getParent());
        } else {
            return null;
        }
    }

    @Override
    public final void enterMethodDecl(GoParser.MethodDeclContext ctx) {
        if (ctx.IDENTIFIER() != null && ctx.signature() != null) {
            Component cmp = createComponent(OOPSourceModelConstants.ComponentType.METHOD);
            String comments = ParseUtil.goLangComments(ctx.getStart().getLine(),
                    Arrays.asList(ProjectFile.content().split("\n")));
            cmp.setComment(comments);
            cmp.setName(ctx.IDENTIFIER().getText());
            ParseUtil.pointParentsToGivenChild(cmp, componentStack);
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
            currCyclomaticComplexity = 1 + countLogicalBinaryOperators(ctx);
            componentStack.push(cmp);
            processParameters(ctx.signature().parameters());
        }
    }

    @Override
    public final void enterFunctionDecl(GoParser.FunctionDeclContext ctx) {
        exitFunctionDecl(ctx);
    }

    @Override
    public final void enterExpression(GoParser.ExpressionContext ctx) {
        String origText = ParseUtil.originalText(ctx);
        if (origText != null) {
        currCyclomaticComplexity += StringUtils.countMatches(origText, " && ");
        currCyclomaticComplexity += StringUtils.countMatches(origText, " || ");
        exitExpression(ctx);
        }
    }

    private void setCodeFragmentFromParameters(GoParser.ParametersContext ctx, Component currMethodCmp) {

        if (ctx.parameterDecl() != null) {
            for (GoParser.ParameterDeclContext paramCtx : ctx.parameterDecl()) {
                String type = ParseUtil.originalText(paramCtx.type_());
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

    private void processParameters(GoParser.ParametersContext ctx) {
        if (ctx.parameterDecl() != null) {
            LetterProvider letterProvider = new LetterProvider();
            List<Component> paramCmps = new ArrayList<>();
            if (!inReceiverContext && !inResultContext) {
                for (GoParser.ParameterDeclContext paramCtx : ctx.parameterDecl()) {
                    String[] types = {};
                    for (int j = 0; j < paramCtx.children.size(); j++) {
                        String type = getChildTypeNameContextText(paramCtx);
                        types = type.split(",");
                        if (types.length < 1) {
                            // without a type we really can't continue...
                            logger.error(
                                    "Error! Did not find TypeNameContext for ParamDeclContext: " + paramCtx.getText());
                            return;
                        } else {
                            for (int g = 0; g < types.length; g++) {
                                types[g] = resolveType(types[g]);
                            }
                        }
                    }
                    List<String> argumentNames = new ArrayList<>();
                    if (paramCtx.identifierList() == null) {
                        // no name provided for method arg, we have to name it ourselves.
                        argumentNames.add(letterProvider.getLetter());
                    } else {
                        paramCtx.identifierList().IDENTIFIER().forEach(nameCtx -> argumentNames.add(nameCtx.getText()));
                    }
                    for (String methodArgName : argumentNames) {
                        Component cmp = createComponent(OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
                        cmp.setName(methodArgName);
                        cmp.setComponentName(ParseUtil.generateComponentName(cmp.name(), componentStack));
                        if (!componentStack.isEmpty()) {
                            final Component completedCmp = componentStack.peek();
                            cmp.setPackageName(completedCmp.packageName());
                        }
                        ParseUtil.pointParentsToGivenChild(cmp, componentStack);
                        for (String type : types) {
                            cmp.insertComponentRef(new SimpleTypeReference(type));
                            paramCmps.add(cmp);
                        }
                    }
                }
            }
            paramCmps.forEach(this::completeComponent);
        }
    }

    @Override
    public final void exitMethodDecl(GoParser.MethodDeclContext ctx) {
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
    public final void exitMethodSpec(GoParser.MethodSpecContext ctx) {
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
    public final void enterResult(GoParser.ResultContext ctx) {
        inResultContext = true;
    }

    @Override
    public final void enterReceiver(GoParser.ReceiverContext ctx) {
        inReceiverContext = true;
    }

    @Override
    public final void enterTypeName(GoParser.TypeNameContext ctx) {
        String resolvedType = resolveType(ctx.getText());
        if (!componentStack.isEmpty() && componentStack.peek().componentType().isMethodComponent()) {
            Component cmp = componentStack.pop();
            if (inResultContext) {
                int vars = 1;
                if (ctx.getParent().getParent() instanceof GoParser.ParameterDeclContext) {
                    GoParser.ParameterDeclContext pctx = (GoParser.ParameterDeclContext) ctx.getParent().getParent();
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
                    structCmp.ifPresent(component -> updateStructChild(component, cmp));
                } else {
                    structWaitingList.add(new AbstractMap.SimpleEntry<>(resolvedType, cmp));
                }
            }
            componentStack.push(cmp);
            GoParser.VarSpecContext tmpContext = findParentVarSpecContext(ctx);
            if (tmpContext != null) {
                for (TerminalNode identifier : tmpContext.identifierList().IDENTIFIER()) {
                    Component localVarCmp = createComponent(OOPSourceModelConstants.ComponentType.LOCAL);
                    localVarCmp.setName(identifier.getText());
                    localVarCmp.setComponentName(ParseUtil.generateComponentName(identifier.getText(), componentStack));
                    localVarCmp.insertComponentRef(new SimpleTypeReference(resolvedType));
                    ParseUtil.pointParentsToGivenChild(localVarCmp, componentStack);
                    completeComponent(localVarCmp);
                }
            }
        }
    }

    private void updateStructChild(Component structCmp, Component structChildCmp) {
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
            if (!child.equals(childCmp.get().uniqueName())) {
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
    public final void exitResult(GoParser.ResultContext ctx) {
        inResultContext = false;
    }

    private void processResult(GoParser.ResultContext ctx, Component methodCmp) {
        if ((ctx.parameters() != null && !ctx.parameters().isEmpty() && ctx.parameters().parameterDecl() != null) || ctx.type_() != null && !ctx.type_().getText().isEmpty()) {
            if (!methodCmp.codeFragment().contains(":")) {
                if (!methodCmp.codeFragment().endsWith(")")) {
                    methodCmp.setCodeFragment(methodCmp.codeFragment() + ") : (");
                } else {
                    methodCmp.setCodeFragment(methodCmp.codeFragment() + " : (");
                }
                if (ctx.parameters() != null && ctx.parameters().parameterDecl() != null) {
                    for (GoParser.ParameterDeclContext paramCtx : ctx.parameters().parameterDecl()) {
                        String paramType = ParseUtil.originalText(paramCtx.type_());
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
                } else if (ctx.type_() != null) {
                    String type = ParseUtil.originalText(ctx.type_());
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
    public final void exitReceiver(GoParser.ReceiverContext ctx) {
        inReceiverContext = false;
    }

    @Override
    public final void enterForStmt(GoParser.ForStmtContext ctx) {
        currCyclomaticComplexity += 1;
    }

    @Override
    public final void enterExprSwitchCase(GoParser.ExprSwitchCaseContext ctx) {
        if (ctx.children != null && ctx.children.size() > 0 && ctx.children.get(0).toString().equals("case")) {
            currCyclomaticComplexity += 1;
        }
    }

    @Override
    public final void enterTypeSwitchCase(GoParser.TypeSwitchCaseContext ctx) {
        if (ctx.children != null && ctx.children.size() > 0 && ctx.children.get(0).toString().equals("case")) {
            currCyclomaticComplexity += 1;
        }
    }

    @Override
    public final void enterCommCase(GoParser.CommCaseContext ctx) {
        if (ctx.children != null && ctx.children.size() > 0 && ctx.children.get(0).toString().equals("case")) {
            currCyclomaticComplexity += 1;
        }
    }

    @Override
    public final void enterIfStmt(GoParser.IfStmtContext ctx) {
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
    public final void exitStructType(GoParser.StructTypeContext ctx) {
        if (!componentStack.isEmpty() && componentStack.peek().componentType().isBaseComponent()) {
            popAndCompleteComponent();
        }
    }

    @Override
    public final void exitInterfaceType(GoParser.InterfaceTypeContext ctx) {
        if (!componentStack.isEmpty() && componentStack.peek().componentType().isBaseComponent()) {
            popAndCompleteComponent();
        }
    }

    @Override
    public final void enterFieldDecl(GoParser.FieldDeclContext ctx) {
        if (!componentStack.isEmpty() && componentStack.peek().componentType().isBaseComponent()) {
            if (ctx.identifierList() != null && !ctx.identifierList().isEmpty()) {
                List<Component> fieldVars = new ArrayList<>();
                for (TerminalNode token : ctx.identifierList().IDENTIFIER()) {
                    Component cmp = createComponent(OOPSourceModelConstants.ComponentType.FIELD);
                    cmp.setName(token.getText());
                    cmp.setComment(
                            ParseUtil.goLangComments(ctx.getStart().getLine(), Arrays.asList(ProjectFile.content().split("\n"))));
                    cmp.setComponentName(ParseUtil.generateComponentName(token.getText(), componentStack));
                    if (ctx.type_().getText().contains("func")) {
                        String line = ProjectFile.content().split("\n")[ctx.type_().start.getLine() - 1];
                        if (line.trim().endsWith("}")) {
                            line = line.substring(0, line.indexOf("}")).trim();
                        }
                        if (line.contains("//")) {
                            line = line.substring(0, line.lastIndexOf("//"));
                        }
                        cmp.setCodeFragment(cmp.name() + " : " + line.substring(line.indexOf("func")).trim());
                    } else {
                        cmp.setCodeFragment(cmp.name() + " : " + ctx.type_().getText());
                    }
                    cmp.insertAccessModifier(visibility(cmp.name()));
                    ParseUtil.pointParentsToGivenChild(cmp, componentStack);
                    String[] types = getChildTypeNameContextText(ctx.type_()).split(",");
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
        List<Component> tmp = new ArrayList<>();
        while (!componentStack.isEmpty()) {
            Component stackCmp = componentStack.pop();
            tmp.add(stackCmp);
            if (stackCmp.componentType().isBaseComponent()) {
                stackCmp.insertComponentRef(new TypeExtensionReference(resolveType(extendsComponent)));
                break;
            }
        }
        tmp.forEach(componentStack::push);
    }

    /**
     * Tries to return the full, unique type name of the given type.
     */
    private String resolveType(String type) {
        type = type.replace("*", "");
        if (currentImportsMap.containsKey(type)) {
            return currentImportsMap.get(type).replaceAll("/", ".");
        }
        for (Map.Entry<String, String> pair : currentImportsMap.entrySet()) {
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
    public final void enterTypeSpec(GoParser.TypeSpecContext ctx) {
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

    static class LetterProvider {
        private int count = -1;
        private final String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        /**
         * Will hit array index out of bounds after 26 letters, but that should never happen.
         */
        public String getLetter() {
            count += 1;
            return letters[count];
        }
    }
}