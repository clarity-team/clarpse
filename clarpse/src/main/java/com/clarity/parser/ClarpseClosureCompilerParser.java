package com.clarity.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clarity.invocation.sources.InvocationSourceChain;
import com.clarity.listener.JavaScriptListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JsAst;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;

@Experimental
public class ClarpseClosureCompilerParser implements ClarpseParser {

    @Override
    public OOPSourceCodeModel extractParseResult(ParseRequestContent rawData) throws Exception {

        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final Map<String, List<InvocationSourceChain>> blockedInvocationSources = new HashMap<String, List<InvocationSourceChain>>();
        for (RawFile file : rawData.getFiles()) {
            Compiler compiler = new Compiler();
            CompilerOptions options = new CompilerOptions();
            options.setIdeMode(true);
            compiler.initOptions(options);
            Node root = new JsAst(SourceFile.fromCode(file.name(), file.content())).getAstRoot(compiler);
            JavaScriptListener jsListener = new JavaScriptListener(srcModel, file, blockedInvocationSources);
            NodeTraversal.traverseEs6(compiler, root, jsListener);
        }
        return srcModel;
    }
}
