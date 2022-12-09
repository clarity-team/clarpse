package com.hadii.test;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.hadii.test.ClarpseTestUtil.unzipArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectFilesTest {

    private static ProjectFiles zipPathProjectFiles;
    private static ProjectFiles InputStreamProjectFiles;
    private static ProjectFiles sourceDirProjectFiles;
    private static String sourceDir;


    @BeforeClass
    public static void setup() throws Exception {
        zipPathProjectFiles = new ProjectFiles(
            Lang.JAVA, Objects.requireNonNull(ClarpseTestUtil.class.getResource("/clarpse.zip")).getFile());
        sourceDir = unzipArchive(
                new File(Objects.requireNonNull(ProjectFilesTest.class.getResource(
                        "/clarpse.zip")).toURI()));
        sourceDirProjectFiles = new ProjectFiles(Lang.JAVA, sourceDir);
        InputStreamProjectFiles =
            new ProjectFiles(Lang.JAVA,
                             ClarpseTestUtil.class.getResourceAsStream("/clarpse.zip"));
    }

    @Test
    public void testFilesFromZipInputStreamFilesNo() {
        assertEquals(35, InputStreamProjectFiles.files().size());

    }

    @Test
    public void testFilesFromZipPathFilesNo() {
        assertEquals(35, zipPathProjectFiles.files().size());
    }

    @Test
    public void testPersistedDirFromSourceDir() throws IOException {
        assertEquals(sourceDirProjectFiles.projectDir(), sourceDir);
    }

    @Test
    public void testPersistedDirFromZipPath() throws IOException {
        assertFalse(zipPathProjectFiles.projectDir().isEmpty());
    }

    @Test
    public void testPersistedDirFromInputStream() throws IOException {
        assertFalse(InputStreamProjectFiles.projectDir().isEmpty());
    }

    @Test
    public void testFilesFromSourceDirFilesNo() {
        assertEquals(35, sourceDirProjectFiles.files().size());
    }

    @Test
    public void testZipInputStreamComponentCheck() throws Exception {
        assertTrue(new ClarpseProject(InputStreamProjectFiles.files(), InputStreamProjectFiles.lang())
                .result().model().getComponent(
            "com.hadii.clarpse.listener.GoLangTreeListener.currPkg").isPresent());
    }

    @Test
    public void testZipPathComponentCheck() throws Exception {
        assertTrue(new ClarpseProject(zipPathProjectFiles.files(),
                zipPathProjectFiles.lang()).result().model().getComponent(
            "com.hadii.clarpse.listener.GoLangTreeListener.currPkg").isPresent());
    }

    @Test
    public void testSourceDirFilesComponentCheck() throws Exception {
        assertTrue(new ClarpseProject(sourceDirProjectFiles.files(),
                sourceDirProjectFiles.lang()).result().model().getComponent(
            "com.hadii.clarpse.listener.GoLangTreeListener.currPkg").isPresent());
    }

    @Test
    public void testParseEmptyJavaProjectFiles() throws Exception {
        assertEquals(0,
                new ClarpseProject(Collections.emptyList(), Lang.JAVA).result().model().size());
    }

    @Test
    public void testParseEmptyGoLangProjectFiles() throws Exception {
        assertEquals(0, new ClarpseProject(Collections.emptyList(), Lang.GOLANG).result().model().size());
    }

    @Test
    public void testParseEmptyJavascriptProjectFiles() throws Exception {
        assertEquals(0, new ClarpseProject(Collections.emptyList(), Lang.JAVASCRIPT).result().model().size());
    }

    @Test
    public void testShiftSubDirs() {
        ProjectFiles projectFiles = new ProjectFiles(Lang.GOLANG);
        ProjectFile projectFile = new ProjectFile("/test/lol/cakes.go", "{}");
        projectFiles.insertFile(projectFile);
        projectFiles.shiftSubDirsLeft();
        assertEquals("/lol/cakes.go",
                     new ArrayList<>(projectFiles.files()).get(0).path());
    }
    @Test
    public void testShiftSubDirsv2() {
        ProjectFiles projectFiles = new ProjectFiles(Lang.GOLANG);
        ProjectFile projectFile = new ProjectFile("/test/lol.go", "{}");
        projectFiles.insertFile(projectFile);
        projectFiles.shiftSubDirsLeft();
        assertEquals("/lol.go", new ArrayList<>(projectFiles.files()).get(0).path());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftSubDirsInvalid() {
        ProjectFiles projectFiles = new ProjectFiles(Lang.GOLANG);
        ProjectFile projectFile = new ProjectFile("/cakes.go", "{}");
        projectFiles.insertFile(projectFile);
        projectFiles.shiftSubDirsLeft();
    }

}
