package com.clarity.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import parser.java.JavaLexer;
import parser.java.JavaParser;

import com.clarity.invocation.sources.InvocationSourceChain;
import com.clarity.parser.java.ClarpseJavaTreeListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Antlr based parser.
 *
 * @author Muntazir Fadhel
 */
public class AntlrParser implements ClarpseParser {

    /**
     * The package in which the ANTLR generated files are located in.
     */
    private final String antlrClassesPackageLocation;

    private final String grammarName;

    private final String clarityListenerPkgLocation;

    private static OOPSourceCodeModel srcModel;

    public static OOPSourceCodeModel getSrcModel() {
        return srcModel;
    }

    public static Map<String, List<InvocationSourceChain>> getBlockedInvocationSources() {
        return blockedInvocationSources;
    }

    private static volatile Map<String, List<InvocationSourceChain>> blockedInvocationSources = new HashMap<String, List<InvocationSourceChain>>();


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

        srcModel = new OOPSourceCodeModel();

        ANTLRInputStream stream = new ANTLRInputStream();
        final JavaLexer lexer = new JavaLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        final JavaParser parser = new JavaParser(tokens);
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        parser.setErrorHandler(new BailErrorStrategy());

        final List<RawFile> files = rawData.getFiles();
        for (final RawFile file : files) {

            stream = new ANTLRInputStream(file.getContent());
            lexer.setInputStream(stream);
            tokens = new CommonTokenStream(lexer);
            parser.setTokenStream(tokens);

            ParseTree tree;
            try {
                tree = parser.compilationUnit();
            } catch (final Exception e) {
                System.out.println("Failed file: " + file.getName());
                System.out.println(e.getCause());
                continue;
            }
            final ClarpseJavaTreeListener listener = new ClarpseJavaTreeListener(srcModel, file.getName(),
                    blockedInvocationSources);
            ParseTreeWalker.DEFAULT.walk(listener, tree);
        }
        return srcModel;
    }
}
