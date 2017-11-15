package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

/**
 * Méthodes utilitaires pour gérer les propriétés d'une ressource.
 */
public final class PropertyUtils {

	private static final String LEGACY_VERSION_PROPERTY = "LEGACY_VERSION";
	private static final QualifiedName LEGACY_VERSION_PROPERTY_KEY = new QualifiedName(Activator.PLUGIN_ID, LEGACY_VERSION_PROPERTY);

	private PropertyUtils() {
		// RAS.
	}

	public static String getLegacyVersion(IProject project) {
		try {
			String legacyVersionName = project.getPersistentProperty(LEGACY_VERSION_PROPERTY_KEY);
			if (legacyVersionName == null || legacyVersionName.isEmpty()) {
				return null;
			}

			return legacyVersionName;

		} catch (CoreException e) {
			ErrorUtils.handle(e);
		}

		return null;
	}

	public static void setLegacyVersion(IProject project, String legacyVersionName) {
		try {
			project.setPersistentProperty(LEGACY_VERSION_PROPERTY_KEY, legacyVersionName);
		} catch (CoreException e) {
			ErrorUtils.handle(e);
		}
	}
}
