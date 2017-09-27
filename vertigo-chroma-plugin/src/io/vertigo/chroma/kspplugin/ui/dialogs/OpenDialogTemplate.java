package io.vertigo.chroma.kspplugin.ui.dialogs;

/**
 * Template pour la factory permettant d'ouvrir une fenêtre de recherche.
 */
public interface OpenDialogTemplate {

	/**
	 * Renvoie la nature de l'objet recherché.
	 * 
	 * @return Nature.
	 */
	String getNature();

	/**
	 * Charge la source de données de la fenêtre de dialogue.
	 * 
	 * @return Liste des éléments à rechercher.
	 */
	Object[] getElements();
}
