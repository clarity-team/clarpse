package com.hadii.clarpse.compiler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents source files to be parsed.
 */
public class PersistedProjectFiles {

    private static final Logger LOGGER = LogManager.getLogger(PersistedProjectFiles.class);
    private final ProjectFiles projectFiles;
    private final String rootDir;
    private final Set<String> dirs;

    PersistedProjectFiles(final ProjectFiles projectFiles, final String rootDir) throws IOException {
        this.projectFiles = projectFiles;
        this.rootDir = rootDir;
        dirs = new HashSet<>();
        LOGGER.info("Persisting " + projectFiles.size() + " files at " + rootDir + ".");
        persist();
    }

    private void persist() throws IOException {
        dirs.add(rootDir);
        for (final ProjectFile projectFile : projectFiles.files()) {
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
            final FileWriter fileWriter = new FileWriter(filePath);
            final PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(projectFile.content());
            printWriter.close();
        }
    }

    Set<String> dirs() {
        return dirs;
    }

    public String rootDir() {
        return rootDir;
    }
}
