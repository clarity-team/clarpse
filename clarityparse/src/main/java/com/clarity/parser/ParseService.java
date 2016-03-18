package com.clarity.parser;

import java.util.HashMap;
import java.util.Map;

import com.clarity.AbstractFactory;
import com.clarity.FactoryProducer;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * @author Muntazir Fadhel
 */
public class ParseService {

    public static final String UNSUPPORTED_PARSE_TYPE = "The specified source language is not supported!";

    private static final Map<String, String> PARSE_TYPE_MAP = new HashMap<String, String>();
    static {
        PARSE_TYPE_MAP.put("java", "java");
    }

    /**
     * @param rawData
     *            content needed to be parsed
     * @param parseType
     *            type of parse to be completed.
     * @return TestSourceFile resource representing the input multipartfile code
     *         base.
     * @throws Exception
     *             Thrown during the parsing process.
     */
    private OOPSourceCodeModel parseRawData(final ParseRequestContent rawData, final String parseType)
            throws Exception {
        final AbstractFactory parserFactory = new FactoryProducer().getFactory(FactoryProducer.PARSE_KEYWORD);
        final IClarityParser parsingTool = parserFactory.getParsingTool(parseType);
        return parsingTool.extractParseResult(rawData);
    }

    /**
     *
     */
    public ParseService() {
    }

    public OOPSourceCodeModel parseProject(final ParseRequestContent rawContent) throws Exception {

        validateParseType(rawContent.getLanguage());
        // parse the files!
        final OOPSourceCodeModel srcModel = parseRawData(rawContent, rawContent.getLanguage());
        return srcModel;
    }

    /**
     * Determine if the given language is supported, throws an exception when
     * language is not supported.
     *
     * @param parseType
     *            project source language
     * @exception IllegalArgumentException
     *                when specified language is not supported
     */
    public void validateParseType(final String parseType) throws IllegalArgumentException {

        if (!PARSE_TYPE_MAP.containsKey(parseType)) {
            throw new IllegalArgumentException(UNSUPPORTED_PARSE_TYPE);
        }
    }
}
