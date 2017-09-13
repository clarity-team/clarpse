package com.clarity.test;


import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ClarpseProject;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
/**
 * Ensure components are displaying the correct associated source file path.
 *
 * @author Muntazir Fadhel
 */
public class ComponentSourceFilePathTest {

    private static final String SOURCEFILE1PACKAGE = "com.foo.test";
    private static final String SOURCEFILE1NAME = "com/foo/test/SourceFile1.java";
    private static final String SOURCEFILE1CODESTRING = "package " + SOURCEFILE1PACKAGE + ";"
            + "import java.lang.String; "
            + "public class TestA { "
            + "public void methodA () { } "
            + "}"
            + " public abstract class TestB {"
            + " private String methodB();"
            + " }";

    private static final String SOURCEFILE2PACKAGE = "com.foo.test.lol";
    private static final String SOURCEFILE2NAME = "com/foo/test/SourceFile2.java";
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
        final RawFile rawFile1 = new RawFile(SOURCEFILE1NAME, SOURCEFILE1CODESTRING);
        final RawFile rawFile2 = new RawFile(SOURCEFILE2NAME, SOURCEFILE2CODESTRING);
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(rawFile1);
        rawData.insertFile(rawFile2);
        final ClarpseProject parseService = new ClarpseProject(rawData);
        sourceCodeModel = parseService.result();
    }

    @Test
    public void testClassAComponentHasCorrectSourceFilePath() throws Exception {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE1PACKAGE + ".TestA");
        assertTrue(component.sourceFile().equals(SOURCEFILE1NAME));
    }

    @Test
    public void testClassAMethodAComponentHasCorrectSourceFilePath() throws Exception {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE1PACKAGE + ".TestA.methodA()");
        assertTrue(component.sourceFile().equals(SOURCEFILE1NAME));
    }

    @Test
    public void testAbstractClassBComponentHasCorrectSourceFilePath() throws Exception {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE1PACKAGE + ".TestB");
        assertTrue(component.sourceFile().equals(SOURCEFILE1NAME));
    }

    @Test
    public void testAbstractClassBMethodBComponentHasCorrectSourceFilePath() throws Exception {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE1PACKAGE
 + ".TestB.methodB()");
        assertTrue(component.sourceFile().equals(SOURCEFILE1NAME));
    }

    @Test
    public void testClassCComponentHasCorrectSourceFilePath() throws Exception {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE2PACKAGE + ".TestC");
        assertTrue(component.sourceFile().equals(SOURCEFILE2NAME));
    }

    @Test
    public void testClassCMethodCComponentHasCorrectSourceFilePath() throws Exception {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE2PACKAGE + ".TestC.methodC()");
        assertTrue(component.sourceFile().equals(SOURCEFILE2NAME));
    }

    @Test
    public void testClassCAbstractClassDComponentHasCorrectSourceFilePath() throws Exception {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE2PACKAGE + ".TestC.TestD");
        assertTrue(component.sourceFile().equals(SOURCEFILE2NAME));
    }

    @Test
    public void testClassCAbstractClassDMethodDComponentHasCorrectSourceFilePath() throws Exception {
        final Component component = sourceCodeModel.getComponent(SOURCEFILE2PACKAGE
 + ".TestC.TestD.methodD()");
        assertTrue(component.sourceFile().equals(SOURCEFILE2NAME));
    }
}
