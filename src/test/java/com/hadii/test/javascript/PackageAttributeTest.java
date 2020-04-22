package com.hadii.test.javascript;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.File;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.SourceFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests to ensure package name attribute of parsed components are correct.
 */
public class PackageAttributeTest {

    @Test
    public void ES6FieldVariablePackageName() throws Exception {
        final String code = "class React() {} class Polygon { constructor(height) {this.height = new React();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/github/http/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon.height").get().packageName().equals("github.http"));
    }

    @Test
    public void ES6ClassPackageName() throws Exception {
        final String code = "class React() {} class Polygon { constructor(height) {this.height = new React();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/github/http/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon").get().packageName().equals("github.http"));
    }

    @Test
    public void ES6LocalVariablePackageName() throws Exception {
        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("src/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("src.Polygon.say.test").get().packageName().equals("src"));
    }

    @Test
    public void ES6MethodParamPackageName() throws Exception {
        final String code = "class Polygon { say(x) {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("src/cupcake/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("src.cupcake.Polygon.say.x").get().packageName().equals("src.cupcake"));
    }

    @Test
    public void ES6MethodPackageName() throws Exception {
        final String code = "class Polygon { constructor() {  new React().test(); } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/github/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.Polygon.constructor").get().packageName().equals("github"));
    }
}
