package com.clarity.listener;

import com.clarity.TrimmedString;
import com.clarity.compiler.RawFile;
import com.google.common.io.Files;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.NodeTraversal.Callback;
import com.google.javascript.rhino.Node;

import java.util.List;
import java.util.Map;

/**
 * Listener for JavaScript ES6+ source files, based on google's closure
 * compiler.
 */
public class JavaScriptExportsListener implements Callback {

    private final Map<String, JSExport> exportsMap;
    private RawFile file;
    private List<String> projectFileTypes;
    private String currPackage = "";
    private String currProjectFileType = "";

    public JavaScriptExportsListener(final RawFile file, List<String> projectFileTypes,
                                     Map<String, JSExport> exportsMap) throws Exception {
        this.file = file;
        this.projectFileTypes = projectFileTypes;
        this.exportsMap = exportsMap;
        System.out.println("\nParsing New JS File: " + file.name() + "\n");
        // Determine current file's file type
        if (file.name().contains("/")) {
            String modFileName = file.name().substring(0, file.name().lastIndexOf("/"));
            for (String s : projectFileTypes) {
                if (modFileName.endsWith(s)) {
                    currPackage = new TrimmedString(s, "/").value().replaceAll("/", ".");
                    currProjectFileType = s;
                    break;
                }
            }
        }
    }

    @Override
    public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
        try {
            return shouldTraverse(n, parent);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void visit(NodeTraversal nodeTraversal, Node node, Node node1) {
    }

    private boolean shouldTraverse(Node n, Node parent) throws Exception {
        if (n.isExport()) {
            // Handle 'default export ... ' type exports
            if (n.getBooleanProp(Node.EXPORT_DEFAULT)) {
                String pkgPrefix = "";
                if (!currProjectFileType.isEmpty()) {
                    pkgPrefix = currPackage + ".";
                }
                String childOneStr = Files.getNameWithoutExtension(file.name());
                if (n.getChildAtIndex(0).getFirstChild() != null && !n.getChildAtIndex(0).getFirstChild().isEmpty()) {
                    if (n.getChildAtIndex(0).getFirstChild() != null) {
                        childOneStr = n.getChildAtIndex(0).getFirstChild().getString();
                    }
                }
                exportsMap.put(pkgPrefix + childOneStr,
                        new JSExport(pkgPrefix + childOneStr,
                                pkgPrefix + childOneStr, currProjectFileType)
                );
            }
            // Handle exports that do not use 'export default ...' syntax
            for (Node exportNode : n.children()) {
                if (exportNode.isExportSpecs()) {
                    for (Node exportSpecsChildNode : exportNode.children()) {
                        if (exportSpecsChildNode.isExportSpec()) {
                            processExportSpec(exportSpecsChildNode);
                        }
                    }
                } else if (exportNode.isExportSpec()) {
                    processExportSpec(exportNode);
                }
            }
        }
        return true;
    }

    private void processExportSpec(Node exportSpec) {
        String pkgPrefix = "";
        if (!currProjectFileType.isEmpty()) {
            pkgPrefix = currPackage + ".";
        }
        if (exportSpec.hasTwoChildren()) {
            String childOneStr = exportSpec.getChildAtIndex(0).getString();
            String childTwoStr = exportSpec.getChildAtIndex(1).getString();
            if (childTwoStr.equals("default")) {
                childTwoStr = childOneStr;
            }
            exportsMap.put(pkgPrefix + childTwoStr,
                    new JSExport(pkgPrefix + childOneStr,
                            pkgPrefix + childTwoStr, currProjectFileType)
            );
        }
    }

    public class JSExport {

        private String exportedPkgAlias;
        private final String exportedPkg;
        private final String fileType;

        JSExport(String exportedPkg, String exportedPkgAlias, String fileType) {
            this.exportedPkg = exportedPkg;
            this.exportedPkgAlias = exportedPkgAlias;
            this.fileType = fileType;
        }

        String fileType() {
            return fileType;
        }

        String exportedPkgAlias() {
            return exportedPkgAlias;
        }

        String exportedPkg() {
            return exportedPkg;
        }
    }
}
