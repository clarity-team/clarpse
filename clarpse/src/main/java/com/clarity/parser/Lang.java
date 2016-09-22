package com.clarity.parser;

/**
 * Indicates Clarpse's currently supported languages.
 *
 * @author Muntazir Fadhel
 */
public enum Lang {

    JAVA("java", ".java");

    private String value;
    private String fileExt;

    private Lang(final String value, final String extension) {
        this.value = value;
        fileExt = extension;
    }

    public String value() {
        return value;
    }

    public String fileExt() {
        return fileExt;
    }
}
