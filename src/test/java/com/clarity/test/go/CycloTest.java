package com.clarity.test.go;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.File;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Tests accuracy of Component cyclomatic complexity attribute. See {@link com.clarity.sourcemodel.Component}.
 */
public class CycloTest {

    @Test
    public void testGoInterfaceMethodComplexity() throws Exception {
        final String code = "package main\n type person interface {\n area() float64 \n} type teacher struct{}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person.x() : (int)").get().cyclo() == 5);
    }

    @Test
    public void testEmptyGoMethodComplexity() throws Exception {
        final String code = "package main\ntype person struct {} \n " +
                "func (p person) x() int {}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
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
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person").get().cyclo() == 3);
    }

    @Test
    public void testGoEmptyStructComplexity() throws Exception {
        final String code = "package main\nimport \"fmt\"\ntype person struct {} \n " +
                "}";
        final SourceFiles rawData = new SourceFiles(Lang.GOLANG);
        rawData.insertFile(new File("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("main.person").get().cyclo() == 0);
    }

}
