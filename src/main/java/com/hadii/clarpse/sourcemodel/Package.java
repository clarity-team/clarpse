package com.hadii.clarpse.sourcemodel;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Represents the metadata of a package in a code base.
 */
public class Package implements Serializable {


    private final String packageName;
    private final String packagePath;
    private final String ellipsisSeparatedPkg;

    public Package(final String packageName, final String packagePath) {
        this.packageName = packageName;
        this.packagePath = packagePath;
        this.ellipsisSeparatedPkg = StringUtils.strip(packagePath.replaceAll("/", "."), ".");
    }

    @Override
    public String toString() {
        return this.packageName + ": " + this.packagePath;
    }

    public String name() {
        return packageName;
    }

    public String path() {
        return packagePath;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Package ref = (Package) obj;
        return this.packageName.equals(ref.packageName) && this.packagePath.equals(ref.packagePath);
    }

    @Override
    public int hashCode() {
        return (this.packageName + ":" + this.packagePath).hashCode();
    }

    public String ellipsisSeparatedPkgPath() {
        return this.ellipsisSeparatedPkg;
    }
}
