package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.ClarpseUtil;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class ParseExtendedAndImplementedAttributesTest {

    private static String code = "package com; \n public class ClassA extends ClassD implements InterfaceC { }";
    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static void setup() throws Exception {

        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ParseService parseService = new ParseService();
        generatedSourceModel = parseService.parseProject(rawData);
        System.out.println(ClarpseUtil.fromJavaToJson(generatedSourceModel));
    }

    @Test
    public void parseImplementedTypesTest() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").getImplementedClasses().get(0)
                .equals("com.InterfaceC"));

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").getImplementedClasses().size() == 1);
    }

    @Test
    public void parseExtendedTypesTest() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").getSuperClasses().get(0).equals("com.ClassD"));

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").getSuperClasses().size() == 1);
    }
}
