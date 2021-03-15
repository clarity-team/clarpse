package com.hadii.clarpse.compiler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Indicates Clarpse's currently supported languages.
 */
public enum Lang {

    JAVA("java", new String[]{".java"}), JAVASCRIPT("javascript", new String[]{".js"}),
    GOLANG("golang", new String[]{".go", ".mod"});

    private static final Map<String, Lang> NAMES_MAP = new HashMap<>();

    static {
        NAMES_MAP.put(JAVA.value, JAVA);
        NAMES_MAP.put(JAVASCRIPT.value, JAVASCRIPT);
        NAMES_MAP.put(GOLANG.value, GOLANG);
    }

    private final String value;
    private final String[] fileExtensions;

    Lang(final String value, final String[] extensions) {
        this.value = value;
        fileExtensions = extensions;
    }

    public static List<Lang> supportedLanguages() {
        final List<Lang> langs = new ArrayList<>();
        for (final Map.Entry<String, Lang> entry : NAMES_MAP.entrySet()) {
            langs.add(entry.getValue());
        }
        return langs;
    }

    @JsonCreator
    public static Lang forValue(final String value) {
        return NAMES_MAP.get(value);
    }

    @JsonValue
    public String value() {
        return value;
    }

    public String[] fileExtensions() {
        return fileExtensions;
    }
}
