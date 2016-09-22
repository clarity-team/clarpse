package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.ClarpseUtil;
import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;

/**
 * Ensure component chained method invocation data of a given class is accurate.
 *
 * @author Muntazir Fadhel
 */
public class ComplexMethodInvocationsTest {

    private static String codeFile1 = "package com;" + "import com.test.AbstractClassA; import com.fvt.AbstractClassE;"
            + "public class ClassA extends ClassB { "
            + "  private CustomObjA fieldVar;"
            + "  public String topMethod() { "
            + " if(true) {}"
            + "     fieldVar = new CustomObjA();"
            + "     boolean bool = fieldVar.fooMethodA(\"lol\", 4).fooMethodB().aMethod(\"test\").abstractMethod();"
            + "     com.CustomObjA.staticMethod();"
            + "     CustomObjA.secondStaticMethod();"
            + "  } "
            + "}";

    private static String codeFile2 = "package com; import com.test.AbstractClassA;"
            + "public class CustomObjA extends AbstractClassA{ "
            + "  public CustomObjB fooMethodA(String s, int t) { "
            + "     return new CustomObjB();"
            + "  } "
            + "  public static String staticMethod() { "
            + "     return \"\";"
            + "  } "
            + "  public static String secondStaticMethod() { "
            + "     return \"\";"
            + "  } "
            + "}";

    private static String codeFile3 = "package com; import com.fvt.AbstractClassE;"
            + "public class CustomObjB extends AbstractClassE{ "
            + "  public CustomObjA fooMethodB() { "
            + "     return true;"
            + "  } "
            + "}";

    private static String codeFile4 = "package com.test; import com.CustomObjB; import com.fvt.AbstractClassE;"
            + "public abstract class AbstractClassA extends AbstractClassE{ "
            + "  public CustomObjB aMethod(String s) { "
            + "     return null;"
            + "  } "
            + "}";

    private static String codeFile5 = "package com.fvt; "
            + "public abstract class AbstractClassE extends NonExistentClass{ "
            + "  abstract boolean abstractMethod();"
            + "}";

    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static void setup() throws Exception {

        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);

        rawData.insertFile(new RawFile("file2", codeFile2));
        rawData.insertFile(new RawFile("file4", codeFile4));
        rawData.insertFile(new RawFile("file1", codeFile1));
        rawData.insertFile(new RawFile("file3", codeFile3));
        rawData.insertFile(new RawFile("file5", codeFile5));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        System.out.println(ClarpseUtil.fromJavaToJson(generatedSourceModel));
    }


    @Test
    public void testClassATopMethodLocalVarInvokesCustomObjAFooMethodA() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.topMethod().bool")
                .componentInvocations(ComponentInvocations.METHOD).get(0).invokedComponent()
                .equals("com.CustomObjA.fooMethodA(java.lang.String,java.lang.Integer)"));
    }

    @Test
    public void testClassATopMethodBoolVarInvokesCustomObjBFooMethodB() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.topMethod().bool")
                .componentInvocations(ComponentInvocations.METHOD).get(1).invokedComponent()
                .equals("com.CustomObjB.fooMethodB()"));
    }

    @Test
    public void testClassATopMethodBoolVarInvokesAbstractClassAAethod() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.topMethod().bool")
                .componentInvocations(ComponentInvocations.METHOD).get(2).invokedComponent()
                .equals("com.test.AbstractClassA.aMethod(java.lang.String)"));
    }

    @Test
    public void testClassATopMethodBoolVarInvokesAbstractClassEAbstractMethod() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.topMethod().bool")
                .componentInvocations(ComponentInvocations.METHOD).get(3).invokedComponent()
                .equals("com.fvt.AbstractClassE.abstractMethod()"));
    }

    @Test
    public void testClassATopMethodInvokesCustomObjAStaticMethod() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.topMethod()")
                .componentInvocations(ComponentInvocations.METHOD).get(0).invokedComponent()
                .equals("com.CustomObjA.staticMethod()"));
    }


    @Test
    public void testClassATopMethodInvokesCustomObjASecondStaticMethod() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.topMethod()")
                .componentInvocations(ComponentInvocations.METHOD).get(1).invokedComponent()
                .equals("com.CustomObjA.secondStaticMethod()"));
    }

}
