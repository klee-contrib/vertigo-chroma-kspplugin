package io.vertigo.chroma.kspplugin.ui.editors.analysis;

/**
 * Couple d'accolades ouvrantes et fermantes.
 */
public class OpenCloseCurlyBrace implements OpenCloseCouple {

	@Override
	public OpenCloseCharacter getOpenCharacter() {
		return OpenCloseCharacter.OPEN_CURLY_BRACE;
	}

	@Override
	public OpenCloseCharacter getCloseCharacter() {
		return OpenCloseCharacter.CLOSE_CURLY_BRACE;
	}

	@Override
	public String getMissingOpeningMessage() {
		return "Missing opening bracket for this closing curly brace.";
	}

	@Override
	public String getMissingClosingMessage() {
		return "Missing closing bracket for this opening curly brace.";
	}
}
