package com.clarity.java;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.clarity.invocation.ComponentInvocation;
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
public class TypeImplementationTest {

    @Test
    public void testAccurateImplementedTypes() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD { }";
        OOPSourceCodeModel generatedSourceModel;
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("com.ClassA").componentInvocations()
                .toArray()[0]).invokedComponent().equals("com.ClassD"));
        assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations().size() == 1);
    }

    @Test
    public void testAccurateMultipleImplementedTypes() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD, ClassE { }";
        OOPSourceCodeModel generatedSourceModel;
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("com.ClassA")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).toArray()[0]).invokedComponent()
                        .equals("com.ClassD"));
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("com.ClassA")
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).toArray()[1]).invokedComponent()
                        .equals("com.ClassE"));
    }

    @Test
    public void testAccurateImplementedTypesSize() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD { }";
        OOPSourceCodeModel generatedSourceModel;
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations().size() == 1);
    }

    @Test
    public void testAccurateMultipleImplementedTypesSize() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD, ClassE { }";
        OOPSourceCodeModel generatedSourceModel;
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations().size() == 2);
    }

    @Test
    public void testAccurateImplementedTypesForNestedClass() throws Exception {

        final String code = "package com; \n public class ClassA {  class ClassB implements ClassD{} }";
        OOPSourceCodeModel generatedSourceModel;
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("com.ClassA.ClassB")
                .componentInvocations().toArray()[0]).invokedComponent().equals("com.ClassD"));

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").componentInvocations().size() == 1);
    }

    @Test
    public void testAccurateImplementedTypesSizeForNestedClass() throws Exception {

        final String code = "package com; \n public class ClassA { public class ClassB implements ClassD{} }";
        OOPSourceCodeModel generatedSourceModel;
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").componentInvocations().size() == 1);
    }
}
