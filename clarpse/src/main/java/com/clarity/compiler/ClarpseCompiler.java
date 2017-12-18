package com.clarity.compiler;

import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Compiles {@link SourceFiles source code} into
 * {@link OOPSourceCodeModel Object Oriented representations}.
 */
public interface ClarpseCompiler {

    /**
     * Compiles source code.
     */
    OOPSourceCodeModel compile(SourceFiles rawData) throws Exception;

}
