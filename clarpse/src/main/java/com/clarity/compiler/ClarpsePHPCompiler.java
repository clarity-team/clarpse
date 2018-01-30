package com.clarity.compiler;

import com.clarity.antlr.php.PHP;
import com.clarity.antlr.php.PHPBaseListener;
import com.clarity.antlr.php.PhpLexer;
import com.clarity.listener.PHPTreeListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;

/**
 * Antlr4 based PHP compiler.
 */
public class ClarpsePHPCompiler implements ClarpseCompiler {

    @Override
    public OOPSourceCodeModel compile(SourceFiles rawData) {
        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final List<RawFile> files = rawData.getFiles();
        for (RawFile file : files) {
            try {
                CharStream charStream = new ANTLRInputStream(file.content());
                PhpLexer lexer = new PhpLexer(charStream);
                TokenStream tokens = new CommonTokenStream(lexer);
                PHP parser = new PHP(tokens);
                PHP.HtmlDocumentContext sourceFileContext = parser.htmlDocument();
                parser.setErrorHandler(new BailErrorStrategy());
                parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
                ParseTreeWalker walker = new ParseTreeWalker();
                PHPBaseListener listener = new PHPTreeListener(srcModel, file);
                walker.walk(listener, sourceFileContext);
            } catch (Throwable t) {
              t.printStackTrace();
            }
        }
        return srcModel;
    }
}