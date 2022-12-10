package com.hadii.clarpse.compiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Represents source files to be parsed.
 */
public class ProjectFiles {

    private static final Logger LOGGER = LogManager.getLogger(ProjectFiles.class);
    private Lang language;
    private List<ProjectFile> files = new ArrayList<>();

    private String projectDir;

    /**
     * Constructs a ProjectFiles instance from a path to a local directory or zip file.
     */
    public ProjectFiles(final Lang language, final String projectPath) throws Exception {
        setLang(language);
        File projectFiles = new File(projectPath);
        LOGGER.info("Project source files location: " + projectFiles.getPath());
        if (!projectFiles.exists()) {
            throw new IllegalArgumentException("The given path does not exist!");
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

    private void setLang(Lang language) {
        LOGGER.info("Setting language to " + language.name() + ".");
        this.language = language;
    }

    public ProjectFiles(Lang language, InputStream zipFileInputStream) throws Exception {
        setLang(language);
        this.files = extractProjectFilesFromStream(zipFileInputStream, language);
    }

    public ProjectFiles(final Lang language, final Collection<ProjectFile> files) {
        setLang(language);
        this.files.addAll(files);
    }

    public ProjectFiles(final Lang language) {
        setLang(language);
    }

    private void initFilesFromZipPath(Lang language, File projectFiles) throws Exception {
        InputStream io = FileUtils.openInputStream(projectFiles);
        LOGGER.info("Converted zip path to an input stream..");
        this.files = extractProjectFilesFromStream(io, language);
    }

    /**
     * For all files, the immediate root subdirectories (and any files directly within) are
     * deleted and all remaining subdirectories are shifted over in its place. If there are any
     * files within the current root directory which will get deleted as a result, an exception
     * is thrown.
     * <p>
     * Sample transformation: /test/foo/cakes/lol.txt  ---> /foo/cakes/lol.txt
     */
    public void shiftSubDirsLeft() {
        LOGGER.info("Shifting all source files sub-dirs left..");
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

    public int size() {
        return this.files.size();
    }

    private void initFilesFromDir(Lang language, File projectFiles) throws IOException {
        LOGGER.info("Reading source files from dir: " + projectFiles.getPath());
        this.projectDir = projectFiles.getAbsolutePath();
        Iterator<File> it = FileUtils.iterateFiles(projectFiles, null, true);
        while (it.hasNext()) {
            File nextFile = it.next();
            if (nextFile.isFile() && anyMatchExtensions(
                nextFile.getName(), language.fileExtns())) {
                this.files.add(new ProjectFile(
                    nextFile.getPath(),
                    FileUtils.readFileToString(nextFile, StandardCharsets.US_ASCII))
                );
            }
        }
        LOGGER.info("Read " + this.files.size() + " files.");
    }

    private void validateFiles() {
        if (this.files.size() == 0) {
            throw new IllegalArgumentException("No source files were found in this project!");
        } else if (this.lang() == Lang.GOLANG && this.files.stream().anyMatch(
            projectFile -> projectFile.shortName().endsWith(".mod"))) {
            throw new IllegalArgumentException("Go projects must include a go.mod file!");
        }
    }

    private List<ProjectFile> extractProjectFilesFromStream(final InputStream is, Lang language)
        throws Exception {
        LOGGER.info("Extracting source files from input stream..");
        List<ProjectFile> projectFiles = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory()
                    && anyMatchExtensions(entry.getName(), language.fileExtns())) {
                    ProjectFile newFile = new ProjectFile(
                        File.separator + entry.getName().replace(" ", "_"),
                        new String(IOUtils.toByteArray(zis), StandardCharsets.UTF_8));
                    LOGGER.debug("Extracted project file " + newFile + ".");
                    projectFiles.add(newFile);
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
        LOGGER.info("Extracted " + projectFiles.size() + " files.");
        return projectFiles;
    }

    private boolean anyMatchExtensions(String s, String[] extn) {
        return Arrays.stream(extn).anyMatch(s::endsWith);
    }

    public Lang lang() {
        return language;
    }

    public final void insertFile(final ProjectFile file) {
        files.add(file);
        LOGGER.debug("Inserted file " + file + ".");
    }

    public final Collection<ProjectFile> files() {
        return files;
    }

    public String projectDir() throws IOException {
        if (this.projectDir != null && !this.projectDir.isEmpty()) {
            return this.projectDir;
        } else {
            this.persistDir();
            return this.projectDir;
        }
    }

    private void persistDir() throws IOException {
        long startTime = System.currentTimeMillis();
        Set<String> dirs = new HashSet<>();
        final String rootDir = System.getProperty("java.io.tmpdir")
                + File.separator + RandomStringUtils.randomAlphanumeric(16);
        LOGGER.info("Persisting files to " + rootDir);
        dirs.add(rootDir);
        for (final ProjectFile projectFile : this.files) {
            final String filePath = rootDir + File.separator + projectFile.path();
            final File file = new File(filePath);
            File parent = new File(file.getParent());
            if (!parent.exists()) {
                Files.createDirectories(parent.toPath());
            }
            while (!dirs.contains(parent.getPath())) {
                dirs.add(parent.getPath());
                parent = new File(parent.getParent());
            }
            final FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8);
            final PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(projectFile.content());
            printWriter.close();
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        LOGGER.info(this.size() + " files were persisted in " + elapsedTime + " ms");
        this.projectDir = rootDir;
    }

    public void filter(Collection<String> filterFilePaths) {
        this.files = this.files.stream().filter(
                file -> filterFilePaths.contains(file.path())).collect(Collectors.toList());
    }
}
