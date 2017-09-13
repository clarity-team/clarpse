package com.clarity.test.javascript;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;

public class JavaScriptSmokeTest {

	private static OOPSourceCodeModel generatedSourceModel;

	@BeforeClass
	public static void setup() throws Exception {
		String code = IOUtils.toString(JavascriptParseTest.class.getClass().getResourceAsStream("/sample-es6.txt"),
				"UTF-8");
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
		rawData.insertFile(new RawFile("polygon.js", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
	}

	@Test
	public void TestAllClassessWereParsed() {
		assertTrue(generatedSourceModel.containsComponent("Square"));
		assertTrue(generatedSourceModel.containsComponent("Rectangle"));
		assertTrue(generatedSourceModel.containsComponent("Polygon"));
		assertTrue(generatedSourceModel.containsComponent("Poly"));
		assertTrue(generatedSourceModel.containsComponent("Triple"));
		assertTrue(generatedSourceModel.containsComponent("BiggerTriple"));
		assertTrue(generatedSourceModel.containsComponent("MyDate"));
		assertTrue(generatedSourceModel.containsComponent("ExtendedUint8Array"));
	}

	@Test
	public void TestAllMethodsWereParsed() {
		assertTrue(generatedSourceModel.containsComponent("Square.set_area"));
		assertTrue(generatedSourceModel.containsComponent("Square.get_area"));
		assertTrue(generatedSourceModel.containsComponent("Rectangle.sayName"));
		assertTrue(generatedSourceModel.containsComponent("Triple.triple"));
		assertTrue(generatedSourceModel.containsComponent("BiggerTriple.triple"));
		assertTrue(generatedSourceModel.containsComponent("Polygon.constructor"));
		assertTrue(generatedSourceModel.containsComponent("Polygon.sayName"));
		assertTrue(generatedSourceModel.containsComponent("Polygon.sayHistory"));
		assertTrue(generatedSourceModel.containsComponent("Poly.getPolyName"));
		assertTrue(generatedSourceModel.containsComponent("ExtendedUint8Array.constructor"));
		assertTrue(generatedSourceModel.containsComponent("Square.constructor"));
		assertTrue(generatedSourceModel.containsComponent("Rectangle.constructor"));
		assertTrue(generatedSourceModel.containsComponent("MyDate.constructor"));
		assertTrue(generatedSourceModel.containsComponent("MyDate.getFormattedDate"));
	}

	@Test
	public void testSquareExtendsPolygon() {
		assertTrue(generatedSourceModel.getComponent("Square").componentInvocations(ComponentInvocations.EXTENSION)
				.get(0).invokedComponent().equals("Polygon"));
	}

	@Test
	public void testStaticMethodWasParsed() {
		assertTrue(generatedSourceModel.getComponent("Triple.triple").modifiers().iterator().next().equals("static"));
	}
}