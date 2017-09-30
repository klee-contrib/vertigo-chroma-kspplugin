package io.vertigo.chroma.kspplugin.ui.commands;

import io.vertigo.chroma.kspplugin.resources.ServiceManager;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogFactory;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogTemplate;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handler de la commande permettant de rechercher une implémentation de service.
 */
public class OpenServiceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		OpenDialogFactory.openDialog(new OpenDialogTemplate() {

			@Override
			public String getNature() {
				return "service method";
			}

			@Override
			public Object[] getElements() {
				/* Charge les services du cache du ServiceManager. */
				return ServiceManager.getInstance().getWorkspace().getServiceImplementations().toArray();
			}
		});
		return null;
	}
}
