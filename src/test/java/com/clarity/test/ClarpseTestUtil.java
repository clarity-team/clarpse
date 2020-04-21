package com.clarity.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.SourceFiles;
import com.clarity.compiler.File;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class ClarpseTestUtil {

    public static OOPSourceCodeModel sourceCodeModel(String githubRepoOwner, String githubrepoName) throws Exception {
        String url = "https://api.github.com/repos/" + githubRepoOwner + "/" + githubrepoName + "/zipball/master";
        final URL repoUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) repoUrl.openConnection();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(new BufferedInputStream(conn.getInputStream(), 1024), baos);
        GitHubRepoInputStream repoStream = new GitHubRepoInputStream(githubRepoOwner, githubrepoName,
                baos.toByteArray());
        final SourceFiles sourceFiles = extractProjectFromArchive(
                new ByteArrayInputStream(repoStream.getBaos()), null, Lang.GOLANG);
        System.out.println("Number of files in " + githubRepoOwner + "/" + githubrepoName + " is "
                + sourceFiles.getFiles().size());
        Date compileBeginDate = new Date();
        OOPSourceCodeModel model = new ClarpseProject(sourceFiles).result();
        System.out.println("Compiling " + githubRepoOwner + "/" + githubrepoName + " took: "
                + ((new Date().getTime() - compileBeginDate.getTime()) / 1000) + " s");
        return model;
    }

    public static OOPSourceCodeModel sourceCodeModel(String testResourceZip, Lang language) throws Exception {
        final SourceFiles sourceFiles = extractProjectFromArchive(
                ClarpseTestUtil.class.getResourceAsStream(testResourceZip), null, language);
        OOPSourceCodeModel model = new ClarpseProject(sourceFiles).result();
        return model;
    }

    public static SourceFiles extractProjectFromArchive(final InputStream is, String project, Lang language)
            throws Exception {

        final SourceFiles sourceFiles = new SourceFiles(language);
        boolean currentlyExtractingProject = false;
        boolean finishedExtracting = false;
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(is);
            ZipEntry entry = zis.getNextEntry();
            // iterates over entries in the zip file
            while ((entry != null) && !finishedExtracting) {
                entry.getName().substring(entry.getName().lastIndexOf(".") + 1, entry.getName().length());
                if (!entry.isDirectory() && (currentlyExtractingProject)
                        && entry.getName().endsWith(language.fileExt())) {
                    sourceFiles.insertFile(new File(entry.getName().replace(" ", "_"),
                            new String(IOUtils.toByteArray(zis), StandardCharsets.UTF_8)));
                } else {
                    // if the project name is specified then keep extracting all
                    // the files in the project
                    if (((project != null) && !project.isEmpty()) && entry.getName().contains(project)) {
                        currentlyExtractingProject = true;
                    }
                    // if the project name is not specified, then extract
                    // everything
                    else if ((project == null) || (project.isEmpty())) {
                        currentlyExtractingProject = true;
                    }
                    // if the project name is specified then stop extracting
                    // once the project has been extracted
                    else if ((project != null) && (!project.isEmpty()) && !entry.getName().contains(project)
                            && (sourceFiles.getFiles().size() > 0)) {
                        currentlyExtractingProject = false;
                        finishedExtracting = true;
                    }
                }

                zis.closeEntry();
                entry = zis.getNextEntry();
            }

            // ensure we actually found some valid source files!
            if ((sourceFiles.getFiles().size() < 1)) {
                System.out.println("No " + language.value() + " source files were found in the uploaded zip project!");
            }
        } catch (final Exception e) {
            throw new Exception("Error while  reading " + language.value() + " source files from zip!", e);
        } finally {
            if (zis != null) {
                zis.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return sourceFiles;
    }
}