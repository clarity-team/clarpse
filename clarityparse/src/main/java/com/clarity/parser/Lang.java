package com.clarity.parser;

/**
 * Indicates Clarpse's currently supported languages.
 *
 * @author Muntazir Fadhel
 */
public enum Lang {

    JAVA("java");

    private String value;

    private Lang(final String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
