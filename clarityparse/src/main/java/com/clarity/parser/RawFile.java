package com.clarity.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Scanner;

/**
 * Represents a file to be sent to clarity's rest api to be parsed.
 *
 * @author Muntazir Fadhel
 */
public class RawFile implements Serializable {

    /**
     * Public constructor.
     */
    public RawFile() {
    }

    private static final long serialVersionUID = -6310632263943431174L;

    /**
     * Public constructor with default params.
     *
     * @param fileName
     *            the name of the file.
     * @param fileContent
     *            the file's contents.
     */
    public RawFile(final String fileName, final String fileContent) {

        content = fileContent;
        name = fileName;
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(final String content) {
        this.content = content;
    }

    public RawFile(final File file) throws FileNotFoundException {

        final Scanner scanner = new Scanner(file, "UTF-8");
        content = scanner.useDelimiter("\\A").next();
        name = file.getName();
    }

    private String name;

    /**
     * Get file name.
     *
     * @return file name
     */
    public final String getName() {
        return name;
    }

    /**
     * @return file's content
     */
    public final String getContent() {
        return content;
    }

    private String content;
}
