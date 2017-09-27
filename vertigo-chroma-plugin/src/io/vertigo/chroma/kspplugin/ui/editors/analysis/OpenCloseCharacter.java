package io.vertigo.chroma.kspplugin.ui.editors.analysis;

import java.util.regex.Pattern;

/**
 * Enumération des caractères ouvrants fermants dans un KSP.
 */
public enum OpenCloseCharacter {

	/**
	 * Accolade ouvrante.
	 */
	OPEN_CURLY_BRACE("(\\{)"),

	/**
	 * Accolade fermante.
	 */
	CLOSE_CURLY_BRACE("(\\})"),

	/**
	 * Parenthèse ouvrante.
	 */
	OPEN_PARENTHESIS("(\\()"),

	/**
	 * Parenthèse fermante.
	 */
	CLOSE_PARENTHESIS("(\\))"),

	/**
	 * Tag Java ouvrant.
	 */
	OPEN_JAVA_TAG("(<%)"),

	/**
	 * Tag Java fermant.
	 */
	CLOSE_JAVA_TAG("(%>)");

	private final Pattern pattern;

	/**
	 * Créé une nouvelle instance de OpenCloseCharacter.
	 * 
	 * @param regex Regex permettant de détecter le caractère.
	 */
	OpenCloseCharacter(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	/**
	 * Obtient le pattern permettant de détecter le caractère.
	 * 
	 * @return Pattern regex.
	 */
	public Pattern getPattern() {
		return pattern;
	}
}
