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
* Tests to ensure module imports are resolved properly.
*/
public class SimpleTypeReferenceTest {

    /**
     * Simple type reference from single named import
     */
   @Test
   public void ConstructorMethodCallTest() throws Exception {
       final String codeA = "export class Polygon { }";
       final String codeB = "import { Polygon } from \'../shapes/polygon\'; \n class Cake { constructor() {  Polygon.test(); } }";
       final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
       rawData.insertFile(new File("com/shapes/polygon.js", codeA));
       rawData.insertFile(new File("com/types/cake.js", codeB));
       final ClarpseProject parseService = new ClarpseProject(rawData);
       final OOPSourceCodeModel generatedSourceModel = parseService.result();
       assertTrue(generatedSourceModel.getComponent("com.types.Cake.constructor").get()
               .references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent()
               .equals("com.shapes.Polygon"));
   }

    /**
     * Simple type reference from double named import
     */
    @Test
    public void ConstructorMethodCallComplexTest() throws Exception {
        final String codeA = "export class Polygon { }; \n export class Cuppy {};";
        final String codeB = "import { Polygon, Cuppy } from \'polygon.js\';  \n class Cake { constructor() {  Polygon.test(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", codeA));
        rawData.insertFile(new File("cake.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Cake.constructor").get()
                .references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent()
                .equals("Polygon"));
    }

    /**
     * Test case: import { export1, export2 } from "module-name";
     */
    @Test
    public void ES6SimpleNamedExternalImportTest() throws Exception {
        final String codeB = "import Button from \'components/Button\'; \n class Cake { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("cake.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Button"));
    }

    /**
     * Test case: import defaultExport, {export1 as alias1} from "module-name";
     */
    @Test
    public void ES6DefaultImportWithNamedImportWithAliasTest() throws Exception {
        final String codeB = "import Coin, { Button as button } from \'components/Button\'; \n class Cake { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("cake.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Button"));
        assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Coin"));
    }

    /**
     * Test case: import { export1 as alias1 } from "module-name";
     */
    @Test
    public void ES6NamedImportWithAliasTest() throws Exception {
        final String codeB = "import { Button as button } from \'components/Button\'; \n class Cake { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("cake.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Button"));
    }

    /**
     * Test case: import { export1 , export2 as alias2 , [...] } from "module-name";
     */
    @Test
    public void ES6NamedImportsWithAndWithoutAliasTest() throws Exception {
        final String codeB = "import { CuppyCake, Button as button } from \'components/Button\'; \n class Cake { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("cake.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Button"));
        assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.CuppyCake"));
    }

    /**
     * Test case: import * from "module-name";
     */
    @Test
    public void ES6AsteriskImportNotSupportedTest() throws Exception {
        final String codeB = "import * from \'components/Button\'; \n class Cake { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("cake.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Cake").get().imports().size() == 0);
    }

    /**
     * Test case: import * as name from "module-name";
     */
    @Test
    public void ES6AliasedAsteriskImportNotSupportedTest() throws Exception {
        final String codeB = "import * as Test from \'components/Button\'; \n class Cake { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("cake.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Cake").get().imports().size() == 0);
    }

    /**
     * Test case: import "module-name";
     */
    @Test
    public void ES6ModuleImportOnlyNotSupportedTest() throws Exception {
        final String codeB = "import \'components/Button\'; \n class Cake { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("cake.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Cake").get().imports().size() == 0);
    }

    @Test
    public void LocalLetVariableTypeDeclaration() throws Exception {
        final String code = "class Polygon { say() { let test = new React(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent().equals("React"));
    }

    @Test
    public void LocalVariableTypeInstantiation() throws Exception {
        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent().equals("React"));
    }

    @Test
    public void MethodTypeDeclarationFromStaticMethodCall() throws Exception {
        final String code = "import { React } from \'github/react.js\'; \n class Polygon { constructor() {  React.test(); } }";
        final String codeB = "class React {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/src/test/polygon.js", code));
        rawData.insertFile(new File("/src/test/github/react.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("src.test.Polygon.constructor")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent()
                .equals("src.test.github.React"));
    }

    @Test
    public void testResolvingOfAbsoluteImportPath() throws Exception {
        final String code = "import { React } from \'/src/test/github/react.js\'; \n class Polygon { constructor() {  React.test(); } }";
        final String codeB = "class React {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/src/test/polygon.js", code));
        rawData.insertFile(new File("/src/test/github/react.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("src.test.Polygon.constructor")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent()
                .equals("src.test.github.React"));
    }

    @Test
    public void testResolvingOfAliasImportType() throws Exception {
        final String code = "import { React as LoL } from \'/src/test/github/react.js\'; \n class Polygon { constructor() {  LoL.test(); } }";
        final String codeB = "class React {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/src/test/polygon.js", code));
        rawData.insertFile(new File("/src/test/github/react.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("src.test.Polygon.constructor")
                .get().references(OOPSourceModelConstants.TypeReferences.SIMPLE).get(0).invokedComponent()
                .equals("src.test.github.React"));
    }
}
