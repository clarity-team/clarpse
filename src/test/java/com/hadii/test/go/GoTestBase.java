package com.hadii.test.go;

import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import org.junit.Before;

public class GoTestBase {

    public ProjectFiles projectFiles;

    @Before
    public void setUp() {
        ProjectFile goModFile = new ProjectFile("go.mod", "module module");
        projectFiles = new ProjectFiles(Lang.GOLANG);
        projectFiles.insertFile(goModFile);
    }

    ProjectFiles goLangProjectFilesFixture(String goModPath) {
        ProjectFiles projectFiles = new ProjectFiles(Lang.GOLANG);
        projectFiles.insertFile(new ProjectFile(goModPath + "/go.mod", "module/module/module"));
        return projectFiles;
    }
}
