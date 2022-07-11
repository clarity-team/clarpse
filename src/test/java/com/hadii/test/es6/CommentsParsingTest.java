package com.hadii.test.es6;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommentsParsingTest {

    @Test
    public void ES6ClassDoc() throws Exception {
        final String code = "/**Test*/ class Polygon extends Test {get prop() {return 'getter'; }}";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().comment().equals("/**Test*/"));
    }

    @Test
    public void ES6InstanceMethodDoc() throws Exception {
        final String code = "class Polygon { /** say doc \n comment */ say() {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().comment().equals("/** say doc \n comment */"));
    }

    @Test
    public void ES6ClassFieldVarDoc() throws Exception {
        final String code = "class Polygon { constructor() {/** the height of /n some stuff \n */ \nthis.height = 4;} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").get().comment().equals("/** the height of /n some stuff \n" +
                " */"));
    }

    @Test
    public void ES6LocalVarDoc() throws Exception {
        final String code = "class Polygon { constructor() { /** some local var docs */ \n var test;} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor.test").get().comment().equals(
                "/** some local var docs */"));
    }

    @Test
    public void ES6ConstructorDoc() throws Exception {
        final String code = "class Polygon { /** constructor doc */ constructor() {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").get().comment().equals(
                "/** constructor doc */"));
    }
}
