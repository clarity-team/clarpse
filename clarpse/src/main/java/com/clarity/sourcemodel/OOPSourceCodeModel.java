package com.clarity.sourcemodel;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A language independent representation of a project that reveals its
 * structural buildup.
 *
 * @author Muntazir Fadhel
 */
public class OOPSourceCodeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public OOPSourceCodeModel() { }

    private final Map<String, Component> components = new ConcurrentHashMap<String, Component>();

    public Map<String, Component> getComponents() throws Exception {
        return components;
    }

    public
    void merge(final OOPSourceCodeModel sourceModel) throws Exception {

        insertComponents(sourceModel.getComponents());
    }

    public void insertComponent(final Component component) throws Exception {
        getComponents().put(component.getUniqueName(), component);
    }

    public boolean containsComponent(final String componentName) throws Exception {
        return getComponents().containsKey(componentName);
    }

    public Component getComponent(final String componentName) throws Exception {
        return this.getComponents().get(componentName);
    }

    public void insertComponents(final Map<String, Component> newCmps) throws Exception {

        for (final Map.Entry<String, Component> entry : newCmps.entrySet()) {
            insertComponent(entry.getValue());
        }
    }
}
