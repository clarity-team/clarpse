package com.clarity.test.javascript;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.reference.ComponentReference;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import org.junit.Assert;
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
        rawData.insertFile(new RawFile("polygon.js", code));
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
        rawData.insertFile(new RawFile("polygon.js", code));
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
