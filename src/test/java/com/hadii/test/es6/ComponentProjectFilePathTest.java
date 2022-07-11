package com.hadii.test.es6;


import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure components are displaying the correct associated source file path.
 */
public class ComponentProjectFilePathTest {

    @Test
    public void ES6ClassHasCorrectSourceFileAttr() throws Exception {
        final String code = "class Polygon {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().sourceFile().equals("/polygon.js"));
    }

    @Test
    public void ES6ClassMethodHasCorrectSourceFileAttr() throws Exception {
        final String code = "class Polygon { say() {}}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().sourceFile().equals("/polygon.js"));
    }

    @Test
    public void ES6ClassConstructorHasCorrectSourceFileAttr() throws Exception {
        final String code = "class Polygon { constructor() {}}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").get().sourceFile().equals("/polygon.js"));
    }

    @Test
    public void ES6ClassFieldVarHasCorrectSourceFileAttr() throws Exception {
        final String code = "class Polygon { constructor() {this.height = 4;}}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").get().sourceFile().equals("/polygon.js"));
    }
}
