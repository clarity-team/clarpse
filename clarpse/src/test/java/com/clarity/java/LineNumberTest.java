package com.clarity.java;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Ensure line begin and line end values for generated components are correct.
 * @author Muntazir Fadhel
 *
 */
public class LineNumberTest {

    private static OOPSourceCodeModel generatedSourceModel;

    static String codeString = ""
            +"   package x; \n "
            +"   import String; \n "
            +"   import List; \n "
            +"   import Magic; \n "
            +"   /** a multi line class comment \n "
            +"   * foo.. \n "
            +"   */  \n"
            +"   class foo {\n "
            +"       /** method multiline \n"
            +"       * comment.  \n"
            +"       */ \n"
            + "      void fooMethod() { \n"
            +"       } \n"
            +"   }";

    public static String ClassComponentBeginLine = "8";
    public static String ClassComponentEndLine = "14";
    public static String MethodComponentBeginLine = "12";
    public static String MethodComponentEndLine = "13";

    @BeforeClass
    public static void setUp() throws Exception {
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ParseService parseService = new ParseService();
        generatedSourceModel = parseService.parseProject(rawData);
    }

    @Test
    public void testCorrectStartLineNoForClassComponent() throws Exception {
        final Component classCmp = generatedSourceModel.getComponent("x.foo");
        Assert.assertTrue(classCmp.getStartLine().equals(ClassComponentBeginLine));
    }

    @Test
    public void testCorrectEndLineNoForClassComponent() throws Exception {
        final Component classCmp = generatedSourceModel.getComponent("x.foo");
        Assert.assertTrue(classCmp.getEndLine().equals(ClassComponentEndLine));
    }

    @Test
    public void testCorrectStartLineNoForMethodComponent() throws Exception {
        final Component methodCmp = generatedSourceModel.getComponent("x.foo.void_fooMethod()");
        Assert.assertTrue(methodCmp.getStartLine().equals(MethodComponentBeginLine));
    }

    @Test
    public void testCorrectEndLineNoForMethodComponent() throws Exception {
        final Component methodCmp = generatedSourceModel.getComponent("x.foo.void_fooMethod()");
        Assert.assertTrue(methodCmp.getEndLine().equals(MethodComponentEndLine));
    }
}
