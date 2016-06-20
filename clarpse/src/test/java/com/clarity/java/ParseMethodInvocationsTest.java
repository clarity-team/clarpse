package com.clarity.java;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Tests Clarpse' ability to detect method calls to external types in java
 * classes.
 *
 * @author Muntazir Fadhel
 */
public class ParseMethodInvocationsTest {


    private static OOPSourceCodeModel generatedSourceModel;

    static String codeString = ""
            +"   package x; \n "
            +"   class foo {\n "
            + "      void fooMethod() { \n"
            + "       String s = \"v\"; s.compareTo(\"x\"); } \n"
            +"   }";

    @BeforeClass
    public static void setUp() throws Exception {
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ParseService parseService = new ParseService();
        generatedSourceModel = parseService.parseProject(rawData);
    }

    @Test
    public void testParseStringCompareToMethodInvocation() throws Exception {
        final Component methodCmp = generatedSourceModel.getComponent("x.foo.void_fooMethod()");
        assertTrue(methodCmp.getExternalClassTypeReferences().get(1).name()
                .equals("java.lang.String.boolean_compareTo(String)"));
    }


}
