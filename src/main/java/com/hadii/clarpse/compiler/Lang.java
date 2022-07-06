package com.hadii.clarpse.compiler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Languages currently supported by Clarpse.
 */
public enum Lang {

    JAVA("java", new String[]{".java"}, new String[]{}),
    JAVASCRIPT("javascript", new String[]{".js"}, new String[]{}),
    GOLANG("golang", new String[]{".go"}, new String[]{".mod"});

    private static final Map<String, Lang> NAMES_MAP = new HashMap<>();

    static {
        NAMES_MAP.put(JAVA.value, JAVA);
        NAMES_MAP.put(JAVASCRIPT.value, JAVASCRIPT);
        NAMES_MAP.put(GOLANG.value, GOLANG);
    }

    private final String value;
    private final String[] sourceFileExtns;
    private final String[] nonSourceFileExtns;

    Lang(final String value, final String[] sourceFileExtns, String[] nonSourceFileExtns) {
        this.value = value;
        this.sourceFileExtns = sourceFileExtns;
        this.nonSourceFileExtns = nonSourceFileExtns;
    }

    public static List<String> supportedSourceFileExtns() {
        List<String> extns = new ArrayList<>();
        Lang.supportedLanguages().forEach(lang -> extns.addAll(
            Arrays.asList(lang.sourceFileExtns())));
        return extns;
    }
    public static List<String> supportedFileExtns() {
        List<String> extns = new ArrayList<>();
        Lang.supportedLanguages().forEach(lang -> extns.addAll(
            Arrays.asList(lang.fileExtns())));
        return extns;
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

    public String[] sourceFileExtns() {
        return sourceFileExtns;
    }

    public String[] nonSourceFileExtns() {
        return this.nonSourceFileExtns;
    }

    public String[] fileExtns() {
        return ArrayUtils.addAll(this.sourceFileExtns, this.nonSourceFileExtns);
    }


}
