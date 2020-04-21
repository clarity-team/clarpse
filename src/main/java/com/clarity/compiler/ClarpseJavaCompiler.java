package com.clarity.compiler;

import java.util.List;

import com.clarity.listener.JavaTreeListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * JavaParser based compiler.
 */
public class ClarpseJavaCompiler implements ClarpseCompiler {
    @Override
    public OOPSourceCodeModel compile(SourceFiles sourceFiles) throws Exception {
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();

        final List<File> files = sourceFiles.getFiles();

        for (final File file : files) {
            new JavaTreeListener(srcModel, file).populateModel();
        }

        return srcModel;
    }
}
