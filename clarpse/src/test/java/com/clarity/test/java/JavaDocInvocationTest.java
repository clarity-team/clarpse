package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.invocation.ComponentInvocation;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Ensure component invocation data of a given class is accurate.
 */
public class JavaDocInvocationTest {

    @Test
    public void simpleJavaDocMentionComponentInvocation() throws Exception {

        final String code = "package com; \n /**\n"
                + " * The url argument must specify an absolute {@link URL}. The name\n"
                + "\n*/\npublic class ClassA { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(((ComponentInvocation) generatedSourceModel.getComponent("com.ClassA")
                .get().componentInvocations(ComponentInvocations.DOC_MENTION).toArray()[0]).invokedComponent()
                        .equals("com.URL"));
    }

    @Test
    public void avoidMethodComponentJavaDocInvocations() throws Exception {

        final String code = "package com; \n /**\n"
                + " * The url argument must specify an absolute {@link URL#test()  }. The name\n"
                + "\n*/\npublic class ClassA { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("com.ClassA").get().componentInvocations().size() == 0);
    }

    @Test
    public void resolveDocCommentShortLinkUsingImport() throws Exception {

        final String code = "package com; \n import org.test.Junit;\n/**\n"
                + " * The url argument must specify an absolute {  @linkplain Junit}. The name\n"
                + "\n*/\npublic class ClassA { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(
                generatedSourceModel.getComponent("com.ClassA").get().componentInvocations(ComponentInvocations.DOC_MENTION)
                        .get(0).invokedComponent().equals("org.test.Junit"));
    }

    @Test
    public void resolveDocCommentLongLink() throws Exception {

        final String code = "package com; \n/**\n"
                + " * The url argument must specify an absolute {@linkplain org.test.Junit}. The name\n"
                + "\n*/\npublic class ClassA { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(
                generatedSourceModel.getComponent("com.ClassA").get().componentInvocations(ComponentInvocations.DOC_MENTION)
                        .get(0).invokedComponent().equals("org.test.Junit"));
    }

    @Test
    public void resolveDocCommentLongLinkv2() throws Exception {

        final String code = "package com; \n import org.test.Junit;\n/**\n"
                + " * The url argument must specify an absolute {@linkplain org.test.Junit}. The name\n"
                + "\n*/\npublic class ClassA { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(
                generatedSourceModel.getComponent("com.ClassA").get().componentInvocations(ComponentInvocations.DOC_MENTION)
                        .get(0).invokedComponent().equals("org.test.Junit"));
    }

    @Test
    public void resolveDocCommentLongLinkWithAlias() throws Exception {

        final String code = "package com; \n import org.test.Junit;\n/**\n"
                + " * The url argument must specify an absolute {  @link org.test.Junit   Junit  }. The name\n"
                + "\n*/\npublic class ClassA { }";
        OOPSourceCodeModel generatedSourceModel;
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        generatedSourceModel = parseService.result();
        assertTrue(
                generatedSourceModel.getComponent("com.ClassA").get().componentInvocations(ComponentInvocations.DOC_MENTION)
                        .get(0).invokedComponent().equals("org.test.Junit"));
    }

}
