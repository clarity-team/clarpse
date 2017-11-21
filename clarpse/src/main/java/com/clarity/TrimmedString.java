package com.clarity;

public class TrimmedString {

    private String untrimmedString;
    private String trimValue;

    public TrimmedString(String untrimmedString, String trimValue) {
        this.untrimmedString = untrimmedString;
        this.trimValue = trimValue;
    }

    public String value() throws Exception {

        if (untrimmedString == null || trimValue == null || untrimmedString.length() < trimValue.length()) {
            throw new Exception("Invalid Input found!");
        }
        String result = untrimmedString;
        if (untrimmedString.startsWith(trimValue)) {
            result = result.substring(trimValue.length());
        }
        if (result.endsWith(trimValue)) {
            result = result.substring(0, result.length() - (1 + trimValue.length()));
        }
        return result;
    }
}
