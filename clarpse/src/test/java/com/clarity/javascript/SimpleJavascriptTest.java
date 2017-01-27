package com.clarity.javascript;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class SimpleJavascriptTest {

	 @Test
	    public void testClassHasMethodChild() throws Exception {

	        final String code = "class Test { class Polygon { constructor(height, width) { } }";
	        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
	        rawData.insertFile(new RawFile("file2.js", code));
	        final ClarpseProject parseService = new ClarpseProject(rawData);
	        final OOPSourceCodeModel generatedSourceModel = parseService.result();
	    }
}
