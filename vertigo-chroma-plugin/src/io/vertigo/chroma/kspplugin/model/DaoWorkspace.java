package io.vertigo.chroma.kspplugin.model;

import java.util.List;

/**
 * Stocke l'ensemble des méthodes de DAO de tout un workspace.
 */
public class DaoWorkspace {

	private final List<DaoImplementation> daoImplementations;

	public DaoWorkspace(List<DaoImplementation> daoImplementations) {
		this.daoImplementations = daoImplementations;
	}

	public List<DaoImplementation> getDaoImplementations() {
		return daoImplementations;
	}
}
