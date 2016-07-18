package com.clarity.parser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;
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

        final ANTLRInputStream stream;
        final List<RawFile> files = rawData.getFiles();
        final Lexer lexer = (Lexer) Class.forName(antlrClassesPackageLocation + "." + grammarName + "Lexer")
                .getConstructor(CharStream.class).newInstance(new ANTLRInputStream());
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final Class<?> cls = Class.forName(antlrClassesPackageLocation + "." + grammarName + "Parser");
        final Object parser = cls.getConstructor(TokenStream.class).newInstance(tokens);
        ((org.antlr.v4.runtime.Parser) parser).getInterpreter().setPredictionMode(PredictionMode.SLL);
        ((Parser) parser).setErrorHandler(new BailErrorStrategy());
        final Method method = cls.getDeclaredMethod("compilationUnit");


        final ExecutorService executor = Executors.newFixedThreadPool(15);
        final int numFilesPerThread = 100;
        final List<List<RawFile>> fileLists = chopped(files, numFilesPerThread);
        for (final List<RawFile> listFiles : fileLists) {

            final Thread t = (new Thread() {
                @Override
                public void run() {

                    for (final RawFile file : listFiles) {

                        stream = new ANTLRInputStream(file.getContent());
                        lexer.setInputStream(stream);
                        tokens = new CommonTokenStream(lexer);
                        ((Parser) parser).setTokenStream(tokens);

                        ParseTree tree;
                        try {
                            tree = (ParseTree) method.invoke(parser);
                        } catch (final Exception e) {
                            System.out.println("Failed file: " + file.getName());
                            System.out.println(e.getCause());
                            continue;
                        }
                        final ClarpseJavaTreeListener listener = new ClarpseJavaTreeListener(srcModel, file.getName(),
                                blockedInvocationSources);
                        ParseTreeWalker.DEFAULT.walk(listener, tree);
                    }
                }
            });
            }
            return srcModel;
        }


        /**
         * chops a list into non-view sublists of length L.
         *
         * @param list
         *            list to chop up
         * @param l
         *            length of sub lists
         * @param <T>
         *            generic
         * @return List containing sublists
         */
        public static <T> List<List<T>> chopped(final List<T> list, final int l) {
            final ArrayList<List<T>> parts = new ArrayList<List<T>>();
            final int n = list.size();
            for (int i = 0; i < n; i += l) {
                parts.add(new ArrayList<T>(list.subList(i, Math.min(n, i + l))));
            }
            return parts;
        }
    }
