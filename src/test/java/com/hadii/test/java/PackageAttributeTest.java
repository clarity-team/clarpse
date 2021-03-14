package com.hadii.test.java;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests to ensure package name attribute of parsed components are correct.
 */
public class PackageAttributeTest {

    @Test
    public final void testClassAccuratePackageName() throws Exception {
        String pkgName = "com.clarity.test";
        String codeString = "package " + pkgName + ";   class SampleJavaClass {  private String sampleClassField;  }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final Component cmp = generatedSourceModel.getComponent("com.clarity.test.SampleJavaClass").get();
        Assert.assertTrue(cmp.packageName().equals(pkgName));
    }

    @Test
    public final void testFieldVarAccuratePackageName() throws Exception {
        String pkgName = "com.clarity.test";
        String codeString = "package " + pkgName + ";   class SampleJavaClass {  private String sampleClassField;  }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final Component cmp = generatedSourceModel.getComponent("com.clarity.test.SampleJavaClass.sampleClassField").get();
        Assert.assertTrue(cmp.packageName().equals(pkgName));
    }

    @Test
    public final void testMethodAccuratePackageName() throws Exception {
        String pkgName = "com.clarity.test";
        String codeString = "package " + pkgName + ";   class SampleJavaClass {  private String method(){}  }";
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        rawData.insertFile(new ProjectFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final Component cmp = generatedSourceModel.getComponent("com.clarity.test.SampleJavaClass.method()").get();
        Assert.assertTrue(cmp.packageName().equals(pkgName));
    }
}
