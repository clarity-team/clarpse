package com.hadii.clarpse.compiler;

/**
 * Factory to retrieve appropriate parsing tool for our projects.
 */
public class CompilerFactory {

    public static ClarpseCompiler getParsingTool(final Lang language) throws Exception {
        switch (language) {

            case JAVA:
            return new ClarpseJavaCompiler();
            case JAVASCRIPT:
            return new ClarpseES6Compiler();
            case GOLANG:
            return new ClarpseGoCompiler();
        default:
            throw new Exception("Could not find parsing tool for: " + language.value());
        }

    }
}
