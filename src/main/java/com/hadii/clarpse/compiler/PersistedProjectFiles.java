package com.hadii.clarpse.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents source files to be parsed.
 */
public class PersistedProjectFiles {

    private ProjectFiles projectFiles;
    private String rootDir;
    private Set<String> dirs;

    public PersistedProjectFiles(ProjectFiles projectFiles, String rootDir) throws IOException {
        this.projectFiles = projectFiles;
        this.rootDir = rootDir;
        this.dirs = new HashSet<>();
        persist();
    }

    private void persist() throws IOException {
        this.dirs.add(rootDir);
        for (ProjectFile projectFile : this.projectFiles.getFiles()) {
            String filePath = this.rootDir + File.separator + projectFile.path();
            File file = new File(filePath);
            File parent = new File(file.getParent());
            if (!parent.exists()) {
                Files.createDirectories(parent.toPath());
            }
            while (!this.dirs.contains(parent.getPath())) {
                this.dirs.add(parent.getPath());
                parent = new File(parent.getParent());
            }
            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(projectFile.content());
            printWriter.close();
        }
    }

    public Set<String> dirs() {
        return this.dirs;
    }

    public String rootDir() {
        return this.rootDir;
    }
}
