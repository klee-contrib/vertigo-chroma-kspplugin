package io.vertigo.chroma.kspplugin.ui.console.patternMatchListeners;

import io.vertigo.chroma.kspplugin.ui.console.hyperlinks.KspNameHyperlink;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class KspNamePatternMatchListener implements IPatternMatchListenerDelegate {

	private TextConsole connectedConsole;

	@Override
	public void connect(TextConsole console) {
		connectedConsole = console;
	}

	@Override
	public void disconnect() {
		connectedConsole = null;
	}

	@Override
	public void matchFound(PatternMatchEvent event) {

		/* Extrait la chaîne trouvée. */
		String kspNameCandidate = extractMatch(event);

		/* Cas de chaîne null. */
		if (kspNameCandidate == null) {
			return;
		}

		/* Ajoute un lien dans la console. */
		addConsoleLink(kspNameCandidate, event);
	}

	private String extractMatch(PatternMatchEvent event) {
		try {
			return connectedConsole.getDocument().get(event.getOffset(), event.getLength());
		} catch (BadLocationException e) {
			ErrorUtils.handle(e);
		}

		return null;
	}

	private void addConsoleLink(String kspNameCandidate, PatternMatchEvent event) {
		try {
			/* Créé un lien de navigation vers le KSP. */
			IHyperlink hyperlink = new KspNameHyperlink(kspNameCandidate);

			/* Ajoute le lien à la console. */
			connectedConsole.addHyperlink(hyperlink, event.getOffset(), event.getLength());
		} catch (BadLocationException e) {
			ErrorUtils.handle(e);
		}
	}
}
