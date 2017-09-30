package io.vertigo.chroma.kspplugin.model;

import java.util.List;

/**
 * Représente un fichier d'implémentation de service métier et l'ensemble de ses méthodes.
 */
public class ServiceFile {

	private List<ServiceImplementation> serviceImplementations;

	/**
	 * Créé une nouvelle instance de ServiceFile.
	 * 
	 * @param file Ressource fichier dans le workspace.
	 * @param serviceImplementations Liste des méthodes.
	 */
	public ServiceFile(List<ServiceImplementation> serviceImplementations) {
		this.serviceImplementations = serviceImplementations;
	}

	/**
	 * @return Renvoie la liste des déclarations KSP.
	 */
	public List<ServiceImplementation> getServiceImplementations() {
		return serviceImplementations;
	}
}
