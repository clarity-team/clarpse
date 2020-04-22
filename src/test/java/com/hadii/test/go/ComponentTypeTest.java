package com.hadii.test.go;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.File;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.SourceFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests to ensure component type attribute of parsed components are accurate.
 */
public class ComponentTypeTest {
    @Test
    public void testInterfaceAnonymousTypeMethodParamType() throws Exception {

        final String code = "package main \n type plain interface \n{ testMethodv2(x value, h int) (value, uintptr) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2(value, int) : (value, uintptr).x")
                .get().componentType() == OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void testGoStructMethodSingleParamComponentType() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) lol(x int) {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.lol(int).x")
                .get().componentType() == OOPSourceModelConstants.ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void testGoInterfaceComponentType() throws Exception {
        final String code = "package main\n type person interface {\n area() float64 \n} type teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get()
                .componentType() == OOPSourceModelConstants.ComponentType.INTERFACE);
    }

    @Test
    public void testGoStructMethodComponentType() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get()
                .componentType() == OOPSourceModelConstants.ComponentType.METHOD);
    }

    @Test
    public void testGoStructFieldVarComponenType() throws Exception {
        final String code = "package main\nimport \"test/math\"\ntype person struct {mathObj math.Person}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.mathObj").get()
                .componentType() == OOPSourceModelConstants.ComponentType.FIELD);
    }

    @Test
    public void testGoStructHasCorrectComponentType() throws Exception {
        final String code = "package main\ntype person struct {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").get()
                .componentType() == OOPSourceModelConstants.ComponentType.STRUCT);
    }


    @Test
    public void testInterfaceMethodSpecComponentType() throws Exception {

        final String code = "package main\ntype person interface { \n//test\n testMethod() int}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.testMethod() : (int)").get()
                .componentType() == OOPSourceModelConstants.ComponentType.METHOD);
    }

    @Test
    public void localVarComponentType() throws Exception {

        final String code = "package main \n type plain struct \n{} \n func (t plain) testMethodv2 () {\n var i int  = 2;\n}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("/src/main/plain.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(
                generatedSourceModel.getComponent("main.plain.testMethodv2().i").get()
                        .componentType() == OOPSourceModelConstants.ComponentType.LOCAL);
    }
}
