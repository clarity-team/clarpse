package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;

/**
 * Ensure component invocation data of a given class is accurate.
 *
 * @author Muntazir Fadhel
 */
public class SimpleMethodInvocationsTest {

    private static String codeFile1 = "package com;" + "import java.util.List; import foo.customObjA; "
            + "public class ClassA { "
            + "  private String fieldVar;"
            + "  public String topMethod(String s, int t) { "
            + "     bottomMethod(s,t);"
            + "     noParamsMethod();"
            + "     return \"foo\";"
            + "  } "
            + "  public int middleMethod(String s, int t) { "
            + "     int x = topMethod(s, t); "
            + "     return 1;"
            + "  } "
            + "  public List<String> bottomMethod(String s, int t) { "
            + "     middleMethod(\"string\", 5); "
            + "     return null;"
            + "  } "
            + "  public void noParamsMethod() {"
            + "  String y = this.topMethod(fieldVar, 6);"
            + "  String z = \"awa\";"
            + "  fieldVar = topMethod(\"test\", 6);"
            + "  bottomMethod(this.topMethod(topMethod(y, 5), 4), middleMethod(y, 4));"
            + "  } "
            + "}";


    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static void setup() throws Exception {

        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeFile1));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
    }

    @Test
    public void testInvokeMethodWithMethodCallParameter() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.noParamsMethod()")
                .componentInvocations(ComponentInvocations.METHOD).get(0).invokedComponent()
                .equals("com.ClassA.bottomMethod(java.lang.String,java.lang.Integer)"));

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.noParamsMethod()")
                .componentInvocations(ComponentInvocations.METHOD).get(1).invokedComponent()
                .equals("com.ClassA.topMethod(java.lang.String,java.lang.Integer)"));

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.noParamsMethod()")
                .componentInvocations(ComponentInvocations.METHOD).get(2).invokedComponent()
                .equals("com.ClassA.topMethod(java.lang.String,java.lang.Integer)"));

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.noParamsMethod()")
                .componentInvocations(ComponentInvocations.METHOD).get(3).invokedComponent()
                .equals("com.ClassA.middleMethod(java.lang.String,java.lang.Integer)"));
    }


    @Test
    public void testTopMethodInvokesBottomMethod() throws Exception {

        Assert.assertTrue(generatedSourceModel
                .getComponent("com.ClassA.topMethod(java.lang.String,java.lang.Integer)")
                .componentInvocations(ComponentInvocations.METHOD)
                .get(0).invokedComponent()
                .equals("com.ClassA.bottomMethod(java.lang.String,java.lang.Integer)"));
    }

    @Test
    public void testTopMethodInvokesNoParamsMethod() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.topMethod(java.lang.String,java.lang.Integer)")
                .componentInvocations(ComponentInvocations.METHOD).get(1).invokedComponent().equals("com.ClassA.noParamsMethod()"));
    }

    @Test
    public void testMiddleMethodLocalVarInvokesTopMethod() throws Exception {

        Assert.assertTrue(generatedSourceModel
                .getComponent("com.ClassA.middleMethod(java.lang.String,java.lang.Integer).x")
                .componentInvocations(ComponentInvocations.METHOD).get(0).invokedComponent()
                .equals("com.ClassA.topMethod(java.lang.String,java.lang.Integer)"));
    }

    @Test
    public void testBottomMethodInvokesMiddleMethodWithLiterals() throws Exception {

        Assert.assertTrue(generatedSourceModel
                .getComponent("com.ClassA.bottomMethod(java.lang.String,java.lang.Integer)")
                .componentInvocations(ComponentInvocations.METHOD).get(0).invokedComponent()
                .equals("com.ClassA.middleMethod(java.lang.String,java.lang.Integer)"));
    }

    @Test
    public void testNoParamsMethodInvokesTopMethodWithFieldVar() throws Exception {

        Assert.assertTrue(generatedSourceModel
                .getComponent("com.ClassA.noParamsMethod().y")
                .componentInvocations(ComponentInvocations.METHOD).get(0).invokedComponent()
                .equals("com.ClassA.topMethod(java.lang.String,java.lang.Integer)"));
    }

    @Test
    public void testFieldVarInvokesTopMethod() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.noParamsMethod().y")
                .componentInvocations(ComponentInvocations.METHOD).get(0).invokedComponent()
                .equals("com.ClassA.topMethod(java.lang.String,java.lang.Integer)"));
    }

}
