package com.clarity.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clarity.invocation.sources.InvocationSourceChain;
import com.clarity.listener.JavaScriptListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.google.javascript.jscomp.AbstractCompiler;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.jscomp.SourceFile;

public class ClarpseClosureCompilerParser implements ClarpseParser {

	@Override
	public OOPSourceCodeModel extractParseResult(ParseRequestContent rawData) throws Exception {

		final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
		final Map<String, List<InvocationSourceChain>> blockedInvocationSources = new HashMap<String, List<InvocationSourceChain>>();
		for (RawFile file : rawData.getFiles()) {
			AbstractCompiler compiler = new Compiler();
			CompilerOptions options = new CompilerOptions();
			options.setLanguageIn(LanguageMode.ECMASCRIPT6);
			CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
			List<SourceFile> list = new ArrayList<SourceFile>();
			list.add(SourceFile.fromCode(file.content(), file.name()));
			new JavaScriptListener(srcModel, file, blockedInvocationSources).populateSourceModel();
		}
		return null;
	}
}
