package io.vertigo.chroma.kspplugin.ui.commands;

import io.vertigo.chroma.kspplugin.resources.DaoManager;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogFactory;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogTemplate;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handler de la commande permettant de rechercher une méthode de DAO/PAO.
 */
public class OpenDaoHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		OpenDialogFactory.openDialog(new OpenDialogTemplate() {

			@Override
			public String getNature() {
				return "DAO/PAO method";
			}

			@Override
			public Object[] getElements() {
				/* Charge les DAO/PAO du cache du DaoManager. */
				return DaoManager.getInstance().getWorkspace().getDaoImplementations().toArray();
			}
		});
		return null;
	}
}
