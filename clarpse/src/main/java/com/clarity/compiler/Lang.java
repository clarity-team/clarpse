package com.clarity.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Indicates Clarpse's currently supported languages.
 */
public enum Lang {

    JAVA("Java", ".java"), JAVASCRIPT("javascript", ".js"), GOLANG("Go", ".go"), PHP("php", ".php");

    private static Map<String, Lang> namesMap = new HashMap<>();

    static {
        namesMap.put(JAVA.value, JAVA);
        namesMap.put(JAVASCRIPT.value, JAVASCRIPT);
        namesMap.put(GOLANG.value, GOLANG);
        namesMap.put(PHP.value, PHP);
    }

    public static List<Lang> supportedLanguages() {
        List<Lang> langs = new ArrayList<>();
        for (Map.Entry<String, Lang> entry : namesMap.entrySet()) {
            // js is still experimental
            if (entry.getValue() != JAVASCRIPT) {
                langs.add(entry.getValue());
            }
        }
        return langs;
    }

    private String value;
    private String fileExt;

    Lang(final String value, final String extension) {
        this.value = value;
        fileExt = extension;
    }

    @JsonCreator
    public static Lang forValue(String value) {
        return namesMap.get(StringUtils.lowerCase(value));
    }

    @JsonValue
    public String value() {
        return value;
    }

    public String fileExt() {
        return fileExt;
    }
}
