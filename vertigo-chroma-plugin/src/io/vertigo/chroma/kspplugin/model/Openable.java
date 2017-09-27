package io.vertigo.chroma.kspplugin.model;

import org.eclipse.swt.graphics.Image;

/**
 * Contrat des objets utilisable dans une fenêtre de recherche.
 */
public interface Openable extends Navigable {

	/**
	 * Renvoie le texte principal à afficher.
	 * 
	 * @return Texte.
	 */
	String getText();

	/**
	 * Renvoie le texte qualifiant complètement l'élément.
	 * 
	 * @return Texte.
	 */
	String getQualifier();

	/**
	 * Renvoie l'icône illustrant l'élément.
	 * 
	 * @return Image.
	 */
	Image getImage();
}
