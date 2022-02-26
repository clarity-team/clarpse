package com.hadii.test;

import com.hadii.clarpse.CommonDir;
import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class RegressionTest {

    /**
     * https://github.com/Zir0-93/clarpse/issues/74
     */
    @Test
    public void shouldNotThrowStackOverflowException() throws Exception {
        ClarpseTestUtil.sourceCodeModel("/go-master.zip", Lang.GOLANG);
    }


    @Test
    public void shouldNotArrayOutOfBoundsException() throws Exception {
        assertTrue(new CommonDir("/test/lol/cakes", "/").value().equalsIgnoreCase("/"));
    }

    @Test
    public void parseSingleLineGoModFileDoesNotThrow() throws Exception {
        final String code = "module _";
        final ProjectFiles rawData = new ProjectFiles(Lang.GOLANG);
        rawData.insertFile(new ProjectFile("go.mod", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
    }
}
