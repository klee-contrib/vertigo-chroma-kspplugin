package io.vertigo.chroma.kspplugin.lexicon;

/**
 * Enumération des lexiques.
 */
public enum Lexicons {

	/**
	 * Mots-clés SQL (select, from...).
	 */
	SQL_KEY_WORDS("resources/sql_keywords.txt"),

	/**
	 * Verbes (create, alter, ...) des déclarations KSP.
	 */
	KSP_VERBS("resources/ksp_verbs.txt"),

	/**
	 * Prépositions des déclarations KSP.
	 */
	KSP_PREPOSITIONS("resources/ksp_prepositions.txt"),

	/**
	 * Natures (DtDefinition, Task, ...) des déclarations KSP.
	 */
	KSP_NATURES("resources/ksp_natures.txt"),

	/**
	 * Attributs des déclarations KSP.
	 */
	KSP_ATTRIBUTES("resources/ksp_attributes.txt"),

	/**
	 * Propriétés des déclarations KSP.
	 */
	KSP_PROPERTIES("resources/ksp_properties.txt");

	private final String path;

	Lexicons(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
