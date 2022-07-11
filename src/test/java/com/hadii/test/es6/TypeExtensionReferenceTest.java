package com.hadii.test.es6;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure component type extensions invocations are accurate.
 */
public class TypeExtensionReferenceTest {

    @Test
    public void testIfParseClassHasCorrectExtendsAttr() throws Exception {
        final String code = "class Shape {} \n class Polygon extends Shape { }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        // assert the Polygon class component has one type extension component
        // invocation
        assertTrue(generatedSourceModel.getComponent("Polygon").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .size() == 1);
        // assert the component being extended is the Shape class
        assertTrue(generatedSourceModel.getComponent("Polygon").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .get(0).invokedComponent().equals("Shape"));
    }

    @Test
    public void testIfParseClassHasCorrectExtendsAttrComplex() throws Exception {
        final String codea = "export default class Shape { }";
        final String codeb = "import {Shape as Shape} from 'test/shape' \n class Polygon extends Shape { }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("/test/shape.js", codea));
        rawData.insertFile(new ProjectFile("polygon.js", codeb));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        // assert the Polygon class component has one type extension component
        // invocation
        assertTrue(generatedSourceModel.getComponent("Polygon").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .size() == 1);
        // assert the component being extended is the Shape class
        assertTrue(generatedSourceModel.getComponent("Polygon").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .get(0).invokedComponent().equals("test.Shape"));
    }


    @Test
    public void testIfParseClassHasCorrectExtendsAttrComplexv2() throws Exception {
        final String codea = "export default shape = class Shape { }";
        final String codeb = "import {shape as Shape} from 'test/shape' \n class Polygon extends Shape { }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new ProjectFile("/test/shape.js", codea));
        rawData.insertFile(new ProjectFile("polygon.js", codeb));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        // assert the Polygon class component has one type extension component
        // invocation
        assertTrue(generatedSourceModel.getComponent("Polygon").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .size() == 1);
        // assert the component being extended is the Shape class
        assertTrue(generatedSourceModel.getComponent("Polygon").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .get(0).invokedComponent().equals("test.shape"));
    }

}
