package com.hadii.clarpse.compiler;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseStart;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StringProvider;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.hadii.clarpse.listener.JavaTreeListener;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JavaParser based compiler.
 */
public class ClarpseJavaCompiler implements ClarpseCompiler {
    @Override
    public OOPSourceCodeModel compile(final ProjectFiles projectFiles) throws IOException {
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final String persistDir = System.getProperty("java.io.tmpdir")
                + File.separator + RandomStringUtils.randomAlphanumeric(16);
        try {
            final PersistedProjectFiles persistedProjectFiles = new PersistedProjectFiles(projectFiles, persistDir);
            final CombinedTypeSolver typeSolver = new CombinedTypeSolver();
            typeSolver.add(new ReflectionTypeSolver());
            typeSolver.add(new JavaParserTypeSolver(persistDir));
            final ParserConfiguration parserConfiguration = new ParserConfiguration();
            parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_8);
            parserConfiguration.setSymbolResolver(new JavaSymbolSolver(typeSolver));
            parserConfiguration.setIgnoreAnnotationsWhenAttributingComments(true);
            for (final ProjectFile file : projectFiles.files()) {
                try {
                    final ByteArrayInputStream in = new ByteArrayInputStream(file.content().getBytes(StandardCharsets.UTF_8));
                    final CompilationUnit cu = new JavaParser(parserConfiguration)
                            .parse(ParseStart.COMPILATION_UNIT, new StringProvider(file.content())).getResult().get();
                    new JavaTreeListener(srcModel, file, typeSolver).visit(cu, null);
                } catch (final Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (final Exception e) {
            throw e;
        } finally {
            FileUtils.deleteQuietly(new File(persistDir));
        }
        return srcModel;
    }
}
