package com.hadii.clarpse.compiler;

import com.hadii.antlr.golang.GoLexer;
import com.hadii.antlr.golang.GoParser;
import com.hadii.antlr.golang.GoParserBaseListener;
import com.hadii.clarpse.CommonDir;
import com.hadii.clarpse.listener.GoLangTreeListener;
import com.hadii.clarpse.reference.ComponentReference;
import com.hadii.clarpse.reference.TypeImplementationReference;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Antlr4 based GoLang compiler.
 */
public class ClarpseGoCompiler implements ClarpseCompiler {
    private void resolveInterfaces(OOPSourceCodeModel srcModel) throws Exception {
        Set<Component> baseComponents = srcModel.components()
                .filter(s -> (s.componentType().isBaseComponent())).collect(Collectors.toSet());
        ImplementedInterfaces implementedInterfacesGatherer = new ImplementedInterfaces(srcModel);
        for (Component baseCmp : baseComponents) {
            List<String> implementedInterfaces = implementedInterfacesGatherer.getImplementedInterfaces(baseCmp);
            for (String implementedInterface : implementedInterfaces) {
                baseCmp.insertComponentRef(new TypeImplementationReference(implementedInterface));
            }
        }
    }

    private List<String> getProjectFileSymbols(List<ProjectFile> files) throws Exception {
        List<String> projectFileTypes = new ArrayList<>();
        String smallestCodeBaseContaininingDir = files.get(0).path();
        for (int i = 1; i < files.size(); i++) {
            smallestCodeBaseContaininingDir = new CommonDir(smallestCodeBaseContaininingDir, files.get(i).path())
                    .value();
        }
        if (smallestCodeBaseContaininingDir.startsWith("/")) {
            smallestCodeBaseContaininingDir = smallestCodeBaseContaininingDir.substring(1);
        }
        for (ProjectFile ProjectFile : files) {
            String modFileName;
            modFileName = ProjectFile.path().replaceAll(smallestCodeBaseContaininingDir, "");
            if (modFileName.startsWith("/")) {
                modFileName = modFileName.substring(1);
            }
            if (modFileName.contains("/")) {
                projectFileTypes.add(modFileName.substring(0, modFileName.lastIndexOf("/")));
            }
        }
        return projectFileTypes;
    }

    @Override
    public OOPSourceCodeModel compile(ProjectFiles projectFiles) throws Exception {
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final List<ProjectFile> files = projectFiles.getFiles();

        if (files.size() < 1) {
            return srcModel;
        } else {
            List<String> projectFileTypes = getProjectFileSymbols(files);
            // sort fileTypes by length in desc order so that the longest types are at the
            // top.
            projectFileTypes.sort(new LengthComp());
            // compile code based on number of workers.
            compileFiles(files, srcModel, projectFileTypes);
            /**
             * In GoLang, interfaces are implemented implicitly. As a result, we handle
             * their detection in the following way: Once we have parsed the entire code
             * base, we compare every parsed interface to every parsed struct to determine
             * if that struct implements the given interface.
             */
            resolveInterfaces(srcModel);
            /**
             * Update cyclomatic complexities of all structs. We do this right now after the source
             * code model has been built as opposed to at parse time because methods for any given
             * struct in Go can be located anywhere in the project, making their parsing order
             * non-deterministic.
             */
            updateStructCyclomaticComplexities(srcModel);
        }
        return srcModel;
    }

    private void updateStructCyclomaticComplexities(OOPSourceCodeModel srcModel) {
        srcModel.components().forEach(v -> {
            if (v.componentType() == OOPSourceModelConstants.ComponentType.STRUCT) {
                int childCount = 0;
                int complexityTotal = 0;
                for (String childrenName : v.children()) {
                    Optional<Component> child = srcModel.getComponent(childrenName);
                    if (child.isPresent() && child.get().componentType().isMethodComponent()) {
                        childCount += 1;
                        complexityTotal += child.get().cyclo();
                    }
                }
                if (childCount != 0 && complexityTotal != 0) {
                    v.setCyclo(complexityTotal / childCount);
                }
            }
        });
    }

    private void compileFiles(List<ProjectFile> files, OOPSourceCodeModel srcModel, List<String> projectFileTypes) {
        // holds types that may be accessed by all the source ProjectFile parsing operations...
        List<Map.Entry<String, Component>> structWaitingList = new ArrayList<>();
        for (ProjectFile projectFile : files) {
            try {
                CharStream charStream = new ANTLRInputStream(projectFile.content());
                TokenStream tokens = new CommonTokenStream(new GoLexer(charStream));
                GoParser parser = new GoParser(tokens);
                GoParser.SourceFileContext sourceFileContext = parser.sourceFile();
                ParseTreeWalker walker = new ParseTreeWalker();
                GoParserBaseListener listener = new GoLangTreeListener(srcModel, projectFileTypes, projectFile, structWaitingList);
                walker.walk(listener, sourceFileContext);
            } catch (Exception | StackOverflowError e) {
                e.printStackTrace();
            }
        }
    }
}

class ImplementedInterfaces {
    private final OOPSourceCodeModel model;
    private final Map<String, List<String>> interfaceMethodSpecsPairs;

    ImplementedInterfaces(OOPSourceCodeModel srcModel) throws Exception {
        model = srcModel;
        Set<Component> allInterfaceComponents = srcModel.components()
                .filter(s -> (s.componentType() == OOPSourceModelConstants.ComponentType.INTERFACE)).collect(Collectors.toSet());
        interfaceMethodSpecsPairs = new HashMap<>();
        for (Component interfaceCmp : allInterfaceComponents) {
            List<String> interfaceMethodSpecs = getListOfMethodSpecs(interfaceCmp);
            if (!interfaceMethodSpecs.isEmpty()) {
            interfaceMethodSpecsPairs.put(interfaceCmp.uniqueName(), interfaceMethodSpecs);
            }
        }
    }

    /**
     * Retrieves a list of Strings corresponding to the the interfaces implemented
     * by the given base {@linkplain Component}.
     */
    List<String> getImplementedInterfaces(Component baseComponent) {
        // holds all the implemented interfaces for the given component
        List<String> implementedInterfaces = new ArrayList<>();
        if (baseComponent.componentType().isBaseComponent()
                && baseComponent.componentType() != OOPSourceModelConstants.ComponentType.INTERFACE) {
            // generate a list of method signatures for the given base component.
            List<String> baseComponentMethodSignatures = new ArrayList<>();
            for (String baseComponentChild : baseComponent.children()) {
                Optional<Component> childCmp = model.getComponent(baseComponentChild);
                if (childCmp.isPresent() && childCmp.get().componentType().isMethodComponent()) {
                    baseComponentMethodSignatures.add(generateMethodSignature(childCmp.get()));
                }
            }
            // check to see if the current component satisfies any of the collected
            // interfaces
            if (!baseComponentMethodSignatures.isEmpty()) {
                for (Entry<String, List<String>> potentiallyImplementedInterface : interfaceMethodSpecsPairs
                        .entrySet()) {
                    if (baseComponentMethodSignatures.containsAll(potentiallyImplementedInterface.getValue())) {
                        // found a match!
                        implementedInterfaces.add(potentiallyImplementedInterface.getKey());
                    }
                }
            }
        }
        return implementedInterfaces;
    }

    /**
     * Returns a list of available (interface method specifications outside the
     * local code base are not available) interface method specifications for the
     * given interface component.
     */
    private List<String> getListOfMethodSpecs(Component interfaceComponent) throws Exception {
        if (interfaceComponent.componentType() != OOPSourceModelConstants.ComponentType.INTERFACE) {
            throw new Exception("Cannot retrieve method specs for a non-interface component!");
        }
        ArrayList<String> methodSpecs = new ArrayList<>();
        for (ComponentReference extend : interfaceComponent.references(OOPSourceModelConstants.TypeReferences.EXTENSION)) {
            Optional<Component> cmp = model.getComponent(extend.invokedComponent());
            if (cmp.isPresent() && cmp.get().componentType() == OOPSourceModelConstants.ComponentType.INTERFACE
                    && !cmp.get().equals(interfaceComponent)) {
                methodSpecs.addAll(getListOfMethodSpecs(cmp.get()));
            }
        }
        for (String childMethod : interfaceComponent.children()) {
            Optional<Component> childMethodCmp = model.getComponent(childMethod);
            if (childMethodCmp.isPresent() && childMethodCmp.get().componentType() == OOPSourceModelConstants.ComponentType.METHOD) {
                methodSpecs.add(generateMethodSignature(childMethodCmp.get()));
            }
        }
        return methodSpecs;
    }

    private String generateMethodSignature(Component methodComponent) {
        StringBuilder signature = new StringBuilder(methodComponent.name() + "(");
        for (String methodParam : methodComponent.children()) {
            Optional<Component> methodParamCmp = model.getComponent(methodParam);
            if (methodParamCmp.isPresent()
                    && methodParamCmp.get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).size() > 0) {
                signature.append(methodParamCmp.get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                        .invokedComponent()).append(",");
            }
        }
        signature = new StringBuilder(signature.toString().replaceAll(",$", ""));
        signature.append(")");
        if (methodComponent.value() != null) {
            signature.append(methodComponent.value().replaceAll(" ", ""));
        }
        return signature.toString();
    }
}

