package com.clarity.test.go;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AccessModifiersTest {

    @Test
    public void testParseGoStructPrivateVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type Teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().modifiers().contains("private"));
    }

    @Test
    public void testGoStructFieldVarPrivateVisibility() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").get().modifiers().contains("private"));
    }

    @Test
    public void testParseGoStructPublicVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ \n type person struct {} type Teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Teacher").get().modifiers().contains("public"));
    }

    @Test
    public void testGoStructFieldVarPublicVisibility() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {MathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.MathObj").get().modifiers().contains("public"));
    }

    @Test
    public void testParseGoInterfacePublicVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type Person interface {} type Teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person").get().modifiers().contains("public"));
    }

    @Test
    public void testParseGoInterfacePrivateVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person interface {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().modifiers().contains("private"));
    }
}
