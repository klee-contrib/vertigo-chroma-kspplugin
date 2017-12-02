package io.vertigo.chroma.kspplugin.model;

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * Représente un fichier DAO/PAO et l'ensemble de ses tâches.
 */
public class DaoFile {

	private final String name;
	private final IFile file;
	private final List<DaoImplementation> daoImplementations;

	/**
	 * Créé une nouvelle instance de DaoFile.
	 * 
	 * @param name Nom du DAO/PAO.
	 * @param file Ressource fichier du DAO/PAO dans le workspace.
	 * @param daoImplementations Liste des tâches.
	 */
	public DaoFile(String name, IFile file, List<DaoImplementation> daoImplementations) {
		this.name = name;
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

	/**
	 * @return Nom de la classe DAO/PAO.
	 */
	public String getName() {
		return name;
	}
}
