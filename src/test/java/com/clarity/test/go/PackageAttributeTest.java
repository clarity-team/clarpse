package com.clarity.test.go;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.File;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests to ensure package name attribute of parsed components are correct.
 */
public class PackageAttributeTest {

    @Test
    public void testGoStructMethodPackageName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().packageName()
                .equals(generatedSourceModel.getComponent("main.person.x() : (int)").get().packageName()));
    }

    @Test
    public void testGoStructMethodParamPackageName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x(z int) int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x(int) : (int).z").get().packageName().equals("main"));
    }

    @Test
    public void testGoStructMethodLocalVarPackageName() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {var z int}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int).z").get().packageName().equals("main"));
    }

    @Test
    public void testGoInterfacePackageName() throws Exception {
        final String code = "package main\ntype person interface {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("src/main/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().packageName()
                .equals("main"));
    }

    @Test
    public void testGoStructPackageName() throws Exception {
        final String code = "package main\ntype person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().packageName().equals("main"));
    }
}
