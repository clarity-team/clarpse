package com.clarity.test.javascript;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.File;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure component invocations for a given component are inherited by its
 * parents.
 */
public class InvocationInheritanceTest {

    @Test
    public void ES6ClassInheritsFieldVarInvocations() throws Exception {
        final String code = "import React from 'react'; \nclass Polygon { constructor() {this.height = new React();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("React"));
    }

    @Test
    public void ES6ClassInheritsMethodLocalVarInvocations() throws Exception {
        final String code = "import React from 'react'; \nclass Polygon { test() { let height = new React();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.test")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("React"));
    }

    @Test
    public void ES6ConstructorDoesNotInheritFieldVarInvocations() throws Exception {
        final String code = "import React from 'react'; \nclass Polygon { constructor() {this.height = new React();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor")
                .get().references().size() == 0);
    }

    @Test
    public void ES6ClassInheritsGetterMethodInvocations() throws Exception {
        final String code = "import React from 'react'; \nclass Polygon { get test() {var height = new React();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.get_test")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0)
                .invokedComponent().equals("React"));
    }
}
