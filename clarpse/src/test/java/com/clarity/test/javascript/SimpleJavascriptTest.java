package com.clarity.test.javascript;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;

public class SimpleJavascriptTest {

    @Test
    public void testParseES6Class() throws Exception {

        final String code = "class Polygon extends Shape { constructor(height, width) { this.width = width; this.height = height; } testMethod() {} function methodCakes(){} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon"));
    }

    @Test
    public void testParseES6ClassExtends() throws Exception {

        final String code = "class Polygon extends Shape { constructor(height, width) { this.width = width; this.height = height; } testMethod() {} function methodCakes(){} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        // assert the Polygon class component has one type extension component
        // invocation
        assertTrue(generatedSourceModel.getComponent("Polygon").componentInvocations(ComponentInvocations.EXTENSION)
                .size() == 1);
        // asert the component being extended is the Shape class
        assertTrue(generatedSourceModel.getComponent("Polygon").componentInvocations(ComponentInvocations.EXTENSION)
                .get(0).invokedComponent().equals("Shape"));

    }
}
