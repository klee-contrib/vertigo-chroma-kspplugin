package io.vertigo.chroma.kspplugin.model;

/**
 * Enumération des pattern de référencement d'un DTO dans un KSP.
 */
public enum DtoReferencePattern {

	/**
	 * Le DTO est référence comme un domaine
	 * <p>
	 * Exemple : DO_DT_UTILISATEUR_DTO, DO_DT_UTILISATEUR_DTC
	 * </p>
	 */
	DOMAIN,

	/**
	 * Le DTO est référencé comme un nom simple.
	 * <p>
	 * Exemple : DT_UTILISATEUR.
	 * </p>
	 */
	SIMPLE_NAME
}
