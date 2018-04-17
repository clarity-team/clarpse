package com.clarity.compiler;

import com.clarity.CommonDir;
import com.clarity.antlr.golang.GolangBaseListener;
import com.clarity.antlr.golang.GolangLexer;
import com.clarity.antlr.golang.GolangParser;
import com.clarity.antlr.golang.GolangParser.SourceFileContext;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.invocation.TypeImplementation;
import com.clarity.listener.GoLangTreeListener;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import edu.emory.mathcs.backport.java.util.Collections;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.Comparator;
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

        Set<Component> baseComponents = srcModel.getComponents().values().stream()
                .filter(s -> (s.componentType().isBaseComponent())).collect(Collectors.toSet());
        ImplementedInterfaces implementedInterfacesGatherer = new ImplementedInterfaces(srcModel);
        for (Component baseCmp : baseComponents) {
            List<String> implementedInterfaces = implementedInterfacesGatherer.getImplementedInterfaces(baseCmp);
            for (String implementedInterface : implementedInterfaces) {
                baseCmp.insertComponentInvocation(new TypeImplementation(implementedInterface));
            }
        }
    }

    private List<String> getProjectFileSymbols(List<RawFile> files) throws Exception {

        List<String> projectFileTypes = new ArrayList<String>();
        String smallestCodeBaseContaininingDir = files.get(0).name();

        for (int i = 1; i < files.size(); i++) {
            smallestCodeBaseContaininingDir = new CommonDir(smallestCodeBaseContaininingDir, files.get(i).name())
                    .value();
        }

        if (smallestCodeBaseContaininingDir.startsWith("/")) {
            smallestCodeBaseContaininingDir.substring(1);
        }

        for (RawFile file : files) {
            String modFileName = "";
            if (file.name().contains("src/")) {
                modFileName = (file.name().substring(file.name().indexOf("src/") + 4));
            } else {
                modFileName = file.name().replaceAll(smallestCodeBaseContaininingDir, "");
                if (modFileName.startsWith("/")) {
                    modFileName = modFileName.substring(1);
                }
            }

            if (modFileName.contains("/")) {
                projectFileTypes.add(modFileName.substring(0, modFileName.lastIndexOf("/")));
            }
        }
        return projectFileTypes;
    }

    @Override
    public OOPSourceCodeModel compile(SourceFiles rawData) throws Exception {
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final List<RawFile> files = rawData.getFiles();

        if (files.size() < 1) {
            return srcModel;
        } else {
            List<String> projectFileTypes = getProjectFileSymbols(files);
            // sort fileTypes by length in desc order so that the longest types are at the
            // top.
            Collections.sort(projectFileTypes, new LengthComp());
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
             * nondeterministic.
             */
            updateStructCyclomaticComplexities(srcModel);
        }
        return srcModel;
    }

    private void updateStructCyclomaticComplexities(OOPSourceCodeModel srcModel) {
        srcModel.getComponents().forEach((k, v) -> {
            if (v.componentType() == ComponentType.STRUCT) {
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
    private void compileFiles(List<RawFile> files, OOPSourceCodeModel srcModel, List<String> projectFileTypes) {
        // holds types that may be accessed by all the source file parsing operations...
        List<Map.Entry<String, Component>> structWaitingList = new ArrayList<>();

        for (RawFile file : files) {
            try {
                CharStream charStream = new ANTLRInputStream(file.content());
                TokenStream tokens = new CommonTokenStream(new GolangLexer(charStream));
                GolangParser parser = new GolangParser(tokens);
                SourceFileContext sourceFileContext = parser.sourceFile();
                parser.setErrorHandler(new BailErrorStrategy());
                parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
                ParseTreeWalker walker = new ParseTreeWalker();
                GolangBaseListener listener = new GoLangTreeListener(srcModel, projectFileTypes, file, structWaitingList);
                walker.walk(listener, sourceFileContext);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            } catch (StackOverflowError error) {
                error.printStackTrace();
                continue;
            }
        }
    }
}

class ImplementedInterfaces {
    private OOPSourceCodeModel model;
    private Map<String, List<String>> interfaceMethodSpecsPairs;

    ImplementedInterfaces(OOPSourceCodeModel srcModel) throws Exception {
        this.model = srcModel;
        Set<Component> allInterfaceComponents = srcModel.getComponents().values().stream()
                .filter(s -> (s.componentType() == ComponentType.INTERFACE)).collect(Collectors.toSet());
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
    public List<String> getImplementedInterfaces(Component baseComponent) {

        // holds all the implemented interfaces for the given component
        List<String> implementedInterfaces = new ArrayList<String>();

        if (baseComponent.componentType().isBaseComponent()
                && baseComponent.componentType() != ComponentType.INTERFACE) {
            // generate a list of method signatures for the given base component.
            List<String> baseComponentMethodSignatures = new ArrayList<String>();
            for (String baseComponentChild : baseComponent.children()) {
                Optional<Component> childCmp = model.getComponent(baseComponentChild);
                if (childCmp.isPresent() && childCmp.get().componentType().isMethodComponent()) {
                    baseComponentMethodSignatures.add(generateMethodSignature(childCmp.get()));
                }
            }

            // check to see if the current component satisfies any of the collected
            // interfaces
            if (!baseComponentMethodSignatures.isEmpty()) {
                for (Entry<String, List<String>> potentiallyImplementedInterface : this.interfaceMethodSpecsPairs
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
        if (interfaceComponent.componentType() != ComponentType.INTERFACE) {
            throw new Exception("Cannot retrieve method specs for a non-interface component!");
        }

        ArrayList<String> methodSpecs = new ArrayList<String>();

        for (ComponentInvocation extend : interfaceComponent.componentInvocations(ComponentInvocations.EXTENSION)) {
            Optional<Component> cmp = model.getComponent(extend.invokedComponent());
            if (cmp.isPresent() && cmp.get().componentType() == ComponentType.INTERFACE
                    && !cmp.get().equals(interfaceComponent)) {
                methodSpecs.addAll(getListOfMethodSpecs(cmp.get()));
            }
        }
        for (String childMethod : interfaceComponent.children()) {
            Optional<Component> childMethodCmp = model.getComponent(childMethod);
            if (childMethodCmp.isPresent() && childMethodCmp.get().componentType() == ComponentType.METHOD) {
                methodSpecs.add(generateMethodSignature(childMethodCmp.get()));
            }
        }
        return methodSpecs;
    }

    private String generateMethodSignature(Component methodComponent) {
        String signature = methodComponent.name() + "(";
        for (String methodParam : methodComponent.children()) {
            Optional<Component> methodParamCmp = model.getComponent(methodParam);
            if (methodParamCmp.isPresent()
                    && methodParamCmp.get().componentInvocations(ComponentInvocations.DECLARATION).size() > 0) {
                signature += methodParamCmp.get().componentInvocations(ComponentInvocations.DECLARATION).get(0)
                        .invokedComponent() + ",";
            }
        }
        signature = signature.replaceAll(",$", "");
        signature += ")";
        if (methodComponent.value() != null) {
            signature += methodComponent.value().replaceAll(" ", "");
        }
        return signature;
    }
}

class LengthComp implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(o2.length(), o1.length());
    }
}
