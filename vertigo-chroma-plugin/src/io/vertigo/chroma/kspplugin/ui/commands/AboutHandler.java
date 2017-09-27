package io.vertigo.chroma.kspplugin.ui.commands;

import io.vertigo.chroma.kspplugin.ui.dialogs.AboutDialog;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handler de la commande permettant d'ouvrir la fenêtre "A propos de".
 */
public class AboutHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		/* Ouvre la fenêtre "A propos de" */
		new AboutDialog(UiUtils.getShell()).open();

		return null;
	}
}
