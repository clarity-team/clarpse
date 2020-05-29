package com.hadii.test.java;

import com.hadii.clarpse.ClarpseUtil;
import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.File;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.SourceFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests related to module-info.java files.
 */
public class ModuleInfoTest {

    @Test
    public final void dontThrowExceptionWhileParsing() throws Exception {
        final String code = "package test; public class Test { }";
        final String codeB = "module lolcakes { requires test; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVA);
        rawData.insertFile(new File("test.java", code));
        rawData.insertFile(new File("module-info.java", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
    }
}
