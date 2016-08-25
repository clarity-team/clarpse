package com.clarity.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import listener.JavaTreeListener;

import com.clarity.invocation.sources.InvocationSourceChain;
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

            try {
               new JavaTreeListener(srcModel, file, blockedInvocationSources).populateModel();
            } catch (final Exception e) {
                continue;
            }
        }
        return srcModel;
    }
}
