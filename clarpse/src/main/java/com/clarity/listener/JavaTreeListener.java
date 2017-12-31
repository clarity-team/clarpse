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
import com.clarity.invocation.TypeParameter;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import com.clarity.sourcemodel.OOPSourceModelConstants.AccessModifiers;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
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
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * As the parse tree is developed by JavaParser, we add listener methods to
 * procedurally capture important information during this process and populate
 * our Source Code Model.
 */
public class JavaTreeListener extends VoidVisitorAdapter<Object> {

    private final Stack<Component> componentStack = new Stack<Component>();
    private final ArrayList<String> currentImports = new ArrayList<String>();
    private String currentPkg = "";
    private final OOPSourceCodeModel srcModel;
    private final Map<String, String> currentImportsMap = new HashMap<String, String>();
    private final RawFile file;

    /**
     * @param srcModel
     *            Source model to populate from the parsing of the given code base.
     * @param file
     *            The path of the source file being parsed.
     */
    public JavaTreeListener(final OOPSourceCodeModel srcModel, final RawFile file) {
        this.srcModel = srcModel;
        this.file = file;
    }

    public void populateModel() throws IOException {

        JavaParser.setDoNotConsiderAnnotationsAsNodeStartForCodeAttribution(false);
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
    private Component createComponent(Node node, ComponentType componentType) {
        final Component newCmp = new Component();
        newCmp.setPackageName(currentPkg);
        newCmp.setComponentType(componentType);
        if (node.getComment() != null) {
            newCmp.setComment(node.getComment().toString());
        }
        newCmp.setSourceFilePath(file.name());
        return newCmp;
    }

    @Override
    public final void visit(PackageDeclaration ctx, Object arg) {
        currentPkg = ctx.getPackageName();
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
        final String fullImportName = ctx.getName().toString().trim().replaceAll(";", "");
        final String shortImportName = ctx.getName().getName().toString().trim().replaceAll(";", "");
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
            for (Node node : ctx.getChildrenNodes()) {
                if (node.toString().equals(ctx.toString().substring(0, ctx.toString().indexOf(".class")))) {
                    ctx.getChildrenNodes().remove(node);
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
            cmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
            cmp.setComponentName(generateComponentName(ctx.getName()));
            cmp.setName(ctx.getName());
            cmp.setImports(currentImports);
            if (ctx.getJavaDoc() != null) {
                for (String docMention : ClarpseUtil.extractDocTypeMentions(ctx.getJavaDoc().toString())) {
                    cmp.insertComponentInvocation(new DocMention(resolveType(docMention)));
                }
                cmp.setComment(ctx.getJavaDoc().toString());
            }
            pointParentsToGivenChild(cmp);

            if (ctx.getExtends() != null) {
                for (final ClassOrInterfaceType outerType : ctx.getExtends()) {
                    cmp.insertComponentInvocation(new TypeExtension(resolveType(outerType.getName())));
                }
            }

            if (ctx.getImplements() != null) {
                for (final ClassOrInterfaceType outerType : ctx.getImplements()) {
                    cmp.insertComponentInvocation(new TypeImplementation(resolveType(outerType.getName())));
                }
            }

            for (final AnnotationExpr annot : ctx.getAnnotations()) {
                populateAnnotation(cmp, annot);
            }

            componentStack.push(cmp);
            for (final Node node : ctx.getChildrenNodes()) {
                if (node instanceof FieldDeclaration || node instanceof Statement || node instanceof Expression
                        || node instanceof MethodDeclaration || node instanceof ConstructorDeclaration
                        || node instanceof ClassOrInterfaceDeclaration || node instanceof EnumDeclaration) {
                    node.accept(this, arg);
                }
            }
            completeComponent();
        }
    }

    @Override
    public final void visit(com.github.javaparser.ast.TypeParameter ctx, Object arg) {
        final Component currComponent = componentStack.pop();
        if (ctx.getTypeBound() != null) {
            for (final ClassOrInterfaceType type : ctx.getTypeBound()) {
                for (final Type innerType : type.getTypeArgs()) {
                    currComponent.insertComponentInvocation(
                            new TypeParameter(resolveType(innerType.toStringWithoutComments())));
                }
            }
        }
        componentStack.push(currComponent);
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
            enumCmp.setComponentName(generateComponentName(ctx.getName()));
            enumCmp.setImports(currentImports);
            enumCmp.setName(ctx.getName());
            enumCmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
            pointParentsToGivenChild(enumCmp);
            if (ctx.getJavaDoc() != null) {
                enumCmp.setComment(ctx.getJavaDoc().toString());
            }
            for (final AnnotationExpr annot : ctx.getAnnotations()) {
                populateAnnotation(enumCmp, annot);
            }
            componentStack.push(enumCmp);
            for (final Node node : ctx.getChildrenNodes()) {
                if (node instanceof FieldDeclaration || node instanceof MethodDeclaration
                        || node instanceof ConstructorDeclaration || node instanceof ClassOrInterfaceDeclaration
                        || node instanceof EnumDeclaration || node instanceof EnumConstantDeclaration) {
                    node.accept(this, arg);
                }
            }
            completeComponent();
        }
    }

    @Override
    public final void visit(final EnumConstantDeclaration ctx, Object arg) {

        final Component enumConstCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.ENUM_CONSTANT);
        enumConstCmp.setName(ctx.getName());
        enumConstCmp.setComponentName(generateComponentName(ctx.getName()));
        pointParentsToGivenChild(enumConstCmp);
        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(enumConstCmp, annot);
        }
        if (ctx.getJavaDoc() != null) {
            enumConstCmp.setComment(ctx.getJavaDoc().toString());
        }
        componentStack.push(enumConstCmp);
        super.visit(ctx, arg);
        completeComponent();
    }

    @Override
    public final void visit(final MethodDeclaration ctx, Object arg) {

        final Component currMethodCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.METHOD);
        currMethodCmp.setName(ctx.getName());
        if (ctx.getType().toString() != null && !ctx.getType().toString().equals("void")) {
            currMethodCmp.setValue(resolveType(ctx.getType().toString()));
        } else {
            currMethodCmp.setValue("void");
        }
        currMethodCmp.setDeclarationTypeSnippet(ctx.getType().toStringWithoutComments());
        currMethodCmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
        String formalParametersString = "(";
        if (ctx.getParameters() != null) {
            formalParametersString += getFormalParameterTypesList(ctx.getParameters());
        }
        formalParametersString += ")";

        if (ctx.getJavaDoc() != null) {
            currMethodCmp.setComment(ctx.getJavaDoc().toString());
        }
        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(currMethodCmp, annot);
        }

        for (final ReferenceType stmt : ctx.getThrows()) {
            currMethodCmp.insertComponentInvocation(
                    new ThrownException(resolveType(stmt.getType().toStringWithoutComments())));
        }
        final String methodSignature = currMethodCmp.name() + formalParametersString;
        currMethodCmp.setComponentName(generateComponentName(methodSignature));
        pointParentsToGivenChild(currMethodCmp);
        componentStack.push(currMethodCmp);
        if (ctx.getParameters() != null) {
            for (final Parameter param : ctx.getParameters()) {
                final Component methodParamCmp = createComponent(param,
                        OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
                methodParamCmp.setName(param.getName());
                methodParamCmp.setDeclarationTypeSnippet(param.getType().toStringWithoutComments());
                for (final AnnotationExpr annot : param.getAnnotations()) {
                    populateAnnotation(methodParamCmp, annot);
                }
                methodParamCmp.setComponentName(generateComponentName(param.getName()));
                methodParamCmp.setAccessModifiers(resolveJavaParserModifiers(param.getModifiers()));
                methodParamCmp.insertComponentInvocation(
                        new TypeDeclaration(resolveType(param.getType().toStringWithoutComments())));
                methodParamCmp.uniqueName();
                pointParentsToGivenChild(methodParamCmp);
                componentStack.push(methodParamCmp);
                completeComponent();
            }

        }
        super.visit(ctx, arg);
        completeComponent();
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
            typesList += resolveType(fpContext.getType().toString()) + ",";
        }
        if (typesList.endsWith(",")) {
            typesList = typesList.substring(0, typesList.length() - 1);
        }
        return typesList;
    }

    @Override
    public final void visit(final ConstructorDeclaration ctx, Object arg) {

        final Component currMethodCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.CONSTRUCTOR);

        currMethodCmp.setValue("void");

        final String methodName = ctx.getName();
        currMethodCmp.setName(methodName);

        currMethodCmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));

        if (ctx.getJavaDoc() != null) {
            currMethodCmp.setComment(ctx.getJavaDoc().toString());
        }

        currMethodCmp.setDeclarationTypeSnippet("void");

        String formalParametersString = "(";
        if (ctx.getParameters() != null) {
            formalParametersString += getFormalParameterTypesList(ctx.getParameters());
        }
        formalParametersString += ")";

        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(currMethodCmp, annot);
        }

        for (final ReferenceType stmt : ctx.getThrows()) {
            currMethodCmp.insertComponentInvocation(
                    new ThrownException(resolveType(stmt.getType().toStringWithoutComments())));
        }

        final String methodSignature = currMethodCmp.name() + formalParametersString;
        currMethodCmp.setComponentName(generateComponentName(methodSignature));
        pointParentsToGivenChild(currMethodCmp);
        componentStack.push(currMethodCmp);
        if (ctx.getParameters() != null) {
            for (final Parameter param : ctx.getParameters()) {
                final Component methodParamCmp = createComponent(param,
                        OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT);
                methodParamCmp.setDeclarationTypeSnippet(param.getType().toStringWithoutComments());
                methodParamCmp.setName(param.getName());
                for (final AnnotationExpr annot : param.getAnnotations()) {
                    populateAnnotation(methodParamCmp, annot);
                }
                methodParamCmp.setComponentName(generateComponentName(param.getName()));
                methodParamCmp.setAccessModifiers(resolveJavaParserModifiers(param.getModifiers()));
                methodParamCmp.insertComponentInvocation(
                        new TypeDeclaration(resolveType(param.getType().toStringWithoutComments())));
                pointParentsToGivenChild(methodParamCmp);
                componentStack.push(methodParamCmp);
                completeComponent();
            }
        }

        super.visit(ctx, arg);
        completeComponent();
    }

    private List<String> resolveJavaParserModifiers(int modifiers) {
        final List<String> modifierList = new ArrayList<String>();

        if (ModifierSet.isAbstract(modifiers)) {
            modifierList.add(AccessModifiers.ABSTRACT.toString());
        }
        if (ModifierSet.isFinal(modifiers)) {
            modifierList.add(AccessModifiers.FINAL.toString());
        }
        if (ModifierSet.isPrivate(modifiers)) {
            modifierList.add(AccessModifiers.PRIVATE.toString());
        }
        if (ModifierSet.isNative(modifiers)) {
            modifierList.add(AccessModifiers.NATIVE.toString());
        }
        if (ModifierSet.isProtected(modifiers)) {
            modifierList.add(AccessModifiers.PROTECTED.toString());
        }
        if (ModifierSet.isPublic(modifiers)) {
            modifierList.add(AccessModifiers.PUBLIC.toString());
        }
        if (ModifierSet.isStatic(modifiers)) {
            modifierList.add(AccessModifiers.STATIC.toString());
        }
        if (ModifierSet.isStrictfp(modifiers)) {
            modifierList.add(AccessModifiers.STRICTFP.toString());
        }
        if (ModifierSet.isSynchronized(modifiers)) {
            modifierList.add(AccessModifiers.SYNCHRONIZED.toString());
        }
        return modifierList;
    }

    @Override
    public final void visit(VariableDeclarationExpr ctx, Object arg) {

        final Component cmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.LOCAL);
        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(cmp, annot);
        }

        cmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
        cmp.setValue(ctx.getType().toStringWithoutComments());
        final List<Component> vars = new ArrayList<Component>();
        for (final VariableDeclarator copy : ctx.getVars()) {
            final Component tmp = new Component(cmp);
            tmp.setName(copy.getId().getName());
            tmp.setComponentName(generateComponentName(copy.getId().getName()));
            pointParentsToGivenChild(tmp);
            vars.add(tmp);
        }

        for (final Component tmpCmp : vars) {
            componentStack.push(tmpCmp);
        }

        super.visit(ctx, arg);

        int numVars = ctx.getVars().size();
        for (int i = 0; i < numVars; i++) {
            completeComponent();
        }
    }

    @Override
    public final void visit(FieldDeclaration ctx, Object arg) {

        if (!componentStack.isEmpty()) {
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
            if (ctx.getJavaDoc() != null) {
                cmp.setComment(ctx.getJavaDoc().toString());
            }
            cmp.setValue(ctx.getType().toStringWithoutComments());
            cmp.setAccessModifiers(resolveJavaParserModifiers(ctx.getModifiers()));
            final List<Component> vars = new ArrayList<Component>();
            for (final VariableDeclarator copy : ctx.getVariables()) {
                final Component tmp = new Component(cmp);
                tmp.setName(copy.getId().getName());
                tmp.setComponentName(generateComponentName(copy.getId().getName()));
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
        }
    }

    private void populateAnnotation(Component cmp, AnnotationExpr annotation) {

        if (annotation != null) {
            String typeName = "";
            final HashMap<String, String> elementValuePairs = new HashMap<String, String>();
            if (annotation instanceof NormalAnnotationExpr) {
                final NormalAnnotationExpr expr = (NormalAnnotationExpr) annotation;
                typeName = resolveAnnotationType(expr.getName().getName());
                for (final MemberValuePair evctx : expr.getPairs()) {
                    elementValuePairs.put(evctx.getName(), evctx.getValue().toStringWithoutComments());
                }
            } else if (annotation instanceof MarkerAnnotationExpr) {
                final MarkerAnnotationExpr expr = (MarkerAnnotationExpr) annotation;
                typeName = resolveAnnotationType(expr.getName().getName());
            } else if (annotation instanceof SingleMemberAnnotationExpr) {
                final SingleMemberAnnotationExpr expr = (SingleMemberAnnotationExpr) annotation;
                typeName = resolveAnnotationType(expr.getName().getName());
                elementValuePairs.put("", expr.getMemberValue().toStringWithoutComments());
            }
            cmp.insertComponentInvocation(new AnnotationInvocation(typeName,
                    new SimpleEntry<String, HashMap<String, String>>(typeName, elementValuePairs)));
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
            final Component currCmp = componentStack.pop();
            currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(ctx.getName())));
            if (ctx.getTypeArgs() != null) {
                for (final Type type : ctx.getTypeArgs()) {
                    currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(type.toStringWithoutComments())));
                }
            }
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void visit(PrimitiveType ctx, Object arg) {
        if (!componentStack.isEmpty()) {
            final Component currCmp = componentStack.pop();
            currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(ctx.toString())));
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
