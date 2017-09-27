package io.vertigo.chroma.kspplugin.ui.editors.ksp.outline;

import org.eclipse.core.runtime.IAdaptable;

public class TreeObject implements IAdaptable {
	private String name;
	private TreeParent parent;

	public TreeObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	/**
	 * Appelé par le LabelProvider.
	 */
	@Override
	public String toString() {
		return getName();
	}

	public String getMainText() {
		return toString();
	}

	public String getSubText() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class key) {
		return null;
	}
}
