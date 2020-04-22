package com.hadii.test.java;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.File;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.SourceFiles;
import com.hadii.clarpse.reference.ComponentReference;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants.TypeReferences;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure component invocation data of a given class is accurate.
 */
public class TypeImplementationReferenceTest {

    @Test
    public void testAccurateImplementedTypes() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("com.ClassA").get().references()
                .toArray()[0]).invokedComponent().equals("com.ClassD"));
        assertTrue(generatedSourceModel.getComponent("com.ClassA").get().references().size() == 1);
    }

    @Test
    public void testAccurateMultipleImplementedTypes() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD, ClassE { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("com.ClassA")
                .get().references(TypeReferences.IMPLEMENTATION).toArray()[0]).invokedComponent()
                        .equals("com.ClassD"));
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("com.ClassA")
                .get().references(TypeReferences.IMPLEMENTATION).toArray()[1]).invokedComponent()
                        .equals("com.ClassE"));
    }

    @Test
    public void testAccurateImplementedTypesSize() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("com.ClassA").get().references().size() == 1);
    }

    @Test
    public void testAccurateMultipleImplementedTypesSize() throws Exception {

        final String code = "package com; \n public class ClassA implements ClassD, ClassE { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("com.ClassA").get().references().size() == 2);
    }

    @Test
    public void testAccurateImplementedTypesForNestedClass() throws Exception {

        final String code = "package com; \n public class ClassA {  class ClassB implements ClassD{} }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("com.ClassA.ClassB")
                .get().references().toArray()[0]).invokedComponent().equals("com.ClassD"));

        assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").get().references().size() == 1);
    }

    @Test
    public void testAccurateImplementedTypesSizeForNestedClass() throws Exception {

        final String code = "package com; \n public class ClassA { public class ClassB implements ClassD{} }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").get().references().size() == 1);
    }
}
