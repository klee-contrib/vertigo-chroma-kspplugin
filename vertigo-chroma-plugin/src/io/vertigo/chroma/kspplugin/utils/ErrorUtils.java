package io.vertigo.chroma.kspplugin.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Méthodes utilitaires pour gérer les erreurs.
 */
public final class ErrorUtils {

	private static final Logger LOGGER = Logger.getLogger("vertigo.chroma.kspplugin");

	private ErrorUtils() {
		// RAS
	}

	/**
	 * Gère les exceptions attrapées par le plugin.
	 * 
	 * @param e Exception.
	 */
	public static void handle(Exception e) {
		LOGGER.log(Level.SEVERE, "Erreur dans le plugin KSP.", e);
	}
}
