package com.hadii.clarpse.compiler;

/**
 * Factory to retrieve appropriate parsing tool for our projects.
 */
public class CompilerFactory {

    public static ClarpseCompiler getParsingTool(final String parseType) throws Exception {
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
