package com.hadii.clarpse.compiler.go;

import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents source files to be parsed.
 */
public class GoModules {

    private List<GoModule> goModules = new ArrayList<>();

    public GoModules(final ProjectFiles projectFiles) {
        this.goModules = generateModules(projectFiles);
    }

    private List<GoModule> generateModules(final ProjectFiles projectFiles) {
        List<GoModule> goModules = new ArrayList<>();
        final Map<ProjectFile, List> moduletoFilesMap = projectFiles.files().stream().filter(
                projectFile -> projectFile.path().endsWith("go.mod")
        ).collect(Collectors.toMap(projectFile -> projectFile, projectFile -> new ArrayList()));
        projectFiles.files().stream().forEach(projectFile -> {
            for (final ProjectFile moduleFile : moduletoFilesMap.keySet()) {
                if (projectFile.path().startsWith(moduleFile.dir())) {
                    moduletoFilesMap.get(moduleFile).add(new ProjectFile(
                            projectFile.path().replace(moduleFile.dir(), ""),
                            projectFile.content()));
                }
            }
        });
        moduletoFilesMap.keySet().stream().forEach(moduleFile -> {
            goModules.add(new GoModule(
                    new ProjectFiles(
                            Lang.GOLANG, moduletoFilesMap.get(moduleFile)), moduleFile));
        });
        return goModules;
    }

    public List<GoModule> list() {
        return this.goModules;
    }
}
