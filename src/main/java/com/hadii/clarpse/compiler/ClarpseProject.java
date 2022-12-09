package com.hadii.clarpse.compiler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * Represents a source code project that is analyzed to produce an object-oriented representation
 * of the code.
 */
public class ClarpseProject {

    private static final Logger LOGGER = LogManager.getLogger(ClarpseProject.class);
    private final ProjectFiles projectFiles;
    private CompileResult compileResult;

    public ClarpseProject(Collection<ProjectFile> projectFiles, Lang lang) {
        if (!supportedLang(lang)) {
            throw new IllegalArgumentException("The specified source language is not supported!");
        }
        this.projectFiles = new ProjectFiles(lang, projectFiles);
    }

    public CompileResult result() throws CompileException {
        if (this.compileResult == null) {
            LOGGER.info("Parsing " + this.projectFiles.files().size() + " "
                            + this.projectFiles.lang().name() + " source files..");
            long startTime = System.nanoTime();
            final ClarpseCompiler parsingTool = CompilerFactory.getParsingTool(this.projectFiles.lang());
            CompileResult compileRes = parsingTool.compile(this.projectFiles);
            long duration = (System.nanoTime() - startTime) / 1000000;
            LOGGER.info("Parsed " + compileRes.model().size() + " components from "
                            + this.projectFiles.size() + " " + this.projectFiles.lang().name()
                            + " files in " + duration + " ms.");
            this.compileResult = compileRes;
        }
        LOGGER.info("Returning generated compile result ..");
        return this.compileResult;
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
