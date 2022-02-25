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
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon.height").get().pkg().path().equals("/github/http"));
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon.height").get().pkg().ellipsisSeparatedPkgPath().equals("github.http"));
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon.height").get().pkg().name().equals("http"));
    }

    @Test
    public void ES6ClassPackageName() throws Exception {
        final String code = "class React {} \n class Polygon { constructor(height) { this.height = new React(); } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("/github/http/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon.height").get().pkg().path().equals("/github/http"));
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon.height").get().pkg().ellipsisSeparatedPkgPath().equals("github.http"));
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon.height").get().pkg().name().equals("http"));
    }

    @Test
    public void ES6LocalVariablePackageName() throws Exception {
        final String code = "class Polygon { say() { var test = new React(); var lol = 4; } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("src/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("src.Polygon.say.test").get().pkg().path().equals(
            "/src"));
        assertTrue(generatedSourceModel.getComponent("src.Polygon.say.test").get().pkg().name().equals(
            "src"));
        assertTrue(generatedSourceModel.getComponent("src.Polygon.say.test").get().pkg().ellipsisSeparatedPkgPath().equals(
            "src"));
    }

    @Test
    public void ES6MethodParamPackageName() throws Exception {
        final String code = "class Polygon { say(x) {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("src/cupcake/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("src.cupcake.Polygon.say.x").get().pkg().path().equals("/src/cupcake"));
        assertTrue(generatedSourceModel.getComponent("src.cupcake.Polygon.say.x").get().pkg().name().equals("cupcake"));
        assertTrue(generatedSourceModel.getComponent("src.cupcake.Polygon.say.x").get().pkg().ellipsisSeparatedPkgPath().equals("src.cupcake"));
    }

    @Test
    public void ES6MethodPackageName() throws Exception {
        final String code = "class Polygon { constructor() {  new React().test(); } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("/github/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.Polygon.constructor").get().pkg().path().equals("/github"));
        assertTrue(generatedSourceModel.getComponent("github.Polygon.constructor").get().pkg().name().equals("github"));
        assertTrue(generatedSourceModel.getComponent("github.Polygon.constructor").get().pkg().ellipsisSeparatedPkgPath().equals("github"));
    }
}
