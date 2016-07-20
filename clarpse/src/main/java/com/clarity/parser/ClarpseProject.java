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
public class ClarpseProject {

    private static final Map<String, String> PARSE_TYPE_MAP = new HashMap<String, String>();
    static {
        PARSE_TYPE_MAP.put("java", "java");
    }

    private ParseRequestContent rawData;
    
    public ClarpseProject(ParseRequestContent rawData) {
    	this.rawData = rawData;
    }
    private OOPSourceCodeModel parseRawData(final ParseRequestContent rawData)
            throws Exception {
        final AbstractFactory parserFactory = new FactoryProducer().getFactory(FactoryProducer.PARSE_KEYWORD);
        final ClarpseParser parsingTool = parserFactory.getParsingTool(rawData.getLanguage());
        return parsingTool.extractParseResult(rawData);
    }

    public OOPSourceCodeModel result() throws Exception {

        validateParseType(this.rawData.getLanguage());
        // parse the files!
        final OOPSourceCodeModel srcModel = parseRawData(this.rawData);
        return srcModel;
    }

    private void validateParseType(final String parseType) throws IllegalArgumentException {

        if (!PARSE_TYPE_MAP.containsKey(parseType)) {
            throw new IllegalArgumentException("The specified source language is not supported!");
        }
    }
}
