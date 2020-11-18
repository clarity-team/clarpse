package com.hadii.clarpse.compiler;

import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;

/**
 * Represents a source code project.
 */
public class ClarpseProject {

    private final SourceFiles rawData;

    public ClarpseProject(SourceFiles rawData) {
        this.rawData = rawData;
    }

    private OOPSourceCodeModel parseRawData(final SourceFiles rawData) throws Exception {
        final ClarpseCompiler parsingTool = CompilerFactory.getParsingTool(rawData.getLanguage());
        return parsingTool.compile(rawData);
    }

    /**
     * The number of workers to use to compute the result.
     */
    public OOPSourceCodeModel result() throws Exception {
        if (!validateParseType(rawData.getLanguage())) {
            throw new IllegalArgumentException("The specified source language is not supported!");
        }
        // parse the files
        return parseRawData(rawData);
    }

    private boolean validateParseType(final Lang parseType) throws IllegalArgumentException {
        boolean isValidLang = false;
        for (Lang language : Lang.supportedLanguages()) {
            if (language == parseType) {
                isValidLang = true;
                break;
            }
        }
        return isValidLang;
    }
}
