package com.hadii.clarpse.compiler;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class ProjectFile {

    private String content;
    private String path;

    public ProjectFile() { }

    public ProjectFile(final String path, final String fileContent) {
        content = fileContent;
        if (!path.startsWith("/")) {
            this.path = "/" + path;
        } else {
            this.path = path;
        }
    }

    public void path(final String path) {
        this.path = path;
    }

    public void content(final String content) {
        this.content = content;
    }

    public ProjectFile(final java.io.File file) throws FileNotFoundException {
        @SuppressWarnings("resource")
        final Scanner scanner = new Scanner(file, "UTF-8");
        content = scanner.useDelimiter("\\A").next();
        path = file.getName();
    }

    public String shortName() {
        String shortName = path;
        if (shortName.contains("/")) {
            shortName = shortName.substring(shortName.lastIndexOf("/") + 1, shortName.lastIndexOf('.'));
        }
        return shortName;
    }

    public final String content() {
        return content;
    }

    public final InputStream stream() {
        return new ByteArrayInputStream(content().getBytes());
    }

    @Override
    public final boolean equals(final Object obj) {
        final ProjectFile file = (ProjectFile) obj;
        return content().equals(file.content())
                && path().equals(file.path());
    }

    public String path() {
        return this.path;
    }

    @Override
    public int hashCode() {
        return content().hashCode() + path().hashCode();
    }

    public ProjectFile copy() {
        final ProjectFile file = new ProjectFile(path(), content());
        return file;
    }
}
