package com.clarity.compiler;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Scanner;

public class File implements Serializable, Cloneable {

    private static final long serialVersionUID = -6310632263943431174L;
    private String content;
    private String name;

    public File() { }

    public File(final String fileName, final String fileContent) {
        content = fileContent;
        name = fileName;
    }

    public void name(final String name) {
        this.name = name;
    }

    public void content(final String content) {
        this.content = content;
    }

    public File(final java.io.File file) throws FileNotFoundException {
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

    public final InputStream stream() {
        return new ByteArrayInputStream(content().getBytes());
    }

    @Override
    public final boolean equals(final Object obj) {

        final File file = (File) obj;
        return content().equals(file.content())
                && name().equals(file.name());
    }

    @Override
    public int hashCode() {
        return content().hashCode() + name().hashCode();
    }

    public File copy() {
        final File file = new File(name(), content());
        return file;
    }
}
