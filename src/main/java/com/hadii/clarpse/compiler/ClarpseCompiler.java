package com.hadii.clarpse.compiler;

import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;

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
