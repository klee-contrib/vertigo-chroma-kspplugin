package io.vertigo.chroma.kspplugin.model;

import io.vertigo.chroma.kspplugin.utils.StringUtils;

import java.util.function.Predicate;

/**
 * Enumération des types de sélection de mot.
 * <p>
 * Utilisé pour la détection de mot courant dans un éditeur, pour les détecteurs de lien et l'autocomplétion.
 * </p>
 */
public enum WordSelectionType {

	/**
	 * Mot en CONSTANT_CASE.
	 */
	CONSTANT_CASE(StringUtils::isConstantCase),

	/**
	 * Mot en camelCase.
	 */
	CAMEL_CASE(StringUtils::isCamelCase),

	/**
	 * Mot en casse SQL (minuscule, majuscule, chiffre, underscore).
	 */
	SNAKE_CASE(StringUtils::isSnakeCase),

	/**
	 * Mot en casse SQL (minuscule, majuscule, chiffre, underscore) avec #.
	 */
	SQL_PARAMETER_NAME(StringUtils::isSqlParameterName),

	/**
	 * Mot représentant un nom canonique Java.
	 */
	CANONICAL_JAVA_NAME(StringUtils::isCanonicalJavaName),

	/**
	 * Mot sans espace.
	 */
	NOT_SPACE(StringUtils::isNotSpace);

	private final Predicate<String> tester;

	WordSelectionType(Predicate<String> tester) {
		this.tester = tester;
	}

	public Predicate<String> getTester() {
		return tester;
	}
}
