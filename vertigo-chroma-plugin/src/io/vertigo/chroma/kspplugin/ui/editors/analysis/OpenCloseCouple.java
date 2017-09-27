package io.vertigo.chroma.kspplugin.ui.editors.analysis;

/**
 * Contrat d'un couple de caractères ouvrants et fermants.
 *
 */
public interface OpenCloseCouple {

	/**
	 * @return Caractère ouvrant.
	 */
	OpenCloseCharacter getOpenCharacter();

	/**
	 * @return Caractère fermant.
	 */
	OpenCloseCharacter getCloseCharacter();

	/**
	 * @return Message pour un caractère ouvrant manquant.
	 */
	String getMissingOpeningMessage();

	/**
	 * @return Message pour un caractère fermant manquant.
	 */
	String getMissingClosingMessage();
}
