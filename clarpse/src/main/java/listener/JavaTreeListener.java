package listener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

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
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
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
public class JavaTreeListener extends VoidVisitorAdapter{

    private final Stack<Component> componentStack = new Stack<Component>();
    private final ArrayList<String> currentImports = new ArrayList<String>();
    private String currentPkg = "";
    private String currFileSourceCode;
    private final OOPSourceCodeModel srcModel;
    private int componentCompletionMultiplier = 1;
    private final Map<String, String> currentImportsMap = new HashMap<String, String>();
    private final RawFile file;
    private final String javaCommentBeginSymbol = "/*";
    private  final String javaCommentEndSymbol = "*/";
    // key = required component name, value = blocked invocation source
    private volatile Map<String, List<InvocationSourceChain>> blockedInvocationSources;
    /**
     * @param srcModel
     *            Source model to populate from the parsing of the given code
     *            base.
     * @param file
     *            The path of the source file being parsed.
     */
    public JavaTreeListener(final OOPSourceCodeModel srcModel, final RawFile file,
            Map<String, List<InvocationSourceChain>> blockedInvocationSources) {
        this.srcModel = srcModel;
        this.file = file;
        this.blockedInvocationSources = blockedInvocationSources;
    }

    public OOPSourceCodeModel result () throws Exception {
    	 FileInputStream in = new FileInputStream("test.java");

         CompilationUnit cu;
         try {
             // parse the file
             cu = com.github.javaparser.JavaParser.parse(file.stream());
         } finally {
             in.close();
         }
         return this.srcModel;
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
    private Component createComponent(String comment, int startLine, int endLine, ComponentType componentType) {
        final Component newCmp = new Component();
        newCmp.setPackageName(currentPkg);
        newCmp.setComponentType(componentType);
        newCmp.setComment(comment);
        newCmp.setStartLine(String.valueOf(startLine));
        newCmp.setEndLine(String.valueOf(endLine));
        newCmp.setSourceFilePath(file.name());
        return newCmp;
    }

    @Override
    public final void visit(PackageDeclaration ctx, Object arg) {
        currentPkg = ctx.getPackageName();
        componentCompletionMultiplier = 1;
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
        final String fullImportName = ctx.toStringWithoutComments();
        final String[] bits = fullImportName.split(Pattern.quote("."));
        final String shortImportName = bits[(bits.length - 1)];
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
    		cmp = createComponent(ctx.getJavaDoc().getContent(), ctx.getBegin().line, ctx.getEnd().line, OOPSourceModelConstants.ComponentType.INTERFACE_COMPONENT);
    	} else {
    		cmp = createComponent(ctx.getJavaDoc().getContent(), ctx.getBegin().line, ctx.getEnd().line, OOPSourceModelConstants.ComponentType.CLASS_COMPONENT);
    	}

    	cmp.setComponentName(generateComponentName(ctx.getName()));
    	cmp.setName(ctx.getName());
    	cmp.setImports(currentImports);
    	pointParentsToGivenChild(cmp);

    	if (ctx.getExtends() != null) {
    		for (ClassOrInterfaceType outerType : ctx.getExtends()) {
    			for (Type type : outerType.getTypeArgs()) {
    			cmp.insertComponentInvocation(new TypeExtension(resolveType(type.toStringWithoutComments()), ctx.getBegin().line));
    			}
    		}
    	}
    	
    	if (ctx.getImplements() != null) {
    		for (ClassOrInterfaceType outerType : ctx.getImplements()) {
    			for (Type type : outerType.getTypeArgs()) {
    			cmp.insertComponentInvocation(new TypeImplementation(resolveType(type.toStringWithoutComments()), ctx.getBegin().line));
    			}
    		}
    	}
    	
    	if (ctx.getAnnotations() != null) {
    		for (AnnotationExpr expr : ctx.getAnnotations()) {
    			expr.getName();
    			expr.get
    		}
    	}
    	super.visit(ctx, arg);
    	componentStack.push(cmp);
    }
    
    @Override
    public final void visit(TypeParameter ctx, Object arg) {
    	Component currComponent = componentStack.pop();
    	if (ctx.getTypeBound() != null) {
    		for (ClassOrInterfaceType type : ctx.getTypeBound()) {
    			for (Type innerType : type.getTypeArgs()) {
    	    	currComponent.insertComponentInvocation(new TypeReferenceInvocation(resolveType(innerType.toStringWithoutComments()), type.getBegin().line));
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

        final Component enumCmp = createComponent(ctx.getJavaDoc().getContent(), ctx.getBegin().line, ctx.getEnd().line, OOPSourceModelConstants.ComponentType.ENUM_COMPONENT);
        enumCmp.setComponentName(generateComponentName(ctx.getName()));
        enumCmp.setImports(currentImports);
        enumCmp.setName(ctx.getName());
        pointParentsToGivenChild(enumCmp);
        super.visit(ctx, arg);
        componentStack.push(enumCmp);
    }

    @Override
    public final void visit(final EnumConstantDeclaration ctx, Object arg) {

        final Component enumConstCmp = createComponent(ctx.getJavaDoc().getContent(), ctx.getBegin().line, ctx.getEnd().line,
                OOPSourceModelConstants.ComponentType.ENUM_CONSTANT_COMPONENT);
        enumConstCmp.setName(ctx.getName());
        enumConstCmp.setComponentName(generateComponentName(ctx.getName()));
        pointParentsToGivenChild(enumConstCmp);
        componentStack.push(enumConstCmp);
        super.visit(ctx, arg);
        completeComponent();
    }

    @Override
    public final void visit(final MethodDeclaration ctx, Object arg) {

        final Component currMethodCmp = createComponent(ctx.getJavaDoc().getContent(), ctx.getBegin().line, ctx.getEnd().line, OOPSourceModelConstants.ComponentType.METHOD_COMPONENT);
        currMethodCmp.setName(ctx.getName());
        if (ctx.getType().toString() != null && !ctx.getType().toString().equals("void")) {
            currMethodCmp.setValue(resolveType(ctx.getType().toString()));
        } else {
            currMethodCmp.setValue("void");
        }
        
        String formalParametersString = "(";
        if (ctx.getParameters() != null) {
            formalParametersString += getFormalParameterTypesList(ctx.getParameters());
        }
        formalParametersString += ")";

        if (ctx.getParameters() != null) {
            formalParametersString += getFormalParameterTypesList(ctx.getParameters());
            for (Parameter param : ctx.getParameters()) {
            	Component methodParamCmp = new Component();
            	methodParamCmp.setName(param.getName());
            	methodParamCmp.setComment(param.getComment().getContent());
            	methodParamCmp.setAccessModifiers(resolveJavaParserModifiers(param.getModifiers()));
            	methodParamCmp.insertComponentInvocation(new TypeDeclaration(resolveType(param.getType().toStringWithoutComments()), param.getBegin().line));
            }
        }
        
        for (ReferenceType stmt : ctx.getThrows()) {
        	currMethodCmp.insertComponentInvocation(new ThrownException(resolveType(stmt.getType().toStringWithoutComments()), stmt.getBegin().line));
        }
        final String methodSignature = currMethodCmp.name() + formalParametersString;
        currMethodCmp.setComponentName(generateComponentName(methodSignature));
        pointParentsToGivenChild(currMethodCmp);
        componentStack.push(currMethodCmp);
        super.visit(ctx, arg);
        completeComponent();
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

        final Component currMethodCmp = createComponent(ctx.getJavaDoc().getContent(), ctx.getBegin().line, ctx.getEnd().line,
                OOPSourceModelConstants.ComponentType.CONSTRUCTOR_COMPONENT);

        currMethodCmp.setValue("void");

        final String methodName = ctx.getName();
        currMethodCmp.setName(methodName);
 
        String formalParametersString = "(";
        
        if (ctx.getParameters() != null) {
            formalParametersString += getFormalParameterTypesList(ctx.getParameters());
            for (Parameter param : ctx.getParameters()) {
            	Component methodParamCmp = new Component();
            	methodParamCmp.setName(param.getName());
            	methodParamCmp.setComment(param.getComment().getContent());
            	methodParamCmp.setAccessModifiers(resolveJavaParserModifiers(param.getModifiers()));
            	methodParamCmp.insertComponentInvocation(new TypeDeclaration(resolveType(param.getType().toStringWithoutComments()), param.getBegin().line));
            }
        }
        
        formalParametersString += ")";

        for (ReferenceType stmt : ctx.getThrows()) {
        	currMethodCmp.insertComponentInvocation(new ThrownException(resolveType(stmt.getType().toStringWithoutComments()), stmt.getBegin().line));
        }
        
        final String methodSignature = currMethodCmp.name() + formalParametersString;
        currMethodCmp.setComponentName(generateComponentName(methodSignature));
        pointParentsToGivenChild(currMethodCmp);
        componentStack.push(currMethodCmp);
        super.visit(ctx, arg);
        completeComponent();
    }
    
   private List<String> resolveJavaParserModifiers(int modifiers) {
	   List<String> modifierList = new ArrayList<String>();
	   
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

        final Component cmp = createComponent(ctx.getComment().getContent(), ctx.getBegin().line, ctx.getEnd().line, OOPSourceModelConstants.ComponentType.LOCAL_VARIABLE_COMPONENT);
        componentStack.push(cmp);
        super.visit(ctx, arg);
        completeComponent();
    }
    
    @Override
    public final void visit(FieldDeclaration ctx, Object arg) {

        final Component currCmp = componentStack.peek();
        if (currCmp.componentType().equals(
                OOPSourceModelConstants.getJavaComponentTypes().get(
                        OOPSourceModelConstants.ComponentType.INTERFACE_COMPONENT))) {
            final Component cmp = createComponent(ctx.getJavaDoc().getContent(), ctx.getBegin().line, ctx.getEnd().line,
                    OOPSourceModelConstants.ComponentType.INTERFACE_CONSTANT_COMPONENT);
            componentStack.push(cmp);
        } else {
            final Component cmp = createComponent(ctx.getJavaDoc().getContent(), ctx.getBegin().line, ctx.getEnd().line, OOPSourceModelConstants.ComponentType.FIELD_COMPONENT);
            componentStack.push(cmp);
        }
        super.visit(ctx, arg);
        completeComponent();
    }

    @Override
    public final void visit(MarkerAnnotationExpr ctx, Object arg) {

        final Component currCmp = componentStack.pop();
        String typeName = "";
        final HashMap<String, String> elementValuePairs = new HashMap<String, String>();
        if (ctx.get) {
            typeName = resolveAnnotationType(ctx.normalAnnotation().typeName().getText());
            for (final ClarpseJavaParser.ElementValuePairContext evctx : ctx.normalAnnotation().elementValuePairList()
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
    public final void enterClassOrInterfaceType(final ClarpseJavaParser.ClassOrInterfaceTypeContext ctx) {

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
    public final void enterType(final ClarpseJavaParser.TypeContext ctx) {

        final Component currCmp = componentStack.pop();
        if ((currCmp.declarationTypeSnippet() == null) && (!currCmp.componentType().isBaseComponent())) {
            currCmp.setDeclarationTypeSnippet(ctx.getText());
        }
        componentStack.push(currCmp);
    }

    @Override
    public final void enterPrimitiveType(final ClarpseJavaParser.PrimitiveTypeContext ctx) {

        final Component currCmp = componentStack.pop();

        currCmp.insertComponentInvocation(new TypeDeclaration(resolveType(ctx.getText()), ctx.getStart().getLine()));

        componentStack.push(currCmp);
    }


    @Override
    public final void enterRegularModifier(final ClarpseJavaParser.RegularModifierContext ctx) {

        final Component currCmp = componentStack.pop();
        currCmp.insertAccessModifier(ctx.getText());
        componentStack.push(currCmp);
    }

    @Override
    public final void enterVariableDeclaratorId(final ClarpseJavaParser.VariableDeclaratorIdContext ctx) {
        if (ctx.Identifier() != null) {
            final Component currCmp = componentStack.pop();
            if (currCmp.componentType().isVariableComponent()) {
                if ((currCmp.componentName() == null) || (currCmp.componentName().isEmpty())) {
                    currCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
                    pointParentsToGivenChild(currCmp);
                    currCmp.setName(ctx.Identifier().getText());
                } else if (ctx.Identifier() != null) {
                    final Component copyCmp = new Component(currCmp);
                    componentCompletionMultiplier += 1;
                    copyCmp.setComponentName(generateComponentName(ctx.Identifier().getText()));
                    copyCmp.setName(ctx.Identifier().getText());
                    pointParentsToGivenChild(copyCmp);
                    componentStack.push(copyCmp);
                }
            }
            componentStack.push(currCmp);
        }
    }

    @Override
    public final void enterVariableInitializer(final ClarpseJavaParser.VariableInitializerContext ctx) {

        final Component currCmp = componentStack.pop();
        currCmp.setValue(ctx.getText());
        componentStack.push(currCmp);
    }

    @Override
    public final void enterCompilationUnit(final ClarpseJavaParser.CompilationUnitContext ctx) {
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
    public final void enterMethodInvocations(final ClarpseJavaParser.MethodInvocationsContext ctx) {

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

    private int getArgumentsSize(MethodCallExpr ctx) {
        int invocationArguments = 0;
        if (ctx.getArgs() != null) {
            invocationArguments = ctx.getArgs().size();
        }
        return invocationArguments;
    }
}
