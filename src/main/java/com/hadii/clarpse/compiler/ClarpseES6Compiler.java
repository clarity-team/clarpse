package com.hadii.clarpse.compiler;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JsAst;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.parsing.Config.JsDocParsing;
import com.google.javascript.rhino.Node;
import com.hadii.clarpse.ResolvedRelativePath;
import com.hadii.clarpse.listener.es6.ES6ClassExport;
import com.hadii.clarpse.listener.es6.ES6ClassImport;
import com.hadii.clarpse.listener.es6.ES6Listener;
import com.hadii.clarpse.listener.es6.ES6Module;
import com.hadii.clarpse.listener.es6.ES6ModulesListener;
import com.hadii.clarpse.listener.es6.ModulesMap;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Compiles JavaScript code.
 */
public class ClarpseES6Compiler implements ClarpseCompiler {

    private static final Logger logger = LogManager.getLogger(ClarpseES6Compiler.class);

    private OOPSourceCodeModel compileFiles(List<ProjectFile> files) {
        OOPSourceCodeModel model = new OOPSourceCodeModel();
        Compiler compiler = setupCompiler();
        ModulesMap modulesMap = new ModulesMap();
        // Stage 1 - Populate modules map on initial pass.
        populateModulesMap(files, compiler, modulesMap);
        // Stage 2 - Now that initial pass is completed, resolve module exports/imports in each module.
        resolveModuleDependencies(modulesMap);
        // Stage 3 - Final parse of all files, populate source code model.
        parseAllSourceCode(files, model, compiler, modulesMap);
        return model;
    }

    private void parseAllSourceCode(List<ProjectFile> files, OOPSourceCodeModel model, Compiler compiler, ModulesMap modulesMap) {
        logger.info("<<< Executing third pass to parse all ES6 source files.. >>>");
        files.forEach(file -> {
            try {
                Node root = new JsAst(com.google.javascript.jscomp.SourceFile.fromCode(file.path(), file.content())).getAstRoot(compiler);
                NodeTraversal.Callback jsListener = new ES6Listener(model, file, files, modulesMap);
                NodeTraversal.traverse(compiler, root, jsListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void resolveModuleDependencies(ModulesMap modulesMap) {
        Set<String> recursedModules = new HashSet<>();
        logger.info("<<< Executing second pass to resolve module imports and exports.. >>>");
        modulesMap.modules().forEach((module) -> {
            try {
                resolveModuleImportsAndExports(module, recursedModules, modulesMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void populateModulesMap(List<ProjectFile> files, Compiler compiler, ModulesMap modulesMap) {
        logger.info("<<< Compiling ES6 files, executing initial pass to generate modules map.. >>>");
        files.forEach(file -> {
            try {
                Node root = new JsAst(com.google.javascript.jscomp.SourceFile.fromCode(file.path(), file.content())).getAstRoot(compiler);
                NodeTraversal.Callback jsListener = new ES6ModulesListener(file, modulesMap);
                NodeTraversal.traverse(compiler, root, jsListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Compiler setupCompiler() {
        Compiler compiler = new Compiler();
        CompilerOptions options = new CompilerOptions();
        options.setParseJsDocDocumentation(JsDocParsing.INCLUDE_DESCRIPTIONS_WITH_WHITESPACE);
        compiler.initOptions(options);
        return compiler;
    }

    private void resolveModuleImportsAndExports(ES6Module currModule, Set<String> recursedModules, ModulesMap modulesMap)
            throws Exception {
        logger.info("Resolving import/exports for module: " + currModule.modulePath());
        // Register module to avoid infinite recursion from cyclical dependencies
        recursedModules.add(currModule.modulePath());
        processModuleImports(currModule, recursedModules, modulesMap);
        processModuleExports(currModule, recursedModules, modulesMap);
    }

    private void processModuleExports(ES6Module currModule, Set<String> recursedModules, ModulesMap modulesMap) throws Exception {
        // PROCESS EXPORTS - Process module exports of the current module.
        for (Node exportNode : currModule.moduleExportNodes()) {
            ES6Module importedModule = null;
            // STEP 1: If the export/import statement references another module, recursively process it first.
            if (referencesAnotherModule(exportNode)) {
                importedModule = processImportedModule(currModule, modulesMap, exportNode, 1);
                if (importedModule == null) {
                    // The referenced module is not local, no point trying to resolve this export/import..
                    continue;
                } else {
                    // Ensure we are not processing module exports in a circular recursion
                    if (!recursedModules.contains(importedModule.modulePath())) {
                        resolveModuleImportsAndExports(importedModule, recursedModules, modulesMap);
                    }
                }
            }
            // STEP 2: Resolve the given export/import statement in the current module
            if (exportNode.getBooleanProp(Node.EXPORT_ALL_FROM)) {
                currModule.insertClassExports(importedModule.classExports());
            } else if (exportNode.getFirstChild() != null && exportNode.getFirstChild().isExportSpecs()) {
                for (Node exportSpecsChildNode : exportNode.getFirstChild().children()) {
                    if (exportSpecsChildNode.isExportSpec()) {
                        String exportVal = exportSpecsChildNode.getChildAtIndex(0).getString();
                        String namedExport = exportSpecsChildNode.getChildAtIndex(1).getString();
                        if (exportVal.equals("default") && namedExport.equals("default")) {
                            // Scenario: export { default } from …;
                            ES6ClassExport matchedExport = importedModule.getDefaultClassExport();
                            currModule.insertClassExport(new ES6ClassExport(
                                    matchedExport.qualifiedClassName(),
                                    matchedExport.qualifiedClassName(),
                                    true));
                        } else if (namedExport.equals("default")) {
                            // Scenario: export { name1 as default, … };
                            List<ES6ClassImport> matchingImport = currModule.matchingImportsByName(exportVal);
                            if (!matchingImport.isEmpty()) {
                                currModule.exportClassImport(matchingImport.get(0), exportVal, true);
                            } else if (currModule.declaredClasses().contains(exportVal)) {
                                insertClassExport(currModule, currModule.declaredClasses().get(0),
                                                  currModule.declaredClasses().get(0), true);
                            }
                        } else {
                            // Scenario: Aggregating Modules -> export { import1 as name1, import2 as name2, …, nameN } from …;
                            if (importedModule != null) {
                                List<ES6ClassExport> matchingExports = importedModule.matchingExportsByName(namedExport);
                                if (!matchingExports.isEmpty()) {
                                    currModule.exportClassExport(matchingExports.get(0), exportVal, false);
                                }
                            } else {
                                List<ES6ClassImport> matchingImport = currModule.matchingImportsByName(exportVal);
                                if (!matchingImport.isEmpty()) {
                                    currModule.exportClassImport(matchingImport.get(0), exportVal, true);
                                } else if (currModule.declaredClasses().contains(exportVal)) {
                                    insertClassExport(currModule, exportVal, namedExport, false);
                                }
                            }
                        }
                    }
                }
            } else if (exportNode.getBooleanProp(Node.EXPORT_DEFAULT)) {
                /**
                 * Handle Default exports of the form:
                 * 1) export default expression;
                 * 2) export default class (…) { … } // also class, function*
                 * 3) export default class name1(…) { … } // also class, function*
                 * 4) export default class {};
                 */
                if (exportNode.getFirstChild() != null && exportNode.getFirstChild().isClass()
                        || (exportNode.getFirstChild().isAssign() && exportNode.getFirstChild().getSecondChild().isClass())
                        || (exportNode.getFirstChild().isName())) {
                    if (exportNode.getChildAtIndex(0).getFirstChild() != null) {
                        if (exportNode.getFirstChild().getFirstChild().isName()) {
                            String className = exportNode.getFirstChild().getFirstChild().getString();
                            insertClassExport(currModule, className, className, true);
                        } else if (exportNode.getFirstChild().getFirstChild().isEmpty()) {
                            // No class name provided! Use module name.
                            insertClassExport(currModule, currModule.name(), currModule.name(), true);
                        }
                    } else {
                        if (exportNode.getFirstChild().isName() && currModule.declaredClasses().contains(exportNode.getFirstChild().getString())) {
                            insertClassExport(currModule, exportNode.getFirstChild().getString(), exportNode.getFirstChild().getString(), true);
                        }
                    }
                }
            } else if (exportNode.getFirstChild().isName() && currModule.declaredClasses().contains(exportNode.getFirstChild().getString())) {
                String className = exportNode.getFirstChild().getString();
                insertClassExport(currModule, className, className, true);

            } else if (exportNode.getFirstChild().isClass() && exportNode.getFirstChild().getFirstChild().isName()) {
                String className = exportNode.getFirstChild().getFirstChild().getString();
                insertClassExport(currModule, className, className, false);
            }
        }
    }


    private void processModuleImports(ES6Module currModule, Set<String> recursedModules, ModulesMap modulesMap) throws Exception {
        for (Node importNode : currModule.moduleImportNodes()) {
            ES6Module importedModule = null;
            if (referencesAnotherModule(importNode)) {
                importedModule = processImportedModule(currModule, modulesMap, importNode, 2);
                if (importedModule == null) {
                    // The referenced module is not local, no point trying to resolve this export/import..
                    continue;
                } else {
                    // Ensure we are not processing module exports in a circular recursion
                    if (!recursedModules.contains(importedModule.modulePath())) {
                        resolveModuleImportsAndExports(importedModule, recursedModules, modulesMap);
                    }
                }
            }
            /**
             * Process imports of the following types:
             * import { export1 } from "module-name";
             * import { export1 as alias1 } from "module-name";
             * import { export1 , export2 } from "module-name";
             * import { foo , bar } from "module-name/path/to/specific/un-exported/file";
             * import { export1 , export2 as alias2 , [...] } from "module-name";
             * import defaultExport, { export1 [ , [...] ] } from "module-name";
             */
            for (Node importChildNode : importNode.children()) {
                if (importChildNode.isImportSpecs()) {
                    for (Node importSpecChildNode : importChildNode.children()) {
                        if (importSpecChildNode.isImport()) {
                            processImportNode(currModule, importedModule, importSpecChildNode);
                        } else if (importSpecChildNode.isImportSpec()) {
                            processImportSpecNode(currModule, importedModule, importSpecChildNode);
                        }
                    }
                } else if (importChildNode.isName()) {
                    processDefaultImport(currModule, importChildNode, importedModule);
                } else if (importChildNode.isImportStar()) {
                    String moduleImportAlias = importNode.getSecondChild().getString();
                    currModule.insertClassImports(importedModule.classExports(), moduleImportAlias);
                }
            }
        }
    }

    private void processDefaultImport(ES6Module currModule, Node importNode, ES6Module importedModule) {
        ES6ClassExport matchedExport = importedModule.getDefaultClassExport();
        String namedImportVal = importNode.getString();
        if (namedImportVal == null && matchedExport.className() != null) {
            namedImportVal = matchedExport.namedExportValue();
        }
        currModule.insertClassImport(new ES6ClassImport(
                matchedExport.qualifiedClassName(),
                namedImportVal,
                true));
    }

    private boolean isDefault(Node importNode) {
        return importNode.hasChildren() && importNode.getFirstChild().isName()
                && importNode.getFirstChild().isDefaultValue();
    }

    private void processImportSpecNode(ES6Module currModule, ES6Module importedModule, Node importSpecChildNode) {
        String importClassName = importSpecChildNode.getChildAtIndex(0).getString();
        String importAlias = importSpecChildNode.getChildAtIndex(1).getString();
        List<ES6ClassExport> matchingExport = importedModule.matchingExportsByName(importClassName);
        if (!matchingExport.isEmpty()) {
            currModule.insertClassImport(new ES6ClassImport(
                    matchingExport.get(0).qualifiedClassName(), importAlias,
                    false));
        }
    }

    private void processImportNode(ES6Module currModule, ES6Module importedModule, Node importSpecChildNode) {
        String importClassName = importSpecChildNode.getChildAtIndex(0).getString();
        String importAlias = importSpecChildNode.getChildAtIndex(1).getString();
        List<ES6ClassExport> matchingExport = importedModule.matchingExportsByName(importClassName);
        if (!matchingExport.isEmpty()) {
            currModule.insertClassImport(new ES6ClassImport(
                    matchingExport.get(0).qualifiedClassName(), matchingExport.get(0).className(),
                    false));
        }
    }

    private boolean referencesAnotherModule(Node importNode) {
        return importNode.hasMoreThanOneChild();
    }

    private ES6Module processImportedModule(ES6Module currModule, ModulesMap modulesMap, Node importNode, int dirChildIndex) throws Exception {
        ES6Module importedModule;
        String importedModuleDir = importNode.getChildAtIndex(dirChildIndex).getString();
        if (absoluteModuleImport(importedModuleDir)) {
            importedModule = processAbsoluteImportModule(modulesMap, importedModuleDir);
        } else {
            importedModule = processRelativeImportModule(currModule, modulesMap, importedModuleDir);
        }
        return importedModule;
    }

    private ES6Module processRelativeImportModule(ES6Module currModule, ModulesMap modulesMap, String importedModuleDir) throws Exception {
        logger.info("Attempting to resolve relative import: " + importedModuleDir);
        ES6Module importedModule;// Relative import path provided..
        if (!relativeModuleImport(importedModuleDir)) {
            importedModuleDir = "./" + importedModuleDir.trim();
        }
        String importedModuleName = FilenameUtils.removeExtension(importedModuleDir.substring(
                importedModuleDir.lastIndexOf("/") + 1));
        importedModuleDir = importedModuleDir.substring(0, importedModuleDir.lastIndexOf("/") + 1);
        String importedModuleRelativePath = new ResolvedRelativePath(currModule.pkgPath(), importedModuleDir).value();
        if (importedModuleRelativePath.equals("/")) {
            importedModuleRelativePath = importedModuleRelativePath.substring(1);
        }
        importedModule = modulesMap.module(importedModuleRelativePath + "/" + importedModuleName);
        if (importedModule != null) {
            logger.info("Successfully matched relative import with module: " + importedModule.modulePkg());
        } else {
            logger.warn("Was not able to match relative import with any local modules.");
        }
        return importedModule;
    }

    private boolean absoluteModuleImport(String importedModuleDir) {
        return importedModuleDir.trim().startsWith("/");
    }

    private ES6Module processAbsoluteImportModule(ModulesMap modulesMap, String importedModuleDir) {
        logger.info("Attempting to resolve absolute import: " + importedModuleDir);
        List<ES6Module> matchingModules = modulesMap.matchingModules(importedModuleDir);
        ES6Module importedModule = null;
        if (!matchingModules.isEmpty() && matchingModules.size() < 2) {
            importedModule = modulesMap.module(matchingModules.get(0).modulePath());
            logger.info("Successfully matched absolute import with module: " + importedModule.modulePkg());
        } else {
            logger.warn("Was not able to match absolute import with any local modules.");
        }
        return importedModule;
    }

    private boolean relativeModuleImport(String importedModuleDir) {
        return importedModuleDir.trim().startsWith("./");
    }

    @Override
    public OOPSourceCodeModel compile(ProjectFiles projectFiles) throws Exception {
        OOPSourceCodeModel srcModel;
        final List<ProjectFile> files = projectFiles.getFiles();
        srcModel = compileFiles(files);
        return srcModel;
    }

    private void insertClassExport(ES6Module module, String className, String exportAlias, boolean isDefault) {
        String pkgSeparator = ".";
        if (module.pkgPath().equals("/")) {
            pkgSeparator = "";
        }
        module.insertClassExport(new ES6ClassExport(
                module.modulePkg() + pkgSeparator + className, exportAlias, isDefault));
    }
}
