package com.clarity.parser;

import com.clarity.AbstractFactory;
import com.clarity.FactoryProducer;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Represents a project to be processed by Clarpse, serves as the entry point
 * into Clarpse.
 */
public class ClarpseProject {

    private final ParseRequestContent rawData;

    public ClarpseProject(ParseRequestContent rawData) {
        this.rawData = rawData;
    }

    private OOPSourceCodeModel parseRawData(final ParseRequestContent rawData) throws Exception {
        final AbstractFactory parserFactory = new FactoryProducer().getFactory(FactoryProducer.PARSE_KEYWORD);
        final ClarpseParser parsingTool = parserFactory.getParsingTool(rawData.getLanguage());
        return parsingTool.extractParseResult(rawData);
    }

    public OOPSourceCodeModel result() throws Exception {

        if (!validateParseType(rawData.getLanguage())) {
            throw new IllegalArgumentException("The specified source language is not supported!");
        }
        // parse the files!
        final OOPSourceCodeModel srcModel = parseRawData(rawData);
        return srcModel;
    }

    private boolean validateParseType(final String parseType) throws IllegalArgumentException {
        boolean isValidLang = false;
        for (Lang language : Lang.supportedLanguages()) {
            if (language.value().equalsIgnoreCase(parseType)) {
                isValidLang = true;
            }
        }
        return isValidLang;
    }
}
