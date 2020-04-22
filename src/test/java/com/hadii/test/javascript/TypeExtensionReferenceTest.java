package com.hadii.test.javascript;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.File;
import com.hadii.clarpse.compiler.SourceFiles;
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
        final String code = "class Polygon extends Shape { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
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
        final String code = "import { Shape as Shape} from 'test/shape' \n class Polygon extends Shape { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        // assert the Polygon class component has one type extension component
        // invocation
        assertTrue(generatedSourceModel.getComponent("Polygon").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .size() == 1);
        // assert the component being extended is the Shape class
        assertTrue(generatedSourceModel.getComponent("Polygon").get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                .get(0).invokedComponent().equals("test.Shape"));
    }

}
