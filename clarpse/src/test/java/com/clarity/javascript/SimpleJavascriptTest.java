package com.clarity.javascript;

import org.junit.Test;

import com.clarity.ClarpseUtil;
import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;

public class SimpleJavascriptTest {

    @Test
    public void testClassHasMethodChild() throws Exception {
        final ParseRequestContent rawData = ClarpseUtil.parseRequestContentObjFromResourceDir("/codebaseA/",
                Lang.JAVASCRIPT);
        final ClarpseProject parseService = new ClarpseProject(rawData);
        parseService.result();
    }
}