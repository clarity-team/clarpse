package com.hadii.clarpse.compiler;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseStart;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StringProvider;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.hadii.clarpse.listener.JavaTreeListener;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JavaParser based compiler.
 */
public class ClarpseJavaCompiler implements ClarpseCompiler {

    private static final Logger LOGGER = LogManager.getLogger(ClarpseJavaCompiler.class);

    @Override
    public CompileResult compile(final ProjectFiles projectFiles) throws IOException {
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final Set<ProjectFile> compileFailures = new HashSet<>();
        if (projectFiles.files().size() > 0) {
            final String persistDir = System.getProperty("java.io.tmpdir")
                + File.separator + RandomStringUtils.randomAlphanumeric(16);
            try {
                final PersistedProjectFiles persistedProjectFiles =
                    new PersistedProjectFiles(projectFiles, persistDir);
                final CombinedTypeSolver typeSolver = setupTypeSolver(persistDir);
                final ParserConfiguration parserConfiguration = setupParserConfig(typeSolver);
                for (final ProjectFile file : projectFiles.files()) {
                    try {
                        final CompilationUnit cu = new JavaParser(parserConfiguration)
                            .parse(ParseStart.COMPILATION_UNIT,
                                   new StringProvider(file.content())).getResult().get();
                        if (cu.getParsed() == Node.Parsedness.UNPARSABLE || file.content().isEmpty()) {
                            LOGGER.warn("Compilation unit (" + file.path() + ") is unparseable!");
                            compileFailures.add(file);
                        }
                        new JavaTreeListener(srcModel, file, typeSolver).visit(cu, null);
                    } catch (final Exception e) {
                        LOGGER.error("Failed to parse file " + file.path() + ".", e);
                        compileFailures.add(file);
                    }
                }
            } finally {
                FileUtils.deleteQuietly(new File(persistDir));
            }
            // Remove incorrect/invalid component references
            removeInvalidRefs(srcModel);
        }
        return new CompileResult(srcModel, compileFailures);
    }

    private void removeInvalidRefs(OOPSourceCodeModel srcModel) {
        srcModel.components().forEach(component -> component.setExternalTypeReferences(component.references().stream().filter(
            componentReference -> componentReference.invokedComponent().startsWith("java.")
                || (srcModel.containsComponent(componentReference.invokedComponent())
                && srcModel.getComponent(
                               componentReference.invokedComponent())
                           .get().componentType().isBaseComponent())).collect(Collectors.toSet())));
        LOGGER.debug("Removed invalid components in the source code model.");
    }

    private CombinedTypeSolver setupTypeSolver(String persistDir) {
        final CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        typeSolver.add(new JavaParserTypeSolver(persistDir));
        return typeSolver;
    }

    private ParserConfiguration setupParserConfig(CombinedTypeSolver typeSolver) {
        final ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE);
        //parserConfiguration.setSymbolResolver(new JavaSymbolSolver(typeSolver));
        parserConfiguration.setIgnoreAnnotationsWhenAttributingComments(true);
        return parserConfiguration;
    }
}
