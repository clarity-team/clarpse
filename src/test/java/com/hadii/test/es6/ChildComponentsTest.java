package com.hadii.test.es6;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChildComponentsTest {

    @Test
    public void ES6InstanceMethodParamComponentIsChildOfInstanceMethod() throws Exception {
        final String code = "class Polygon { constructor() {} say(height, length) {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().children().contains("Polygon.say.height"));
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().children().contains("Polygon.say.length"));
    }

    @Test
    public void noJsFilesParsedTest() throws Exception {
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.components().count() == 0);
    }

    @Test
    public void ES6GetterIsChildOfParentClass() throws Exception {
        final String code = "class Polygon { get height() {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().children().get(0).equals("Polygon.get_height"));
    }

    @Test
    public void ES6FieldVariableIsChildOfClass() throws Exception {
        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().children().get(1).equals("Polygon.height"));
    }

    @Test
    public void ES6ConstructorIsChildOfParentClass() throws Exception {
        final String code = "class Polygon { constructor() {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().children().get(0).equals("Polygon.constructor"));
    }

    @Test
    public void ES6InstanceMethodIsChildOfParentClass() throws Exception {
        final String code = "class Polygon { say() {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get().children().get(0).equals("Polygon.say"));
    }

    @Test
    public void ES6ConstructorParamComponentsIsChildOfConstructor() throws Exception {
        final String code = "class Polygon { constructor(height, length) {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").get().children().get(0)
                .equals("Polygon.constructor.height"));
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").get().children().get(1)
                .equals("Polygon.constructor.length"));
    }

    @Test
    public void ES6LocalVariableIsChildOfParentMethod() throws Exception {
        final String code = "class Polygon { say() { var test = new React(); var lol = 4; } }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get().children().get(1).equals("Polygon.say.lol"));
    }
}
