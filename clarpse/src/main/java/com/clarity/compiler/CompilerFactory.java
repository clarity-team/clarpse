package com.clarity.compiler;

import com.clarity.AbstractFactory;

/**
 * Factory to retrieve appropriate parsing tool for our projects.
 */
public class CompilerFactory extends AbstractFactory {

    @Override
    public final ClarpseCompiler getParsingTool(final String parseType) throws Exception {
        switch (parseType.toLowerCase()) {

        case "java":
            return new ClarpseJavaCompiler();
        case "javascript":
            return new ClarpseJSCompiler();
        case "go":
            return new ClarpseGoCompiler();
        default:
            throw new Exception("Could not find parsing tool for: " + parseType);
        }

    }
}
