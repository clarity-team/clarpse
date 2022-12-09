package com.hadii.test.java;


import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
/**
 * Ensure components are displaying the correct associated source ProjectFile path.
 */
public class ComponentProjectFilePathTest {

    private static final String SOURCEFILE1PACKAGE = "com.foo.test";
    private static final String SOURCEFILE1NAME = "/com/foo/test/SourceFile1.java";
    private static final String SOURCEFILE1CODESTRING = "package " + SOURCEFILE1PACKAGE + ";"
            + "import java.lang.String; "
            + "public class TestA { "
            + "public void methodA () { } "
            + "}"
            + " public abstract class TestB {"
            + " private String methodB();"
            + " }";

    private static final String SOURCEFILE2PACKAGE = "com.foo.test.lol";
    private static final String SOURCEFILE2NAME = "/com/foo/test/SourceFile2.java";
    private static final String SOURCEFILE2CODESTRING = "package " + SOURCEFILE2PACKAGE + "; "
            + "import java.lang.String; "
            + "public class TestC { "
            + "public void methodC () { } "
            + " public abstract class TestD {"
            + " private String methodD();"
            + " }"
            + "}";

    private static OOPSourceCodeModel sourceCodeModel;

    @BeforeClass
    public static void setup() throws Exception {
        final ProjectFile file1 = new ProjectFile(SOURCEFILE1NAME, SOURCEFILE1CODESTRING);
        final ProjectFile file2 = new ProjectFile(SOURCEFILE2NAME, SOURCEFILE2CODESTRING);
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(file1);
        rawData.insertFile(file2);
        final ClarpseProject parseService = new ClarpseProject(rawData.files(), rawData.lang());
        sourceCodeModel = parseService.result().model();
    }

    @Test
    public void testClassAComponentHasCorrectSourceFilePath() {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE1PACKAGE + ".TestA").get();
        assertTrue(component.sourceFile().equals(SOURCEFILE1NAME));
    }

    @Test
    public void testClassAMethodAComponentHasCorrectSourceFilePath() {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE1PACKAGE + ".TestA.methodA()").get();
        assertTrue(component.sourceFile().equals(SOURCEFILE1NAME));
    }

    @Test
    public void testAbstractClassBComponentHasCorrectSourceFilePath() {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE1PACKAGE + ".TestB").get();
        assertTrue(component.sourceFile().equals(SOURCEFILE1NAME));
    }

    @Test
    public void testAbstractClassBMethodBComponentHasCorrectSourceFilePath() {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE1PACKAGE
                + ".TestB.methodB()").get();
        assertTrue(component.sourceFile().equals(SOURCEFILE1NAME));
    }

    @Test
    public void testClassCComponentHasCorrectSourceFilePath() {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE2PACKAGE + ".TestC").get();
        assertTrue(component.sourceFile().equals(SOURCEFILE2NAME));
    }

    @Test
    public void testClassCMethodCComponentHasCorrectSourceFilePath() {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE2PACKAGE + ".TestC.methodC()").get();
        assertTrue(component.sourceFile().equals(SOURCEFILE2NAME));
    }

    @Test
    public void testClassCAbstractClassDComponentHasCorrectSourceFilePath() {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE2PACKAGE + ".TestC.TestD").get();
        assertTrue(component.sourceFile().equals(SOURCEFILE2NAME));
    }

    @Test
    public void testClassCAbstractClassDMethodDComponentHasCorrectSourceFilePath() {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE2PACKAGE
                + ".TestC.TestD.methodD()").get();
        assertTrue(component.sourceFile().equals(SOURCEFILE2NAME));
    }
}
