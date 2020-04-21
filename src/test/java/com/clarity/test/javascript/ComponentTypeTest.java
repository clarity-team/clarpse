package com.clarity.test.javascript;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.File;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests to ensure component type attribute of parsed components are accurate.
 */
public class ComponentTypeTest {

    @Test
    public void ES6ClassHasCorrectComponentType() throws Exception {
        final String code = "\n\n class Polygon { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").get()
                .componentType() == OOPSourceModelConstants.ComponentType.CLASS);
    }

    @Test
    public void testParsedES6InstanceMethodParamComponentType() throws Exception {
        final String code = "class Polygon { constructor() {} say(height) {}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.height")
                .get().componentType() == OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void ES6ConstructorComponentTypeIsCorrect() throws Exception {
        final String code = "class Polygon { constructor() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(
                generatedSourceModel.getComponent("Polygon.constructor").get()
                        .componentType() == OOPSourceModelConstants.ComponentType.CONSTRUCTOR);
    }

    @Test
    public void ES6LocalVariableComponentType() throws Exception {
        final String code = "class Polygon { say() { var test = new React(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test").get()
                .componentType() == OOPSourceModelConstants.ComponentType.LOCAL);
    }

    @Test
    public void ES6LocalLetVariableComponentType() throws Exception {
        final String code = "class Polygon { say() { let test = new React(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test")
                .get().componentType() == OOPSourceModelConstants.ComponentType.LOCAL);
    }

    @Test
    public void ES6FieldVariableComponentType() throws Exception {
        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height")
                .get().componentType() == OOPSourceModelConstants.ComponentType.FIELD);
    }

    @Test
    public void ES6SetterMethodComponentTypeIsCorrect() throws Exception {
        final String code = "class Polygon { set height(value) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.set_height").get()
                .componentType() == OOPSourceModelConstants.ComponentType.METHOD);
    }

    @Test
    public void testParsedES6ConstructorParamComponentType() throws Exception {
        final String code = "class Polygon { constructor(height) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor.height")
                .get().componentType() == OOPSourceModelConstants.ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT);
    }

    @Test
    public void ES6InstanceMethodComponentTypeIsCorrect() throws Exception {
        final String code = "class Polygon { say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").get()
                .componentType() == OOPSourceModelConstants.ComponentType.METHOD);
    }
}
