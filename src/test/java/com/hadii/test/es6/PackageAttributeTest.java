package com.hadii.test.es6;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests to ensure package name attribute of parsed components are correct.
 */
public class PackageAttributeTest {

    @Test
    public void ES6FieldVariablePackageName() throws Exception {
        final String code = "class React {} class Polygon { constructor(height) {this.height = new React();} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("/github/http/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon.height").get().packageName().equals("github.http"));
    }

    @Test
    public void ES6ClassPackageName() throws Exception {
        final String code = "class React {} \n class Polygon { constructor(height) { this.height = new React(); } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("/github/http/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon").get().packageName().equals("github.http"));
    }

    @Test
    public void ES6LocalVariablePackageName() throws Exception {
        final String code = "class Polygon { say() { var test = new React(); var lol = 4; } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("src/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("src.Polygon.say.test").get().packageName().equals("src"));
    }

    @Test
    public void ES6MethodParamPackageName() throws Exception {
        final String code = "class Polygon { say(x) {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("src/cupcake/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("src.cupcake.Polygon.say.x").get().packageName().equals("src.cupcake"));
    }

    @Test
    public void ES6MethodPackageName() throws Exception {
        final String code = "class Polygon { constructor() {  new React().test(); } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("/github/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.Polygon.constructor").get().packageName().equals("github"));
    }
}
