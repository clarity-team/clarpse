package com.clarity.parser;

import com.clarity.listener.JavaScriptListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JsAst;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config.JsDocParsing;
import com.google.javascript.rhino.Node;

@Experimental
public class ClarpseJSParser implements ClarpseParser {

    @Override
    public OOPSourceCodeModel extractParseResult(ParseRequestContent rawData) throws Exception {

        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        for (RawFile file : rawData.getFiles()) {
            Compiler compiler = new Compiler();
            CompilerOptions options = new CompilerOptions();
            options.setIdeMode(true);
            options.setParseJsDocDocumentation(JsDocParsing.INCLUDE_DESCRIPTIONS_WITH_WHITESPACE);
            compiler.initOptions(options);
            Node root = new JsAst(SourceFile.fromCode(file.name(), file.content())).getAstRoot(compiler);
            JavaScriptListener jsListener = new JavaScriptListener(srcModel, file);
            NodeTraversal.traverseEs6(compiler, root, jsListener);
        }
        return srcModel;
    }
}