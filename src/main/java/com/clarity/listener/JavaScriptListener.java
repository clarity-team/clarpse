package com.clarity.listener;

import com.clarity.ResolvedRelativePath;
import com.clarity.TrimmedString;
import com.clarity.compiler.File;
import com.clarity.reference.SimpleTypeReference;
import com.clarity.reference.TypeExtensionReference;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.NodeTraversal.Callback;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT;
import static com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT;

/**
 * Listener for JavaScript ES6+ source files, based on google's closure
 * compiler.
 */
public class JavaScriptListener implements Callback {

    private final Stack<Component> componentStack = new Stack<>();
    private final List<File> files;
    private final Map<String, JavaScriptExportsListener.JSExport> exportsMap;
    private OOPSourceCodeModel srcModel;
    private File file;
    private List<String> projectFileTypes;
    private String currPackage = "";
    private String currProjectFileType = "";
    private final Map<String, String> currentImportsMap = new HashMap<>();
    private int currCyclomaticComplexity = 0;

    public JavaScriptListener(final OOPSourceCodeModel srcModel, final File file, List<String> projectFileTypes,
                              List<File> files, Map<String, JavaScriptExportsListener.JSExport> exportsMap) throws Exception {
        this.srcModel = srcModel;
        this.file = file;
        this.projectFileTypes = projectFileTypes;
        this.files = files;
        this.exportsMap = exportsMap;
        System.out.println("\nParsing New JS File: " + file.name() + "\n");

        if (file.name().contains("/")) {
            String modFileName = file.name().substring(0, file.name().lastIndexOf("/"));
            for (String s : projectFileTypes) {
                if (modFileName.endsWith(s)) {
                    currPackage = new TrimmedString(s, "/").value().replaceAll("/", ".");
                    currProjectFileType = s;
                    break;
                }
            }
        }
    }

    @Override
    public void visit(NodeTraversal t, Node n, Node parent) {

        String fileName = null;
        try {
            fileName = new TrimmedString(file.name(), "/").value();
            String baseCmpName = null;

            if (n.isClass()) {
                if (n.getParent().isExport() && n.getParent().getBooleanProp(Node.EXPORT_DEFAULT)) {
                    if (fileName.contains("/")) {
                        baseCmpName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
                    } else {
                        baseCmpName = fileName.substring(0, fileName.lastIndexOf("."));
                    }
                } else if (n.hasChildren()) {
                    baseCmpName = n.getFirstChild().getString();
                }
                baseCmpName = new TrimmedString(baseCmpName, "/").value().replaceAll("/", ".");
            } else if (n.isMemberFunctionDef()) {
                baseCmpName = n.getString();
            } else if (n.isGetterDef()) {
                baseCmpName = "get_" + n.getString();
            } else if (n.isSetterDef()) {
                baseCmpName = "set_" + n.getString();
            }

            if (baseCmpName != null) {
                while (!componentStack.isEmpty() && (componentStack.peek().componentName().contains(baseCmpName + ".")
                        || componentStack.peek().componentName().endsWith(baseCmpName))) {
                    completeComponent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Returns the package name represented by the given import source path.
     * E.g. getImportPackage("/proj/module/A.js") -> "proj.module"
     * This method handles relative and absolute paths provided for the import source path.
     */
    private String getImportPackage(String importSourcePath) throws Exception {
        String importSourceType = null;
        if (importSourcePath.startsWith("/")) {
            // absolute import path provided..
            if (importSourcePath.endsWith(".js") && importSourcePath.contains("/")) {
                importSourceType = importSourcePath.substring(0, importSourcePath.lastIndexOf("/"));
            }
        } else {
            // relative import path provided...
            importSourceType = new TrimmedString(
                    new ResolvedRelativePath(currProjectFileType, importSourcePath).value(), "/").value();
        }
        for (String fileTypes : projectFileTypes) {
            if (importSourceType.startsWith(fileTypes)) {
                importSourceType = new TrimmedString(fileTypes, "/").value().replaceAll("/", ".");
                break;
            }
        }
        return importSourceType.replaceAll("/", ".");
    }

    private boolean shouldTraverse(Node n, Node parent) throws Exception {
        if (n.isImport()) {
            // Represents the module name of the import statement.
            String moduleName = n.getLastChild().getString();
            // Add file ending to module if it does not exist
            if (!moduleName.matches("^.*\\.[a-z]+$")) {
                moduleName += ".js";
            }
            // Get package represented by the moduleName path
            String importSourcePackage = getImportPackage(moduleName);
            // Update imports map..
            if (n.getFirstChild().isName()) {
                processDefaultImport(n.getFirstChild().getString(),
                        new TrimmedString(importSourcePackage + "." + n.getFirstChild().getString(), ".").value());
            }
            for (Node importNode : n.children()) {
                if (importNode.isImportSpecs()) {
                    for (Node importSpecsChildNode : importNode.children()) {
                        if (importSpecsChildNode.isImportSpec()) {
                            processImportSpec(importSpecsChildNode, importSourcePackage);
                        }
                    }
                } else if (importNode.isImportSpec()) {
                    processImportSpec(importNode, importSourcePackage);
                }
            }
        }
        Component cmp = null;
        if (n.isClass()) {
            processClass(cmp, n);
        } else if (n.isMemberFunctionDef()) {
            processMemberFunctionDef(cmp, n);
        } else if (n.isGetterDef()) {
            currCyclomaticComplexity = 1;
            String cmpName = "get_" + n.getString();
            System.out.println("Found getter definition: " + cmpName);
            cmp = createComponent(ComponentType.METHOD, n);
            cmp.setComponentName(ParseUtil.generateComponentName(cmpName, componentStack));
            cmp.setName(cmpName);
            updateParentChildrenData(cmp);
            if (n.isStaticMember()) {
                cmp.insertAccessModifier("static");
            }
            cmp.insertAccessModifier("public");
            componentStack.push(cmp);
        } else if (n.isSetterDef()) {
            currCyclomaticComplexity = 1;
            String cmpName = "set_" + n.getString();
            System.out.println("Found setter definition: " + cmpName);
            cmp = createComponent(ComponentType.METHOD, n);
            cmp.setComponentName(ParseUtil.generateComponentName(cmpName, componentStack));
            cmp.setName(cmpName);
            cmp.setPackageName(currPackage);
            cmp.insertAccessModifier("public");
            if (n.isStaticMember()) {
                cmp.insertAccessModifier("static");
            }
            updateParentChildrenData(cmp);
            componentStack.push(cmp);
        } else if (n.isParamList() && !componentStack.isEmpty() && componentStack.peek().componentType().isMethodComponent()) {
            List<Component> generatedParamComponents = new ArrayList<>();
            // determine type of param component to create based on type of current component at the top of stack
            ComponentType paramComponentType = METHOD_PARAMETER_COMPONENT;
            if (componentStack.peek().componentType() == ComponentType.CONSTRUCTOR) {
                paramComponentType = CONSTRUCTOR_PARAMETER_COMPONENT;
            }
            for (Node param : n.children()) {
                String paramName = null;
                if (param.isString() || param.isName()) {
                    paramName = param.getString();
                } else if (param.isDefaultValue()) {
                    paramName = param.getFirstChild().getString();
                } else {
                    throw new Exception("Unrecognized function param type! " + param.toString());
                }
                cmp = createComponent(paramComponentType, n);
                cmp.setComponentName(ParseUtil.generateComponentName(paramName, componentStack));
                cmp.setName(paramName);
                cmp.setPackageName(currPackage);
                updateParentChildrenData(cmp);
                generatedParamComponents.add(cmp);
            }
            // Set parent method code Fragment using param list
            Component parentMethod = componentStack.peek();
            parentMethod.setCodeFragment(parentMethod.name() + generateCodeFragment(generatedParamComponents));
            // Complete method param components
            for (Component paramCmp : generatedParamComponents) {
                componentStack.push(paramCmp);
                completeComponent();
            }
        } else if (n.isAssign() && n.getFirstChild().hasChildren() && n.getFirstChild().getFirstChild().isThis()
                && ParseUtil.newestMethodComponent(componentStack).componentType() == ComponentType.CONSTRUCTOR) {
            String fieldVarname = n.getFirstChild().getSecondChild().getString();
            System.out.println("Found field variable: " + fieldVarname);
            cmp = createComponent(ComponentType.FIELD, n);
            cmp.setComponentName(generateComponentName(fieldVarname, ComponentType.FIELD));
            cmp.setName(fieldVarname);
            cmp.setPackageName(currPackage);
            cmp.insertAccessModifier("private");
            processVariableAssignment(cmp, n.getSecondChild());
            updateParentChildrenData(cmp);
            componentStack.push(cmp);
            completeComponent();
        } else if (!componentStack.isEmpty() && (n.isVar() || n.isLet())
                && (ParseUtil.newestMethodComponent(componentStack).componentType() == ComponentType.METHOD
                || ParseUtil.newestMethodComponent(componentStack).componentType() == ComponentType.CONSTRUCTOR)) {
            String localVarName = n.getFirstChild().getString();
            System.out.println("Found local variable: " + localVarName);
            cmp = createComponent(ComponentType.LOCAL, n);
            cmp.setComponentName(ParseUtil.generateComponentName(localVarName, componentStack));
            cmp.setName(localVarName);
            cmp.setPackageName(currPackage);
            processVariableAssignment(cmp, n.getFirstChild().getFirstChild());
            updateParentChildrenData(cmp);
            componentStack.push(cmp);
            completeComponent();
        } else if (n.isGetProp() && n.getFirstChild().isName() && !n.getFirstChild().getString().isEmpty()
                && (NodeUtil.isImportedName(n.getFirstChild())
                || Character.isUpperCase(n.getFirstChild().getString().codePointAt(0)))
                && !componentStack.isEmpty()) {
            Component latestCmp = componentStack.pop();
            latestCmp.insertComponentRef(new SimpleTypeReference(resolveType(n.getFirstChild().getString())));
            componentStack.push(latestCmp);
        } else if (n.isCase() || n.isSwitch() || n.isIf() || n.isAnd() || n.isOr() || n.isHook()) {
            currCyclomaticComplexity += 1;
        }
        return true;
    }

    private void processDefaultImport(String pkgClass, String fullPkg) throws Exception {
        updateImportsMap(pkgClass, fullPkg, true);
    }

    private void processImportSpec(Node importSpec, String origin) throws Exception {
        String importPrefix = "";
        if (!origin.isEmpty()) {
            importPrefix = new TrimmedString(origin, ".").value() + ".";
        }
        if (importSpec.hasOneChild()) {
            String childOneStr = importSpec.getChildAtIndex(0).getString();
            String fullPkg = importPrefix + childOneStr;
            updateImportsMap(childOneStr, fullPkg, false);
        } else if (importSpec.hasTwoChildren()) {
            String childOneStr = importSpec.getChildAtIndex(0).getString();
            String childTwoStr = importSpec.getChildAtIndex(1).getString();
            String fullPkg = importPrefix + childOneStr;
            updateImportsMap(childTwoStr, fullPkg, false);
        }
    }

    private void updateImportsMap(String importComponent, String importPkg, boolean defaultExport) throws Exception {
        boolean foundLocalMatch = false;
        if (!defaultExport) {
            if (exportsMap.containsKey(importPkg)) {
                JavaScriptExportsListener.JSExport jsExport = exportsMap.get(importPkg);
                if (jsExport.exportedPkgAlias() != null && jsExport.exportedPkgAlias().equals(importPkg)) {
                    importPkg = jsExport.exportedPkg();
                }
            }
        } else {
            String searchBasePkg = new TrimmedString(importPkg.replace(importComponent, ""), ".").value();
            for (JavaScriptExportsListener.JSExport value : exportsMap.values()) {
                String potentialExportPkg = value.exportedPkgAlias();
                if (value.exportedPkgAlias() == null) {
                    potentialExportPkg = value.exportedPkg();
                }
                if (value.fileType().replace("/", ".").equals(searchBasePkg)) {
                    currentImportsMap.put(importComponent, potentialExportPkg);
                    return;
                }
            }
        }
        currentImportsMap.put(importComponent, importPkg);
    }

    private String generateCodeFragment(List<Component> components) {
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

    private void processClass(Component cmp, Node n) throws Exception {
        cmp = createComponent(ComponentType.CLASS, n);
        String name = "";
        if (n.getParent().isExport() && n.getParent().getBooleanProp(Node.EXPORT_DEFAULT)) {
            String fileName = new TrimmedString(file.name(), "/").value();
            if (fileName.contains("/")) {
                name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
            } else {
                name = fileName.substring(0, fileName.lastIndexOf("."));
            }
        } else if (n.hasChildren()) {
            name = n.getFirstChild().getString();
        }
        name = name.replaceAll("/", ".");
        cmp.setComponentName(ParseUtil.generateComponentName(name, componentStack));
        cmp.setName(name);
        cmp.setPackageName(currPackage);
        updateParentChildrenData(cmp);
        if (n.getSecondChild().isName()) {
            // this class extends another class
            System.out.println("this class extends " + n.getSecondChild().getString());
            cmp.insertComponentRef(new TypeExtensionReference(resolveType(n.getSecondChild().getString())));
        }
        componentStack.push(cmp);
    }

    private void processMemberFunctionDef(Component cmp, Node n) throws Exception {
        currCyclomaticComplexity = 1;
        if (n.getString() != null && n.getString().equals("constructor")) {
            System.out.println("Found constructor");
            cmp = createComponent(ComponentType.CONSTRUCTOR, n);
            cmp.setComponentName(ParseUtil.generateComponentName("constructor", componentStack));
            cmp.setName("constructor");
            cmp.setPackageName(currPackage);
            updateParentChildrenData(cmp);
            componentStack.push(cmp);
        } else {
            System.out.println("Found instance method: " + n.getString());
            cmp = createComponent(ComponentType.METHOD, n);
            cmp.setComponentName(ParseUtil.generateComponentName(n.getString(), componentStack));
            cmp.setName(n.getString());
            cmp.setPackageName(currPackage);
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
                cmp.insertComponentRef(new SimpleTypeReference(resolveType(invokedType)));
            } else {
                invokedType = assignmentNode.getFirstChild().getString();
                cmp.insertComponentRef(new SimpleTypeReference(resolveType(invokedType)));
            }
            cmp.setCodeFragment(cmp.name() + " : " + invokedType);
        } else {
            cmp.setCodeFragment(cmp.name());
        }
    }

    private void completeComponent() {
        if (!componentStack.isEmpty()) {
            final Component completedCmp = componentStack.pop();
            System.out.println("Completing component: " + completedCmp.uniqueName());
            ParseUtil.copyRefsToParents(completedCmp, componentStack);
            // update cyclomatic complexity if component is a method
            if (completedCmp.componentType().isMethodComponent()
                    && !ParseUtil.componentStackContainsInterface(componentStack)) {
                completedCmp.setCyclo(currCyclomaticComplexity);
            } else if (completedCmp.componentType() == ComponentType.CLASS) {
                completedCmp.setCyclo(ParseUtil.calculateClassCyclo(completedCmp, srcModel));
                completedCmp.setImports(currentImportsMap.values().stream()
                        .collect(Collectors.toList()));
            }
            srcModel.insertComponent(completedCmp);
        }
    }

    private String generateComponentName(final String identifier, ComponentType componentType) throws Exception {
        if (componentType == ComponentType.FIELD) {
            return ParseUtil.newestBaseComponent(componentStack).componentName() + "." + identifier;
        } else {
            return ParseUtil.generateComponentName(identifier, componentStack);
        }
    }

    /**
     * Creates a new component representing the given node object, see
     * {@link Component}.
     */
    private Component createComponent(ComponentType componentType, Node n) {
        final Component newCmp = new Component();
        newCmp.setComponentType(componentType);
        newCmp.setSourceFilePath(file.name());
        if (NodeUtil.getBestJSDocInfo(n) != null) {
            String doc = NodeUtil.getBestJSDocInfo(n).getOriginalCommentString();
            if (doc != null) {
                newCmp.setComment(doc);
            }
        }
        return newCmp;
    }

    /**
     * Updates the list of children to include the given child component for parent components of the given
     * component if they exist.
     */
    private void updateParentChildrenData(Component childCmp) throws Exception {
        if (childCmp.componentType() == ComponentType.FIELD) {
            ParseUtil.newestBaseComponent(componentStack).insertChildComponent(childCmp.componentName());
        } else {
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

    /**
     * Tries to return the full, unique type name of the given type.
     */
    private String resolveType(final String type) {

        if (currentImportsMap.containsKey(type)) {
            return currentImportsMap.get(type).replaceAll("/", ".");
        }
        final Iterator<?> it = currentImportsMap.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry pair = (Map.Entry) it.next();
            if (type.startsWith((String) pair.getKey())) {
                return (((String) pair.getValue()).replaceAll("/", "."));
            }
        }
        return type;
    }
}
