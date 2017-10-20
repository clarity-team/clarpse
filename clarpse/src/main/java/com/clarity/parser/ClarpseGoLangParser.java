package com.clarity.parser;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.clarity.antlr.golang.GolangBaseListener;
import com.clarity.antlr.golang.GolangLexer;
import com.clarity.antlr.golang.GolangParser;
import com.clarity.antlr.golang.GolangParser.SourceFileContext;
import com.clarity.listener.GoLangTreeListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Antlr4 based GoLang parser.
 */
public class ClarpseGoLangParser implements ClarpseParser {

    @Override
    public final OOPSourceCodeModel extractParseResult(final ParseRequestContent rawData) throws Exception {

        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();

        final List<RawFile> files = rawData.getFiles();
        for (final RawFile file : files) {
            CharStream charStream = new ANTLRInputStream(file.content());
            GolangLexer lexer = new GolangLexer(charStream);
            TokenStream tokens = new CommonTokenStream(lexer);
            GolangParser parser = new GolangParser(tokens);
            SourceFileContext sourceFileContext = parser.sourceFile();
            ParseTreeWalker walker = new ParseTreeWalker();
            GolangBaseListener listener = new GoLangTreeListener(srcModel, file);
            walker.walk(listener, sourceFileContext);
        }
        return srcModel;
    }
}
