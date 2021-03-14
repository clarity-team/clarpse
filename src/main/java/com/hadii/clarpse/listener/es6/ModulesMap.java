package com.hadii.clarpse.listener.es6;

import org.apache.commons.io.FilenameUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModulesMap {

    private final Map<String, ES6Module> modulesMap = new HashMap<>();

    public void insertModule(ES6Module module) {
        this.modulesMap.put(module.modulePath(), module);
    }

    public ES6Module module(String modulePath) {
        return this.modulesMap.get(modulePath);
    }

    public ES6Module moduleByFilePath(String filePath) {
        return this.modulesMap.get(FilenameUtils.removeExtension(filePath));
    }

    public boolean containsModule(String modulePath) {
        return this.modulesMap.containsKey(modulePath);
    }

    public Collection<ES6Module> modules() {
        return this.modulesMap.values();
    }

    public List<ES6Module> matchingModules(String importedModuleDir) {
        return this.modules().stream().filter(module -> module.modulePath()
                .endsWith(FilenameUtils.removeExtension(importedModuleDir)))
                .collect(Collectors.toList());
    }
}
