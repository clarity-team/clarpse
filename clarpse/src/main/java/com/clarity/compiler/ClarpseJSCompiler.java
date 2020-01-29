package com.clarity.compiler;

import com.clarity.listener.JavaScriptExportsListener;
import com.clarity.listener.JavaScriptListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JsAst;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config.JsDocParsing;
import com.google.javascript.rhino.Node;
import edu.emory.mathcs.backport.java.util.Collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClarpseJSCompiler implements ClarpseCompiler {

    private OOPSourceCodeModel compileFiles(List<RawFile> files, List<String> projectFileTypes) {
        OOPSourceCodeModel model = new OOPSourceCodeModel();
        final Map<String, JavaScriptExportsListener.JSExport> exportsMap = new HashMap<>();
        Compiler compiler = new Compiler();
        CompilerOptions options = new CompilerOptions();
        options.setIdeMode(true);
        options.setParseJsDocDocumentation(JsDocParsing.INCLUDE_DESCRIPTIONS_WITH_WHITESPACE);
        compiler.initOptions(options);
        // Generate exports map on initial pass
        files.forEach(file -> {
            try {
                Node root = new JsAst(SourceFile.fromCode(file.name(), file.content())).getAstRoot(compiler);
                NodeTraversal.Callback jsListener = new JavaScriptExportsListener(file, projectFileTypes, exportsMap);
                NodeTraversal.traverse(compiler, root, jsListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // Parse JS files with populated export map
        files.forEach(file -> {
            try {
                Node root = new JsAst(SourceFile.fromCode(file.name(), file.content())).getAstRoot(compiler);
                NodeTraversal.Callback jsListener = new JavaScriptListener(model, file, projectFileTypes, files, exportsMap);
                NodeTraversal.traverse(compiler, root, jsListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return model;
    }

    private List<String> getProjectFileTypes(List<RawFile> files) throws Exception {
        List<String> projectFileTypes = new ArrayList<>();
        for (RawFile file : files) {
            String modFileName = file.name();
            if (file.name().startsWith("/")) {
                modFileName = file.name().substring(1);
            }
            if (modFileName.contains("/")) {
                projectFileTypes.add(modFileName.substring(0, modFileName.lastIndexOf("/")));
            }
        }
        return projectFileTypes;
    }

    @Override
    public OOPSourceCodeModel compile(SourceFiles rawData) throws Exception {
        OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final List<RawFile> files = rawData.getFiles();
        System.out.println(files.size());
        List<String> projectFileTypes = getProjectFileTypes(files);
        // sort fileTypes by length in desc order so that the longest types are at the
        // top.
        Collections.sort(projectFileTypes, new LengthComp());
        srcModel = compileFiles(files, projectFileTypes);
        return srcModel;
    }
}
