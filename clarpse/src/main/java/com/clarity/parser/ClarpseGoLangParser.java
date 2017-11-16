package com.clarity.parser;

import java.util.ArrayList;
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
import org.apache.commons.lang3.StringUtils;

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

/**
 * Antlr4 based GoLang parser.
 */
public class ClarpseGoLangParser implements ClarpseParser {

    @Override
    public final OOPSourceCodeModel extractParseResult(final ParseRequestContent rawData) throws Exception {

        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();

        final List<RawFile> files = rawData.getFiles();
        System.out.println(files.size());
        List<String> projectFileTypes = new ArrayList<String>();

        int slashesInCurrSmallestCodeBaseContainingDir = -1;
        String smallestCodeBaseContaininingDir = "";
        for (RawFile file : files) {
            int slashesInFileName = StringUtils.countMatches(file.name(), "/");
            if (((slashesInFileName < slashesInCurrSmallestCodeBaseContainingDir)
                    || (slashesInCurrSmallestCodeBaseContainingDir == -1)) && (slashesInFileName > 0)) {

                slashesInCurrSmallestCodeBaseContainingDir = StringUtils.countMatches(file.name(), "/");
                smallestCodeBaseContaininingDir = file.name().substring(0, file.name().lastIndexOf("/"));
            } else if (slashesInFileName >= slashesInCurrSmallestCodeBaseContainingDir
                    && !file.name().contains(smallestCodeBaseContaininingDir)) {
                smallestCodeBaseContaininingDir = findLargestContainingDir(smallestCodeBaseContaininingDir,
                        file.name());
                slashesInCurrSmallestCodeBaseContainingDir = StringUtils.countMatches(smallestCodeBaseContaininingDir,
                        "/");

            } else if (slashesInCurrSmallestCodeBaseContainingDir > 0 && slashesInFileName < 1) {
                smallestCodeBaseContaininingDir = "";
                slashesInCurrSmallestCodeBaseContainingDir = 0;
            }
        }
        if (smallestCodeBaseContaininingDir.startsWith("/")) {
            smallestCodeBaseContaininingDir.substring(1);
        }

        for (RawFile file : files) {
            if (file.name().contains("src/")) {
                file.name(file.name().substring(file.name().indexOf("src/") + 4));
            } else {
                String fileName = file.name().replaceAll(smallestCodeBaseContaininingDir, "");
                if (fileName.startsWith("/")) {
                    fileName = fileName.substring(1);
                }
                file.name(fileName);
            }

            if (file.name().contains("/")) {
                projectFileTypes.add(file.name().substring(0, file.name().lastIndexOf("/")));
            }
        }

        for (RawFile file : files) {
            CharStream charStream = new ANTLRInputStream(file.content());
            GolangLexer lexer = new GolangLexer(charStream);
            TokenStream tokens = new CommonTokenStream(lexer);
            GolangParser parser = new GolangParser(tokens);
            SourceFileContext sourceFileContext = parser.sourceFile();
            ParseTreeWalker walker = new ParseTreeWalker();
            GolangBaseListener listener = new GoLangTreeListener(srcModel, projectFileTypes, file);
            walker.walk(listener, sourceFileContext);
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
        return srcModel;
    }

    public String findLargestContainingDir(String dir1, String dir2) {
        if (!dir1.contains("/") || !dir2.contains("/")) {
            return "";
        } else {
            String dir1Parts[] = dir1.split("/");
            String dir2Parts[] = dir2.split("/");
            List<String> matchingParts = new ArrayList<String>();
            int i = 0;
            while (dir1Parts[i].equals(dir2Parts[i])) {
                matchingParts.add(dir1Parts[i]);
                i++;
                if (i >= dir1Parts.length || i >= dir2Parts.length) {
                    break;
                }
            }
            return StringUtils.join(matchingParts, "/");
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
            signature += methodParamCmp.componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                    + ",";
        }
        signature = signature.replaceAll(",$", "");
        signature += ")";
        if (methodComponent.value() != null) {
            signature += methodComponent.value().replaceAll(" ", "");
        }
        return signature;
    }
}
