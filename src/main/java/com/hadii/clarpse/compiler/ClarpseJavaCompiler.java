package com.hadii.clarpse.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseStart;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.StringProvider;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.hadii.clarpse.listener.JavaTreeListener;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import static com.github.javaparser.Providers.provider;

/**
 * JavaParser based compiler.
 */
public class ClarpseJavaCompiler implements ClarpseCompiler {
    @Override
    public OOPSourceCodeModel compile(ProjectFiles projectFiles) throws IOException {
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final String persistDir = System.getProperty("java.io.tmpdir")
                +  File.separator + RandomStringUtils.randomAlphanumeric(16);
        try {
            PersistedProjectFiles persistedProjectFiles = new PersistedProjectFiles(projectFiles, persistDir);
            CombinedTypeSolver typeSolver = new CombinedTypeSolver();
            typeSolver.add(new ReflectionTypeSolver());
            persistedProjectFiles.dirs().forEach(dir -> {
                typeSolver.add(new JavaParserTypeSolver(dir));
            });
            ParserConfiguration parserConfiguration = new ParserConfiguration();
            parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE);
            parserConfiguration.setSymbolResolver(new JavaSymbolSolver(typeSolver));
            for (ProjectFile file : projectFiles.getFiles()) {
                try {
                    ByteArrayInputStream in = new ByteArrayInputStream(file.content().getBytes(StandardCharsets.UTF_8));
                    CompilationUnit cu = new JavaParser(parserConfiguration)
                            .parse(ParseStart.COMPILATION_UNIT, new StringProvider(file.content())).getResult().get();
                    new JavaTreeListener(srcModel, file, typeSolver).visit(cu, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            FileUtils.deleteQuietly(new File(persistDir));
        }
        return srcModel;
    }
}
