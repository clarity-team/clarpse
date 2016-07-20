package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ClarpseProject;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Tests to ensure package name attribute of parsed components are correct.
 *
 * @author Muntazir Fadhel
 */
public class PackageAttributeTest {

    @Test
    public final void testClassAccuratePackageName() throws Exception {
         String pkgName = "com.clarity.test";
         String codeString = "package " + pkgName + ";   class SampleJavaClass {  private String sampleClassField;  }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
       OOPSourceCodeModel generatedSourceModel = parseService.result();
        final Component cmp = generatedSourceModel.getComponent("com.clarity.test.SampleJavaClass");
        Assert.assertTrue(cmp.packageName().equals(pkgName));
    }

    @Test
    public final void testFieldVarAccuratePackageName() throws Exception {
         String pkgName = "com.clarity.test";
        String codeString = "package " + pkgName + ";   class SampleJavaClass {  private String sampleClassField;  }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final Component cmp = generatedSourceModel.getComponent("com.clarity.test.SampleJavaClass.sampleClassField");
        Assert.assertTrue(cmp.packageName().equals(pkgName));
    }
    
    @Test
    public final void testMethodAccuratePackageName() throws Exception {
         String pkgName = "com.clarity.test";
        String codeString = "package " + pkgName + ";   class SampleJavaClass {  private String method(){}  }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        OOPSourceCodeModel generatedSourceModel = parseService.result();
        final Component cmp = generatedSourceModel.getComponent("com.clarity.test.SampleJavaClass.method()");
        Assert.assertTrue(cmp.packageName().equals(pkgName));
    }
}
