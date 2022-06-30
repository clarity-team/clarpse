package com.hadii.clarpse.compiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Represents source files to be parsed.
 */
public class ProjectFiles {

    private final Lang language;
    private List<ProjectFile> files = new ArrayList<>();

    /**
     * Constructs a ProjectFiles instance from either a path to a local directory or a
     * path to a local zip file.
     */
    public ProjectFiles(final Lang language, final String projectPath) throws Exception {
        this.language = language;
        File projectFiles = new File(projectPath);
        if (!projectFiles.exists()) {
            throw new IllegalArgumentException("The given path does not exist! " + projectPath);
        } else if (projectFiles.isFile()
            && anyMatchExtensions(projectFiles.getName(), new String[]{".zip"})) {
            initFilesFromZipPath(language, projectFiles);
        } else if (projectFiles.isDirectory()) {
            initFilesFromDir(language, projectFiles);
        } else {
            throw new IllegalArgumentException(
                "The supplied project path must either be a local directory path or a "
                    + "local zip file path.");
        }
        validateFiles();
    }

    public ProjectFiles(Lang language, InputStream zipFileInputStream) throws Exception {
        this.language = language;
        this.files = extractProjectFilesFromZip(zipFileInputStream, language);
    }

    public ProjectFiles(final Lang language, final List<ProjectFile> files) {
        this.language = language;
        this.files.addAll(files);
    }

    public ProjectFiles(final Lang language) {
        this.language = language;
    }

    private void initFilesFromZipPath(Lang language, File projectFiles) throws Exception {
        InputStream io = FileUtils.openInputStream(projectFiles);
        this.files = extractProjectFilesFromZip(io, language);
    }

    /**
     * For all files, the immediate root subdirectories (and any files directly within) are
     * deleted and all remaining subdirectories are shifted over in its place. If there are any
     * files within the current root directory which will get deleted as a result, an exception
     * is thrown.
     *
     * Sample transformation: /test/foo/cakes/lol.txt  ---> /foo/cakes/lol.txt
     */
    public void shiftSubDirsLeft() {
        this.files = this.files.stream().map(file -> {
            if (StringUtils.countMatches(file.path(), File.separator) > 1) {
                return new ProjectFile(file.path().substring(
                    StringUtils.ordinalIndexOf(file.path(), File.separator, 2)
                ), file.content());
            } else {
                throw new IllegalArgumentException("Cannot shift file: " + file.path() + ".");
            }
        }).collect(Collectors.toList());
    }

    private void initFilesFromDir(Lang language, File projectFiles) throws IOException {
        Iterator<File> it = FileUtils.iterateFiles(projectFiles, null, true);
        while (it.hasNext()) {
            File nextFile = (File) it.next();
            if (nextFile.isFile() && anyMatchExtensions(
                nextFile.getName(), language.fileExtensions())) {
                this.files.add(new ProjectFile(
                    nextFile.getPath(),
                    FileUtils.readFileToString(nextFile, StandardCharsets.US_ASCII))
                );
            }
        }
    }

    private void validateFiles() {
        if (this.files.size() == 0) {
            throw new IllegalArgumentException("No source files were found in this project!");
        } else if (this.getLanguage() == Lang.GOLANG && this.files.stream().anyMatch(
            projectFile -> projectFile.shortName().endsWith(".mod"))) {
            throw new IllegalArgumentException("Go projects must include a go.mod file!");
        }
    }

    private List<ProjectFile> extractProjectFilesFromZip(final InputStream is, Lang language)
        throws Exception {
        List<ProjectFile> projectFiles = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                entry.getName().substring(entry.getName().lastIndexOf(".") + 1
                );
                if (!entry.isDirectory() && anyMatchExtensions(entry.getName(),
                                                               language.fileExtensions())) {
                    projectFiles.add(new ProjectFile(File.separator + entry.getName().replace(" ", "_"),
                                                     new String(IOUtils.toByteArray(zis),
                                                                StandardCharsets.UTF_8)));
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        } catch (final Exception e) {
            throw new Exception("Error while  reading " + language.value() + " source files from "
                                    + "zip!", e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return projectFiles;
    }

    private boolean anyMatchExtensions(String s, String[] extn) {
        return Arrays.stream(extn).anyMatch(s::endsWith);
    }

    public Lang getLanguage() {
        return language;
    }

    public final void insertFile(final ProjectFile file) {
        files.add(file);
    }

    public final List<ProjectFile> files() {
        return files;
    }

    public final void setFiles(final List<ProjectFile> files) {
        this.files = files;
    }
}
