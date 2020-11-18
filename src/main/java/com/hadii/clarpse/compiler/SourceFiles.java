package com.hadii.clarpse.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents source files to be parsed.
 */
@JsonInclude(Include.NON_NULL)
public class SourceFiles implements Serializable {

    private final Lang language;
    private static final long serialVersionUID = 196237203663853669L;
    private List<File> files = new ArrayList<>();

    public SourceFiles(Lang language, File ... files) {
        this.language = language;
        this.files.addAll(Arrays.asList(files));
    }

    public SourceFiles(final Lang language) {
        this.language = language;
    }

    public Lang getLanguage() {
        return language;
    }

    public final void insertFile(final File file) {
        files.add(file);
    }

    public final List<File> getFiles() {
        return files;
    }

    public final void setFiles(final List<File> files) {
        this.files = files;
    }
}
