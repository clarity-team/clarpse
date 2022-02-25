package com.hadii.test.go;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CodeFragmentTest {

    @Test
    public void testInterfaceMethodSpecCodeFragment() throws Exception {
        final String code = "package main\ntype person interface { testMethod() int}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.testMethod() : (int)").get().codeFragment().equals("testMethod() : (int)"));
    }

    @Test
    public void testGoReturnStructMethodComplexCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import \"main\" \n func (p main.Person)" +
            " x(args []value, x,y map[value]value, v, u value) (j,i []value, map[value]value.test)  {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        rawData.insertFile(new ProjectFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.x([]value, " +
                                                                 "map[value]value, map[value]value, value, value) : ([]value, []value, map[value]value.test)").get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.x([]value, map[value]value, map[value]value, value, value) : ([]value, []value, map[value]value.test)").get().codeFragment().equals("x([]value, map[value]value, map[value]value, value, value) : ([]value, []value, map[value]value.test)"));
        assertTrue(generatedSourceModel.getComponent("main.Person.x([]value, map[value]value, map[value]value, value, value) : ([]value, []value, map[value]value.test).u")
                .get().parentUniqueName().equals("main.Person.x([]value, map[value]value, map[value]value, value, value) : ([]value, []value, map[value]value.test)"));
        assertTrue(generatedSourceModel.getComponent("main.Person.x([]value, map[value]value, map[value]value, value, value) : ([]value, []value, map[value]value.test)")
                .get().parentUniqueName().equals("main.Person"));
    }

    @Test
    public void testInterfaceComplexMethodSpecParamsExist() throws Exception {
        final String code = "package go\n import \"game/org\" \n type person interface { testMethod() org.Cake}";
        final String codeB = "package org\n type Cake struct {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("src/github/go/person.go", code));
        rawData.insertFile(new ProjectFile("src/github/game/org/cakes.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.go.person.testMethod() : (org.Cake)").get().codeFragment()
                .equals("testMethod() : (org.Cake)"));
    }

    @Test
    public void fieldVarCodeFragment() throws Exception {

        final String code = "package main\n import\"fmt\"\n type person struct {SuggestFor []value}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.SuggestFor").get().codeFragment()
                .equals("SuggestFor : []value"));
    }

    @Test
    public void structMethodCodeFragment() throws Exception {

        final String code = "package main\n import\"fmt\"\n type Command struct {} func (c *Command) SetHelpCommand(cmd *Command) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Command.SetHelpCommand(*Command)")
                .get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Command.SetHelpCommand(*Command)")
                .get().codeFragment().equals("SetHelpCommand(*Command)"));
    }


    @Test
    public void fieldVarCodeFragmentWithComment() throws Exception {

        final String code = "package main\n import\"fmt\"\n type person struct {usageFunc func(*Command) error" +
                " // Usage can be defined by application\n}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person.usageFunc").get().codeFragment()
                .equals("usageFunc : func(*Command) error"));
    }

    @Test
    public void fieldVarFuncTypeCodeFragment() throws Exception {

        final String code = "package main\n type person struct { PersistentPreRun func(cmd *Command, args []value) }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.person.PersistentPreRun").get()
                .codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.person.PersistentPreRun").get()
                .codeFragment().equals("PersistentPreRun : func(cmd *Command, args []value)"));
    }

    @Test
    public void fieldVarFuncTypeCodeFragmentWithComment() throws Exception {

        final String code = "package main\n type person struct { PersistentPreRun func(cmd *Command, args []value)//test \n}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.person.PersistentPreRun")
                .get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.person.PersistentPreRun").get()
                .codeFragment().equals("PersistentPreRun : func(cmd *Command, args []value)"));
    }

    @Test
    public void testInterfaceAnonymousTypeMethods() throws Exception {
        final String code = "package main \n type plain interface \n{ testMethodv2() (value, uintptr) {} }";
        final String codeB = "package main\n type Person struct {}";

        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/plain.go", code));
        rawData.insertFile(new ProjectFile("/src/main/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.plain.testMethodv2() : (value, uintptr)").get().codeFragment().equals("testMethodv2() : (value, uintptr)"));
    }

    @Test
    public void fieldVarFuncTypeCodeFragmentv2() throws Exception {

        final String code = "package main\n type person struct { globNormFunc func(f *flag.FlagSet, name value)" +
                " flag.NormalizedName }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.person.globNormFunc").get()
                .codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.person.globNormFunc").get().codeFragment()
                .equals("globNormFunc : func(f *flag.FlagSet, name value) flag.NormalizedName"));
    }

    @Test
    public void testGoStructMethodCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() int {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        rawData.insertFile(new ProjectFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.x() : (int)").get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (int)").get().codeFragment()
                .equals("x() : (int)"));
    }

    @Test
    public void testGoStructMethodSpecCodeFragment() throws Exception {
        final String code = "package main\ntype Person interface {  Get(key interface{}) (value interface{}, ok bool) }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.Get(interface{}) : (interface{}, bool)").get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.Get(interface{}) : (interface{}, bool)")
                .get().codeFragment().equals("Get(interface{}) : (interface{}, bool)"));
    }

    @Test
    public void testGoStructMethodWithFuncAsParamCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {  func (c *Person) SetGlobalNormalizationFunc(n func(f " +
                "*flag.FlagSet, name value)) {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name value))").get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name value))").get().codeFragment()
                .equals("SetGlobalNormalizationFunc(func(f *flag.FlagSet, name value))"));
    }

    @Test
    public void testGoInterfaceMethodWithFuncAsParamCodeFragment() throws Exception {
        final String code = "package main\ntype Person interface {  func SetGlobalNormalizationFunc(n func(f *flag.FlagSet, name value)) }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name value))").get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name value))").get().codeFragment()
                .equals("SetGlobalNormalizationFunc(func(f *flag.FlagSet, name value))"));

        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(func(f *flag.FlagSet, name value))").get().parentUniqueName()
                .equals("main.Person"));
    }

    @Test
    public void testGoStructMethodWithFuncAsReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {  func (c *Person) SetGlobalNormalizationFunc(n int) (func(f *flag.FlagSet, name value)) {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(int) : (func(f *flag.FlagSet, name value))").get().codeFragment().equals(
                "SetGlobalNormalizationFunc(int) : (func(f *flag.FlagSet, name value))"));
    }

    @Test
    public void testGoStructMethodWithFuncAsPartOfReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {  func (c *Person) SetGlobalNormalizationFunc(n int) (x int, func(f *flag.FlagSet, name value)) {} }";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.SetGlobalNormalizationFunc(int) : (int, func(f *flag.FlagSet, name value))").get().codeFragment().equals(
                "SetGlobalNormalizationFunc(int) : (int, func(f *flag.FlagSet, name value))"));
    }

    @Test
    public void testGoStructMethodMultipleDeclarationReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (x,y int) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        rawData.insertFile(new ProjectFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (int, int)").get().codeFragment().equals("x() : (int, int)"));
    }

    @Test
    public void testGoStructMethodMultipleIndividualDeclarationReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (x uint8,y int) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        rawData.insertFile(new ProjectFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.x() : (uint8, int)").get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (uint8, int)").get().codeFragment().equals("x() : (uint8, int)"));
    }

    @Test
    public void testGoStructMethodMultipleComplexDeclarationReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (x,z uint8, y int) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        rawData.insertFile(new ProjectFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (uint8, uint8, int)").get().codeFragment().equals("x() : (uint8, uint8, int)"));
    }

    @Test
    public void testGoStructMethodMultipleReturnCodeFragment() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p tester.Person) x() (value, int) {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        rawData.insertFile(new ProjectFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Person.x() : (value, int)").get().codeFragment().equals("x() : (value, int)"));
    }

    @Test
    public void testGoNoReturnStructMethodCodeFragmentIsNull() throws Exception {
        final String code = "package main\ntype Person struct {}";
        final String codeB = "package main\n import tester \"main\" \n func (p main.Person) x() {}";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("/src/main/cherry.go", code));
        rawData.insertFile(new ProjectFile("/src/com/cakes/test.go", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        System.out.println(generatedSourceModel.getComponent("main.Person.x()").get().codeFragment());
        assertTrue(generatedSourceModel.getComponent("main.Person.x()").get().codeFragment().equals("x()"));
    }
}
