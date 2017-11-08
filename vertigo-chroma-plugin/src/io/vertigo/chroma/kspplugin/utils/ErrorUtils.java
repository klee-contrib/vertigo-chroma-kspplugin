package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.Activator;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

/**
 * Méthodes utilitaires pour gérer les erreurs.
 */
public final class ErrorUtils {

	private static final ILog LOGGER = Activator.getDefault().getLog();

	private ErrorUtils() {
		// RAS
	}

	/**
	 * Gère les exceptions attrapées par le plugin.
	 * 
	 * @param e Exception.
	 */
	public static void handle(Exception e) {
		LOGGER.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, "Erreur non gérée dans le plugin KSP.", e));
	}
}
