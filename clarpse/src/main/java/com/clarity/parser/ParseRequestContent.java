package com.clarity.parser;

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
public class ParseRequestContent implements Serializable {

    private String language;

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language
     *            the language to set
     */
    public void setLanguage(final Lang language) {
        this.language = language.getValue();
    }

    public ParseRequestContent(final Lang language) {
        this.language = language.getValue();
    }

    private static final long serialVersionUID = 196237203663853669L;

    private List<RawFile> files = new ArrayList<RawFile>();

    /**
     * Inserts a file to the content body.
     *
     * @param file
     *            file to be inserted.
     */
    public final void insertFile(final RawFile file) {
        files.add(file);
    }

    /**
     * Retrieves all the files from the content body.
     *
     * @return List of all the files.
     */
    public final List<RawFile> getFiles() {

        return files;
    }

    /**
     * Retrieves all the files from the content body.
     *
     * @param files
     *            List of all the files.
     */
    public final void setFiles(final List<RawFile> files) {

        this.files = files;

    }
}
