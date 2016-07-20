package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.ClarpseUtil;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;

/**
 * Ensure component invocation data of a given class is accurate.
 *
 * @author Muntazir Fadhel
 */
public class TypeImplementationTest {

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
    public void typeImplementationTest() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.IMPLEMENTATION)
                .get(0).invokedComponent()
                .equals("com.InterfaceC"));

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.IMPLEMENTATION)
                .size() == 1);
    }

    @Test
    public void typeExtensionTest() throws Exception {

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.EXTENSION)
                .get(0).invokedComponent().equals("com.ClassD"));

        System.out.println(generatedSourceModel.getComponents());
        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.EXTENSION)
                .size() == 1);
    }
}
