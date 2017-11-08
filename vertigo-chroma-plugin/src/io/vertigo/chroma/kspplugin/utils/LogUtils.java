package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.Activator;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

/**
 * Méthodes utilitaires pour gérer les logs.
 */
public final class LogUtils {

	private static final ILog LOGGER = Activator.getDefault().getLog();

	private LogUtils() {
		// RAS
	}

	/**
	 * Gère les exceptions attrapées par le plugin.
	 * 
	 * @param e Exception.
	 */
	public static void info(String s) {
		LOGGER.log(new Status(Status.INFO, Activator.PLUGIN_ID, Status.OK, s, null));
	}
}
