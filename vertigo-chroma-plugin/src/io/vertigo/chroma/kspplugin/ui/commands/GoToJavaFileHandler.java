package io.vertigo.chroma.kspplugin.ui.commands;

import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.ui.commands.core.Navigator;
import io.vertigo.chroma.kspplugin.utils.KspStringUtils;
import io.vertigo.chroma.kspplugin.utils.MessageUtils;
import io.vertigo.chroma.kspplugin.utils.StringUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handler de la commande permettant de naviguer du nom KSP vers l'implémentation Java.
 */
public class GoToJavaFileHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		executeCore();
		return null;
	}

	private static void executeCore() {

		/* Extrait le mot courant. */
		String currentWord = UiUtils.getCurrentEditorCurrentWord(WordSelectionType.CONSTANT_CASE);
		if (currentWord == null) {
			MessageUtils.showNoKspWordSelectedMessage();
			return;
		}

		/* Extrait un nom de DtDefinition. */
		String dtName = KspStringUtils.getDtDefinitionName(currentWord);
		if (dtName != null) {
			/* Calcul le nom en PascalCase */
			String javaName = StringUtils.toPascalCase(dtName);
			/* Ouvre le fichier Java. */
			Navigator.goToDtoFile(javaName);

			return;
		}

		/* Extrait un nom de tâche DAO / PAO. */
		String taskName = KspStringUtils.getTaskName(currentWord);
		if (taskName != null) {
			/* Calcul le nom en PascalCase */
			String javaName = StringUtils.toCamelCase(taskName);
			/* Ouvre le fichier Java. */
			Navigator.goToDaoImplementation(javaName);

			return;
		}

		/* Aucun KSP trouvé. */
		MessageUtils.showNoKspFoundMessage(currentWord);

	}
}
