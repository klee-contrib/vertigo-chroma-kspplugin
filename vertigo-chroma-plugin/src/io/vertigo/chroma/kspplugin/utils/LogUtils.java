package io.vertigo.chroma.kspplugin.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Méthodes utilitaires pour gérer les logs.
 */
public final class LogUtils {

	private static final Logger LOGGER = Logger.getLogger("vertigo.chroma.kspplugin");

	private LogUtils() {
		// RAS
	}

	/**
	 * Gère les exceptions attrapées par le plugin.
	 * 
	 * @param e Exception.
	 */
	public static void info(String s) {
		LOGGER.log(Level.INFO, s);
	}
}
