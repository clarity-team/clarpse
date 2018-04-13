package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure component invocation data of a given class is accurate.
 */
public class TypeImplementationTest {

    @Test
    public void testAccurateImplementedTypes() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("com.ClassA").get().componentInvocations()
                .toArray()[0]).invokedComponent().equals("com.ClassD"));
        assertTrue(generatedSourceModel.getComponent("com.ClassA").get().componentInvocations().size() == 1);
    }

    @Test
    public void testAccurateMultipleImplementedTypes() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD, ClassE { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("com.ClassA")
                .get().componentInvocations(ComponentInvocations.IMPLEMENTATION).toArray()[0]).invokedComponent()
                        .equals("com.ClassD"));
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("com.ClassA")
                .get().componentInvocations(ComponentInvocations.IMPLEMENTATION).toArray()[1]).invokedComponent()
                        .equals("com.ClassE"));
    }

    @Test
    public void testAccurateImplementedTypesSize() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").get().componentInvocations().size() == 1);
    }

    @Test
    public void testAccurateMultipleImplementedTypesSize() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD, ClassE { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").get().componentInvocations().size() == 2);
    }

    @Test
    public void testAccurateImplementedTypesForNestedClass() throws Exception {

        final String code = "package com; \n public class ClassA {  class ClassB implements ClassD{} }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("com.ClassA.ClassB")
                .get().componentInvocations().toArray()[0]).invokedComponent().equals("com.ClassD"));

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").get().componentInvocations().size() == 1);
    }

    @Test
    public void testAccurateImplementedTypesSizeForNestedClass() throws Exception {

        final String code = "package com; \n public class ClassA { public class ClassB implements ClassD{} }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").get().componentInvocations().size() == 1);
    }
}
