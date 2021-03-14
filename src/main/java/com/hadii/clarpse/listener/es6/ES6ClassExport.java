package com.hadii.clarpse.listener.es6;

public class ES6ClassExport {

    /**
     * Fully qualified class name of this exported class.
     */
    private final String qualifiedClassName;
    /**
     * What an import statement would have to reference this export as.
     */
    private String namedExportValue;
    /**
     * The class name this export represents.
     */
    private String className;
    /**
     * Is default export?
     */
    private final boolean isDefault;

    public ES6ClassExport(String qualifiedClassName, String namedExportValue, boolean isDefault) {
        this.qualifiedClassName = qualifiedClassName;
        this.namedExportValue = namedExportValue;
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
                "\nNamed ImportValue: " + this.namedExportValue +
                "\nIs Default? " + this.isDefault;
    }

    public String namedExportValue() {
        return this.namedExportValue;
    }

    public String qualifiedClassName() {
        return this.qualifiedClassName;
    }
}
