package com.hadii.clarpse.listener.es6;

import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.NodeTraversal.Callback;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.listener.ParseUtil;
import com.hadii.clarpse.reference.SimpleTypeReference;
import com.hadii.clarpse.reference.TypeExtensionReference;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Listener for JavaScript ES6+ source files, based on google's closure
 * compiler.
 */
public class ES6Listener implements Callback {

    private static final Logger logger = LogManager.getLogger(ES6Listener.class);
    private final Stack<Component> componentStack = new Stack<>();
    private final ModulesMap modulesMap;
    private final ES6Module module;
    private final OOPSourceCodeModel srcModel;
    private final ProjectFile file;
    private int currCyclomaticComplexity = 0;

    public ES6Listener(OOPSourceCodeModel srcModel, ProjectFile file,
                       List<ProjectFile> files, ModulesMap modulesMap) throws Exception {
        this.srcModel = srcModel;
        this.file = file;
        this.modulesMap = modulesMap;
        module = modulesMap.module(FilenameUtils.removeExtension(this.file.path()));
        logger.info("Parsing New JS File: " + file.path());
    }

    private static String declarationSnippet(Token token) {
        switch (token) {
            case TRUE:
            case FALSE:
                return "Boolean";
            case STRING:
            case STRING_TYPE:
            case STRING_KEY:
                return "String";
            case NUMBER:
                return "Number";
            case ARRAYLIT:
            case ARRAY_PATTERN:
            case ARRAY_TYPE:
                return "Array";
            case OBJECTLIT:
            case OBJECT_PATTERN:
                return "Object";
            default:
                break;
        }
        return null;
    }

    private static String generateCodeFragment(List<Component> components) {
        String codeFragment = "(";
        for (Component cmp : components) {
            codeFragment += cmp.name() + ", ";
        }
        codeFragment = codeFragment.trim();
        if (codeFragment.endsWith(",")) {
            codeFragment = codeFragment.substring(0, codeFragment.length() - 1);
        }
        codeFragment += ")";
        return codeFragment;
    }

    @Override
    public void visit(NodeTraversal t, Node n, Node parent) {

        if (n.isClass()) {
            while (!componentStack.isEmpty()) {
                Component latestComponent = componentStack.peek();
                if (latestComponent.componentType().isBaseComponent()) {
                    completeComponent();
                    break;
                } else {
                    completeComponent();
                }
            }
        } else if (n.isMemberFunctionDef() || n.isGetterDef() || n.isSetterDef()) {
            while (!componentStack.isEmpty()) {
                Component latestComponent = componentStack.peek();
                if (latestComponent.componentType().isMethodComponent()) {
                    completeComponent();
                    break;
                } else {
                    completeComponent();
                }
            }
        } else if (n.isThis() || n.isVar() || n.isLet()) {
            while (!componentStack.isEmpty()) {
                Component latestComponent = componentStack.peek();
                if (latestComponent.componentType().isVariableComponent()) {
                    completeComponent();
                    break;
                } else {
                    completeComponent();
                }
            }
        }
    }

    @Override
    public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
        try {
            return shouldTraverse(n, parent);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private boolean shouldTraverse(Node n, Node parent) throws Exception {
        Component cmp = null;
        if (n.isClass()) {
            processClass(cmp, n);
        } else if (n.isMemberFunctionDef()) {
            processMemberFunctionDef(cmp, n);
        } else if (n.isGetterDef()) {
            processGetterDef(n);
        } else if (n.isSetterDef()) {
            processSetterDef(n);
        } else if (validParamList(n)) {
            processParams(n);
        } else if (isFieldVar(n)) {
            processFieldVar(n);
        } else if (isLocalVar(n)) {
            processLocalVar(n);
        } else if (n.isCase() || n.isSwitch() || n.isIf() || n.isAnd() || n.isOr() || n.isHook()) {
            currCyclomaticComplexity += 1;
        } else if (n.isName() && !n.isEmpty() && resolveType(n.getString()) != null) {
            processTypeReference(n);
        }
        return true;
    }

    private void processTypeReference(Node n) throws Exception {
        if (!componentStack.isEmpty()) {
            Component latestCmp = componentStack.peek();
            String cmpType = resolveType(n.getString());
            if (cmpType != null && !cmpType.equals(latestCmp.uniqueName())) {
                latestCmp.insertComponentRef(new SimpleTypeReference(cmpType));
                updateParentChildrenData(latestCmp);
                logger.info("Associated type reference: " + cmpType + " with component: " + latestCmp.componentName());
            }
        }
    }

    private void processLocalVar(Node n) throws Exception {
        Component cmp;
        String localVarName = n.getFirstChild().getString();
        logger.info("Found local variable: " + localVarName);
        cmp = createComponent(OOPSourceModelConstants.ComponentType.LOCAL, n);
        cmp.setComponentName(ParseUtil.generateComponentName(localVarName, componentStack));
        cmp.setName(localVarName);
        cmp.setPackageName(module.modulePkg());
        processVariableAssignment(cmp, n.getFirstChild().getFirstChild());
        updateParentChildrenData(cmp);
        componentStack.push(cmp);
    }

    private boolean isLocalVar(Node n) throws Exception {
        return !componentStack.isEmpty() && (n.isVar() || n.isLet())
                && (ParseUtil.newestMethodComponent(componentStack).componentType() == OOPSourceModelConstants.ComponentType.METHOD
                || ParseUtil.newestMethodComponent(componentStack).componentType() == OOPSourceModelConstants.ComponentType.CONSTRUCTOR);
    }

    private void processFieldVar(Node n) throws Exception {
        if (n.getFirstChild().getSecondChild().isString()) {
            String fieldVarname = n.getFirstChild().getSecondChild().getString();
            Component cmp = createComponent(OOPSourceModelConstants.ComponentType.FIELD, n);
            cmp.setComponentName(generateComponentName(fieldVarname, OOPSourceModelConstants.ComponentType.FIELD));
            cmp.setName(fieldVarname);
            cmp.setPackageName(module.modulePkg());
            cmp.insertAccessModifier("private");
            processVariableAssignment(cmp, n.getSecondChild());
            updateParentChildrenData(cmp);
            logger.info("Processed field variable: " + cmp.componentName());
            componentStack.push(cmp);
        }

    }

    private boolean isFieldVar(Node n) throws Exception {
        return n.isAssign() && n.getFirstChild().hasChildren() && n.getFirstChild().getFirstChild().isThis()
                && !componentStack.isEmpty() && ParseUtil.newestMethodComponent(
                        componentStack).componentType() == OOPSourceModelConstants.ComponentType.CONSTRUCTOR;
    }

    private void processParams(Node n) throws Exception {
        Component cmp;
        List<Component> generatedParamComponents = new ArrayList<>();
        // determine type of param component to create based on type of current component at the top of stack
        OOPSourceModelConstants.ComponentType paramComponentType =
                OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT;
        if (componentStack.peek().componentType() == OOPSourceModelConstants.ComponentType.CONSTRUCTOR) {
            paramComponentType = OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT;
        }
        for (Node param : n.children()) {
            String paramName = null;
            if (param.isString() || param.isName()) {
                paramName = param.getString();
            } else if (param.isDefaultValue()) {
                paramName = param.getFirstChild().getString();
            } else {
                throw new Exception("Unrecognized function param type! " + param.getString());
            }
            cmp = createComponent(paramComponentType, n);
            cmp.setComponentName(ParseUtil.generateComponentName(paramName, componentStack));
            cmp.setName(paramName);
            cmp.setPackageName(module.modulePkg());
            updateParentChildrenData(cmp);
            generatedParamComponents.add(cmp);
        }
        // Set parent method code Fragment using param list
        Component parentMethod = componentStack.peek();
        parentMethod.setCodeFragment(parentMethod.name() + generateCodeFragment(generatedParamComponents));
        logger.info("Generated code fragment: " + parentMethod.codeFragment() + " for component: " + parentMethod.componentName());
        // Complete method param components
        for (Component paramCmp : generatedParamComponents) {
            componentStack.push(paramCmp);
        }
    }

    private boolean validParamList(Node n) {
        return n.isParamList() && !componentStack.isEmpty() && componentStack.peek().componentType().isMethodComponent();
    }

    private void processSetterDef(Node n) throws Exception {
        Component cmp;
        currCyclomaticComplexity = 1;
        String cmpName = "set_" + n.getString();
        logger.info("Found setter definition: " + cmpName);
        cmp = createComponent(OOPSourceModelConstants.ComponentType.METHOD, n);
        cmp.setComponentName(ParseUtil.generateComponentName(cmpName, componentStack));
        cmp.setName(cmpName);
        cmp.setPackageName(module.modulePkg());
        cmp.insertAccessModifier("public");
        if (n.isStaticMember()) {
            cmp.insertAccessModifier("static");
        }
        updateParentChildrenData(cmp);
        componentStack.push(cmp);
    }

    private void processGetterDef(Node n) throws Exception {
        Component cmp;
        currCyclomaticComplexity = 1;
        String cmpName = "get_" + n.getString();
        logger.info("Found getter definition: " + cmpName);
        cmp = createComponent(OOPSourceModelConstants.ComponentType.METHOD, n);
        cmp.setComponentName(ParseUtil.generateComponentName(cmpName, componentStack));
        cmp.setName(cmpName);
        cmp.setPackageName(module.modulePkg());
        updateParentChildrenData(cmp);
        if (n.isStaticMember()) {
            cmp.insertAccessModifier("static");
        }
        componentStack.push(cmp);
    }

    private void processClass(Component cmp, Node n) throws Exception {
        cmp = createComponent(OOPSourceModelConstants.ComponentType.CLASS, n);
        String name = null;
        if (NodeUtil.isNameDeclaration(n.getParent().getParent())) {
            if (n.getParent().isName()) {
                name = n.getParent().getString();
            }
        } else if (n.hasChildren() && n.getFirstChild().isName()) {
            name = n.getFirstChild().getString();
        } else {
            name = file.shortName();
        }
        cmp.setComponentName(ParseUtil.generateComponentName(name, componentStack));
        cmp.setName(name);
        cmp.setPackageName(module.modulePkg());
        updateParentChildrenData(cmp);
        if (n.getSecondChild().isName()) {
            // this class extends another class
            logger.info("this class extends " + n.getSecondChild().getString());
            if (resolveType(n.getSecondChild().getString()) != null) {
                cmp.insertComponentRef(new TypeExtensionReference(resolveType(n.getSecondChild().getString())));
            }
        }
        componentStack.push(cmp);
    }

    private void processMemberFunctionDef(Component cmp, Node n) throws Exception {
        currCyclomaticComplexity = 1;
        if (n.hasOneChild() && NodeUtil.isEs6Constructor(n.getFirstChild())) {
            logger.info("Found constructor");
            cmp = createComponent(OOPSourceModelConstants.ComponentType.CONSTRUCTOR, n);
            cmp.setComponentName(ParseUtil.generateComponentName("constructor", componentStack));
            cmp.setName("constructor");
            cmp.setPackageName(module.modulePkg());
            updateParentChildrenData(cmp);
            componentStack.push(cmp);
        } else {
            logger.info("Found instance method: " + n.getString());
            cmp = createComponent(OOPSourceModelConstants.ComponentType.METHOD, n);
            cmp.setComponentName(ParseUtil.generateComponentName(n.getString(), componentStack));
            cmp.setName(n.getString());
            cmp.setPackageName(module.modulePkg());
            if (n.isStaticMember()) {
                cmp.insertAccessModifier("static");
            }
            cmp.insertAccessModifier("public");
            updateParentChildrenData(cmp);
            componentStack.push(cmp);
        }
    }

    private void processVariableAssignment(Component cmp, Node assignmentNode) {
        if (assignmentNode != null && NodeUtil.isLiteralValue(assignmentNode, false)) {
            cmp.setCodeFragment(cmp.name() + " : " + declarationSnippet(assignmentNode.getToken()));
        } else if (assignmentNode != null && assignmentNode.hasChildren() && assignmentNode.isNew()
                && (assignmentNode.getFirstChild().isName() || assignmentNode.getFirstChild().isGetProp())) {
            String invokedType;
            if (assignmentNode.getFirstChild().isGetProp()) {
                invokedType = assignmentNode.getFirstChild().getFirstChild().getString();
                if (resolveType(invokedType) != null) {
                    cmp.insertComponentRef(new SimpleTypeReference(resolveType(invokedType)));
                }
            } else {
                invokedType = assignmentNode.getFirstChild().getString();
                if (resolveType(invokedType) != null) {
                    cmp.insertComponentRef(new SimpleTypeReference(resolveType(invokedType)));
                }
            }
            cmp.setCodeFragment(cmp.name() + " : " + invokedType);
        } else {
            cmp.setCodeFragment(cmp.name());
        }
    }

    private void completeComponent() {
        if (!componentStack.isEmpty()) {
            Component completedCmp = componentStack.pop();
            logger.info("Completing component: " + completedCmp.uniqueName());
            ParseUtil.copyRefsToParents(completedCmp, componentStack);
            // update cyclomatic complexity if component is a method
            if (completedCmp.componentType().isMethodComponent()
                    && !ParseUtil.componentStackContainsInterface(componentStack)) {
                completedCmp.setCyclo(currCyclomaticComplexity);
            } else if (completedCmp.componentType() == OOPSourceModelConstants.ComponentType.CLASS) {
                completedCmp.setCyclo(ParseUtil.calculateClassCyclo(completedCmp, srcModel));
                completedCmp.setImports(new ArrayList<>(module.getClassImports().stream()
                                                              .map(ES6ClassImport::qualifiedClassName)
                                                              .collect(Collectors.toList())));
            }
            srcModel.insertComponent(completedCmp);
        }
    }

    private String generateComponentName(String identifier,
                                         OOPSourceModelConstants.ComponentType componentType) throws Exception {
        if (componentType == OOPSourceModelConstants.ComponentType.FIELD) {
            return ParseUtil.newestBaseComponent(componentStack).componentName() + "." + identifier;
        } else {
            return ParseUtil.generateComponentName(identifier, componentStack);
        }
    }

    /**
     * Creates a new component representing the given node object, see
     * {@link Component}.
     */
    private Component createComponent(OOPSourceModelConstants.ComponentType componentType, Node n) {
        Component newCmp = new Component();
        newCmp.setComponentType(componentType);
        newCmp.setSourceFilePath(file.path());
        if (NodeUtil.getBestJSDocInfo(n) != null) {
            String doc = NodeUtil.getBestJSDocInfo(n).getOriginalCommentString();
            if (doc != null) {
                newCmp.setComment(doc);
            }
        }
        return newCmp;
    }

    /**
     * Updates the parent's list of children to include the given child component for parent components of the given
     * component if they exist.
     */
    private void updateParentChildrenData(Component childCmp) throws Exception {
        if (!childCmp.componentType().isBaseComponent()) {
            if (childCmp.componentType() == OOPSourceModelConstants.ComponentType.FIELD) {
                ParseUtil.newestBaseComponent(componentStack).insertChildComponent(childCmp.componentName());
            } else {
                if (!componentStack.isEmpty()) {
                    String parentName = childCmp.parentUniqueName();
                    for (int i = componentStack.size() - 1; i >= 0; i--) {
                        if (componentStack.get(i).uniqueName().equals(parentName)) {
                            componentStack.get(i).insertChildComponent(childCmp.uniqueName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Tries to return the full, unique type name of the given type, null otherwise.
     */
    private String resolveType(String type) {
        List<ES6ClassImport> tmpType = module.matchingImportsByName(type);
        if (!tmpType.isEmpty()) {
            return tmpType.get(0).qualifiedClassName();
        } else if (module.declaredClasses().contains(type)) {
            if (module.modulePkg().isEmpty()) {
                return type;
            } else {
                return module.modulePkg() + "." + type;
            }
        } else { return null; }
    }
}
