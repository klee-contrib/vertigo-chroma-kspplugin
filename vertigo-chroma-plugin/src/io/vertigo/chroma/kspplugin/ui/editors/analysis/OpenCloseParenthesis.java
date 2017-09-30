package io.vertigo.chroma.kspplugin.ui.editors.analysis;

/**
 * Couple de parenthèses ouvrantes et fermantes.
 */
public class OpenCloseParenthesis implements OpenCloseCouple {

	@Override
	public OpenCloseCharacter getOpenCharacter() {
		return OpenCloseCharacter.OPEN_PARENTHESIS;
	}

	@Override
	public OpenCloseCharacter getCloseCharacter() {
		return OpenCloseCharacter.CLOSE_PARENTHESIS;
	}

	@Override
	public String getMissingOpeningMessage() {
		return "Missing opening parenthesis for this closing parenthesis.";
	}

	@Override
	public String getMissingClosingMessage() {
		return "Missing closing parenthesis for this opening parenthesis.";
	}
}
