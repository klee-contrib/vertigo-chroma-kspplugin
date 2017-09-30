package io.vertigo.chroma.kspplugin.ui.commands.core;

import io.vertigo.chroma.kspplugin.model.DaoImplementation;
import io.vertigo.chroma.kspplugin.model.DtoFile;
import io.vertigo.chroma.kspplugin.model.KspDeclaration;
import io.vertigo.chroma.kspplugin.resources.DaoManager;
import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.resources.KspManager;
import io.vertigo.chroma.kspplugin.utils.MessageUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

/**
 * Navigateur entre les différents objets (déclaration KSP, implémentation Java).
 */
public final class Navigator {

	private Navigator() {
		// RAS.
	}

	/**
	 * Trouve une implémentation de DAO à partir de son nom Java et navigue dessus.
	 * 
	 * @param javaName Nom Java de la méthode de DAO. Exemple : getUtilisateur.
	 */
	public static void goToDaoImplementation(String javaName) {
		DaoImplementation daoImplementation = DaoManager.getInstance().findDaoImplementation(javaName);
		if (daoImplementation == null) {
			MessageUtils.showNoTaskImplementationFoundMessage(javaName);
			return;
		}
		UiUtils.navigateTo(daoImplementation);
	}

	/**
	 * Trouve une déclaration KSP à partir du nom Java et navigue dessus.
	 * 
	 * @param javaName Nom Java de l'objet déclaré. Exemple : getUtilisateur, UtilisateurCritere, Commentaire.
	 */
	public static void goToKspDeclaration(String javaName) {
		KspDeclaration kspDeclaration = KspManager.getInstance().findKspDeclaration(javaName);
		if (kspDeclaration == null) {
			MessageUtils.showNoKspDeclarationFoundMessage(javaName);
			return;
		}

		UiUtils.navigateTo(kspDeclaration);
	}

	/**
	 * Trouve un fichier de DTO à partir de son nom Java et navigue dessus.
	 * 
	 * @param javaName Nom Java du DTO. Exemple : UtilisateurCritere.
	 */
	public static void goToDtoFile(String javaName) {
		DtoFile dtoFile = DtoManager.getInstance().findDtoFile(javaName);
		if (dtoFile == null) {
			MessageUtils.showNoJavaFileFoundMessage(javaName);
			return;
		}
		UiUtils.navigateTo(dtoFile);
	}
}
