package io.vertigo.chroma.kspplugin.legacy;

import org.eclipse.core.resources.IProject;

/**
 * Interface d'écoute des changements de version d'un projet.
 */
public interface LegacyVersionListener {

	/**
	 * Gère le changement de version d'un projet.
	 * 
	 * @param project Projet.
	 * @param newVerwion Nouvelle version.
	 */
	void versionChanged(IProject project, LegacyVersion newVerwion);
}
