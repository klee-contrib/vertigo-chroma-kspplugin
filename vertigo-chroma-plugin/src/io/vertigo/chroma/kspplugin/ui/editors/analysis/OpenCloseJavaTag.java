package io.vertigo.chroma.kspplugin.ui.editors.analysis;

/**
 * Couple de tags Java.
 */
public class OpenCloseJavaTag implements OpenCloseCouple {

	@Override
	public OpenCloseCharacter getOpenCharacter() {
		return OpenCloseCharacter.OPEN_JAVA_TAG;
	}

	@Override
	public OpenCloseCharacter getCloseCharacter() {
		return OpenCloseCharacter.CLOSE_JAVA_TAG;
	}

	@Override
	public String getMissingOpeningMessage() {
		return "Missing opening Java tag for this closing Java tag.";
	}

	@Override
	public String getMissingClosingMessage() {
		return "Missing closing Java tag for this opening Java tag.";
	}
}
