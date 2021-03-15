package com.hadii.test.go;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests to ensure package name attribute of parsed components are correct.
 */
public class PackageAttributeTest {

    @Test
    public void testGoStructMethodPackageName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().packageName()
                .equals(generatedSourceModel.getComponent("main.person.x() : (int)").get().packageName()));
    }

    @Test
    public void testGoStructMethodParamPackageName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x(z int) int {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x(int) : (int).z").get().packageName().equals("main"));
    }

    @Test
    public void testGoStructMethodLocalVarPackageName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {var z int}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int).z").get().packageName().equals("main"));
    }

    @Test
    public void testGoInterfacePackageName() throws Exception {
        final String code = "package main\ntype person interface {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().packageName()
                .equals("main"));
    }

    @Test
    public void testGoStructPackageName() throws Exception {
        final String code = "package main\ntype person struct {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().packageName().equals("main"));
    }
}
