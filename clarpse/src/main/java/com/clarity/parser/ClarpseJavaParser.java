package com.clarity.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.clarity.invocation.sources.InvocationSourceChain;
import com.clarity.listener.JavaTreeListener;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * JavaParser based parser.
 *
 * @author Muntazir Fadhel
 */
public class ClarpseJavaParser implements ClarpseParser {

    @Override
    public final OOPSourceCodeModel extractParseResult(final ParseRequestContent rawData) throws Exception {

        final OOPSourceCodeModel srcModel = new OOPSourceCodeModel();
        final Map<String, List<InvocationSourceChain>> blockedInvocationSources = new HashMap<String, List<InvocationSourceChain>>();

        final List<RawFile> files = rawData.getFiles();
        for (final RawFile file : files) {
            final Runnable parseFile = new Thread() {
                @Override
                public void run() {
                    try {
                        new JavaTreeListener(srcModel, file, blockedInvocationSources).populateModel();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future future = executor.submit(parseFile);
            executor.shutdown();

            try {
                // there should be a lower timeout value
                // but tests keep failing on low memory environments
                // and the build environment.
                future.get(500, TimeUnit.MILLISECONDS);
            } catch (Exception ie) {
                ie.printStackTrace();
                continue;
            }
            if (!executor.isTerminated()) {
                executor.shutdownNow(); // If you want to stop the code that
                                        // hasn't finished.
            }
        }
        return srcModel;
    }
}
