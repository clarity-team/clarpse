package com.clarity.parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

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

/**
 * Antlr4 based GoLang parser.
 */
public class ClarpseGoLangParser implements ClarpseParser {

    @Override
    public final OOPSourceCodeModel extractParseResult(final ParseRequestContent rawData) throws Exception {
        Date startTime = new Date();
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        System.out.println("number of files is: " + rawData.getFiles().size());
        final List<RawFile> files = rawData.getFiles();
        List<String> projectFileTypes = new ArrayList<String>();

        if (files.size() < 1) {
            return srcModel;
        } else {
            String smallestCodeBaseContaininingDir = files.get(0).name();

            for (int i = 1; i < files.size(); i++) {
                smallestCodeBaseContaininingDir = new CommonDir(smallestCodeBaseContaininingDir, files.get(i).name())
                        .string();
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

            // sort fileTypes by length in desc order so that the longest types are at the
            // top.
            Collections.sort(projectFileTypes, new LengthComp());

            for (RawFile file : files) {
                try {
                    CharStream charStream = new ANTLRInputStream(file.content());
                    GolangLexer lexer = new GolangLexer(charStream);
                    TokenStream tokens = new CommonTokenStream(lexer);
                    GolangParser parser = new GolangParser(tokens);
                    SourceFileContext sourceFileContext = parser.sourceFile();
                    ParseTreeWalker walker = new ParseTreeWalker();
                    GolangBaseListener listener = new GoLangTreeListener(srcModel, projectFileTypes, file);
                    walker.walk(listener, sourceFileContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * In GoLang, interfaces are implemented implicitly. As a result, we handle
         * their detection in the following way: Once we have parsed the entire code
         * base, we compare every paprsed interface to every parsed struct to see what
         * interfaces the struct implements. Then we modify the source code model to
         * represent our findings accordingly.
         */
        Set<Component> baseComponents = srcModel.getComponents().values().stream()
                .filter(s -> (s.componentType().isBaseComponent())).collect(Collectors.toSet());
        ImplementedInterfaces implementedInterfacesGatherer = new ImplementedInterfaces(srcModel);
        for (Component baseCmp : baseComponents) {
            List<String> implementedInterfaces = implementedInterfacesGatherer.getImplementedIntefaces(baseCmp);
            for (String implementedInterface : implementedInterfaces) {
                baseCmp.insertComponentInvocation(new TypeImplementation(implementedInterface));
            }
        }
        System.out.println("GoLang parsing took: " + ((new Date().getTime() - startTime.getTime()) / 1000) + " seconds");
        return srcModel;
    }
}

class ImplementedInterfaces {
    private OOPSourceCodeModel model;
    private Map<String, List<String>> interfaceMethodSpecsPairs;

    ImplementedInterfaces(OOPSourceCodeModel srcModel) throws Exception {
        this.model = srcModel;
        Set<Component> allInterfaceComponents = srcModel.getComponents().values().stream()
                .filter(s -> (s.componentType() == ComponentType.INTERFACE)).collect(Collectors.toSet());
        interfaceMethodSpecsPairs = new HashMap<String, List<String>>();
        for (Component interfaceCmp : allInterfaceComponents) {
            interfaceMethodSpecsPairs.put(interfaceCmp.uniqueName(), getListOfMethodSpecs(interfaceCmp));
        }
    }

    /**
     * Retrieves a list of Strings corresponding to the the interfaces implemented
     * by the given base {@linkplain Component}.
     */
    public List<String> getImplementedIntefaces(Component baseComponent) throws Exception {

        // holds all the implemented interfaces for the given component
        List<String> implementedInterfaces = new ArrayList<String>();

        if (baseComponent.componentType().isBaseComponent()
                && baseComponent.componentType() != ComponentType.INTERFACE) {
            // generate a list of method signatures for the given base component.
            List<String> baseComponentMethodSignatures = new ArrayList<String>();
            for (String baseComponentChild : baseComponent.children()) {
                Component childCmp = model.getComponent(baseComponentChild);
                if (childCmp != null && childCmp.componentType().isMethodComponent()) {
                    baseComponentMethodSignatures.add(generateMethodSignature(childCmp));
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
            Component cmp = model.getComponent(extend.invokedComponent());
            if (cmp != null && cmp.componentType() == ComponentType.INTERFACE) {
                methodSpecs.addAll(getListOfMethodSpecs(cmp));
            }
        }
        for (String childMethod : interfaceComponent.children()) {
            Component childMethodCmp = model.getComponent(childMethod);
            if (childMethodCmp != null && childMethodCmp.componentType() == ComponentType.METHOD) {
                methodSpecs.add(generateMethodSignature(childMethodCmp));
            }
        }
        return methodSpecs;
    }

    private String generateMethodSignature(Component methodComponent) {
        String signature = methodComponent.name() + "(";
        for (String methodParam : methodComponent.children()) {
            Component methodParamCmp = model.getComponent(methodParam);
            if (methodParamCmp != null && methodParamCmp.invocations().size() > 0) {
                signature += methodParamCmp.componentInvocations(ComponentInvocations.DECLARATION).get(0)
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
