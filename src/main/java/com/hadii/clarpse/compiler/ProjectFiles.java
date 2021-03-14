package com.hadii.clarpse.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents source files to be parsed.
 */
public class ProjectFiles {

    private final Lang language;
    private List<ProjectFile> files = new ArrayList<>();

    public ProjectFiles(Lang language, ProjectFile... files) {
        this.language = language;
        this.files.addAll(Arrays.asList(files));
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

    public final List<ProjectFile> getFiles() {
        return files;
    }

    public final void setFiles(final List<ProjectFile> files) {
        this.files = files;
    }
}
