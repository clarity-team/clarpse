package com.clarity.listener;

import com.clarity.ClarpseUtil;
import com.clarity.compiler.RawFile;
import com.clarity.invocation.AnnotationInvocation;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.DocMention;
import com.clarity.invocation.ThrownException;
import com.clarity.invocation.TypeDeclaration;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

/**
 * As the parse tree is developed by JavaParser, we add listener methods to
 * procedurally capture important information during this process and populate
 * our Source Code Model.
 */
public class JavaTreeListener extends VoidVisitorAdapter<Object> {

    private final Stack<Component> componentStack = new Stack<Component>();
    private final ArrayList<String> currentImports = new ArrayList<>();
    private String currentPkg = "";
    private final OOPSourceCodeModel srcModel;
    private final Map<String, String> currentImportsMap = new HashMap<String, String>();
    private final RawFile file;
    private final String[] lines;
    private int currCyclomaticComplexity = 0;


    /**
     * @param srcModel Source model to populate from the parsing of the given code base.
     * @param file     The path of the source file being parsed.
     */
    public JavaTreeListener(final OOPSourceCodeModel srcModel, final RawFile file) {
        this.srcModel = srcModel;
        this.file = file;
        lines = file.content().split("\\r?\\n");
    }

    public void populateModel() throws IOException {

        CompilationUnit cu;
        ByteArrayInputStream in = null;
        try {
            in = new ByteArrayInputStream(file.content().getBytes(StandardCharsets.UTF_8));
            // parse the file
            cu = com.github.javaparser.JavaParser.parse(in);
            visit(cu, null);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
    }

    private void completeComponent() {
        if (!componentStack.isEmpty()) {
            final Component completedCmp = componentStack.pop();

            // update cyclomatic complexity if component is a method or class
            if (completedCmp.componentType() == ComponentType.METHOD
                    || completedCmp.componentType() == ComponentType.CONSTRUCTOR) {
                completedCmp.setCyclo(currCyclomaticComplexity);
            } else if (completedCmp.componentType() == ComponentType.CLASS
                    || completedCmp.componentType() == ComponentType.ENUM) {
                // class component cyclo attribute is a weighted average of children method complexities.
                int childCount = 0;
                int complexityTotal = 0;
                for (String childrenName : completedCmp.children()) {
                    Optional<Component> child = srcModel.getComponent(childrenName);
                    if (child.isPresent()) {
                        childCount += 1;
                        complexityTotal += child.get().cyclo();
                    }
                }
                if (childCount != 0 && complexityTotal != 0) {
                    completedCmp.setCyclo(complexityTotal / childCount);
                }
            }

            // include the processed component's invocations into its parent
            // components
            for (final Component parentCmp : componentStack) {

                final Iterator<ComponentInvocation> invocationIterator = completedCmp.invocations().iterator();
                while (invocationIterator.hasNext()) {

                    // We do not want to bubble up type implementations and
                    // extensions to the parent component because a child class for example
                    // could extend its containing class component. Without this check
                    // this would cause the parent class to have a type extension to itself
                    // which will cause problems down the line.
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
        String componentName;
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
    private Component createComponent(Node node, ComponentType componentType) {
        final Component newCmp = new Component();
        newCmp.setPackageName(currentPkg);
        newCmp.setComponentType(componentType);
        if (componentType.isVariableComponent()) {
            newCmp.setLine(node.getRange().get().end.line);
        } else {
            newCmp.setLine(node.getBegin().get().line);
        }
        if (node.getComment().isPresent()) {
            newCmp.setComment(node.getComment().toString());
        }
        newCmp.setCode(String.join("\n", Arrays.copyOfRange(lines, node.getRange().get().begin.line - 1, node.getRange().get().end.line)));
        newCmp.setSourceFilePath(file.name());
        return newCmp;
    }

    @Override
    public final void visit(PackageDeclaration ctx, Object arg) {
        currentPkg = ctx.getNameAsString();
        currentImports.clear();
        if (!componentStack.isEmpty()) {
            System.out.println(
                    "Clarity Java Listener found new package declaration while component stack not empty! component stack size is: "
                            + componentStack.size());
        }
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(ImportDeclaration ctx, Object arg) {
        final String fullImportName = ctx.getNameAsString().toString().trim().replaceAll(";", "");
        final String shortImportName = ctx.getName().getId().trim().replaceAll(";", "");
        currentImports.add(fullImportName);
        currentImportsMap.put(shortImportName, fullImportName);
        if (currentPkg.isEmpty()) {
            currentPkg = "";
        }
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(ClassExpr ctx, Object arg) {
        if (ctx.toString().endsWith(".class")) {
            for (Node node : ctx.getChildNodes()) {
                if (node.toString().equals(ctx.toString().substring(0, ctx.toString().indexOf(".class")))) {
                    ctx.remove(node);
                }
            }

        }
    }

    @Override
    public final void visit(ClassOrInterfaceDeclaration ctx, Object arg) {

        if (!componentStackContainsMethod()) {
            final Component cmp;
            if (ctx.isInterface()) {
                cmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.INTERFACE);
            } else {
                cmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.CLASS);
            }
            if (ctx.getTypeParameters().isNonEmpty()) {
                String fragment = "<";
                for (Type typeParam : ctx.getTypeParameters()) {
                    fragment += typeParam.asString() + ", ";
                }
                fragment = fragment.trim();
                if (fragment.endsWith(",")) {
                    fragment = fragment.substring(0, fragment.length() - 1);
                }
                cmp.setCodeFragment(fragment + ">");
            }

            cmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
            cmp.setComponentName(generateComponentName(ctx.getNameAsString()));
            cmp.setName(ctx.getNameAsString());
            cmp.setImports(currentImports);
            if (ctx.getComment().isPresent()) {
                for (String docMention : ClarpseUtil.extractDocTypeMentions(ctx.getComment().get().toString())) {
                    cmp.insertComponentInvocation(new DocMention(resolveType(docMention)));
                }
                cmp.setComment(ctx.getComment().get().toString());
            }
            pointParentsToGivenChild(cmp);

            if (ctx.getExtendedTypes() != null) {
                for (final ClassOrInterfaceType outerType : ctx.getExtendedTypes()) {
                    cmp.insertComponentInvocation(new TypeExtension(resolveType(outerType.getNameAsString())));
                }
            }

            if (ctx.getImplementedTypes() != null) {
                for (final ClassOrInterfaceType outerType : ctx.getImplementedTypes()) {
                    cmp.insertComponentInvocation(new TypeImplementation(resolveType(outerType.getNameAsString())));
                }
            }

            for (final AnnotationExpr annot : ctx.getAnnotations()) {
                populateAnnotation(cmp, annot);
            }

            componentStack.push(cmp);
            for (final Node node : ctx.getChildNodes()) {
                if (node instanceof FieldDeclaration || node instanceof Statement || node instanceof Expression
                        || node instanceof MethodDeclaration || node instanceof ConstructorDeclaration
                        || node instanceof ClassOrInterfaceDeclaration || node instanceof EnumDeclaration) {
                    node.accept(this, arg);
                }
            }
            completeComponent();
        }
    }

    /**
     * Returns the full, unique type name of the given type.
     */
    private String resolveType(final String type) {

        if (currentImportsMap.containsKey(type)) {
            return currentImportsMap.get(type);
        }
        if (type.contains(".")) {
            return type;
        }
        if (OOPSourceModelConstants.getJavaDefaultClasses().containsKey(type)) {
            return OOPSourceModelConstants.getJavaDefaultClasses().get(type);
        }
        if (!currentPkg.isEmpty()) {
            return currentPkg + "." + type;
        } else {
            return type;
        }
    }

    @Override
    public final void visit(EnumDeclaration ctx, Object arg) {

        if (!componentStackContainsMethod()) {
            final Component enumCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.ENUM);
            enumCmp.setComponentName(generateComponentName(ctx.getNameAsString()));
            enumCmp.setImports(currentImports);
            enumCmp.setName(ctx.getNameAsString());
            enumCmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
            pointParentsToGivenChild(enumCmp);
            if (ctx.getComment().isPresent()) {
                enumCmp.setComment(ctx.getComment().get().toString());
            }
            for (final AnnotationExpr annot : ctx.getAnnotations()) {
                populateAnnotation(enumCmp, annot);
            }
            componentStack.push(enumCmp);
            for (final Node node : ctx.getChildNodes()) {
                if (node instanceof FieldDeclaration || node instanceof MethodDeclaration
                        || node instanceof ConstructorDeclaration || node instanceof ClassOrInterfaceDeclaration
                        || node instanceof EnumDeclaration || node instanceof EnumConstantDeclaration) {
                    node.accept(this, arg);
                }
            }
            completeComponent();
        }
    }

    private int countLogicalBinaryOperators(Node n) {
        int logicalBinaryOperators = 0;
        String[] codeLines = n.removeComment().toString().split("\\r?\\n");
        for (String codeLine : codeLines) {
            if (!codeLine.startsWith("/")) {
                logicalBinaryOperators += StringUtils.countMatches(codeLine, " && ");
                logicalBinaryOperators += StringUtils.countMatches(codeLine, " || ");
                logicalBinaryOperators += StringUtils.countMatches(codeLine, " ? ");
            }
        }
        return logicalBinaryOperators;
    }

    @Override
    public final void visit(final EnumConstantDeclaration ctx, Object arg) {

        final Component enumConstCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.ENUM_CONSTANT);
        enumConstCmp.setName(ctx.getNameAsString());
        enumConstCmp.setComponentName(generateComponentName(ctx.getNameAsString()));
        pointParentsToGivenChild(enumConstCmp);
        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(enumConstCmp, annot);
        }
        if (ctx.getComment().isPresent()) {
            enumConstCmp.setComment(ctx.getComment().get().toString());
        }
        componentStack.push(enumConstCmp);
        super.visit(ctx, arg);
        completeComponent();
    }

    @Override
    public final void visit(final MethodDeclaration ctx, Object arg) {

        currCyclomaticComplexity = 0;
        if (!componentStackContainsMethod()) {
            final Component currMethodCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.METHOD);
            currMethodCmp.setName(ctx.getNameAsString());
            currMethodCmp.setCodeFragment(ctx.getType().asString());
            currMethodCmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
            String formalParametersString = "(";
            if (ctx.getParameters() != null) {
                formalParametersString += getFormalParameterTypesList(ctx.getParameters());
            }
            formalParametersString += ")";

            if (ctx.getComment().isPresent()) {
                currMethodCmp.setComment(ctx.getComment().get().toString());
            }
            for (final AnnotationExpr annot : ctx.getAnnotations()) {
                populateAnnotation(currMethodCmp, annot);
            }

            currCyclomaticComplexity += ctx.findAll(ReturnStmt.class).size() + 1;

            for (final ReferenceType stmt : ctx.getThrownExceptions()) {
                currMethodCmp.insertComponentInvocation(
                        new ThrownException(resolveType(stmt.getMetaModel().getTypeName())));
            }
            final String methodSignature = currMethodCmp.name() + formalParametersString;
            String codeFragment = currMethodCmp.name() + formalParametersString;
            if (ctx.getType().toString() != null && !ctx.getType().toString().equals("void")) {
                codeFragment += " : " + ctx.getType().toString();
            }
            currMethodCmp.setCodeFragment(codeFragment);
            currMethodCmp.setComponentName(generateComponentName(methodSignature));
            pointParentsToGivenChild(currMethodCmp);
            componentStack.push(currMethodCmp);
            if (ctx.getParameters() != null) {
                for (final Parameter param : ctx.getParameters()) {
                    final Component methodParamCmp = createComponent(param,
                            OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
                    methodParamCmp.setName(param.getNameAsString());
                    methodParamCmp.setCodeFragment(param.getType().asString());
                    for (final AnnotationExpr annot : param.getAnnotations()) {
                        populateAnnotation(methodParamCmp, annot);
                    }
                    methodParamCmp.setComponentName(generateComponentName(param.getNameAsString()));
                    methodParamCmp.setAccessModifiers(resolveJavaParserModifiers(param.getModifiers()));
                    methodParamCmp.insertComponentInvocation(
                            new TypeDeclaration(resolveType(param.getType().asString())));
                    pointParentsToGivenChild(methodParamCmp);
                    componentStack.push(methodParamCmp);
                    completeComponent();
                }

            }
            currCyclomaticComplexity += countLogicalBinaryOperators(ctx);
            super.visit(ctx, arg);
            completeComponent();
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

    private String getFormalParameterTypesList(final List<Parameter> formalParameterList) {

        String typesList = "";
        for (final Parameter fpContext : formalParameterList) {
            typesList += fpContext.getType().toString().trim() + ", ";
        }
        typesList = typesList.trim();
        while (typesList.trim().endsWith(",")) {
            typesList = typesList.substring(0, typesList.length() - 1).trim();
        }
        return typesList;
    }

    @Override
    public final void visit(final ConstructorDeclaration ctx, Object arg) {
        currCyclomaticComplexity = 0;
        if (!componentStackContainsMethod()) {
            final Component currMethodCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.CONSTRUCTOR);
            final String methodName = ctx.getNameAsString();
            currMethodCmp.setName(methodName);

            currMethodCmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));

            if (ctx.getComment().isPresent()) {
                currMethodCmp.setComment(ctx.getComment().get().toString());
            }

            currMethodCmp.setCodeFragment("void");

            currCyclomaticComplexity += ctx.findAll(ReturnStmt.class).size() + 1;

            String formalParametersString = "(";
            if (ctx.getParameters() != null) {
                formalParametersString += getFormalParameterTypesList(ctx.getParameters());
            }
            formalParametersString += ")";

            for (final AnnotationExpr annot : ctx.getAnnotations()) {
                populateAnnotation(currMethodCmp, annot);
            }

            for (final ReferenceType stmt : ctx.getThrownExceptions()) {
                currMethodCmp.insertComponentInvocation(
                        new ThrownException(resolveType(stmt.getMetaModel().getTypeName())));
            }

            final String methodSignature = currMethodCmp.name() + formalParametersString;
            String codeFragment = currMethodCmp.name() + formalParametersString;
            currMethodCmp.setCodeFragment(codeFragment);
            currMethodCmp.setComponentName(generateComponentName(methodSignature));
            pointParentsToGivenChild(currMethodCmp);
            componentStack.push(currMethodCmp);
            if (ctx.getParameters() != null) {
                for (final Parameter param : ctx.getParameters()) {
                    final Component methodParamCmp = createComponent(param,
                            OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT);
                    methodParamCmp.setCodeFragment(param.getType().asString());
                    methodParamCmp.setName(param.getNameAsString());
                    for (final AnnotationExpr annot : param.getAnnotations()) {
                        populateAnnotation(methodParamCmp, annot);
                    }
                    methodParamCmp.setComponentName(generateComponentName(param.getNameAsString()));
                    methodParamCmp.setAccessModifiers(resolveJavaParserModifiers(param.getModifiers()));
                    methodParamCmp.insertComponentInvocation(
                            new TypeDeclaration(resolveType(param.getType().asString())));
                    pointParentsToGivenChild(methodParamCmp);
                    componentStack.push(methodParamCmp);
                    completeComponent();
                }
            }
            currCyclomaticComplexity += countLogicalBinaryOperators(ctx);
            super.visit(ctx, arg);
            completeComponent();
        }
    }

    private List<String> resolveJavaParserModifiers(EnumSet<Modifier> modifiers) {
        final List<String> modifierList = new ArrayList<>();
        for (Modifier modifier : modifiers) {
            modifierList.add(modifier.toString().toLowerCase().trim());
        }
        return modifierList;
    }

    @Override
    public final void visit(IfStmt ctx, Object arg) {
        currCyclomaticComplexity += 1;
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(CatchClause ctx, Object arg) {
        currCyclomaticComplexity += 1;
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(ForeachStmt ctx, Object arg) {
        currCyclomaticComplexity += 1;
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(ForStmt ctx, Object arg) {
        currCyclomaticComplexity += 1;
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(WhileStmt ctx, Object arg) {
        currCyclomaticComplexity += 1;
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(ThrowStmt ctx, Object arg) {
        currCyclomaticComplexity += 1;
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(SwitchStmt ctx, Object arg) {
        for (SwitchEntryStmt sEStmt : ctx.getEntries()) {
            if (sEStmt.isSwitchEntryStmt() &&
                    (sEStmt.getStatements().size() > 0 && !sEStmt.getStatements().get(0).toString().equals("break;"))) {
                currCyclomaticComplexity += 1;
            }
        }
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(VariableDeclarationExpr ctx, Object arg) {
        try {
            final Component cmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.LOCAL);
            for (final AnnotationExpr annot : ctx.getAnnotations()) {
                populateAnnotation(cmp, annot);
            }

            cmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
            final List<Component> vars = new ArrayList<>();
            for (final VariableDeclarator copy : ctx.getVariables()) {
                final Component tmp = new Component(cmp);
                tmp.setName(copy.getNameAsString());
                tmp.setComponentName(generateComponentName(copy.getNameAsString()));
                pointParentsToGivenChild(tmp);
                vars.add(tmp);
            }

            for (final Component tmpCmp : vars) {
                componentStack.push(tmpCmp);
            }

            super.visit(ctx, arg);

            int numVars = ctx.getVariables().size();
            for (int i = 0; i < numVars; i++) {
                completeComponent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void visit(FieldDeclaration ctx, Object arg) {

        if (!componentStack.isEmpty()) {

            try {
                final Component currCmp = componentStack.peek();
                final Component cmp;

                if (currCmp.componentType().equals(OOPSourceModelConstants.getJavaComponentTypes()
                        .get(OOPSourceModelConstants.ComponentType.INTERFACE))) {
                    cmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.INTERFACE_CONSTANT);
                } else {
                    cmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.FIELD);
                }
                for (final AnnotationExpr annot : ctx.getAnnotations()) {
                    populateAnnotation(cmp, annot);
                }
                if (ctx.getComment().isPresent()) {
                    cmp.setComment(ctx.getComment().get().toString());
                }
                cmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
                final List<Component> vars = new ArrayList<>();
                for (final VariableDeclarator copy : ctx.getVariables()) {
                    final Component tmp = new Component(cmp);
                    tmp.setName(copy.getNameAsString());
                    tmp.setCodeFragment(tmp.name() + " : " + copy.getType().toString());
                    tmp.setComponentName(generateComponentName(copy.getNameAsString()));
                    pointParentsToGivenChild(tmp);
                    vars.add(tmp);
                }

                for (final Component tmpCmp : vars) {
                    componentStack.push(tmpCmp);
                }
                super.visit(ctx, arg);

                int numVars = ctx.getVariables().size();
                for (int i = 0; i < numVars; i++) {
                    completeComponent();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void populateAnnotation(Component cmp, AnnotationExpr annotation) {

        if (annotation != null) {
            String typeName = "";
            final HashMap<String, String> elementValuePairs = new HashMap<>();
            if (annotation instanceof NormalAnnotationExpr) {
                final NormalAnnotationExpr expr = (NormalAnnotationExpr) annotation;
                typeName = resolveAnnotationType(expr.asNormalAnnotationExpr().getNameAsString());
                for (final MemberValuePair evctx : expr.getPairs()) {
                    elementValuePairs.put(evctx.getNameAsString(), evctx.getValue().toString());
                }
            } else if (annotation instanceof MarkerAnnotationExpr) {
                final MarkerAnnotationExpr expr = (MarkerAnnotationExpr) annotation;
                typeName = resolveAnnotationType(expr.asMarkerAnnotationExpr().getChildNodes().get(0).toString());
            } else if (annotation instanceof SingleMemberAnnotationExpr) {
                final SingleMemberAnnotationExpr expr = (SingleMemberAnnotationExpr) annotation;
                typeName = resolveAnnotationType(expr.asSingleMemberAnnotationExpr().getNameAsString());
                elementValuePairs.put("", expr.getMemberValue().toString());
            }
            cmp.insertComponentInvocation(new AnnotationInvocation(typeName,
                    new SimpleEntry<>(typeName, elementValuePairs)));
        }
    }

    private String resolveAnnotationType(String annotationType) {
        if (OOPSourceModelConstants.getJavaPredefinedAnnotations().containsKey(annotationType)) {
            return annotationType;
        } else {
            return resolveType(annotationType);
        }
    }

    @Override
    public final void visit(ClassOrInterfaceType ctx, Object arg) {

        if (!componentStack.isEmpty()) {
            final Component currCmp = componentStack.peek();
            currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(ctx.asClassOrInterfaceType().getNameAsString())));
            List<Type> typeArguments = new ArrayList<>();
            if (ctx.getTypeArguments().isPresent()) {
                typeArguments.addAll(ctx.getTypeArguments().get());
                for (Type typeArg : typeArguments) {
                    if (typeArg.isClassOrInterfaceType()) {
                        if (((ClassOrInterfaceType) typeArg).getTypeArguments().isPresent()) {
                            visit(((ClassOrInterfaceType) typeArg), arg);
                        } else {
                            currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(typeArg.asClassOrInterfaceType().getNameAsString())));
                        }
                    } else {
                        currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(typeArg.asString())));
                    }
                }
            }
        }
    }

    @Override
    public final void visit(PrimitiveType ctx, Object arg) {
        if (!componentStack.isEmpty()) {
            final Component currCmp = componentStack.pop();
            currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(ctx.asString())));
            componentStack.push(currCmp);
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
