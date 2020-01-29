package com.clarity.test.go;


import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure components are displaying the correct associated source file path.
 */
public class ComponentSourceFilePathTest {

    @Test
    public void testGoStructHasCorrectSourceFileAttr() throws Exception {
        final String code = "package main\ntype person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get().sourceFile().equals("person.go"));
    }

    @Test
    public void testGoStructMethodCorrectSourceFileAttr() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get().sourceFile().equals("person.go"));
    }

    @Test
    public void testGoInterfaceMethodSourceFileAttr() throws Exception {
        final String code = "package main\n type person interface {\n area() float64 \n} type teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.area() : (float64)")
                .get().sourceFile().equals("person.go"));
    }

    @Test
    public void testGoInterfaceSourceFileAttr() throws Exception {
        final String code = "package main\n type person interface {\n area() float64 \n} type teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person")
                .get().sourceFile().equals("person.go"));
    }
}
