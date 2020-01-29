package com.clarity.test.go;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ResolveImportsTest {
    @Test
    public void testShortImportType() throws Exception {
        final String code = "package main\n import\"fmt\"\n type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().imports().get(0).equals("fmt"));
    }

    @Test
    public void testLongImportType() throws Exception {
        final String code = "package main\n import m \"fmt\"\n type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().imports().get(0).equals("fmt"));
    }

    @Test
    public void testImportUsesFullUniquePathIfPossible() throws Exception {
        final String code = "package main\n import g \"github\"\n type person struct {}";
        final String codeB = "package github";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("/src/main/main.go", code));
        rawData.insertFile(new RawFile("/src/http/cakes/github/person.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().imports().get(0).equals("http.cakes.github"));
    }

    @Test
    public void testDotImportType() throws Exception {
        final String code = "package main\n import . \"fmt\"\n type person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().imports().get(0).equals("fmt"));
    }
}
