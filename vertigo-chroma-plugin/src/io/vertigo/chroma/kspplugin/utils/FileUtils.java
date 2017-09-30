package io.vertigo.chroma.kspplugin.utils;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Méthodes utilitaires pour gérer des fichiers.
 */
public final class FileUtils {

	private FileUtils() {
		// RAS.
	}

	/**
	 * Calcul le chemin absolu d'un chemin relatif à un chemin absolu.
	 * 
	 * @param absoluteFilePath Chemin absolu de référence.
	 * @param relativeFilePath Chemin relatif.
	 * @return Chemin absolu du chemin relatif.
	 */
	public static String getAbsolutePath(String absoluteFilePath, String relativeFilePath) {
		try {
			Path basePath = FileSystems.getDefault().getPath(absoluteFilePath); // NOSONAR
			Path resolvedPath = basePath.getParent().resolve(relativeFilePath);
			Path absolutePath = resolvedPath.normalize();
			return absolutePath.toString();
		} catch (IllegalArgumentException e) { // NOSONAR
			/* Erreur à la construction du fichier : on sort. */
			return null;
		}
	}
}
