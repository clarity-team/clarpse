package com.clarity.sourcemodel;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * A language independent representation of a project that reveals it
 * structural buildup.
 *
 * @author Muntazir Fadhel
 *
 */
public class OOPSourceCodeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public OOPSourceCodeModel() { }
    /**
     * list of class components.
     */
    private final Map<String, Component> components = new ConcurrentHashMap<String, Component>();

    /**
     * @return components contained in this source model
     */
    public Map<String, Component> getComponents() throws Exception {
        return components;
    }

    public
    void merge(final OOPSourceCodeModel sourceModel) throws Exception {

        insertComponents(sourceModel.getComponents());
    }

    /**
     * @param component class component to add to the source model.
     * @throws Exception
     */
    public void insertComponent(final Component component) throws Exception {
        getComponents().put(component.getUniqueName(), component);
    }

    public boolean containsComponent(final String componentName) {
        return components.containsKey(componentName);
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
