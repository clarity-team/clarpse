package com.clarity.listener;

import java.util.List;
import java.util.Map;

import com.clarity.invocation.sources.InvocationSourceChain;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.google.javascript.jscomp.AbstractCompiler;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.JsAst;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.NodeTraversal.AbstractPostOrderCallback;
import com.google.javascript.rhino.InputId;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

/**
 * Listener for JavaScript ES6+ source files, based on google's closure compiler.
 */
public class JavaScriptListener implements CompilerPass {

	private OOPSourceCodeModel srcModel;
	private RawFile file;
	private AbstractCompiler compiler;
	private Map<String, List<InvocationSourceChain>> blockedInvocationSources;

	public JavaScriptListener(final OOPSourceCodeModel srcModel, final RawFile file,
			Map<String, List<InvocationSourceChain>> blockedInvocationSources) {
		this.srcModel = srcModel;
		this.file = file;
		this.blockedInvocationSources = blockedInvocationSources;
	}

	public void populateSourceModel() {
		Compiler compiler = new Compiler();
        CompilerOptions options = new CompilerOptions();
        options.setIdeMode(true);
        compiler.initOptions(options);
        Node root = new JsAst(SourceFile.fromCode(this.file.name(), this.file.content())).getAstRoot(compiler);
        NodeTraversal.traverseEs6(compiler, root, new Traversal());
	}

	@Override
	public void process(Node externs, Node root) {}

	private class Traversal extends AbstractPostOrderCallback {
		@Override
		public void visit(NodeTraversal t, Node n, Node parent) {
			if (n.isClass()) {
				System.out.println(n.getFirstChild().getString());
			}
			if (n.isMemberFunctionDef() || n.isGetterDef() || n.isSetterDef()) {
				System.out.println(n.getString());
			}
			if (n.isFunction()) {
				System.out.println(n.getFirstChild().getString());
			}
		}
	}
}
