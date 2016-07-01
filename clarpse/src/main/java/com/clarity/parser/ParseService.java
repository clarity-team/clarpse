package com.clarity.parser;

import java.util.HashMap;
import java.util.Map;

import com.clarity.AbstractFactory;
import com.clarity.FactoryProducer;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Entry point into Clarpse.
 *
 * @author Muntazir Fadhel
 */
public class ParseService {

    public static final String UNSUPPORTED_PARSE_TYPE = "The specified source language is not supported!";

    private static final Map<String, String> PARSE_TYPE_MAP = new HashMap<String, String>();
    static {
        PARSE_TYPE_MAP.put("java", "java");
    }

    private OOPSourceCodeModel parseRawData(final ParseRequestContent rawData, final String parseType)
            throws Exception {
        final AbstractFactory parserFactory = new FactoryProducer().getFactory(FactoryProducer.PARSE_KEYWORD);
        final ClarpseParser parsingTool = parserFactory.getParsingTool(parseType);
        return parsingTool.extractParseResult(rawData);
    }

    public OOPSourceCodeModel parseProject(final ParseRequestContent rawContent) throws Exception {

        validateParseType(rawContent.getLanguage());
        // parse the files!
        final OOPSourceCodeModel srcModel = parseRawData(rawContent, rawContent.getLanguage());
        return srcModel;
    }

    public void validateParseType(final String parseType) throws IllegalArgumentException {

        if (!PARSE_TYPE_MAP.containsKey(parseType)) {
            throw new IllegalArgumentException(UNSUPPORTED_PARSE_TYPE);
        }
    }
}
