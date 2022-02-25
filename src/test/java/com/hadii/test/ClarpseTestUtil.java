package com.hadii.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import org.apache.commons.io.IOUtils;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;

public class ClarpseTestUtil {

    public static OOPSourceCodeModel sourceCodeModel(String githubRepoOwner, String githubrepoName) throws Exception {
        String url = "https://api.github.com/repos/" + githubRepoOwner + "/" + githubrepoName + "/zipball/master";
        final URL repoUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) repoUrl.openConnection();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(new BufferedInputStream(conn.getInputStream(), 1024), baos);
        GitHubRepoInputStream repoStream = new GitHubRepoInputStream(githubRepoOwner, githubrepoName,
                baos.toByteArray());
        final ProjectFiles projectFiles = extractProjectFromArchive(
                new ByteArrayInputStream(repoStream.getBaos()), null, Lang.GOLANG);
        System.out.println("Number of files in " + githubRepoOwner + "/" + githubrepoName + " is "
                + projectFiles.files().size());
        Date compileBeginDate = new Date();
        OOPSourceCodeModel model = new ClarpseProject(projectFiles).result();
        System.out.println("Compiling " + githubRepoOwner + "/" + githubrepoName + " took: "
                + ((new Date().getTime() - compileBeginDate.getTime()) / 1000) + " s");
        return model;
    }

    public static boolean checkIfFileHasExtension(String s, String[] extn) {
        return Arrays.stream(extn).anyMatch(entry -> s.endsWith(entry));
    }

    public static OOPSourceCodeModel sourceCodeModel(String testResourceZip, Lang language) throws Exception {
        final ProjectFiles projectFiles = extractProjectFromArchive(
                ClarpseTestUtil.class.getResourceAsStream(testResourceZip), null, language);
        OOPSourceCodeModel model = new ClarpseProject(projectFiles).result();
        return model;
    }

    public static ProjectFiles extractProjectFromArchive(final InputStream is, String project, Lang language)
            throws Exception {
        final ProjectFiles projectFiles = new ProjectFiles(language);
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
                        && checkIfFileHasExtension(entry.getName(), language.fileExtensions())) {
                    projectFiles.insertFile(new ProjectFile("/" + entry.getName().replace(" ", "_"),
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
                            && (projectFiles.files().size() > 0)) {
                        currentlyExtractingProject = false;
                        finishedExtracting = true;
                    }
                }

                zis.closeEntry();
                entry = zis.getNextEntry();
            }

            // ensure we actually found some valid source files!
            if ((projectFiles.files().size() < 1)) {
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
        return projectFiles;
    }
}