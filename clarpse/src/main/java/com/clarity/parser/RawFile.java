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
public class RawFile implements Serializable, Cloneable {

    private static final long serialVersionUID = -6310632263943431174L;
    private String content;
    private String name;

    public RawFile() {
    }

    public RawFile(final String fileName, final String fileContent) {

        content = fileContent;
        name = fileName;
    }

    public void name(final String name) {
        this.name = name;
    }

    public void content(final String content) {
        this.content = content;
    }

    public RawFile(final File file) throws FileNotFoundException {

        @SuppressWarnings("resource")
        final Scanner scanner = new Scanner(file, "UTF-8");
        content = scanner.useDelimiter("\\A").next();
        name = file.getName();
    }

    public final String name() {
        return name;
    }

    public final String content() {
        return content;
    }

    @Override
    public final boolean equals(final Object obj) {

        final RawFile rawFile = (RawFile) obj;
        return content().equals(rawFile.content()) && name().equals(rawFile.name());
    }

    @Override
    public int hashCode() {
        return content().hashCode() + name().hashCode();
    }

    public RawFile copy() {
        final RawFile rawFile = new RawFile(name(), content());
        return rawFile;
    }
}
