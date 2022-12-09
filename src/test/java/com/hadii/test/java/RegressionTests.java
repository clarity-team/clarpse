package com.hadii.test.java;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.Assert.assertTrue;

public class RegressionTests {

    @Test
    public void testParameterizedTestIntegrationTestsClassIsParsedProperly_i111() throws Exception {
        final ProjectFiles rawData = new ProjectFiles(Lang.JAVA);
        String javaCode =
            IOUtils.toString(Objects.requireNonNull(RegressionTests.class.getResourceAsStream(
                "/ParameterizedTestIntegrationTests.java")), StandardCharsets.UTF_8.name());
        rawData.insertFile(new ProjectFile("ParameterizedTestIntegrationTests.java", javaCode));
        final ClarpseProject parseService = new ClarpseProject(rawData.files(), rawData.lang());
        OOPSourceCodeModel generatedSourceModel = parseService.result().model();
        assertTrue(generatedSourceModel.containsComponent("ParameterizedTestIntegrationTests"));
    }
}
