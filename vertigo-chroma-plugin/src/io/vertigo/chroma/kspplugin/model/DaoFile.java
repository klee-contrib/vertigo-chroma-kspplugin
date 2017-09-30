package io.vertigo.chroma.kspplugin.model;

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * Représente un fichier DAO/PAO et l'ensemble de ses tâches.
 */
public class DaoFile {

	private IFile file;
	private List<DaoImplementation> daoImplementations;

	/**
	 * Créé une nouvelle instance de DaoFile.
	 * 
	 * @param file Ressource fichier du DAO/PAO dans le workspace.
	 * @param daoImplementations Liste des tâches.
	 */
	public DaoFile(IFile file, List<DaoImplementation> daoImplementations) {
		super();
		this.file = file;
		this.daoImplementations = daoImplementations;
		for (DaoImplementation daoImplementation : daoImplementations) {
			daoImplementation.setFile(this);
		}
	}

	/**
	 * @return Renvoie la ressource fichier DAO/PAO dans le workspace.
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * @return Renvoie la liste des tâches.
	 */
	public List<DaoImplementation> getDaoImplementations() {
		return daoImplementations;
	}
}
