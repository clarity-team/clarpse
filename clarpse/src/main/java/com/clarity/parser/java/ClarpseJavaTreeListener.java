package com.clarity.parser.java;

import invocation.AnnotationInvocation;
import invocation.ThrownException;
import invocation.TypeDeclaration;
import invocation.TypeExtension;
import invocation.TypeImpementation;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import parser.java.JavaBaseListener;
import parser.java.JavaParser;
import parser.java.JavaParser.AnnotationTypeDeclarationContext;
import parser.java.JavaParser.FormalParameterContext;
import parser.java.JavaParser.FormalParameterListContext;

import com.clarity.parser.AntlrUtil;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;

/**
 * As the parse tree is developed by Antlr, we add listener methods to capture
 * important information during this process and populate our Source Code Model.
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
    private boolean ignoreTreeWalk = false;
    private final String sourceFilePath;
    private static final String JAVA_BLOCK_COMMENT_BEGIN_SYMBOL = "/*";
    private static final String JAVA_BLOCK_COMMENT_END_SYMBOL = "*/";

    /**
     * @param srcModel
     *            Source model to populate from the parsing of the given code
     *            base.
     * @param sourceFilePath
     *            The path of the source file being parsed.
     */
    public ClarpseJavaTreeListener(final OOPSourceCodeModel srcModel, final String sourceFilePath) {
        this.srcModel = srcModel;
        this.sourceFilePath = sourceFilePath;
    }

    /**
     * Cleanup tasks to do before completing and removing the component from the
     * stack:
     * 1) Update all parent component's external class references to
     * include those of the current component
     * 2) Update immediate parent
     * component's child components to include the current component towards the
     * current component as their child component.
     */
    private void completeComponent() {
        for (int i = 0; i < componentCompletionMultiplier; i++) {
            if (!componentStack.isEmpty()) {
                final Component completedCmp = componentStack.pop();
                System.out.println(completedCmp.getUniqueName());
                for (int j = componentStack.size() - 1; j >= 0; j--) {
                    final Component possibleParentComponent = componentStack.get(j);
                    if (possibleParentComponent.getComponentType().isBaseComponent()
                            || componentStack.get(j).getComponentType().isMethodComponent()) {
                        // Step 1)
                        possibleParentComponent.insertTypeReferences(completedCmp.getExternalClassTypeReferences());
                        // Step 2)
                        if (j == (componentStack.size() - componentCompletionMultiplier)) {
                            possibleParentComponent.insertChildComponent(completedCmp.getUniqueName());
                        }
                    }
                }
                try {
                    srcModel.insertComponent(completedCmp);
                } catch (final Exception e) {
                    System.out.println("Could not add component to source model! " + completedCmp.getUniqueName());
                    e.printStackTrace();
                }
            }
        }
        componentCompletionMultiplier = 1;
    }

    /**
     * Generates appropriate name for the component. Uses the current stack of
     * parents components as prefixes to the name.
     *
     * @param identifier
     *            short hand name of the component
     * @return full name of the component
     */
    private String generateComponentName(final String identifier) {
        String componentName = "";

        if (!componentStack.isEmpty()) {
            final Component completedCmp = componentStack.peek();
            componentName = completedCmp.getComponentName() + "." + identifier;
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
        newCmp.setCode(AntlrUtil.getFormattedText(ctx));
        newCmp.setPackageName(currentPkg);
        newCmp.setComponentType(componentType);
        newCmp.setComment(AntlrUtil.getContextMultiLineComment(ctx, currFileSourceCode,
                JAVA_BLOCK_COMMENT_BEGIN_SYMBOL, JAVA_BLOCK_COMMENT_END_SYMBOL));
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
        componentStack.clear();
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
    public final void enterAnnotationTypeDeclaration(final AnnotationTypeDeclarationContext ctx) {

        ignoreTreeWalk = true;
    }

    @Override
    public final void exitAnnotationTypeDeclaration(final AnnotationTypeDeclarationContext ctx) {

        ignoreTreeWalk = false;
    }

    @Override
    public final void enterClassDeclaration(final JavaParser.ClassDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            final Component classCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.CLASS_COMPONENT);
            classCmp.setCode(currFileSourceCode);
            classCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
            classCmp.setName(ctx.Identifier().getText());
            classCmp.setImports(currentImports);
            if (ctx.type() != null) {
                classCmp.insertTypeReference(new TypeExtension(ctx.type().getText(), ctx.getStart().getLine()));
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

        if (!ignoreTreeWalk) {
            if (currentImportsMap.containsKey(type)) {
                return currentImportsMap.get(type);
            }
            if (type.contains(".")) {
                return type;
            }
            if (OOPSourceModelConstants.getJavaDefaultClasses().containsKey(type)) {
                return OOPSourceModelConstants.getJavaDefaultClasses().get(type);
            }
        }

        if (!currentPkg.isEmpty()) {
            return currentPkg + "." + type;
        } else {
            return type;
        }
    }

    @Override
    public final void exitClassDeclaration(final JavaParser.ClassDeclarationContext ctx) {
        if (!ignoreTreeWalk) {

            completeComponent();
        }
    }

    @Override
    public final void enterEnumDeclaration(final JavaParser.EnumDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            final Component enumCmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.ENUM_COMPONENT);
            enumCmp.setCode(currFileSourceCode);
            enumCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
            enumCmp.setImports(currentImports);
            enumCmp.setName(ctx.Identifier().getText());
            componentStack.push(enumCmp);
        }
    }

    @Override
    public final void exitEnumDeclaration(final JavaParser.EnumDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            completeComponent();
        }
    }

    @Override
    public final void enterEnumConstant(final JavaParser.EnumConstantContext ctx) {
        if (!ignoreTreeWalk) {
            final Component enumConstCmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.ENUM_CONSTANT_COMPONENT);
            enumConstCmp.setName(ctx.Identifier().getText());
            enumConstCmp.setCode(AntlrUtil.getFormattedText(ctx));
            enumConstCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));

            componentStack.push(enumConstCmp);
        }
    }

    @Override
    public final void exitEnumConstant(final JavaParser.EnumConstantContext ctx) {
        if (!ignoreTreeWalk) {
            completeComponent();
        }
    }

    @Override
    public final void enterInterfaceDeclaration(final JavaParser.InterfaceDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            final Component interfaceCmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.INTERFACE_COMPONENT);
            interfaceCmp.setCode(AntlrUtil.getFormattedText(ctx));
            interfaceCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
            interfaceCmp.setImports(currentImports);
            interfaceCmp.setName(ctx.Identifier().getText());
            componentStack.push(interfaceCmp);
        }
    }

    @Override
    public final void exitInterfaceDeclaration(final JavaParser.InterfaceDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            completeComponent();
        }
    }

    @Override
    public final void enterMethodDeclaration(final JavaParser.MethodDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currMethodCmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.METHOD_COMPONENT);
            currMethodCmp.setCode(AntlrUtil.getFormattedText(ctx));
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

            final String methodSignature = currMethodCmp.getValue() + "_" + currMethodCmp.getName()
                    + formalParametersString;
            currMethodCmp.setComponentName(generateComponentName(methodSignature));
            componentStack.push(currMethodCmp);
        }
    }

    private String getFormalParameterTypesList(final FormalParameterListContext formalParameterList) {

        String typesList = "";
        for (final FormalParameterContext fpContext : formalParameterList.formalParameter()) {
            typesList += resolveType(fpContext.type().getText()) + ",";
        }

        if (typesList.endsWith(",")) {
            typesList = typesList.substring(0, typesList.length() - 1);
        }
        return typesList;
    }

    @Override
    public final void enterInterfaceMethodDeclaration(final JavaParser.InterfaceMethodDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currMethodCmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.METHOD_COMPONENT);

            final String methodName = ctx.Identifier().getText();
            currMethodCmp.setName(methodName);

            if (ctx.type() != null) {
                currMethodCmp.setValue(ctx.type().getText());
            } else {
                currMethodCmp.setValue("void");
            }

            String formalParametersString = "(";
            if (ctx.formalParameters().formalParameterList() != null) {
                formalParametersString += getFormalParameterTypesList(ctx.formalParameters().formalParameterList());
            }
            formalParametersString += ")";

            final String methodSignature = currMethodCmp.getValue() + "_" + currMethodCmp.getName()
                    + formalParametersString;


            currMethodCmp.setComponentName(generateComponentName(methodSignature));
            componentStack.push(currMethodCmp);
        }
    }

    @Override
    public final void enterConstructorDeclaration(final JavaParser.ConstructorDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
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

            final String methodSignature = currMethodCmp.getValue() + "_" + currMethodCmp.getName()
                    + formalParametersString;

            currMethodCmp.setComponentName(generateComponentName(methodSignature));
            componentStack.push(currMethodCmp);
        }
    }

    @Override
    public final void exitMethodDeclaration(final JavaParser.MethodDeclarationContext ctx) {
        if (!ignoreTreeWalk) {

            completeComponent();
        }
    }

    @Override
    public final void exitInterfaceMethodDeclaration(final JavaParser.InterfaceMethodDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            completeComponent();
        }
    }

    @Override
    public final void exitConstructorDeclaration(final JavaParser.ConstructorDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            completeComponent();
        }
    }

    @Override
    public final void enterQualifiedNameList(final JavaParser.QualifiedNameListContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currMethodCmp = componentStack.pop();
            for (final JavaParser.QualifiedNameContext qctx : ctx.qualifiedName()) {
                currMethodCmp.insertTypeReference(new ThrownException(resolveType(qctx.getText()), ctx
                        .getStart()
                        .getLine()));
            }
            componentStack.push(currMethodCmp);
        }
    }

    @Override
    public final void enterFormalParameter(final JavaParser.FormalParameterContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currMethodCmp = componentStack.peek();
            if (currMethodCmp.getComponentType().equals(
                    OOPSourceModelConstants.getJavaComponentTypes().get(
                            OOPSourceModelConstants.ComponentType.CONSTRUCTOR_COMPONENT))) {
                final Component cmp = createComponent(ctx,
                        OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT);
                cmp.setCode(AntlrUtil.getFormattedText(ctx));
                componentStack.push(cmp);
            } else {
                final Component cmp = createComponent(ctx,
                        OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
                cmp.setCode(AntlrUtil.getFormattedText(ctx));
                componentStack.push(cmp);
            }
        }
    }

    @Override
    public final void exitFormalParameter(final JavaParser.FormalParameterContext ctx) {
        if (!ignoreTreeWalk) {
            completeComponent();
        }
    }

    @Override
    public final void enterLocalVariableDeclaration(final JavaParser.LocalVariableDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            final Component cmp = createComponent(ctx,
                    OOPSourceModelConstants.ComponentType.LOCAL_VARIABLE_COMPONENT);
            cmp.setCode(AntlrUtil.getFormattedText(ctx));
            componentStack.push(cmp);
        }
    }

    @Override
    public final void exitLocalVariableDeclaration(final JavaParser.LocalVariableDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            completeComponent();
        }
    }

    @Override
    public final void enterFieldDeclaration(final JavaParser.FieldDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currCmp = componentStack.peek();
            if (currCmp.getComponentType().equals(
                    OOPSourceModelConstants.getJavaComponentTypes().get(
                            OOPSourceModelConstants.ComponentType.INTERFACE_COMPONENT))) {
                final Component cmp = createComponent(ctx,
                        OOPSourceModelConstants.ComponentType.INTERFACE_CONSTANT_COMPONENT);
                cmp.setCode(AntlrUtil.getFormattedText(ctx));
                componentStack.push(cmp);
            } else {
                final Component cmp = createComponent(ctx, OOPSourceModelConstants.ComponentType.FIELD_COMPONENT);
                cmp.setCode(AntlrUtil.getFormattedText(ctx));
                componentStack.push(cmp);
            }
        }
    }

    @Override
    public final void exitFieldDeclaration(final JavaParser.FieldDeclarationContext ctx) {
        if (!ignoreTreeWalk) {
            completeComponent();
        }
    }

    @Override
    public final void enterTypeList(final JavaParser.TypeListContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currCmp = componentStack.pop();
            for (final JavaParser.TypeContext tempType : ctx.type()) {
                currCmp.insertTypeReference(new TypeImpementation(resolveType(tempType.getText()), ctx
                        .getStart()
                        .getLine()));
            }
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterTypeParameters(final JavaParser.TypeParametersContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currCmp = componentStack.pop();

            currCmp.setDeclarationTypeSnippet(AntlrUtil.getFormattedText(ctx));

            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterAnnotation(final JavaParser.AnnotationContext ctx) {
        if (!ignoreTreeWalk) {

            final Component currCmp = componentStack.pop();

            final HashMap<String, String> elementValuePairs = new HashMap<String, String>();
            if (ctx.elementValuePairs() != null) {
                for (final JavaParser.ElementValuePairContext evctx : ctx.elementValuePairs().elementValuePair()) {
                    elementValuePairs.put(evctx.Identifier().getText(), evctx.elementValue().getText());
                }
            }
            if (ctx.elementValue() != null) {
                elementValuePairs.put(ctx.elementValue().getText(), "");
            }
            currCmp.insertTypeReference(new AnnotationInvocation(resolveType(ctx.annotationName().getText()), ctx.start
                    .getLine(), new SimpleEntry<String, HashMap<String, String>>(ctx.annotationName().getText(),
                            elementValuePairs)));

            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterClassOrInterfaceType(final JavaParser.ClassOrInterfaceTypeContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currCmp = componentStack.pop();

            String type = "";
            for (final TerminalNode ciftx : ctx.Identifier()) {
                type += ciftx.getText() + ".";
            }
            type = type.substring(0, type.length() - 1);
            currCmp.insertTypeReference(new TypeDeclaration(resolveType(type), ctx.getStart().getLine()));
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterType(final JavaParser.TypeContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currCmp = componentStack.pop();
            if ((currCmp.getDeclarationTypeSnippet() == null) && (!currCmp.getComponentType().isBaseComponent())) {
                currCmp.setDeclarationTypeSnippet(ctx.getText());
            }
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterPrimitiveType(final JavaParser.PrimitiveTypeContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currCmp = componentStack.pop();

            currCmp.insertTypeReference(new TypeDeclaration(resolveType(ctx.getText()), ctx.getStart()
                    .getLine()));

            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterRegularModifier(final JavaParser.RegularModifierContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currCmp = componentStack.pop();

            currCmp.insertAccessModifier(ctx.getText());

            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterPrimary(final JavaParser.PrimaryContext ctx) {
        if (!ignoreTreeWalk) {
            // System.out.println(AntlrUtil.getFormattedText(ctx));
            final Component currCmp = componentStack.pop();
            // if (ctx.Identifier() != null) {
            // currCmp.insertTypeReference(new
            // TypeReference(resolveType(ctx.getText()),
            // ctx.getStart().getLine()));
            // }
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterVariableDeclaratorId(final JavaParser.VariableDeclaratorIdContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currCmp = componentStack.pop();
            if ((currCmp.getComponentName() == null) || (currCmp.getComponentName().isEmpty())) {
                currCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
                currCmp.setName(ctx.Identifier().getText());
            } else {
                final Component copyCmp = new Component(currCmp);
                componentCompletionMultiplier += 1;
                copyCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
                copyCmp.setName(ctx.Identifier().getText());
                componentStack.push(copyCmp);
            }
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterVariableInitializer(final JavaParser.VariableInitializerContext ctx) {
        if (!ignoreTreeWalk) {
            final Component currCmp = componentStack.pop();
            currCmp.setValue(ctx.getText());

            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterCompilationUnit(final JavaParser.CompilationUnitContext ctx) {
        currFileSourceCode = AntlrUtil.getFormattedText(ctx);
    }

    @Override
    public final void enterMethodInvocation(final JavaParser.MethodInvocationContext ctx) {
        System.out.println("Found method call: " + AntlrUtil.getFormattedText(ctx));
    }
}
