package com.hadii.clarpse.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents Content to be parsed.
 *
 * @author Muntazir Fadhel
 */
@JsonInclude(Include.NON_NULL)
public class SourceFiles implements Serializable {

    private String language;
    private static final long serialVersionUID = 196237203663853669L;
    private List<File> files = new ArrayList<File>();

    public SourceFiles() {
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final Lang language) {
        this.language = language.value();
    }

    public SourceFiles(final Lang language) {
        this.language = language.value();
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
