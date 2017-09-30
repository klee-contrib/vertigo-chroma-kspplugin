package io.vertigo.chroma.kspplugin.ui.commands;

import io.vertigo.chroma.kspplugin.resources.WsRouteManager;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogFactory;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogTemplate;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handler de la commande permettant de rechercher une route de webservice.
 */
public class OpenWsRouteHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		OpenDialogFactory.openDialog(new OpenDialogTemplate() {

			@Override
			public String getNature() {
				return "route";
			}

			@Override
			public Object[] getElements() {
				/* Charge les routes du cache du WsRouteManager. */
				return WsRouteManager.getInstance().getWorkspace().getWsRoutes().toArray();
			}
		});
		return null;
	}
}
