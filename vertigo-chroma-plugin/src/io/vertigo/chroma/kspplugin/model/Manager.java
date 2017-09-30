package io.vertigo.chroma.kspplugin.model;

/**
 * Contrat des managers.
 * <p>
 * Les managers ont un état qui doit être initialisé au démarrage du plugin.
 * </p>
 */
@FunctionalInterface
public interface Manager {

	/**
	 * Initialise le manager.
	 */
	void init();
}
