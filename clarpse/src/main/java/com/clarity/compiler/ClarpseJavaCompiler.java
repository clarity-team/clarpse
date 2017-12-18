package com.clarity.compiler;

import java.util.List;

import com.clarity.listener.JavaTreeListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * JavaParser based compiler.
 */
public class ClarpseJavaCompiler implements ClarpseCompiler {
    @Override
    public OOPSourceCodeModel compile(SourceFiles rawData) throws Exception {
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();

        final List<RawFile> files = rawData.getFiles();

        for (final RawFile file : files) {
            new JavaTreeListener(srcModel, file).populateModel();
        }

        return srcModel;
    }
}
