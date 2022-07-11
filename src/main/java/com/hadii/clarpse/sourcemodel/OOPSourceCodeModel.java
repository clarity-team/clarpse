package com.hadii.clarpse.sourcemodel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A language independent representation of a codebase that reveals its
 * structural buildup.
 */
public class OOPSourceCodeModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(OOPSourceCodeModel.class);

    public OOPSourceCodeModel() {
    }

    private final Map<String, Component> components = new HashMap<>();

    private Map<String, Component> getComponents() {
        return components;
    }

    public void merge(final OOPSourceCodeModel sourceModel) {
        insertComponents(sourceModel.getComponents());
    }

    public int size() {
        return components.size();
    }

    public void insertComponent(final Component component) {
        LOGGER.debug("Inserted component " + component + ".");
        components.put(component.uniqueName(), component);
    }

    public boolean containsComponent(final String componentName) {
        return getComponents().containsKey(componentName);
    }

    public Optional<Component> getComponent(final String componentName) {
        return Optional.ofNullable(this.getComponents().get(componentName));
    }

    public void insertComponents(final Map<String, Component> newCmps) {
        for (final Map.Entry<String, Component> entry : newCmps.entrySet()) {
            insertComponent(entry.getValue());
        }
    }

    public void removeComponent(String cmpUniqueName) {
        this.components.remove(cmpUniqueName);
        LOGGER.debug("Removed component " + cmpUniqueName + ".");
    }

    public Stream<Component> components() {
        return components.values().stream();
    }
}
