package io.vertigo.chroma.kspplugin.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

/**
 * Méthodes utilitaires pour gérer les propriétés d'une ressource.
 */
public final class PropertyUtils {

	private static final String LEGACY_VERSION_PROPERTY = "LEGACY_VERSION";

	private PropertyUtils() {
		// RAS.
	}

	public static String getLegacyVersion(IProject project) {
		try {
			String legacyVersionName = project.getPersistentProperty(new QualifiedName("", LEGACY_VERSION_PROPERTY));
			if (legacyVersionName == null || legacyVersionName.isEmpty()) {
				return null;
			}
		} catch (CoreException e) {
			ErrorUtils.handle(e);
		}

		return null;
	}

	public static void setLegacyVersion(IProject project, String legacyVersionName) {
		try {
			project.setPersistentProperty(new QualifiedName("", LEGACY_VERSION_PROPERTY), legacyVersionName);
		} catch (CoreException e) {
			ErrorUtils.handle(e);
		}
	}
}
