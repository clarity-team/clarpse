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

    JAVA("java", ".java"), JAVASCRIPT("javascript", ".js"), GOLANG("golang", ".go");

    private static Map<String, Lang> namesMap = new HashMap<String, Lang>(1);

    static {
        namesMap.put(JAVA.value, JAVA);
        namesMap.put(JAVASCRIPT.value, JAVASCRIPT);
        namesMap.put(GOLANG.value, GOLANG);
    }

    public static List<Lang> supportedLanguages() {
        List<Lang> langs = new ArrayList<Lang>();
        for (Map.Entry<String, Lang> entry : namesMap.entrySet()) {
            langs.add(entry.getValue());
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
