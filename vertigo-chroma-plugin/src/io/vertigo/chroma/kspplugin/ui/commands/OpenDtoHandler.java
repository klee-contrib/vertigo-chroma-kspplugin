package io.vertigo.chroma.kspplugin.ui.commands;

import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogFactory;
import io.vertigo.chroma.kspplugin.ui.dialogs.OpenDialogTemplate;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handler de la commande permettant de rechercher un DTO.
 */
public class OpenDtoHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		OpenDialogFactory.openDialog(new OpenDialogTemplate() {

			@Override
			public String getNature() {
				return "DTO";
			}

			@Override
			public Object[] getElements() {
				/* Charge les DTO du cache du DtoManager. */
				return DtoManager.getInstance().getWorkspace().getDtoFiles().toArray();
			}
		});

		return null;
	}
}
