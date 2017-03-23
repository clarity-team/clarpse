package com.clarity.javascript;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class SimpleJavascriptTest {

	@Test
	public void testClassHasMethodChild() throws Exception {

		final String code = "class Polygon { constructor(height, width) { this.width = width; this.height = height; } }";
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
		rawData.insertFile(new RawFile("polygon.js", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		final OOPSourceCodeModel generatedSourceModel = parseService.result();
	}
}
