package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.reference.ComponentReference;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Assert;
import org.junit.Test;

/**
 * Ensure component type extensions invocations are accurate.
 */
public class TypeExtensionReferenceTest {

    @Test
    public void testAccurateExtendedTypes() throws Exception {
        final String code = "package com; \n public class ClassA extends ClassD<?> { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(((ComponentReference) generatedSourceModel.getComponent("com.ClassA").get().references()
                .toArray()[0]).invokedComponent().equals("com.ClassD"));
    }

    @Test
    public void testAccurateExtendedTypesSize() throws Exception {
        final String code = "package com; \n public class ClassA extends ClassD { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").get().references().size() == 1);
    }

    @Test
    public void testAccurateExtendedTypesForNestedClass() throws Exception {
        final String code = "package com; \n public class ClassA { public class ClassB extends ClassD{} }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(((ComponentReference) generatedSourceModel.getComponent("com.ClassA.ClassB")
                .get().references().toArray()[0]).invokedComponent().equals("com.ClassD"));

        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").get().references().size() == 1);
    }

    @Test
    public void testAccurateExtendedTypesSizeForNestedClass() throws Exception {
        final String code = "package com; \n public class ClassA { public class ClassB extends ClassD{} }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").get().references().size() == 1);
    }
}
