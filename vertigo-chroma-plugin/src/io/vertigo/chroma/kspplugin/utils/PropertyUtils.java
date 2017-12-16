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

	private static final ProjectPropertyDescriptor LEGACY_VERSION = new ProjectPropertyDescriptor("LEGACY_VERSION");
	private static final ProjectPropertyDescriptor DTO_PARENT_CLASSES = new ProjectPropertyDescriptor("DTO_PARENT_CLASSES");

	private PropertyUtils() {
		// RAS.
	}

	public static String getLegacyVersion(IProject project) {
		return LEGACY_VERSION.getValue(project);
	}

	public static void setLegacyVersion(IProject project, String legacyVersionName) {
		LEGACY_VERSION.setValue(project, legacyVersionName);
	}

	public static String getDtoParentClasses(IProject project) {
		String serialized = DTO_PARENT_CLASSES.getValue(project);
		if (VertigoStringUtils.isEmpty(serialized)) {
			return null;
		}
		return serialized;
	}

	public static void setDtoParentClasses(IProject project, String dtoParents) {
		DTO_PARENT_CLASSES.setValue(project, dtoParents);
	}

	/**
	 * Descripteur de propriété d'un projet permettant d'obtenir et de définir la valeur de la propriété.
	 * <p>
	 * La valeur est stockée dans un fichier propre au plugin, dans le dossier .settings du projet.
	 * </p>
	 */
	private static class ProjectPropertyDescriptor {
		private final String propertyName;

		public ProjectPropertyDescriptor(String propertyName) {
			this.propertyName = propertyName;
		}

		private String getValue(IProject project) {
			IEclipsePreferences node = getProjectPluginNode(project);
			if (node == null) {
				LogUtils.info("Get " + propertyName + " : node null !");
				return null;
			}
			String propertyValue = node.get(propertyName, null);
			if (propertyValue == null || propertyValue.isEmpty()) {
				return null;
			}

			return propertyValue;
		}

		private void setValue(IProject project, String propertyValue) {
			/*
			 * On fait l'action dans un job asynchrone pour éviter une ResourceException: The resource tree is locked for modifications
			 */

			Job job = new Job("KspPreferenceSaving") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					IEclipsePreferences node = getProjectPluginNode(project);
					if (node == null) {
						LogUtils.info("Set " + propertyName + " : node null !");
						return Status.OK_STATUS;
					}
					if (VertigoStringUtils.isEmpty(propertyValue)) {
						node.remove(propertyName);
					} else {
						node.put(propertyName, propertyValue);
					}

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

		private static IEclipsePreferences getProjectPluginNode(IProject project) {
			IScopeContext projectScope = new ProjectScope(project);
			return projectScope.getNode(Activator.PLUGIN_ID);
		}
	}
}
