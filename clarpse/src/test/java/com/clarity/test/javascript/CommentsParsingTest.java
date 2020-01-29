package com.clarity.test.javascript;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommentsParsingTest {

    @Test
    public void ES6ClassDoc() throws Exception {
        final String code = "/**Test*/ class Polygon extends Test {get prop() {return 'getter'; }}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().comment().equals("/**Test*/"));
    }

    @Test
    public void ES6InstanceMethodDoc() throws Exception {
        final String code = "class Polygon { /** say doc \n comment */ say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().comment().equals("/** say doc \n comment */"));
    }

    @Test
    public void ES6ClassFieldVarDoc() throws Exception {
        final String code = "class Polygon { constructor() {/** the height of /n some stuff \n */ \nthis.height = 4;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").get().comment().equals("/** the height of /n some stuff \n" +
                " */"));
    }

    @Test
    public void ES6LocalVarDoc() throws Exception {
        final String code = "class Polygon { constructor() { /** some local var docs */ \n var test;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor.test").get().comment().equals(
                "/** some local var docs */"));
    }

    @Test
    public void ES6ConstructorDoc() throws Exception {
        final String code = "class Polygon { /** constructor doc */ constructor() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").get().comment().equals(
                "/** constructor doc */"));
    }
}
