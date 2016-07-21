package com.clarity.parser.java;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import parser.java.JavaBaseListener;
import parser.java.JavaParser;
import parser.java.JavaParser.FormalParameterContext;
import parser.java.JavaParser.FormalParameterListContext;
import parser.java.JavaParser.ImplementsTypeContext;
import parser.java.JavaParser.MethodInvocationContext;

import com.clarity.invocation.AnnotationInvocation;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.ThrownException;
import com.clarity.invocation.TypeDeclaration;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.invocation.sources.BindedInvocationSource;
import com.clarity.invocation.sources.InvocationSource;
import com.clarity.invocation.sources.InvocationSourceChain;
import com.clarity.invocation.sources.MethodInvocationSourceChain;
import com.clarity.invocation.sources.MethodInvocationSourceImpl;
import com.clarity.parser.AntlrUtil;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;

/**
 * As the parse tree is developed by Antlr, we add listener methods to
 * procedurally capture important information during this process and populate
 * our Source Code Model.
 *
 * @author Muntazir Fadhel
 */
public class ClarpseJavaTreeListener extends JavaBaseListener {

    private final Stack<Component> componentStack = new Stack<Component>();
    private final ArrayList<String> currentImports = new ArrayList<String>();
    private String currentPkg = "";
    private String currFileSourceCode;
    private final OOPSourceCodeModel srcModel;
    private int componentCompletionMultiplier = 1;
    private final Map<String, String> currentImportsMap = new HashMap<String, String>();
    private final String sourceFilePath;
    private final String javaCommentBeginSymbol = "/*";
    private  final String javaCommentEndSymbol = "*/";
    // key = required component name, value = blocked invocation source
    private volatile Map<String, List<InvocationSourceChain>> blockedInvocationSources;
    /**
     * @param srcModel
     *            Source model to populate from the parsing of the given code
     *            base.
     * @param sourceFilePath
     *            The path of the source file being parsed.
     */
    public ClarpseJavaTreeListener(final OOPSourceCodeModel srcModel, final String sourceFilePath,
            Map<String, List<InvocationSourceChain>> blockedInvocationSources) {
        this.srcModel = srcModel;
        this.sourceFilePath = sourceFilePath;
        this.blockedInvocationSources = blockedInvocationSources;
    }

    private void completeComponent() {
        for (int i = 0; i < componentCompletionMultiplier; i++) {
            if (!componentStack.isEmpty()) {
                final Component completedCmp = componentStack.pop();
                srcModel.insertComponent(completedCmp);
                final List<InvocationSourceChain> blockedSources = blockedInvocationSources.get(completedCmp
                        .uniqueName());
                if (blockedSources != null) {
                    final List<InvocationSourceChain> blockedSourcesCopy = new ArrayList<InvocationSourceChain>(
                            blockedSources);
                    for (final InvocationSourceChain src : blockedSourcesCopy) {
                        src.process();
                    }
                }
            }
        }
        componentCompletionMultiplier = 1;
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
    private Component createComponent(final ParserRuleContext ctx,
            final OOPSourceModelConstants.ComponentType componentType) {
        final Component newCmp = new Component();
        newCmp.setPackageName(currentPkg);
        newCmp.setComponentType(componentType);
        newCmp.setComment(AntlrUtil.getContextMultiLineComment(ctx, currFileSourceCode,
                javaCommentBeginSymbol, javaCommentEndSymbol));
        newCmp.setStartLine(String.valueOf(ctx.getStart().getLine()));
        newCmp.setEndLine(String.valueOf(ctx.getStop().getLine()));
        newCmp.setSourceFilePath(sourceFilePath);
        return newCmp;
    }

    @Override
    public final void enterPackageDeclaration(final JavaParser.PackageDeclarationContext ctx) {
        currentPkg = ctx.qualifiedName().getText();
        componentCompletionMultiplier = 1;
        currentImports.clear();
        if (!componentStack.isEmpty()) {
            System.out
            .println("Clarity Java Listener found new package declaration while component stack not empty! component stack size is: "
                    + componentStack.size());
        }
    }

    @Override
    public final void enterImportDeclaration(final JavaParser.ImportDeclarationContext ctx) {
        final String fullImportName = ctx.qualifiedName().getText();
        final String[] bits = fullImportName.split(Pattern.quote("."));
        final String shortImportName = bits[(bits.length - 1)];
        currentImports.add(fullImportName);
        currentImportsMap.put(shortImportName, fullImportName);
        if (currentPkg.isEmpty()) {
            currentPkg = "";
        }
    }

    @Override
    public final void enterClassDeclaration(final JavaParser.ClassDeclarationContext ctx) {

        if (ctx.Identifier() != null) {
            final Component classCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.CLASS_COMPONENT);
            classCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
            classCmp.setName(ctx.Identifier().getText());
            classCmp.setImports(currentImports);
            pointParentsToChild(classCmp);
            if (ctx.extendsType() != null) {
                classCmp.insertComponentInvocation(new TypeExtension(resolveType(ctx.extendsType().getText()), ctx
                        .getStart().getLine()));
            }
            componentStack.push(classCmp);
        }
    }
    /**
     * Returns corresponding import statement based on given type.
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
    public final void exitClassDeclaration(final JavaParser.ClassDeclarationContext ctx) {

        completeComponent();
    }

    @Override
    public final void enterEnumDeclaration(final JavaParser.EnumDeclarationContext ctx) {

        final Component enumCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.ENUM_COMPONENT);
        enumCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
        enumCmp.setImports(currentImports);
        enumCmp.setName(ctx.Identifier().getText());
        pointParentsToChild(enumCmp);
        componentStack.push(enumCmp);
    }

    @Override
    public final void exitEnumDeclaration(final JavaParser.EnumDeclarationContext ctx) {

        completeComponent();
    }

    @Override
    public final void enterEnumConstant(final JavaParser.EnumConstantContext ctx) {

        final Component enumConstCmp = createComponent(ctx,
                OOPSourceModelConstants.ComponentType.ENUM_CONSTANT_COMPONENT);
        enumConstCmp.setName(ctx.Identifier().getText());
        enumConstCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
        pointParentsToChild(enumConstCmp);
        componentStack.push(enumConstCmp);
    }

    @Override
    public final void exitEnumConstant(final JavaParser.EnumConstantContext ctx) {

        completeComponent();
    }

    @Override
    public final void enterInterfaceDeclaration(final JavaParser.InterfaceDeclarationContext ctx) {

        final Component interfaceCmp = createComponent(ctx,
                OOPSourceModelConstants.ComponentType.INTERFACE_COMPONENT);
        interfaceCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
        interfaceCmp.setImports(currentImports);
        pointParentsToChild(interfaceCmp);
        interfaceCmp.setName(ctx.Identifier().getText());
        componentStack.push(interfaceCmp);
    }

    @Override
    public final void exitInterfaceDeclaration(final JavaParser.InterfaceDeclarationContext ctx) {

        completeComponent();
    }

    @Override
    public final void enterMethodDeclaration(final JavaParser.MethodDeclarationContext ctx) {

        final Component currMethodCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.METHOD_COMPONENT);
        currMethodCmp.setName(ctx.Identifier().getText());
        if (ctx.type() != null) {
            currMethodCmp.setValue(resolveType(ctx.type().getText()));
        } else {
            currMethodCmp.setValue("void");
        }

        String formalParametersString = "(";
        if (ctx.formalParameters().formalParameterList() != null) {
            formalParametersString += getFormalParameterTypesList(ctx.formalParameters().formalParameterList());
        }
        formalParametersString += ")";

        final String methodSignature = currMethodCmp.name() + formalParametersString;
        currMethodCmp.setComponentName(generateComponentName(methodSignature));
        pointParentsToChild(currMethodCmp);
        componentStack.push(currMethodCmp);
    }


    private String getFormalParameterTypesList(final FormalParameterListContext formalParameterList) {

        String typesList = "";
        for (final FormalParameterContext fpContext : formalParameterList.formalParameter()) {
            typesList += resolveType(fpContext.type().getText()) + ",";
        }
        if (formalParameterList.lastFormalParameter() != null) {
            final String lFp = resolveType(formalParameterList.lastFormalParameter().type().getText());
            typesList += lFp;
        }
        if (typesList.endsWith(",")) {
            typesList = typesList.substring(0, typesList.length() - 1);
        }
        return typesList;
    }

    @Override
    public final void enterInterfaceMethodDeclaration(final JavaParser.InterfaceMethodDeclarationContext ctx) {

        final Component currMethodCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.METHOD_COMPONENT);

        final String methodName = ctx.Identifier().getText();
        currMethodCmp.setName(methodName);

        if (ctx.type() != null) {
            currMethodCmp.setValue(resolveType(ctx.type().getText()));
        } else {
            currMethodCmp.setValue("void");
        }

        String formalParametersString = "(";
        if (ctx.formalParameters().formalParameterList() != null) {
            formalParametersString += getFormalParameterTypesList(ctx.formalParameters().formalParameterList());
        }
        formalParametersString += ")";

        final String methodSignature = currMethodCmp.name() + formalParametersString;

        currMethodCmp.setComponentName(generateComponentName(methodSignature));
        pointParentsToChild(currMethodCmp);
        componentStack.push(currMethodCmp);
    }


    @Override
    public final void enterConstructorDeclaration(final JavaParser.ConstructorDeclarationContext ctx) {

        final Component currMethodCmp = createComponent(ctx,
                OOPSourceModelConstants.ComponentType.CONSTRUCTOR_COMPONENT);

        currMethodCmp.setValue("void");

        final String methodName = ctx.Identifier().getText();
        currMethodCmp.setName(methodName);

        String formalParametersString = "(";
        if (ctx.formalParameters().formalParameterList() != null) {
            formalParametersString += getFormalParameterTypesList(ctx.formalParameters().formalParameterList());
        }
        formalParametersString += ")";

        final String methodSignature = currMethodCmp.name() + formalParametersString;

        currMethodCmp.setComponentName(generateComponentName(methodSignature));
        pointParentsToChild(currMethodCmp);
        componentStack.push(currMethodCmp);
    }



    @Override
    public final void exitMethodDeclaration(final JavaParser.MethodDeclarationContext ctx) {
        completeComponent();
    }

    @Override
    public final void exitInterfaceMethodDeclaration(final JavaParser.InterfaceMethodDeclarationContext ctx) {

        completeComponent();
    }

    @Override
    public final void exitConstructorDeclaration(final JavaParser.ConstructorDeclarationContext ctx) {
        completeComponent();
    }

    @Override
    public final void enterQualifiedNameList(final JavaParser.QualifiedNameListContext ctx) {

        final Component currMethodCmp = componentStack.pop();
        for (final JavaParser.QualifiedNameContext qctx : ctx.qualifiedName()) {
            currMethodCmp.insertComponentInvocation(new ThrownException(resolveType(qctx.getText()), ctx.getStart()
                    .getLine()));
        }
        componentStack.push(currMethodCmp);
    }

    @Override
    public final void enterFormalParameter(final JavaParser.FormalParameterContext ctx) {

        final Component currMethodCmp = componentStack.peek();

        if (currMethodCmp.componentType().toString().equals(ComponentType.CONSTRUCTOR_COMPONENT.toString())) {
            final Component cmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT);
            componentStack.push(cmp);
        } else {
            final Component cmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
            componentStack.push(cmp);
        }
    }

    @Override
    public final void enterLastFormalParameter(final JavaParser.LastFormalParameterContext ctx) {

        final Component currMethodCmp = componentStack.peek();
        if (currMethodCmp.componentType().toString().equals(ComponentType.CONSTRUCTOR_COMPONENT.toString())) {
            final Component cmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT);
            componentStack.push(cmp);
        } else {
            final Component cmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
            componentStack.push(cmp);
        }
    }

    @Override
    public final void exitFormalParameter(final JavaParser.FormalParameterContext ctx) {

        completeComponent();
    }

    @Override
    public final void exitLastFormalParameter(final JavaParser.LastFormalParameterContext ctx) {
        completeComponent();
    }

    @Override
    public final void enterLocalVariableDeclaration(final JavaParser.LocalVariableDeclarationContext ctx) {

        final Component cmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.LOCAL_VARIABLE_COMPONENT);
        componentStack.push(cmp);

    }

    @Override
    public final void exitLocalVariableDeclaration(final JavaParser.LocalVariableDeclarationContext ctx) {
        completeComponent();
    }

    @Override
    public final void enterFieldDeclaration(final JavaParser.FieldDeclarationContext ctx) {

        final Component currCmp = componentStack.peek();
        if (currCmp.componentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(
                        OOPSourceModelConstants.ComponentType.INTERFACE_COMPONENT))) {
            final Component cmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.INTERFACE_CONSTANT_COMPONENT);
            componentStack.push(cmp);
        } else {
            final Component cmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.FIELD_COMPONENT);
            componentStack.push(cmp);
        }

    }

    @Override
    public final void exitFieldDeclaration(final JavaParser.FieldDeclarationContext ctx) {
        completeComponent();
    }

    @Override
    public final void enterImplementsTypeList(final JavaParser.ImplementsTypeListContext ctx) {

        final Component currCmp = componentStack.pop();
        for (final ImplementsTypeContext tempType : ctx.implementsType()) {
            currCmp.insertComponentInvocation(new TypeImplementation(resolveType(tempType.getText()), ctx.getStart()
                    .getLine()));
        }
        componentStack.push(currCmp);

    }

    @Override
    public final void enterTypeParameters(final JavaParser.TypeParametersContext ctx) {

        final Component currCmp = componentStack.pop();
        currCmp.setDeclarationTypeSnippet(AntlrUtil.getFormattedText(ctx));
        componentStack.push(currCmp);

    }

    @Override
    public final void enterAnnotation(final JavaParser.AnnotationContext ctx) {

        final Component currCmp = componentStack.pop();
        String typeName = "";
        final HashMap<String, String> elementValuePairs = new HashMap<String, String>();
        if (ctx.normalAnnotation() != null && ctx.normalAnnotation().elementValuePairList() != null) {
            typeName = resolveAnnotationType(ctx.normalAnnotation().typeName().getText());
            for (final JavaParser.ElementValuePairContext evctx : ctx.normalAnnotation().elementValuePairList()
                    .elementValuePair()) {
                elementValuePairs.put(evctx.Identifier().getText(), evctx.elementValue().getText());
            }
        } else if (ctx.markerAnnotation() != null) {
            typeName = resolveAnnotationType(ctx.markerAnnotation().typeName().getText());
        } else if (ctx.singleElementAnnotation() != null) {
            typeName = resolveAnnotationType(ctx.singleElementAnnotation().typeName().getText());
            elementValuePairs.put("", ctx.singleElementAnnotation().elementValue().getText());
        }
        currCmp.insertComponentInvocation(new AnnotationInvocation(typeName, ctx.start.getLine(),
                new SimpleEntry<String, HashMap<String, String>>(typeName, elementValuePairs)));

        componentStack.push(currCmp);
    }

    private String resolveAnnotationType(String annotationType) {
        if (OOPSourceModelConstants.getJavaPredefinedAnnotations().containsKey(annotationType)) {
            return annotationType;
        } else {
            return resolveType(annotationType);
        }
    }

    @Override
    public final void enterClassOrInterfaceType(final JavaParser.ClassOrInterfaceTypeContext ctx) {

        final Component currCmp = componentStack.pop();

        String type = "";
        for (final TerminalNode ciftx : ctx.Identifier()) {
            type += ciftx.getText() + ".";
        }
        type = type.substring(0, type.length() - 1);
        currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(type), ctx.getStart().getLine()));
        componentStack.push(currCmp);

    }

    @Override
    public final void enterType(final JavaParser.TypeContext ctx) {

        final Component currCmp = componentStack.pop();
        if ((currCmp.declarationTypeSnippet() == null) && (!currCmp.componentType().isBaseComponent())) {
            currCmp.setDeclarationTypeSnippet(ctx.getText());
        }
        componentStack.push(currCmp);
    }

    @Override
    public final void enterPrimitiveType(final JavaParser.PrimitiveTypeContext ctx) {

        final Component currCmp = componentStack.pop();

        currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(ctx.getText()), ctx.getStart().getLine()));

        componentStack.push(currCmp);
    }


    @Override
    public final void enterRegularModifier(final JavaParser.RegularModifierContext ctx) {

        final Component currCmp = componentStack.pop();
        currCmp.insertAccessModifier(ctx.getText());
        componentStack.push(currCmp);
    }

    @Override
    public final void enterVariableDeclaratorId(final JavaParser.VariableDeclaratorIdContext ctx) {
        if (ctx.Identifier() != null) {
            final Component currCmp = componentStack.pop();
            if (currCmp.componentType().isVariableComponent()) {
                if ((currCmp.componentName() == null) || (currCmp.componentName().isEmpty())) {
                    currCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
                    pointParentsToChild(currCmp);
                    currCmp.setName(ctx.Identifier().getText());
                } else if (ctx.Identifier() != null) {
                    final Component copyCmp = new Component(currCmp);
                    componentCompletionMultiplier += 1;
                    copyCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
                    copyCmp.setName(ctx.Identifier().getText());
                    pointParentsToChild(copyCmp);
                    componentStack.push(copyCmp);
                }
            }
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterVariableInitializer(final JavaParser.VariableInitializerContext ctx) {

        final Component currCmp = componentStack.pop();
        currCmp.setValue(ctx.getText());
        componentStack.push(currCmp);
    }

    @Override
    public final void enterCompilationUnit(final JavaParser.CompilationUnitContext ctx) {
        currFileSourceCode = AntlrUtil.getFormattedText(ctx);
        componentStack.clear();
    }

    private String retrieveContainingClassName(MethodInvocationContext ctx) {

        String containingClassName = "";

        // local method call..
        if (ctx.localMethodName() != null) {
            final List<ComponentType> baseTypes = new ArrayList<ComponentType>();
            baseTypes.add(ComponentType.CLASS_COMPONENT);
            baseTypes.add(ComponentType.INTERFACE_COMPONENT);
            baseTypes.add(ComponentType.ENUM_COMPONENT);
            containingClassName = newestStackComponent(baseTypes).uniqueName();
        }

        // variable or static method call..
        if (ctx.typeName() != null) {
            final Component variableComponent = findLocalSourceFileComponent(ctx.typeName().getText());
            if (variableComponent != null && variableComponent.name() != null) {
                final List<ComponentInvocation> typeInstantiations = variableComponent
                        .componentInvocations(ComponentInvocations.DECLARATION);
                if (!typeInstantiations.isEmpty()) {
                    containingClassName = typeInstantiations.get(0).invokedComponent();
                }
            } else {
                final String text = ctx.typeName().getText();
                if (text.contains(".")) {
                    containingClassName = text;
                } else {
                    containingClassName = resolveType(text);
                }
            }
        }
        return containingClassName;
    }

    @Override
    public final void enterMethodInvocations(final JavaParser.MethodInvocationsContext ctx) {

        if (!componentStack.isEmpty()) {
            final Component currCmp = componentStack.peek();
            final List<InvocationSource> methodSources = new ArrayList<InvocationSource>();

            for (final MethodInvocationContext methodCtx : ctx.methodInvocation()) {

                methodSources.add(new BindedInvocationSource(new MethodInvocationSourceImpl(
                        retrieveContainingClassName(methodCtx), extractMethodCall(methodCtx), methodCtx.getStart()
                        .getLine(), getArgumentsSize(methodCtx), srcModel, blockedInvocationSources), currCmp));
            }

            final MethodInvocationSourceChain methodChain = new MethodInvocationSourceChain(methodSources, srcModel, blockedInvocationSources);
            methodChain.process();
        }
    }

    private String extractMethodCall(MethodInvocationContext ctx) {

        if (ctx.localMethodName() != null) {
            return ctx.localMethodName().getText();
        } else {
            if (ctx.Identifier() != null) {
                return ctx.Identifier().getText();
            }
        }
        return "";
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

    private void pointParentsToChild(Component childCmp) {

        if (!componentStack.isEmpty()) {
            final String parentName = childCmp.parentUniqueName();
            for (int i = componentStack.size() - 1; i >= 0; i--) {
                if (componentStack.get(i).uniqueName().equals(parentName)) {
                    componentStack.get(i).insertChildComponent(childCmp.uniqueName());
                }
            }
        }
    }

    private int getArgumentsSize(MethodInvocationContext ctx) {
        int invocationArguments = 0;
        if (ctx.argumentList() != null) {
            invocationArguments = ctx.argumentList().argument().size();
        }
        return invocationArguments;
    }
}
