package com.hadii.test.javascript;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.File;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.SourceFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.Component;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Tests accuracy of Component cyclomatic complexity attribute. See {@link Component}.
 */
public class CycloTest {

    @Test
    public void testES6AndIfCyclo() throws Exception {
        final String code = "class Polygon { constructor() {  if (true == false && false == true || true) {} } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/src/test/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("src.test.Polygon.constructor")
                .get().cyclo() == 4);
    }

    @Test
    public void testES6HookCyclo() throws Exception {
        final String code = "class Polygon { constructor() {  x = true ? true : false } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/src/test/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("src.test.Polygon.constructor")
                .get().cyclo() == 2);
    }

    @Test
    public void testEmptyMethodCyclo() throws Exception {
        final String code = "class Polygon { constructor() { } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/src/test/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("src.test.Polygon")
                .get().cyclo() == 1);
    }

    @Test
    public void testES6ClassCyclo() throws Exception {
        final String code = "class Polygon { say() {  if (true == false && false == true || true) {} }  " +
                "bye() {  x = true ? true : false } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/src/test/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("src.test.Polygon")
                .get().cyclo() == 3);
    }

    @Test
    public void testES6SwitchStatementCyclo() throws Exception {
        final String code = "class Polygon { say() {  switch(expression) {\n" +
                "  case x:\n" +
                "    // code block\n" +
                "    break;\n" +
                "  case y:\n" +
                "    // code block\n" +
                "    break;\n" +
                "  default:\n" +
                "    // code block\n" +
                "} } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new File("/src/test/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("src.test.Polygon.say")
                .get().cyclo() == 4);
    }
}
