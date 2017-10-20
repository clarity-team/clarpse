package com.clarity.parser;

import com.clarity.AbstractFactory;

/**
 * Factory to retrieve appropriate parsing tool for our projects.
 */
public class ParserFactory extends AbstractFactory {

    @Override
    public final ClarpseParser getParsingTool(final String parseType) throws Exception {
        switch (parseType.toLowerCase()) {

        case "java":
            return new ClarpseJavaParser();
        case "javascript":
            return new ClarpseJSParser();
        case "golang":
            return new ClarpseGoLangParser();
        default:
            throw new Exception("Could not find parsing tool for: " + parseType);
        }

    }
}
