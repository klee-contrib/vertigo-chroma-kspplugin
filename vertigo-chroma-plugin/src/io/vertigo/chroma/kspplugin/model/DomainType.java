package io.vertigo.chroma.kspplugin.model;

/**
 * Type de domaine.
 */
public enum DomainType {

	/**
	 * Inconnu.
	 */
	UNKNOWN,

	/**
	 * Domaine primitif (String, Entier...).
	 */
	PRIMITIVE,

	/**
	 * Domaine pour un DTO.
	 */
	DTO,

	/**
	 * Domaine pour une liste de DTO.
	 */
	DTC
}
