package com.hadii.test.java;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
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
        final String codeD = "package com; \n public interface ClassD  { }";
        OOPSourceCodeModel generatedSourceModel;
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("com/ClassA.java", code));
        rawData.insertFile(new ProjectFile("com/ClassD.java", codeD));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("com.ClassA").get().references()
                .toArray()[0]).invokedComponent().equals("com.ClassD"));
        assertTrue(generatedSourceModel.getComponent("com.ClassA").get().references().size() == 1);
    }

    @Test
    public void testAccurateMultipleImplementedTypes() throws Exception {
        final String code = "package com; \n public class ClassA implements ClassD, ClassE { }";
        final String codeD = "package com; \n public interface ClassD  { }";
        final String codeE = "package com; \n public interface ClassE { }";
        OOPSourceCodeModel generatedSourceModel;
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("com/ClassA.java", code));
        rawData.insertFile(new ProjectFile("com/ClassD.java", codeD));
        rawData.insertFile(new ProjectFile("com/ClassE.java", codeE));
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
        final String codeD = "package com; \n public interface ClassD  { }";
        OOPSourceCodeModel generatedSourceModel;
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("com/ClassA.java", code));
        rawData.insertFile(new ProjectFile("com/ClassD.java", codeD));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("com.ClassA").get().references().size() == 1);
    }

    @Test
    public void testAccurateMultipleImplementedTypesSize() throws Exception {
        final String code = "package com; \n public class ClassA implements ClassD, ClassE { }";
        final String codeD = "package com; \n public interface ClassD  { }";
        final String codeE = "package com; \n public interface ClassE { }";
        OOPSourceCodeModel generatedSourceModel;
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("com/ClassA.java", code));
        rawData.insertFile(new ProjectFile("com/ClassD.java", codeD));
        rawData.insertFile(new ProjectFile("com/ClassE.java", codeE));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("com.ClassA").get().references().size() == 2);
    }

    @Test
    public void testAccurateImplementedTypesForNestedClass() throws Exception {
        final String code = "package com; \n public class ClassA {  class ClassB implements ClassD{} }";
        final String codeD = "package com; \n public interface ClassD { }";
        OOPSourceCodeModel generatedSourceModel;
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("com/ClassA.java", code));
        rawData.insertFile(new ProjectFile("com/ClassD.java", codeD));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentReference) generatedSourceModel.getComponent("com.ClassA.ClassB")
                .get().references().toArray()[0]).invokedComponent().equals("com.ClassD"));

        assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").get().references().size() == 1);
    }

    @Test
    public void testAccurateImplementedTypesSizeForNestedClass() throws Exception {
        final String code = "package com; \n public class ClassA { class ClassB implements ClassD { } }";
        final String codeD = "package com; \n public interface ClassD  { }";
        OOPSourceCodeModel generatedSourceModel;
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("com/ClassA.java", code));
        rawData.insertFile(new ProjectFile("com/ClassD.java", codeD));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").get().references().size() == 1);
    }
}
