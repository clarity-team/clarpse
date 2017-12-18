package com.clarity.compiler;

import java.util.ArrayList;
import java.util.List;

import com.clarity.CommonDir;
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

public class ClarpseJSCompiler implements ClarpseCompiler {

    @SuppressWarnings("deprecation")
    private OOPSourceCodeModel compileFiles(List<RawFile> files, List<String> projectFileTypes) {
        OOPSourceCodeModel model = new OOPSourceCodeModel();
        for (RawFile file : files) {
            try {
                Compiler compiler = new Compiler();
                CompilerOptions options = new CompilerOptions();
                options.setIdeMode(true);
                options.setParseJsDocDocumentation(JsDocParsing.INCLUDE_DESCRIPTIONS_WITH_WHITESPACE);
                compiler.initOptions(options);
                Node root = new JsAst(SourceFile.fromCode(file.name(), file.content())).getAstRoot(compiler);
                JavaScriptListener jsListener = new JavaScriptListener(model, file, projectFileTypes);
                NodeTraversal.traverseEs6(compiler, root, jsListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    private List<String> getProjectFileTypes(List<RawFile> files) throws Exception {
        List<String> projectFileTypes = new ArrayList<String>();
        String smallestCodeBaseContaininingDir = "";

        for (int i = 1; i < files.size(); i++) {
            smallestCodeBaseContaininingDir = new CommonDir(smallestCodeBaseContaininingDir, files.get(i).name())
                    .string();
        }

        if (smallestCodeBaseContaininingDir.startsWith("/")) {
            smallestCodeBaseContaininingDir.substring(1);
        }

        for (RawFile file : files) {
            String modFileName = "";
            if (file.name().contains("src/")) {
                modFileName = (file.name().substring(file.name().indexOf("src/") + 4));
            } else {
                modFileName = file.name().replaceAll(smallestCodeBaseContaininingDir, "");
                if (modFileName.startsWith("/")) {
                    modFileName = modFileName.substring(1);
                }
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
