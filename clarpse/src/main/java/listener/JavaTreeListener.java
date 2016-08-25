package listener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.nio.charset.StandardCharsets;

import com.clarity.invocation.AnnotationInvocation;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.ThrownException;
import com.clarity.invocation.TypeDeclaration;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.invocation.TypeReferenceInvocation;
import com.clarity.invocation.sources.BindedInvocationSource;
import com.clarity.invocation.sources.InvocationSource;
import com.clarity.invocation.sources.InvocationSourceChain;
import com.clarity.invocation.sources.MethodInvocationSourceChain;
import com.clarity.invocation.sources.MethodInvocationSourceImpl;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import com.clarity.sourcemodel.OOPSourceModelConstants.AccessModifiers;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.TypeParameter;
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
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * As the parse tree is developed by JavaParser, we add listener methods to
 * procedurally capture important information during this process and populate
 * our Source Code Model.
 *
 * @author Muntazir Fadhel
 */
public class JavaTreeListener extends VoidVisitorAdapter {

    private final Stack<Component> componentStack = new Stack<Component>();
    private final ArrayList<String> currentImports = new ArrayList<String>();
    private String currentPkg = "";
    private final OOPSourceCodeModel srcModel;
    private final Map<String, String> currentImportsMap = new HashMap<String, String>();
    private final RawFile file;
    // key = required component name, value = blocked invocation source
    private volatile Map<String, List<InvocationSourceChain>> blockedInvocationSources;

    /**
     * @param srcModel
     *            Source model to populate from the parsing of the given code
     *            base.
     * @param file
     *            The path of the source file being parsed.
     */
    public JavaTreeListener(final OOPSourceCodeModel srcModel,
            final RawFile file,
            Map<String, List<InvocationSourceChain>> blockedInvocationSources) {
        this.srcModel = srcModel;
        this.file = file;
        this.blockedInvocationSources = blockedInvocationSources;
    }

    public void populateModel() throws IOException {

        CompilationUnit cu;
        ByteArrayInputStream in = null;
        try {
            in = new ByteArrayInputStream(file.content().getBytes(
                    StandardCharsets.UTF_8));
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
            srcModel.insertComponent(completedCmp);
            final List<InvocationSourceChain> blockedSources = blockedInvocationSources
                    .get(completedCmp.uniqueName());
            if (blockedSources != null) {
                final List<InvocationSourceChain> blockedSourcesCopy = new ArrayList<InvocationSourceChain>(
                        blockedSources);
                for (final InvocationSourceChain src : blockedSourcesCopy) {
                    src.process();
                }
            }
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
            newCmp.setComment(node.getComment().getContent());
        }
        newCmp.setStartLine(String.valueOf(node.getBegin().line));
        newCmp.setEndLine(String.valueOf(node.getEnd().line));
        newCmp.setSourceFilePath(file.name());
        return newCmp;
    }

    @Override
    public final void visit(PackageDeclaration ctx, Object arg) {
        currentPkg = ctx.getPackageName();
        currentImports.clear();
        if (!componentStack.isEmpty()) {
            System.out
                    .println("Clarity Java Listener found new package declaration while component stack not empty! component stack size is: "
                            + componentStack.size());
        }
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(ImportDeclaration ctx, Object arg) {
        final String fullImportName = ctx.getName().toString().trim()
                .replaceAll(";", "");
        final String shortImportName = ctx.getName().getName().toString()
                .trim().replaceAll(";", "");
        currentImports.add(fullImportName);
        currentImportsMap.put(shortImportName, fullImportName);
        if (currentPkg.isEmpty()) {
            currentPkg = "";
        }
        super.visit(ctx, arg);
    }

    @Override
    public final void visit(ClassOrInterfaceDeclaration ctx, Object arg) {

        final Component cmp;
        if (ctx.isInterface()) {
            cmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.INTERFACE_COMPONENT);
        } else {
            cmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.CLASS_COMPONENT);
        }

        cmp.setComponentName(generateComponentName(ctx.getName()));
        cmp.setName(ctx.getName());
        cmp.setImports(currentImports);
        pointParentsToGivenChild(cmp);

        if (ctx.getExtends() != null) {
            for (final ClassOrInterfaceType outerType : ctx.getExtends()) {
                cmp.insertComponentInvocation(new TypeExtension(
                        resolveType(outerType.getName()), ctx.getBegin().line));
            }
        }

        if (ctx.getImplements() != null) {
            for (final ClassOrInterfaceType outerType : ctx.getImplements()) {
                cmp.insertComponentInvocation(new TypeImplementation(
                        resolveType(outerType.getName()), ctx.getBegin().line));
            }
        }

        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(cmp, annot);
        }

        componentStack.push(cmp);
        super.visit(ctx, arg);
        completeComponent();
    }

    @Override
    public final void visit(TypeParameter ctx, Object arg) {
        final Component currComponent = componentStack.pop();
        if (ctx.getTypeBound() != null) {
            for (final ClassOrInterfaceType type : ctx.getTypeBound()) {
                for (final Type innerType : type.getTypeArgs()) {
                    currComponent
                            .insertComponentInvocation(new TypeReferenceInvocation(
                                    resolveType(innerType
                                            .toStringWithoutComments()), type
                                            .getBegin().line));
                }
            }
        }
        componentStack.push(currComponent);
    }

    /**
     * Returns the extend type name of the given type.
     *
     * @param type
     *            type to resolve
     * @return full name of the given type
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

        final Component enumCmp = createComponent(ctx,
                OOPSourceModelConstants.ComponentType.ENUM_COMPONENT);
        enumCmp.setComponentName(generateComponentName(ctx.getName()));
        enumCmp.setImports(currentImports);
        enumCmp.setName(ctx.getName());
        pointParentsToGivenChild(enumCmp);
        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(enumCmp, annot);
        }
        componentStack.push(enumCmp);
        super.visit(ctx, arg);
        completeComponent();
    }

    @Override
    public final void visit(final EnumConstantDeclaration ctx, Object arg) {

        final Component enumConstCmp = createComponent(ctx,
                OOPSourceModelConstants.ComponentType.ENUM_CONSTANT_COMPONENT);
        enumConstCmp.setName(ctx.getName());
        enumConstCmp.setComponentName(generateComponentName(ctx.getName()));
        pointParentsToGivenChild(enumConstCmp);
        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(enumConstCmp, annot);
        }
        componentStack.push(enumConstCmp);
        super.visit(ctx, arg);
        completeComponent();
    }

    @Override
    public final void visit(final MethodDeclaration ctx, Object arg) {

        final Component currMethodCmp = createComponent(ctx,
                OOPSourceModelConstants.ComponentType.METHOD_COMPONENT);
        currMethodCmp.setName(ctx.getName());
        if (ctx.getType().toString() != null
                && !ctx.getType().toString().equals("void")) {
            currMethodCmp.setValue(resolveType(ctx.getType().toString()));
        } else {
            currMethodCmp.setValue("void");
        }

        String formalParametersString = "(";
        if (ctx.getParameters() != null) {
            formalParametersString += getFormalParameterTypesList(ctx
                    .getParameters());
        }
        formalParametersString += ")";

        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(currMethodCmp, annot);
        }

        for (final ReferenceType stmt : ctx.getThrows()) {
            currMethodCmp.insertComponentInvocation(new ThrownException(
                    resolveType(stmt.getType().toStringWithoutComments()), stmt
                            .getBegin().line));
        }
        final String methodSignature = currMethodCmp.name()
                + formalParametersString;
        currMethodCmp.setComponentName(generateComponentName(methodSignature));
        pointParentsToGivenChild(currMethodCmp);
        componentStack.push(currMethodCmp);
        if (ctx.getParameters() != null) {
            for (final Parameter param : ctx.getParameters()) {
                final Component methodParamCmp = createComponent(
                        param,
                        OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
                methodParamCmp.setName(param.getName());
                for (final AnnotationExpr annot : param.getAnnotations()) {
                    populateAnnotation(methodParamCmp, annot);
                }
                methodParamCmp.setComponentName(generateComponentName(param
                        .getName()));
                methodParamCmp
                        .setAccessModifiers(resolveJavaParserModifiers(param
                                .getModifiers()));
                methodParamCmp.insertComponentInvocation(new TypeDeclaration(
                        resolveType(param.getType().toStringWithoutComments()),
                        param.getBegin().line));
                methodParamCmp.uniqueName();
                pointParentsToGivenChild(methodParamCmp);
                componentStack.push(methodParamCmp);
                completeComponent();
            }

        }
        super.visit(ctx, arg);
        completeComponent();
    }

    private String getFormalParameterTypesList(
            final List<Parameter> formalParameterList) {

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

        final Component currMethodCmp = createComponent(ctx,
                OOPSourceModelConstants.ComponentType.CONSTRUCTOR_COMPONENT);

        currMethodCmp.setValue("void");

        final String methodName = ctx.getName();
        currMethodCmp.setName(methodName);

        String formalParametersString = "(";
        if (ctx.getParameters() != null) {
            formalParametersString += getFormalParameterTypesList(ctx
                    .getParameters());
        }
        formalParametersString += ")";

        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(currMethodCmp, annot);
        }

        for (final ReferenceType stmt : ctx.getThrows()) {
            currMethodCmp.insertComponentInvocation(new ThrownException(
                    resolveType(stmt.getType().toStringWithoutComments()), stmt
                            .getBegin().line));
        }

        final String methodSignature = currMethodCmp.name()
                + formalParametersString;
        currMethodCmp.setComponentName(generateComponentName(methodSignature));
        pointParentsToGivenChild(currMethodCmp);
        componentStack.push(currMethodCmp);
        if (ctx.getParameters() != null) {
            for (final Parameter param : ctx.getParameters()) {
                final Component methodParamCmp = createComponent(
                        param,
                        OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT);
                methodParamCmp.setName(param.getName());
                for (final AnnotationExpr annot : param.getAnnotations()) {
                    populateAnnotation(methodParamCmp, annot);
                }
                methodParamCmp.setComponentName(generateComponentName(param
                        .getName()));
                methodParamCmp
                        .setAccessModifiers(resolveJavaParserModifiers(param
                                .getModifiers()));
                methodParamCmp.insertComponentInvocation(new TypeDeclaration(
                        resolveType(param.getType().toStringWithoutComments()),
                        param.getBegin().line));
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

        final Component cmp = createComponent(ctx,
                OOPSourceModelConstants.ComponentType.LOCAL_VARIABLE_COMPONENT);
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

        for (final VariableDeclarator copy : ctx.getVars()) {
            completeComponent();
        }
    }

    @Override
    public final void visit(FieldDeclaration ctx, Object arg) {

        final Component currCmp = componentStack.peek();
        final Component cmp;
        if (currCmp
                .componentType()
                .equals(OOPSourceModelConstants
                        .getJavaComponentTypes()
                        .get(OOPSourceModelConstants.ComponentType.INTERFACE_COMPONENT))) {
            cmp = createComponent(
                    ctx,
                    OOPSourceModelConstants.ComponentType.INTERFACE_CONSTANT_COMPONENT);
        } else {
            cmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.FIELD_COMPONENT);
        }
        for (final AnnotationExpr annot : ctx.getAnnotations()) {
            populateAnnotation(cmp, annot);
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

        for (final VariableDeclarator copy : ctx.getVariables()) {
            completeComponent();
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
                    elementValuePairs.put(evctx.getName(), evctx.getValue()
                            .toStringWithoutComments());
                }
            } else if (annotation instanceof MarkerAnnotationExpr) {
                final MarkerAnnotationExpr expr = (MarkerAnnotationExpr) annotation;
                typeName = resolveAnnotationType(expr.getName().getName());
            } else if (annotation instanceof SingleMemberAnnotationExpr) {
                final SingleMemberAnnotationExpr expr = (SingleMemberAnnotationExpr) annotation;
                typeName = resolveAnnotationType(expr.getName().getName());
                elementValuePairs.put("", expr.getMemberValue()
                        .toStringWithoutComments());
            }
            cmp.insertComponentInvocation(new AnnotationInvocation(typeName,
                    annotation.getBegin().line,
                    new SimpleEntry<String, HashMap<String, String>>(typeName,
                            elementValuePairs)));
        }
    }

    private String resolveAnnotationType(String annotationType) {
        if (OOPSourceModelConstants.getJavaPredefinedAnnotations().containsKey(
                annotationType)) {
            return annotationType;
        } else {
            return resolveType(annotationType);
        }
    }

    @Override
    public final void visit(ClassOrInterfaceType ctx, Object arg) {

        if (!componentStack.isEmpty()) {
            final Component currCmp = componentStack.pop();
            currCmp.insertComponentInvocation(new TypeDeclaration(
                    resolveType(ctx.getName()), ctx.getBegin().line));
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void visit(PrimitiveType ctx, Object arg) {
        if (!componentStack.isEmpty()) {
            final Component currCmp = componentStack.pop();
            currCmp.insertComponentInvocation(new TypeDeclaration(
                    resolveType(ctx.toString()), ctx.getBegin().line));
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void visit(MethodCallExpr ctx, Object arg) {
        if (!componentStack.isEmpty()) {
            final Component currCmp = componentStack.peek();
            final List<InvocationSource> methodSources = new ArrayList<InvocationSource>();

            while (ctx.getScope() != null
                    && ctx.getScope() instanceof MethodCallExpr) {

                methodSources.add(0, new BindedInvocationSource(
                        new MethodInvocationSourceImpl("", ctx.getNameExpr()
                                .getName(), ctx.getBegin().line, ctx.getArgs()
                                .size(), srcModel, blockedInvocationSources),
                        currCmp));
                ctx = (MethodCallExpr) ctx.getScope();
            }

            methodSources.add(
                    0,
                    new BindedInvocationSource(new MethodInvocationSourceImpl(
                            retrieveContainingClassName(ctx.getScope(), ctx
                                    .getNameExpr().getName()), ctx
                                    .getNameExpr().getName(),
                            ctx.getBegin().line, ctx.getArgs().size(),
                            srcModel, blockedInvocationSources), currCmp));
            final MethodInvocationSourceChain methodChain = new MethodInvocationSourceChain(
                    methodSources, srcModel, blockedInvocationSources);
            methodChain.process();
        }
        super.visit(ctx, arg);
    }

    private String retrieveContainingClassName(Expression expression,
            String methodName) {

        String containingClassName = "";

        // local method call..
        if (expression == null
                || expression.toStringWithoutComments().equals("this")) {
            final List<ComponentType> baseTypes = new ArrayList<ComponentType>();
            baseTypes.add(ComponentType.CLASS_COMPONENT);
            baseTypes.add(ComponentType.INTERFACE_COMPONENT);
            baseTypes.add(ComponentType.ENUM_COMPONENT);
            containingClassName = newestStackComponent(baseTypes).uniqueName();
        } else if (methodName.equals("super")) {
            final List<ComponentType> baseTypes = new ArrayList<ComponentType>();
            baseTypes.add(ComponentType.CLASS_COMPONENT);
            baseTypes.add(ComponentType.INTERFACE_COMPONENT);
            baseTypes.add(ComponentType.ENUM_COMPONENT);
            containingClassName = newestStackComponent(baseTypes)
                    .componentInvocations(ComponentInvocations.EXTENSION)
                    .get(0).invokedComponent();
        } else {
            String name = expression.toStringWithoutComments();
            // variable or static method call..
            final Component variableComponent = findLocalSourceFileComponent(name);
            if (variableComponent != null && variableComponent.name() != null) {
                final List<ComponentInvocation> typeInstantiations = variableComponent
                        .componentInvocations(ComponentInvocations.DECLARATION);
                if (!typeInstantiations.isEmpty()) {
                    containingClassName = typeInstantiations.get(0)
                            .invokedComponent();
                }
            } else {
                final String text = name;
                if (text.contains(".")) {
                    containingClassName = text;
                } else {
                    containingClassName = resolveType(text);
                }
            }
        }
        return containingClassName;
    }

    private Component newestStackComponent(List<ComponentType> possibleTypes) {

        Component newestComponent = new Component();
        final Iterator<Component> iter = componentStack.iterator();
        while (iter.hasNext()) {
            final Component next = iter.next();
            for (final ComponentType cmpType : possibleTypes) {
                if (next.componentType() == cmpType) {
                    newestComponent = next;
                }
            }
        }
        return newestComponent;
    }

    private Component findLocalSourceFileComponent(String componentShortName) {

        for (int i = componentStack.size() - 1; i >= 0; i--) {
            for (final String childCmpName : componentStack.get(i).children()) {
                if (childCmpName.endsWith("." + componentShortName)) {
                    return srcModel.getComponent(childCmpName);
                }
            }
        }
        return new Component();
    }

    private void pointParentsToGivenChild(Component childCmp) {

        if (!componentStack.isEmpty()) {
            final String parentName = childCmp.parentUniqueName();
            for (int i = componentStack.size() - 1; i >= 0; i--) {
                if (componentStack.get(i).uniqueName().equals(parentName)) {
                    componentStack.get(i).insertChildComponent(
                            childCmp.uniqueName());
                }
            }
        }
    }

    private int getArgumentsSize(MethodCallExpr ctx) {
        int invocationArguments = 0;
        if (ctx.getArgs() != null) {
            invocationArguments = ctx.getArgs().size();
        }
        return invocationArguments;
    }
}
