package com.clarity.test.java;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
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
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final Component cmp = generatedSourceModel.getComponent("com.clarity.test.SampleJavaClass").get();
        Assert.assertTrue(cmp.packageName().equals(pkgName));
    }

    @Test
    public final void testFieldVarAccuratePackageName() throws Exception {
        String pkgName = "com.clarity.test";
        String codeString = "package " + pkgName + ";   class SampleJavaClass {  private String sampleClassField;  }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final Component cmp = generatedSourceModel.getComponent("com.clarity.test.SampleJavaClass.sampleClassField").get();
        Assert.assertTrue(cmp.packageName().equals(pkgName));
    }

    @Test
    public final void testMethodAccuratePackageName() throws Exception {
        String pkgName = "com.clarity.test";
        String codeString = "package " + pkgName + ";   class SampleJavaClass {  private String method(){}  }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final Component cmp = generatedSourceModel.getComponent("com.clarity.test.SampleJavaClass.method()").get();
        Assert.assertTrue(cmp.packageName().equals(pkgName));
    }
}
