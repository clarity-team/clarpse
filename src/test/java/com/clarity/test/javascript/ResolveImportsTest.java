package com.clarity.test.javascript;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.File;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

 /**
 * Tests to ensure module imports are resolved properly.
 */
public class ResolveImportsTest {

    /**
    * Test case:
    *   Exporting Module: export class export1 {}
    *   Importing Module: import { export1 } from "module-name/path/to/specific/un-exported/file";
    */
    @Test
    public void SingleNamedLocalImportTest() throws Exception {
        final String codeA = "export class Polygon { }";
        final String codeB = "import { Polygon } from \'../shapes/polygon\'; \n class Cake { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("com/shapes/polygon.js", codeA));
        rawData.insertFile(new File("com/types/cake.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("com.types.Cake").get().imports().contains("com.shapes.Polygon"));
    }

     /**
      * Test case:
      *     Exporting Module: class export1 {} export { export1 as export2}
      *     Importing Module: import { export2 } from "/../file";
      */
     @Test
     public void AliasExportAndNamedLocalImportTest() throws Exception {
         final String codeA = "class Polygon { } export { Polygon as Triangle };";
         final String codeB = "import { Triangle } from \'../shapes/polygon\'; \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("com/shapes/polygon.js", codeA));
         rawData.insertFile(new File("com/types/cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("com.types.Cake").get().imports().contains("com.shapes.Polygon"));
     }


     /**
      * Test case:
      *     Exporting Module: export default class {}
      *     Importing Module: import export2 from "/../file";
      */
     @Test
     public void UnnamedDefaultExport() throws Exception {
         final String code = "export default class { }";
         final String codeB = "import Cakes from \'./test\'; class Muffin {}";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("/src/github/test.js", code));
         rawData.insertFile(new File("/src/github/muffin.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("src.github.Muffin").get().imports()
         .contains("src.github.test"));
     }

     /**
      * Test case
      *     *   Exporting Module: export class export1 {} export class export2 {}
      *     *   Importing Module: import { export1, .. } from ".././file";
      */
     @Test
     public void MultipleNamedLocalImportTest() throws Exception {
         final String codeA = "export class Polygon { }; \n export class Cuppy {};";
         final String codeB = "import { Polygon, Cuppy } from \'polygon.js\';  \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("polygon.js", codeA));
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("Polygon"));
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("Cuppy"));
     }

     /**
      * Test case
      *     *   Exporting Module: class export1 {} class export2 {} export {export1, export2 as export3, export4}
      *     *   Importing Module: import { export3, export4 } from ".././file";
      */
     @Test
     public void AliasExportAndMultipleNamedLocalImportTest() throws Exception {
         final String codeA = "class Lemo { } class Choco {} export {Lemo as Nade, Choco as Late};";
         final String codeB = "import { Nade, Late } from \'polygon.js\';  \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("polygon.js", codeA));
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("Lemo"));
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("Choco"));
     }

     @Test
     public void MultipleSimilarNamedLocalImportTest() throws Exception {
         final String codeA = "export class Polygon { };";
         final String codeB = "import { Polygon } from \'test/polygon.js\';  \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("/test/polygon.js", codeA));
         rawData.insertFile(new File("test/test/polygon.js", codeA));
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("test.Polygon"));
         assertTrue(!generatedSourceModel.getComponent("Cake").get().imports().contains("test.test.Polygon"));
     }

     /**
      * Test case
      *     *   Importing Module: import { export1, export2 } from "module-name";
      */
     @Test
     public void SimpleNamedExternalImportTest() throws Exception {
         final String codeB = "import Button from \'components/Button\'; \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Button"));
     }

     /**
      * Test case
      *     *   Importing Module: import defaultExport, {export1 as alias1} from "module-name";
      */
     @Test
     public void DefaultImportWithNamedImportWithAliasTest() throws Exception {
         final String codeB = "import Coin, { Button as button } from \'components/Button\'; \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Button"));
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Coin"));
     }

     /**
      * Test case
      *     *   Exporting Module: export default class export1 {}
      *     *   Importing Module: import export2 from "module-name";
      */
     @Test
     public void DefaultExportAliasImportTest() throws Exception {
         final String codeB = "export default class Cake { };";
         final String codeC = "import Muffin from \'tester/ingredients/cake.js\' class Dessert { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("tester/ingredients/cake.js", codeB));
         rawData.insertFile(new File("dessert.js", codeC));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Dessert").get().imports().contains("tester.ingredients.Cake"));
     }

     /**
      * Test case
      *     *   Importing Module: import defaultExport from "module-name";
      */
     @Test
     public void DefaultImportTest() throws Exception {
         final String codeB = "import Coin from \'components/Button\'; \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Coin"));
     }

     /**
      * Test case
      *     *   Exporting Module: export export1
      *     *   Importing Module: import { export1 as alias1 } from "module-name";
      */
     @Test
     public void NamedImportWithAliasTest() throws Exception {
         final String codeB = "import { Button as button } from \'components/Button\'; \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Button"));
     }

     /**
      * Test case
      *     *   Exporting Module: class export1{} export { export1 as default}
      *     *   Importing Module: import export2 from "module-name";
      */
     @Test
     public void ComplexDefaultExportAndImportTest() throws Exception {
         final String codeB = "class Cake { } \n export { Cake as default } ";
         final String codeC = "import Muffin from \'cake.js\' class Dessert { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("cake.js", codeB));
         rawData.insertFile(new File("dessert.js", codeC));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Dessert").get().imports().contains("Cake"));
     }

     /**
      * Test case
      *     *   Exporting Module: export export1
      *     *   Importing Module: import { export1 , export2 as alias2 , [...] } from "module-name";
      */
     @Test
     public void NamedImportsWithAndWithoutAliasTest() throws Exception {
         final String codeB = "import { CuppyCake, Button as button } from \'components/Button\'; \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.Button"));
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().contains("components.CuppyCake"));
     }

     /**
      * Test case
      *     *   Exporting Module:
      *     *   Importing Module: import * from "module-name";
      */
     @Test
     public void AsteriskImportNotSupportedTest() throws Exception {
         final String codeB = "import * from \'components/Button\'; \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().size() == 0);
     }

     /**
      * Test case
      *     *   Exporting Module: export export1
      *     *   Importing Module: import * as name from "module-name";
      */
     @Test
     public void AliasedAsteriskImportNotSupportedTest() throws Exception {
         final String codeB = "import * as Test from \'components/Button\'; \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().size() == 0);
     }

     /**
      * Test case
      *     *   Exporting Module: export export1
      *     *   Importing Module: import "module-name";
      */
     @Test
     public void ModuleImportOnlyNotSupportedTest() throws Exception {
         final String codeB = "import \'components/Button\'; \n class Cake { }";
         final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
         rawData.insertFile(new File("cake.js", codeB));
         final ClarpseProject parseService = new ClarpseProject(rawData);
         final OOPSourceCodeModel generatedSourceModel = parseService.result();
         assertTrue(generatedSourceModel.getComponent("Cake").get().imports().size() == 0);
     }
}
