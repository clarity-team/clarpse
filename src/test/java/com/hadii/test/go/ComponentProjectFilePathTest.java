package com.hadii.test.go;


import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure components are displaying the correct associated source ProjectFile path.
 */
public class ComponentProjectFilePathTest {

    @Test
    public void testGoStructHasCorrectSourceFileAttr() throws Exception {
        final String code = "package main\ntype person struct {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().sourceFile().equals("/person.go"));
    }

    @Test
    public void testGoStructMethodCorrectSourceFileAttr() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get().sourceFile().equals("/person.go"));
    }

    @Test
    public void testGoInterfaceMethodSourceFileAttr() throws Exception {
        final String code = "package main\n type person interface {\n area() float64 \n} type teacher struct{}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.area() : (float64)")
                .get().sourceFile().equals("/person.go"));
    }

    @Test
    public void testGoInterfaceSourceFileAttr() throws Exception {
        final String code = "package main\n type person interface {\n area() float64 \n} type teacher struct{}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().sourceFile().equals("/person.go"));
    }
}
