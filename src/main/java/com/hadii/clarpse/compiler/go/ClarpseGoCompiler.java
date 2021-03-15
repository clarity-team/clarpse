package com.hadii.clarpse.compiler.go;

import com.hadii.antlr.golang.GoLexer;
import com.hadii.antlr.golang.GoParser;
import com.hadii.antlr.golang.GoParserBaseListener;
import com.hadii.clarpse.CommonDir;
import com.hadii.clarpse.compiler.ClarpseCompiler;
import com.hadii.clarpse.compiler.ClarpseES6Compiler;
import com.hadii.clarpse.compiler.LengthComp;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger LOGGER = LogManager.getLogger(ClarpseES6Compiler.class);

    private void resolveInterfaces(final OOPSourceCodeModel srcModel) throws Exception {
        final Set<Component> baseComponents = srcModel.components()
                                                      .filter(s -> (s.componentType().isBaseComponent()))
                                                      .collect(Collectors.toSet());
        LOGGER.info("Detected " + baseComponents.size() + " interfaces to resolve.");
        final ImplementedInterfaces implementedInterfacesGatherer = new ImplementedInterfaces(srcModel);
        for (final Component baseCmp : baseComponents) {
            final List<String> implementedInterfaces = implementedInterfacesGatherer
                    .getImplementedInterfaces(baseCmp);
            for (final String implementedInterface : implementedInterfaces) {
                baseCmp.insertComponentRef(new TypeImplementationReference(implementedInterface));
            }
        }
    }

    private List<String> getProjectFileTypes(final List<ProjectFile> files) throws Exception {
        final List<String> projectFileTypes = new ArrayList<>();
        String smallestCodeBaseContaininingDir = files.get(0).path();
        for (int i = 1; i < files.size(); i++) {
            smallestCodeBaseContaininingDir = new CommonDir(smallestCodeBaseContaininingDir,
                                                            files.get(i).path()).value();
        }
        if (smallestCodeBaseContaininingDir.startsWith("/")) {
            smallestCodeBaseContaininingDir = smallestCodeBaseContaininingDir.substring(1);
        }
        for (final ProjectFile projectFile : files) {
            String modFileName;
            modFileName = projectFile.path().replaceAll(smallestCodeBaseContaininingDir, "");
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
    public OOPSourceCodeModel compile(final ProjectFiles projectFiles) throws Exception {
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final List<ProjectFile> files = projectFiles.files();
        final List<GoModule> modules = new GoModules(projectFiles).list();
        if (modules.isEmpty()) {
            compileGoModule(srcModel, files);
        } else {
            for (GoModule module : modules) {
                compileGoModule(srcModel, module.getProjectFiles().files());
            }
        }
        return srcModel;
    }

    private void compileGoModule(OOPSourceCodeModel srcModel, List<ProjectFile> files) throws Exception {
        final List<String> projectFileTypes = getProjectFileTypes(files);
        // sort fileTypes by length in desc order, helps with type resolution.
        projectFileTypes.sort(new LengthComp());
        parseGoFiles(files, srcModel, projectFileTypes);
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

    private void updateStructCyclomaticComplexities(final OOPSourceCodeModel srcModel) {
        LOGGER.info("Updating cyclomatic complexities.");
        srcModel.components().forEach(v -> {
            if (v.componentType() == OOPSourceModelConstants.ComponentType.STRUCT) {
                int childCount = 0;
                int complexityTotal = 0;
                for (final String childrenName : v.children()) {
                    final Optional<Component> child = srcModel.getComponent(childrenName);
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

    private void parseGoFiles(final List<ProjectFile> files, final OOPSourceCodeModel srcModel,
                              final List<String> projectFileTypes) {
        // holds types that may be accessed by all the source ProjectFile parsing operations...
        final List<Map.Entry<String, Component>> structWaitingList = new ArrayList<>();
        for (final ProjectFile projectFile : files) {
            try {
                final CharStream charStream = new ANTLRInputStream(projectFile.content());
                final TokenStream tokens = new CommonTokenStream(new GoLexer(charStream));
                final GoParser parser = new GoParser(tokens);
                final GoParser.SourceFileContext sourceFileContext = parser.sourceFile();
                final ParseTreeWalker walker = new ParseTreeWalker();
                final GoParserBaseListener listener = new GoLangTreeListener(
                        srcModel, projectFileTypes, projectFile, structWaitingList);
                walker.walk(listener, sourceFileContext);
            } catch (final Exception | StackOverflowError e) {
                e.printStackTrace();
            }
        }
    }
}

class ImplementedInterfaces {
    private final OOPSourceCodeModel model;
    private final Map<String, List<String>> interfaceMethodSpecsPairs;

    ImplementedInterfaces(final OOPSourceCodeModel srcModel) throws Exception {
        model = srcModel;
        final Set<Component> allInterfaceComponents = srcModel.components()
                                                              .filter(s -> (s.componentType() == OOPSourceModelConstants.ComponentType.INTERFACE)).collect(Collectors.toSet());
        interfaceMethodSpecsPairs = new HashMap<>();
        for (final Component interfaceCmp : allInterfaceComponents) {
            final List<String> interfaceMethodSpecs = getListOfMethodSpecs(interfaceCmp);
            if (!interfaceMethodSpecs.isEmpty()) {
                interfaceMethodSpecsPairs.put(interfaceCmp.uniqueName(), interfaceMethodSpecs);
            }
        }
    }

    /**
     * Retrieves a list of Strings corresponding to the the interfaces implemented
     * by the given base {@linkplain Component}.
     */
    List<String> getImplementedInterfaces(final Component baseComponent) {
        // holds all the implemented interfaces for the given component
        final List<String> implementedInterfaces = new ArrayList<>();
        if (baseComponent.componentType().isBaseComponent()
                && baseComponent.componentType() != OOPSourceModelConstants.ComponentType.INTERFACE) {
            // generate a list of method signatures for the given base component.
            final List<String> baseComponentMethodSignatures = new ArrayList<>();
            for (final String baseComponentChild : baseComponent.children()) {
                final Optional<Component> childCmp = model.getComponent(baseComponentChild);
                if (childCmp.isPresent() && childCmp.get().componentType().isMethodComponent()) {
                    baseComponentMethodSignatures.add(generateMethodSignature(childCmp.get()));
                }
            }
            // check to see if the current component satisfies any of the collected
            // interfaces
            if (!baseComponentMethodSignatures.isEmpty()) {
                for (final Entry<String, List<String>> potentiallyImplementedInterface : interfaceMethodSpecsPairs
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
    private List<String> getListOfMethodSpecs(final Component interfaceComponent) throws Exception {
        if (interfaceComponent.componentType() != OOPSourceModelConstants.ComponentType.INTERFACE) {
            throw new Exception("Cannot retrieve method specs for a non-interface component!");
        }
        final ArrayList<String> methodSpecs = new ArrayList<>();
        for (final ComponentReference extend
                :interfaceComponent.references(OOPSourceModelConstants.TypeReferences.EXTENSION)) {
            final Optional<Component> cmp = model.getComponent(extend.invokedComponent());
            if (cmp.isPresent() && cmp.get().componentType() == OOPSourceModelConstants.ComponentType.INTERFACE
                    && !cmp.get().equals(interfaceComponent)) {
                methodSpecs.addAll(getListOfMethodSpecs(cmp.get()));
            }
        }
        for (final String childMethod : interfaceComponent.children()) {
            final Optional<Component> childMethodCmp = model.getComponent(childMethod);
            if (childMethodCmp.isPresent() && childMethodCmp.get().componentType() == OOPSourceModelConstants.ComponentType.METHOD) {
                methodSpecs.add(generateMethodSignature(childMethodCmp.get()));
            }
        }
        return methodSpecs;
    }

    private String generateMethodSignature(final Component methodComponent) {
        StringBuilder signature = new StringBuilder(methodComponent.name() + "(");
        for (final String methodParam : methodComponent.children()) {
            final Optional<Component> methodParamCmp = model.getComponent(methodParam);
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

