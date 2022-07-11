package com.hadii.clarpse.compiler.go;

import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents source files to be parsed.
 */
public class GoModules {

    private static final Logger LOGGER = LogManager.getLogger(GoModules.class);

    private List<GoModule> goModules = new ArrayList<>();

    public GoModules(final ProjectFiles projectFiles) {
        this.goModules = generateModules(projectFiles);
        LOGGER.info(this.goModules.size() + " Go modules were detected.");
    }

    private List<GoModule> generateModules(final ProjectFiles projectFiles) {
        List<GoModule> goModules = new ArrayList<>();
        // Collected all go.mod files from the given list of all project files.
        final Map<ProjectFile, List> moduletoFilesMap = projectFiles.files().stream().filter(
            projectFile -> projectFile.path().endsWith("go.mod")
        ).collect(Collectors.toMap(projectFile -> projectFile, projectFile -> new ArrayList()));
        // Group associated source files and modules together.
        projectFiles.files().forEach(projectFile -> {
            for (final ProjectFile moduleFile : moduletoFilesMap.keySet()) {
                if (moduleFile.dir().equals("/") || projectFile.path().startsWith(moduleFile.dir())) {
                    moduletoFilesMap.get(moduleFile).add(projectFile);
                }
            }
        });
        moduletoFilesMap.keySet().forEach(moduleFile -> {
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
