package com.hadii.clarpse.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents source files to be parsed.
 */
public class ProjectFiles {

    private final Lang language;
    private List<ProjectFile> files = new ArrayList<>();

    public ProjectFiles(final Lang language, final List<ProjectFile> files) {
        this.language = language;
        this.files.addAll(files);
    }

    public ProjectFiles(final Lang language) {
        this.language = language;
    }

    public Lang getLanguage() {
        return language;
    }

    public final void insertFile(final ProjectFile file) {
        files.add(file);
    }

    public final List<ProjectFile> files() {
        return files;
    }

    public final void setFiles(final List<ProjectFile> files) {
        this.files = files;
    }
}
