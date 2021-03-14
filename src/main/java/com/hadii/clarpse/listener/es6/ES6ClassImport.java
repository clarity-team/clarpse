package com.hadii.clarpse.listener.es6;

public class ES6ClassImport {

    /**
     * Fully qualified class name of this exported class.
     */
    private final String qualifiedClassName;
    /**
     * What an import statement would have to reference this import as.
     */
    private String namedImportValue;
    /**
     * Is default export?
     */
    private final boolean isDefault;

    public ES6ClassImport(String qualifiedClassName, String namedImportValue, boolean isDefault) {
        this.qualifiedClassName = qualifiedClassName;
        this.namedImportValue = namedImportValue;
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public String className() {
        if (this.qualifiedClassName.contains(".")) {
            return this.qualifiedClassName.substring(this.qualifiedClassName.lastIndexOf(".") + 1);
        } else {
            return this.qualifiedClassName;
        }
    }

    public String asText() {
        return "Qualified Class Name: " + this.qualifiedClassName +
                "\nNamed ImportValue: " + this.namedImportValue +
                "\nIs Default? " + this.isDefault;
    }
    public String namedImportValue() {
        return this.namedImportValue;
    }

    public String qualifiedClassName() {
        return this.qualifiedClassName;
    }
}
