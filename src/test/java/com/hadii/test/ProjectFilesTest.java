package com.hadii.test;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

import static com.hadii.test.ClarpseTestUtil.unzipArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectFilesTest {

    private static ProjectFiles filesFromZipPath;
    private static ProjectFiles filesFromZipInputStream;
    private static ProjectFiles filesFromSourceDirectory;


    @BeforeClass
    public static void setup() throws Exception {
        filesFromZipPath = new ProjectFiles(
            Lang.JAVA, Objects.requireNonNull(ClarpseTestUtil.class.getResource("/clarpse.zip")).getFile());
        filesFromSourceDirectory =
            new ProjectFiles(Lang.JAVA,
                             unzipArchive(
                                 new File(Objects.requireNonNull(ProjectFilesTest.class.getResource(
                                     "/clarpse.zip")).toURI())));
        filesFromZipInputStream =
            new ProjectFiles(Lang.JAVA,
                             ClarpseTestUtil.class.getResourceAsStream("/clarpse.zip"));
    }

    @Test
    public void testFilesFromZipInputStreamFilesNo() throws Exception {
        assertEquals(35, filesFromZipInputStream.files().size());

    }

    @Test
    public void testFilesFromZipPathFilesNo() throws Exception {
        assertEquals(35, filesFromZipPath.files().size());
    }

    @Test
    public void testFilesFromSourceDirFilesNo() throws Exception {
        assertEquals(35, filesFromSourceDirectory.files().size());
    }

    @Test
    public void testZipInputStreamComponentCheck() throws Exception {
        assertTrue(new ClarpseProject(filesFromZipInputStream).result().getComponent(
            "com.hadii.clarpse.listener.GoLangTreeListener.currPkg").isPresent());
    }

    @Test
    public void testZipPathComponentCheck() throws Exception {
        assertTrue(new ClarpseProject(filesFromZipPath).result().getComponent(
            "com.hadii.clarpse.listener.GoLangTreeListener.currPkg").isPresent());
    }

    @Test
    public void testSourceDirFilesComponentCheck() throws Exception {
        assertTrue(new ClarpseProject(filesFromSourceDirectory).result().getComponent(
            "com.hadii.clarpse.listener.GoLangTreeListener.currPkg").isPresent());
    }

    @Test
    public void testParseEmptyJavaProjectFiles() throws Exception {
        assertEquals(0, new ClarpseProject(new ProjectFiles(Lang.JAVA)).result().size());
    }

    @Test
    public void testParseEmptyGoLangProjectFiles() throws Exception {
        assertEquals(0, new ClarpseProject(new ProjectFiles(Lang.GOLANG)).result().size());
    }

    @Test
    public void testParseEmptyJavascriptProjectFiles() throws Exception {
        assertEquals(0, new ClarpseProject(new ProjectFiles(Lang.JAVASCRIPT)).result().size());
    }

    @Test
    public void testShiftSubDirs() throws Exception {
        ProjectFiles projectFiles = new ProjectFiles(Lang.GOLANG);
        ProjectFile projectFile = new ProjectFile("/test/lol/cakes.go", "{}");
        projectFiles.insertFile(projectFile);
        projectFiles.shiftSubDirsLeft();
        assertEquals("/lol/cakes.go", projectFiles.files().get(0).path());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftSubDirsInvalid() throws Exception {
        ProjectFiles projectFiles = new ProjectFiles(Lang.GOLANG);
        ProjectFile projectFile = new ProjectFile("/cakes.go", "{}");
        projectFiles.insertFile(projectFile);
        projectFiles.shiftSubDirsLeft();
    }

}
