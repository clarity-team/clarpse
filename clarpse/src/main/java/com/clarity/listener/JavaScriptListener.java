package com.clarity.listener;

import java.util.List;
import java.util.Map;

import com.clarity.invocation.sources.InvocationSourceChain;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.google.javascript.jscomp.AbstractCompiler;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.NodeTraversal.AbstractPostOrderCallback;
import com.google.javascript.rhino.InputId;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

/**
 * Supports ES6+, based on google's closure compiler.
 *
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
		Node script = new Node(Token.SCRIPT);
	    script.setStaticSourceFile(
	        SourceFile.fromCode(file.name(), file.content()));
	    script.setInputId(new InputId(file.name()));
	    
		process(null, script);
	}

	@Override
	public void process(Node externs, Node root) {

		AbstractCompiler compiler = new Compiler();
		NodeTraversal.traverseEs6(compiler, root, new Traversal());
	}

	private class Traversal extends AbstractPostOrderCallback {
		@Override
		public void visit(NodeTraversal t, Node n, Node parent) {
			if (n.isClass()) {
				System.out.println("Were in business!");
			}
			
			if (n.isVar()) {
				System.out.println("Were in business!");
			}
			
			if (n.isScript()) {
				visit(t, n, parent);
			}
		}
	}
}
