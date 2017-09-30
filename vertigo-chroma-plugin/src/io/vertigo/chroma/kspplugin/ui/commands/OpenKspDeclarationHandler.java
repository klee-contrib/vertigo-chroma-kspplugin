package io.vertigo.chroma.kspplugin.ui.commands;

import io.vertigo.chroma.kspplugin.resources.KspManager;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogFactory;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogTemplate;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handler de la commande permettant d'ouvrir une déclaration de KSP.
 */
public class OpenKspDeclarationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		OpenDialogFactory.openDialog(new OpenDialogTemplate() {

			@Override
			public String getNature() {
				return "KSP declaration";
			}

			@Override
			public Object[] getElements() {
				/* Charge les KSP du cache du KspManager. */
				return KspManager.getInstance().getWorkspace().getKspDeclarations().toArray();
			}
		});
		return null;
	}
}
