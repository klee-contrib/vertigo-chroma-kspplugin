package io.vertigo.chroma.kspplugin.ui.commands;

import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.ui.commands.core.Navigator;
import io.vertigo.chroma.kspplugin.utils.MessageUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handler de la commande permettant de naviguer du nom Java vers la déclaration KSP.
 */
public class GoToKspDeclarationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		executeCore();
		return null;
	}

	private static void executeCore() {

		/* Extrait le mot courant. */
		String javaName = UiUtils.getCurrentEditorCurrentWord(WordSelectionType.CAMEL_CASE);
		if (javaName == null) {
			MessageUtils.showNoJavaWordSelectedMessage();
			return;
		}

		/* Ouvre le KSP. */
		Navigator.goToKspDeclaration(javaName);
	}
}
