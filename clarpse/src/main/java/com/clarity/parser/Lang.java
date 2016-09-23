package com.clarity.parser;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import org.apache.commons.lang3.StringUtils;

/**
 * Indicates Clarpse's currently supported languages.
 *
 * @author Muntazir Fadhel
 */
public enum Lang {

    JAVA("java", ".java");

	private static Map<String, Lang> namesMap = new HashMap<String, Lang>(1);

    static {
        namesMap.put(JAVA.value, JAVA);
    }

    private String value;
    private String fileExt;

    private Lang(final String value, final String extension) {
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
