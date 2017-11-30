package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Méthodes utilitaires pour gérer les propriétés d'une ressource.
 */
public final class PropertyUtils {

	private static final String LEGACY_VERSION_PROPERTY = "LEGACY_VERSION";

	private PropertyUtils() {
		// RAS.
	}

	public static String getLegacyVersion(IProject project) {
		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences node = projectScope.getNode(Activator.PLUGIN_ID);
		if (node == null) {
			LogUtils.info("Get legacy version : node null !");
			return null;
		}
		String legacyVersionName = node.get(LEGACY_VERSION_PROPERTY, null);
		if (legacyVersionName == null || legacyVersionName.isEmpty()) {
			return null;
		}

		return legacyVersionName;
	}

	public static void setLegacyVersion(IProject project, String legacyVersionName) {
		/*
		 * On fait l'action dans un job asynchrone pour éviter une ResourceException: The resource tree is locked for modifications
		 */

		Job job = new Job("KspPreferenceSaving") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IScopeContext projectScope = new ProjectScope(project);
				IEclipsePreferences node = projectScope.getNode(Activator.PLUGIN_ID);
				if (node == null) {
					LogUtils.info("Set legacy version : node null !");
					return Status.OK_STATUS;
				}
				node.put(LEGACY_VERSION_PROPERTY, legacyVersionName);
				try {
					node.flush();
				} catch (BackingStoreException e) {
					ErrorUtils.handle(e);
				}
				return Status.OK_STATUS;
			}
		};

		job.setPriority(Job.SHORT);
		job.schedule();
	}
}
