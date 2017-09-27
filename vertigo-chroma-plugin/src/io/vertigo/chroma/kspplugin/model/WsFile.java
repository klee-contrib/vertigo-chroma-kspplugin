package io.vertigo.chroma.kspplugin.model;

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * Représente un fichier Java de webservices et l'ensemble de ses routes.
 */
public class WsFile {

	private IFile file;
	private List<WsRoute> wsRoutes;

	/**
	 * Créé une nouvelle instance de WsFile.
	 * 
	 * @param file Ressource fichier du webservice Java dans le workspace.
	 * @param wsRoutes Liste des routes.
	 */
	public WsFile(IFile file, List<WsRoute> wsRoutes) {
		this.file = file;
		this.wsRoutes = wsRoutes;
	}

	/**
	 * @return Renvoie la ressource fichier du webservice dans le workspace.
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * @return Renvoie la liste des routes.
	 */
	public List<WsRoute> getWsRoutes() {
		return wsRoutes;
	}
}
