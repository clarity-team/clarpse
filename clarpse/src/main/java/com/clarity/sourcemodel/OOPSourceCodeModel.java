package com.clarity.sourcemodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A language independent representation of a codebase that reveals its
 * structural buildup.
 *
 * @author Muntazir Fadhel
 */
public class OOPSourceCodeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public OOPSourceCodeModel() { }

    private final Map<String, Component> components = new HashMap<String, Component>();

    public Map<String, Component> getComponents() {
        return components;
    }

    public
    void merge(final OOPSourceCodeModel sourceModel) {

        insertComponents(sourceModel.getComponents());
    }

    public void insertComponent(final Component component) {

        components.put(component.uniqueName(), component);
    }

    public boolean containsComponent(final String componentName) {
        return getComponents().containsKey(componentName);
    }

    public Component getComponent(final String componentName) {
        return this.getComponents().get(componentName.replaceAll("\\s+", ""));
    }

    public void insertComponents(final Map<String, Component> newCmps) {

        for (final Map.Entry<String, Component> entry : newCmps.entrySet()) {
            insertComponent(entry.getValue());
        }
    }
}
