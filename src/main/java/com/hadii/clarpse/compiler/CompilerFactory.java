package com.hadii.clarpse.compiler;

import com.hadii.clarpse.compiler.go.ClarpseGoCompiler;

/**
 * Factory to retrieve appropriate parsing tool for our projects.
 */
public class CompilerFactory {

    public static ClarpseCompiler getParsingTool(final Lang language) throws CompileException {
        switch (language) {

            case JAVA:
            return new ClarpseJavaCompiler();
            case JAVASCRIPT:
            return new ClarpseES6Compiler();
            case GOLANG:
            return new ClarpseGoCompiler();
        default:
            throw new CompileException("Could not find parsing tool for: " + language.value());
        }

    }
}
