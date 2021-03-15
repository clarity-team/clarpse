package com.hadii.clarpse.compiler;

import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;

/**
 * Compiles {@link ProjectFiles source code} into
 * {@link OOPSourceCodeModel Object Oriented representations}.
 */
public interface ClarpseCompiler {

    /**
     * Compiles source code.
     * @param projectFiles Files to compile.
     * @return See {@link OOPSourceCodeModel}
     */
    OOPSourceCodeModel compile(ProjectFiles projectFiles) throws Exception;
}
