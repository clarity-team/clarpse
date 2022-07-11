package com.hadii.clarpse.compiler;

import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;

import java.util.Set;

public class CompileResult {

    /**
     * List of files that could not be parsed.
     */
    private Set<ProjectFile> failures;
    private final OOPSourceCodeModel model;

    public CompileResult(OOPSourceCodeModel model) {
        this.model = model;
    }

    public CompileResult(OOPSourceCodeModel model, Set<ProjectFile> failures) {
        this(model);
        this.failures = failures;
    }

    public OOPSourceCodeModel model() {
        return this.model;
    }

    public Set<ProjectFile> failures() {
        return this.failures;
    }
}
