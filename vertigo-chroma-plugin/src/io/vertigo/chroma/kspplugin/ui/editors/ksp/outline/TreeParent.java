package io.vertigo.chroma.kspplugin.ui.editors.ksp.outline;

import java.util.ArrayList;

public class TreeParent extends TreeObject {
	private ArrayList<TreeObject> children;

	public TreeParent(String name) {
		super(name);
		children = new ArrayList<>();
	}

	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeObject child) {
		children.remove(child); // NOSONAR
		child.setParent(null);
	}

	public TreeObject[] getChildren() {
		return children.toArray(new TreeObject[children.size()]);
	}

	public boolean hasChildren() {
		return children.isEmpty();
	}
}
