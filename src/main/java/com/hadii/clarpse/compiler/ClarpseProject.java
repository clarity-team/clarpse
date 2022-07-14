package com.hadii.clarpse.compiler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a source code project that is analyzed to produce an object-oriented representation
 * of the code.
 */
public class ClarpseProject {

    private static final Logger LOGGER = LogManager.getLogger(ClarpseProject.class);
    private final ProjectFiles projectFiles;

    public ClarpseProject(ProjectFiles projectFiles) {
        this.projectFiles = projectFiles;
    }

    private CompileResult parseFiles(final ProjectFiles sourceFiles) throws CompileException {
        LOGGER.info("Parsing " + sourceFiles.files().size() + " " + sourceFiles.getLanguage().name()
                        + " source files..");
        long startTime = System.nanoTime();
        final ClarpseCompiler parsingTool = CompilerFactory.getParsingTool(sourceFiles.getLanguage());
        CompileResult compileResult = parsingTool.compile(sourceFiles);
        long duration = (System.nanoTime() - startTime) / 1000000;
        LOGGER.info("Parsed " + compileResult.model().size() + " components from "
                        + sourceFiles.size() + " " + sourceFiles.getLanguage().name() + " files in "
                        + duration + " ms.");
        return compileResult;
    }

    public CompileResult result() throws CompileException {
        if (!supportedLang(projectFiles.getLanguage())) {
            throw new IllegalArgumentException("The specified source language is not supported!");
        }
        // parse the files
        return parseFiles(projectFiles);
    }

    private boolean supportedLang(final Lang language) throws IllegalArgumentException {
        boolean isValidLang = false;
        for (Lang tmpLang : Lang.supportedLanguages()) {
            if (language == tmpLang) {
                isValidLang = true;
                break;
            }
        }
        return isValidLang;
    }
}
