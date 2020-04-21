package com.clarity.test.javascript;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.File;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChildComponentsTest {

    @Test
    public void ES6InstanceMethodParamComponentIsChildOfInstanceMethod() throws Exception {
        final String code = "class Polygon { constructor() {} say(height, length) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().children().contains("Polygon.say.height"));
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().children().contains("Polygon.say.length"));
    }

    @Test
    public void ES6GetterIsChildOfParentClass() throws Exception {
        final String code = "class Polygon { get height() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().children().get(0).equals("Polygon.get_height"));
    }

    @Test
    public void ES6FieldVariableIsChildOfClass() throws Exception {
        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().children().get(1).equals("Polygon.height"));
    }

    @Test
    public void ES6ConstructorIsChildOfParentClass() throws Exception {
        final String code = "class Polygon { constructor() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().children().get(0).equals("Polygon.constructor"));
    }

    @Test
    public void ES6InstanceMethodIsChildOfParentClass() throws Exception {
        final String code = "class Polygon { say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().children().get(0).equals("Polygon.say"));
    }

    @Test
    public void ES6ConstructorParamComponentsIsChildOfConstructor() throws Exception {
        final String code = "class Polygon { constructor(height, length) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").get().children().get(0)
                .equals("Polygon.constructor.height"));
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").get().children().get(1)
                .equals("Polygon.constructor.length"));
    }

    @Test
    public void ES6LocalVariableIsChildOfParentMethod() throws Exception {
        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().children().get(1).equals("Polygon.say.lol"));
    }
}
