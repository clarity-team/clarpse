package com.hadii.test.go;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ResolveImportsTest {
    @Test
    public void testShortImportType() throws Exception {
        final String code = "package main\n import\"fmt\"\n type person struct {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().imports().get(0).equals("fmt"));
    }

    @Test
    public void testLongImportType() throws Exception {
        final String code = "package main\n import m \"fmt\"\n type person struct {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().imports().get(0).equals("fmt"));
    }

    @Test
    public void testImportUsesFullUniquePathIfPossible() throws Exception {
        final String code = "package main\n import g \"github\"\n type person struct {}";
        final String codeB = "package github";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/main.go", code));
        rawData.insertFile(new ProjectFile("/src/http/cakes/github/person.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().imports().get(0).equals("http.cakes.github"));
    }

    @Test
    public void testDotImportType() throws Exception {
        final String code = "package main\n import . \"fmt\"\n type person struct {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().imports().get(0).equals("fmt"));
    }
}
