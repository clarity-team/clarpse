package com.hadii.clarpse.compiler.go;

import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents source files to be parsed.
 */
public class GoModule {

    private final ProjectFiles projectFiles;
    private final String moduleName;

    public GoModule(ProjectFiles files, ProjectFile moduleFile) {
        this.moduleName = this.extractModuleName(moduleFile);
        if (this.moduleName.isEmpty()) {
            throw new IllegalArgumentException("Module name cannot be empty!");
        }
        this.projectFiles = new ProjectFiles(Lang.GOLANG);
        files.files().stream().forEach(file -> {
            if (file.path().endsWith(".go")) {
                if (moduleFile.dir().equals("/")) {
                    this.projectFiles.insertFile(new ProjectFile(
                        file.path(),
                        file.content().replaceAll(this.moduleName + "/", "")
                            .replaceAll(this.moduleName, "")));
                } else {
                    // Non-root level module, transform file path to relative path
                    this.projectFiles.insertFile(new ProjectFile(
                        file.path().replace(moduleFile.dir(), ""),
                        file.content().replaceAll(this.moduleName + "/", "")
                            .replaceAll(this.moduleName, "")));
                }
            }
        });
    }

    private String extractModuleName(ProjectFile moduleFile) {
        Pattern p = Pattern.compile("^ *module *(.*) *$", Pattern.MULTILINE);
        Matcher m = p.matcher(moduleFile.content());
        if (m.find()) {
            return m.group(1);
        } else {
            throw new IllegalArgumentException("Could not extract module name!");
        }
    }

    public ProjectFiles getProjectFiles() {
        return projectFiles;
    }

    public String getModuleName() {
        return moduleName;
    }
}
