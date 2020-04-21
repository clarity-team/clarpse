package com.clarity.compiler;

import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Compiles {@link SourceFiles source code} into
 * {@link OOPSourceCodeModel Object Oriented representations}.
 */
public interface ClarpseCompiler {

    /**
     * Compiles source code.
     * @param sourceFiles Files to compile.
     * @return See {@link OOPSourceCodeModel}
     */
    OOPSourceCodeModel compile(SourceFiles sourceFiles) throws Exception;
}
