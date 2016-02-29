package com.clarity.sourcemodel;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A language independent representation of a project that reveals it structural
 * buildup.
 *
 * @author Muntazir Fadhel
 */
public class OOPSourceCodeModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * list of class components.
	 */
	private final Map<String, Component> components = new LinkedHashMap<String, Component>();

	/**
	 * @return components contained in this source model
	 */
	public final Map<String, Component> getComponents() {
		return components;
	}

	/**
	 * @param component
	 *            class component to be removed.
	 */
	protected final void removeComponent(final Component component) {
		components.remove(component.getUniqueName());
	}

	/**
	 * @param component
	 *            class component to add to the source model.
	 */
	public final void insertComponent(final Component component) {
		synchronized (this) {
			components.put(component.getUniqueName(), component);
		}
	}
}
