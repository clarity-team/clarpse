package com.clarity.test.javascript;


import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure components are displaying the correct associated source file path.
 */
public class ComponentSourceFilePathTest {

    @Test
    public void ES6ClassHasCorrectSourceFileAttr() throws Exception {
        final String code = "class Polygon {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().sourceFile().equals("polygon.js"));
    }

    @Test
    public void ES6ClassMethodHasCorrectSourceFileAttr() throws Exception {
        final String code = "class Polygon { say() {}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().sourceFile().equals("polygon.js"));
    }

    @Test
    public void ES6ClassConstructorHasCorrectSourceFileAttr() throws Exception {
        final String code = "class Polygon { constructor() {}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").get().sourceFile().equals("polygon.js"));
    }

    @Test
    public void ES6ClassFieldVarHasCorrectSourceFileAttr() throws Exception {
        final String code = "class Polygon { constructor() {this.height = 4;}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").get().sourceFile().equals("polygon.js"));
    }
}
