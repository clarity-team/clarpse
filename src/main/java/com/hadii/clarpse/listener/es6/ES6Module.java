package com.hadii.clarpse.listener.es6;

import com.google.javascript.rhino.Node;
import com.hadii.clarpse.ClarpseUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ES6Module {

    private static final Logger logger = LogManager.getLogger(ES6Module.class);
    private final String name;
    private final ArrayList<Node> moduleExportNodes;
    private final ArrayList<Node> moduleImportNodes;
    private final String modulePath;
    private final String modulePkg;
    private List<String> declaredClasses;
    private List<ES6ClassExport> classExports;
    private List<ES6ClassImport> classImports;
    private String pkgPath;

    public ES6Module(String modulePath) {
        this.pkgPath = generatePackagePath(modulePath);
        this.modulePkg = StringUtils.strip(FilenameUtils.removeExtension(
                this.pkgPath.replaceAll("/$", ""))
                .replace("/", "."), ".");
        this.name = FilenameUtils.getBaseName(modulePath);
        this.modulePath = FilenameUtils.removeExtension(modulePath);
        this.classExports = new ArrayList<>();
        this.classImports = new ArrayList<>();
        this.moduleExportNodes = new ArrayList<Node>();
        this.moduleImportNodes = new ArrayList<Node>();
        this.declaredClasses = new ArrayList<>();
    }

    public List<String> declaredClasses() {
        return this.declaredClasses;
    }


    public ArrayList<Node> moduleImportNodes() {
        return moduleImportNodes;
    }

    public void insertDeclaredClass(String declaredClass) {
        this.declaredClasses.add(declaredClass);
    }
    public String name() {
        return this.name;
    }

    public String modulePkg() {
        return this.modulePkg;
    }

    public void insertModuleExportNode(Node n) {
        this.moduleExportNodes.add(n);
    }

    public List<Node> moduleExportNodes() {
        return this.moduleExportNodes;
    }

    public List<ES6ClassExport> classExports() {
        return classExports;
    }

    public String modulePath() {
        return this.modulePath;
    }

    public void insertClassExport(ES6ClassExport eS6ClassExport) {
        logger.info("Inserting class export into module " + this.modulePath + ":\n" + eS6ClassExport.asText());
        this.classExports.add(eS6ClassExport);
    }

    public List<ES6ClassExport> matchingExportsByName(String namedExportValue) {
        return this.classExports.stream().filter(classExport -> classExport.namedExportValue().equals(namedExportValue)).collect(Collectors.toList());
    }

    public List<ES6ClassImport> matchingImportsByName(String namedImportValue) {
        return this.classImports.stream().filter(es6ClassImport -> es6ClassImport
                .namedImportValue().equals(namedImportValue)).collect(Collectors.toList());
    }

    /**
     * Sets the given import as an export in the current module.
     */
    public void exportClassImport(ES6ClassImport classImport, String exportAlias, boolean isDefault) {
        this.classExports().add(new ES6ClassExport(classImport.qualifiedClassName(), exportAlias, isDefault));
    }

    /**
     * Re-exports the given export in the current module.
     */
    public void exportClassExport(ES6ClassExport classExport, String exportAlias, boolean isDefault) {
        this.classExports().add(new ES6ClassExport(classExport.qualifiedClassName(), exportAlias, isDefault));
    }

    public void insertClassImport(ES6ClassImport es6ClassImport) {
        logger.info("Inserting class import into module " + this.modulePath + "\n:" + es6ClassImport.asText());
        this.classImports.add(es6ClassImport);
    }

    public List<ES6ClassImport> getClassImports() {
        return classImports;
    }

    /**
     * Given a file/module path (e.g. ./test.js or foo/bar/test.js, this method returns the corresponding package name
     * (e.g. ./test.js returns "/" and /bar/lol/test.js --> "bar.lol". It is assumed that the given module Path is
     * an absolute path.
     */
    public String generatePackagePath(String modulePath) {
        if (!modulePath.contains("/")) {
            return "/";
        } else {
            Path f = Paths.get(modulePath);
            return f.getParent().toString();
        }
    }

    public String pkgPath() {
        return this.pkgPath;
    }

    public void insertClassExports(List<ES6ClassExport> classExports) {
        classExports.forEach(classExport -> this.insertClassExport(classExport));
    }

    public ES6ClassExport getDefaultClassExport() {
        ES6ClassExport defaultExport = this.classExports.stream().filter(classExport -> classExport.isDefault())
            .collect(ClarpseUtil.toSingleton());
        return defaultExport;
    }

    public void insertModuleImportNode(Node n) {
        this.moduleImportNodes.add(n);
    }

    public void insertClassImports(List<ES6ClassExport> classExports, String importedModuleAlias) {
        classExports.stream().forEach(export -> this.insertClassImport(
                new ES6ClassImport(export.qualifiedClassName(), importedModuleAlias, false)
        ));
    }
}
