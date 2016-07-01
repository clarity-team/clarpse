package com.clarity.parser;

import com.clarity.AbstractFactory;


/**
 * Factory to retrieve appropriate parsing tool for our projects.
 *
 * @author Muntazir Fadhel
 */
public class ParserFactory extends AbstractFactory {

    @Override
    public final com.clarity.parser.ClarpseParser getParsingTool(final String parseType) throws Exception {
        switch (parseType.toLowerCase()) {

        case "java":
            return new AntlrParser("Java", "parser.java", "com.clarity.parser.java");

        default:
            throw new Exception("Could not find parsing tool for: " + parseType);
        }

    }
}
