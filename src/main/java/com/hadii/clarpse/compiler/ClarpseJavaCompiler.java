package com.hadii.clarpse.compiler;

import java.util.List;

import com.hadii.clarpse.listener.JavaTreeListener;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;

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
