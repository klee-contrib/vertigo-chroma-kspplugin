package io.vertigo.chroma.kspplugin.resources.core;

import io.vertigo.chroma.kspplugin.model.Navigable;

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * Contrat de la réalisation d'un magasin de ressources.
 *
 * @param <T> Type de l'élément.
 */
public interface ResourceStoreImplementor<T extends Navigable> {

	/**
	 * Indique si un fichier est candidat pour fournir des éléments pour le magasin.
	 * 
	 * @param file
	 * @return
	 */
	boolean isCandidate(IFile file);

	/**
	 * Obtient la liste des éléments à stocker dans le magasin pour un fournisseur de fichier donné.
	 * 
	 * @param fileProvider Fournisseur de fichier.
	 * @return Liste des éléments.
	 */
	List<T> getItems(FileProvider fileProvider);
}
