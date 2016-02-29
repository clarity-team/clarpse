package com.clarity.parser;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Antlr based parser.
 *
 * @author Muntazir Fadhel
 */
public class AntlrParser implements IClarityParser {

    /**
     * The package in which the ANTLR generated files are located in.
     */
    private final String antlrClassesPackageLocation;

    private final String grammarName;

    private final String clarityListenerPkgLocation;

    private static final String PARSE_RESULT_OBJECT_TYPE = "com.clarity.sourcemodel.OOPSourceCodeModel";

    /**
     * @param parserGrammarName
     *            name of the antlr grammer file
     * @param parserAntlrClassesPackageLocation
     *            type of parseResult Object to be populated by the anltr parser
     * @param clarityListenerPackageLocation
     *            the package location of the ParseTree Listener to use for the
     *            parser
     * @throws Exception
     *             Exception
     */
    public AntlrParser(final String parserGrammarName, final String parserAntlrClassesPackageLocation, final String clarityListenerPackageLocation) throws Exception {

        grammarName = parserGrammarName;
        antlrClassesPackageLocation = parserAntlrClassesPackageLocation;
        clarityListenerPkgLocation = clarityListenerPackageLocation;
    }

    /*
     * (non-Javadoc)
     * @see com.zir0.clarity.parser.AbstractClarityParser#extractSourceModel ()
     */
    @Override
    public final OOPSourceCodeModel extractParseResult(final ParseRequestContent rawData) throws Exception {

        final OOPSourceCodeModel antlrParseResult = (OOPSourceCodeModel) Class.forName(PARSE_RESULT_OBJECT_TYPE)
                .getConstructor().newInstance();

        final ArrayList<RawFile> files = rawData.getFiles();
        for (final RawFile file : files) {

            final Lexer lexer = (Lexer) Class.forName(antlrClassesPackageLocation + "." + grammarName + "Lexer")
                    .getConstructor(CharStream.class).newInstance(new ANTLRInputStream(file.getContent()));
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final Class<?> cls = Class.forName(antlrClassesPackageLocation + "." + grammarName + "Parser");
            final Object parser = cls.getConstructor(TokenStream.class).newInstance(tokens);
            final Method method = cls.getDeclaredMethod("compilationUnit");
            final ParseTree tree = (ParseTree) method.invoke(parser);

            final ParseTreeWalker walker = new ParseTreeWalker();
            final ParseTreeListener listener = (ParseTreeListener) Class
                    .forName(clarityListenerPkgLocation + "." + "Clarity" + grammarName + "Listener")
                    .getConstructor(OOPSourceCodeModel.class).newInstance(antlrParseResult);
            walker.walk(listener, tree);
        }

        return antlrParseResult;
    }
}
