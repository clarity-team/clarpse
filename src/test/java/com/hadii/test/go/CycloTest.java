package com.hadii.test.go;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.Component;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Tests accuracy of Component cyclomatic complexity attribute. See {@link Component}.
 */
public class CycloTest extends GoTestBase{

    @Test
    public void testGoInterfaceMethodComplexity() throws Exception {
        final String code = "package main\n type person interface {\n area() float64 \n} type teacher struct{}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person.area() : (float64)")
                .get()
                .cyclo() == 0);
    }

    @Test
    public void testGoMethodComplexity() throws Exception {
        final String code = "package main\ntype person struct {} \n " +
                "func (p person) x() int {" +
                "    for i := 0; i < 10; i++ {\n" +
                "      if 7%2 == 0 && true {\n" +
                "        // && || \n" +
                "        fmt.Println(\"7 is even\")\n" +
                "    } else {\n" +
                "        fmt.Println(\"7 is odd\")\n" +
                "    } \n " +
                "   }" +
                "}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get().cyclo() == 5);
    }

    @Test
    public void testEmptyGoMethodComplexity() throws Exception {
        final String code = "package main\ntype person struct {} \n " +
                "func (p person) x() int {}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get().cyclo() == 1);
    }


    @Test
    public void testGoMethodExprSwitchComplexity() throws Exception {
        final String code = "package main\ntype person struct {} \n func (p person) x() int { " +
                "switch os := runtime.GOOS; os {\n" +
                "case \"darwin\":\n" +
                "fmt.Println(\"OS X.\")\n" +
                "case \"linux\":\n" +
                "fmt.Println(\"Linux.\")\n" +
                "default:\n" +
                "// freebsd, openbsd,\n" +
                "// plan9, windows...\n" +
                "fmt.Printf(\"%s.\", os)\n" +
                "}" +
                "}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get().cyclo() == 3);
    }

    @Test
    public void testGoMethodTypeSwitchComplexity() throws Exception {
        final String code = "package main\nimport \"fmt\"\ntype person struct {} \n func (p person) x() int { " +
                "switch v := i.(type) {\n" +
                "case int:\n" +
                "fmt.Printf(\"Twice %v is %v\\n\", v, v*2)\n" +
                "case string:\n" +
                "fmt.Printf(\"%q is %v bytes long\\n\", v, len(v))\n" +
                "default:\n" +
                "fmt.Printf(\"I don't know about type %T!\\n\", v)\n" +
                "}" +
                "}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get().cyclo() == 3);
    }

    @Test
    public void testGoStructComplexity() throws Exception {
        final String code = "package main\nimport \"fmt\"\ntype person struct {} \n " +
                "func (p person) x() int { " +
                "switch v := i.(type) {\n" +
                "case int:\n" +
                "fmt.Printf(\"Twice %v is %v\\n\", v, v*2)\n" +
                "case string:\n" +
                "fmt.Printf(\"%q is %v bytes long\\n\", v, len(v))\n" +
                "default:\n" +
                "fmt.Printf(\"I don't know about type %T!\\n\", v)\n" +
                "} }" +
                "func (p person) z() int {" +
                "    if 7%2 == 0 && true {\n" +
                "        // && || \n" +
                "        fmt.Println(\"7 is even\")\n" +
                "    } else {\n" +
                "        fmt.Println(\"7 is odd\")\n" +
                "    } " +
                "}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person").get().cyclo() == 3);
    }

    @Test
    public void testGoEmptyStructComplexity() throws Exception {
        final String code = "package main\nimport \"fmt\"\ntype person struct {} \n " +
                "}";
        projectFiles.insertFile(new ProjectFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(projectFiles);
        final OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person").get().cyclo() == 0);
    }

}
