package io.vertigo.chroma.kspplugin.legacy;

import io.vertigo.chroma.kspplugin.model.Manager;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;
import io.vertigo.chroma.kspplugin.utils.JdtUtils;
import io.vertigo.chroma.kspplugin.utils.LogUtils;
import io.vertigo.chroma.kspplugin.utils.PropertyUtils;
import io.vertigo.chroma.kspplugin.utils.ResourceUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * Gère les différentes versions du framework Vertigo/Kasper.
 */
public final class LegacyManager implements Manager, IResourceChangeListener {

	private static LegacyManager instance;
	private final ProjectVersionMap map = new ProjectVersionMap();
	private final Collection<LegacyVersionListener> legacyVersionListeners = new ArrayList<>();

	/**
	 * @return Instance du singleton.
	 */
	public static synchronized LegacyManager getInstance() {
		if (instance == null) {
			instance = new LegacyManager();
		}
		return instance;
	}

	@Override
	public void init() {
		initStore();
		initListener();
	}

	/**
	 * Retourne la stratégie pour un projet donné.
	 * 
	 * @param project Projet.
	 * @return Stratégie.
	 */
	public LegacyStrategy getStrategy(IProject project) {
		return getVersion(project).getStrategy();
	}

	/**
	 * Retourne la stratégie pour le projet d'un fichier donné.
	 * 
	 * @param file Fichier.
	 * @return Stratégie.
	 */
	public LegacyStrategy getStrategy(IFile file) {
		return getStrategy(file.getProject());
	}

	/**
	 * Retourne la stratégie pour le projet de l'éditeur courant.
	 * 
	 * @return Stratégie.
	 */
	public LegacyStrategy getCurrentStrategy() {
		return getStrategy(UiUtils.getCurrentEditorProject());
	}

	/**
	 * Obtient la version d'un projet.
	 * 
	 * @param project Projet.
	 * @return Version.
	 */
	public LegacyVersion getVersion(IProject project) {
		return this.map.getOrDefault(project, LegacyVersion.NO_FRAMEWORK);
	}

	/**
	 * Met à jour la version d'un projet.
	 * <p>
	 * Recalcule les stores.
	 * </p>
	 * 
	 * @param project Projet.
	 * @param legacyVersion Version.
	 */
	public void setVersion(IProject project, LegacyVersion legacyVersion) {

		setProjectVersion(project, legacyVersion);

		fireLegacyVersionChanged(project, legacyVersion);
	}

	public LegacyVersion getDefaultVersion(IProject project) {
		return getProjectLegacyVersion(project);
	}

	public void addLegacyVersionChangedListener(LegacyVersionListener listener) {
		legacyVersionListeners.add(listener);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event == null || event.getDelta() == null || event.getType() != IResourceChangeEvent.POST_CHANGE) {
			return;
		}

		try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) throws CoreException {
					final IResource resource = delta.getResource();

					/* Visite le workspace. */
					if (resource instanceof IWorkspaceRoot) {
						return true;
					}

					/* Visite le projet. */
					if (resource instanceof IProject) {
						return true;
					}

					/* Cas d'un ajout : potentiellement une ouverture de projet. */
					if (delta.getKind() == IResourceDelta.ADDED) {
						handleProject(resource.getProject());
					}

					return false;
				}
			});
		} catch (CoreException e) {
			ErrorUtils.handle(e);
		}
	}

	private void initStore() {
		/* Parcourt les projets ouverts. */
		for (IProject project : ResourceUtils.getProjectMap().keySet()) {
			handleProject(project);
		}
	}

	/**
	 * Initialise le listener de ressources du workspace.
	 */
	private void initListener() {
		/* Comme la durée de vie du store est celle du plugin, il n'est pas nécessaire de prévoir de se désabonner. */
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	private void fireLegacyVersionChanged(IProject project, LegacyVersion legacyVersion) {
		for (LegacyVersionListener listener : legacyVersionListeners) {
			listener.versionChanged(project, legacyVersion);
		}
	}

	private void handleProject(IProject project) {
		/* Vérifie si le projet n'est pas déjà connu. */
		if (this.map.containsKey(project)) {
			return;
		}

		String legacyVersionName = PropertyUtils.getLegacyVersion(project);

		/* La version est stockée dans les propriétés du projet : on l'utilise. */
		if (legacyVersionName != null) {
			LegacyVersion propertyVersion = LegacyVersion.valueOf(legacyVersionName);
			setProjectVersion(project, propertyVersion);
			return;
		}

		/* Version inconnue : on calcule la version par défaut. */
		LegacyVersion defaultVersion = getDefaultVersion(project);
		setProjectVersion(project, defaultVersion);
	}

	private void setProjectVersion(IProject project, LegacyVersion legacyVersion) {
		LogUtils.info("Projet " + project.getName() + " en version " + legacyVersion.name());

		/* Récupère la stratégie pour la version */
		this.map.put(project, legacyVersion);

		PropertyUtils.setLegacyVersion(project, legacyVersion.name());
	}

	private static LegacyVersion getProjectLegacyVersion(IProject project) {

		/* Vérifie que le projet est un projet Java. */
		if (!JdtUtils.isJavaProject(project)) {
			return LegacyVersion.NO_FRAMEWORK;
		}

		/* Vertigo */
		if (isTypeExists("io.vertigo.dynamox.task.TaskEngineProc", project)) {
			return LegacyVersion.VERTIGO;
		}

		/* Kasper 6 */
		if (isTypeExists("org.primefaces.context.RequestContext", project)) {
			return LegacyVersion.KASPER_6;
		}

		/* Kasper 6 - Spark */
		if (isTypeExists("spark.commons.SparkRuntimeException", project)) {
			return LegacyVersion.SPARK_KASPER_6;
		}

		/* Kasper 5 */
		if (isTypeExists("org.codehaus.janino.ScriptEvaluator", project)) {
			return LegacyVersion.KASPER_5;
		}

		/* Kasper 4 */
		if (isTypeExists("kasperx.annotation.DtDefinition", project)) {
			return LegacyVersion.KASPER_4;
		}

		/* Kasper 3 OO */
		if (isTypeExists("kasper.model.KFile", project)) {
			return LegacyVersion.KASPER_3_OO;
		}

		/* Kasper 3 */
		if (isTypeExists("kasper.model.SuperDtObject", project)) {
			return LegacyVersion.KASPER_3;
		}

		/* Kasper 2 */
		if (isTypeExists("kasper.model.DtObject", project)) {
			return LegacyVersion.KASPER_2;
		}

		/* Aucun framework */
		return LegacyVersion.NO_FRAMEWORK;
	}

	private static boolean isTypeExists(String fullyQualifiedName, IProject project) {
		return JdtUtils.getJavaType(fullyQualifiedName, project) != null;
	}

	/**
	 * Map projet vers version.
	 */
	private static class ProjectVersionMap extends HashMap<IProject, LegacyVersion> {
		private static final long serialVersionUID = 1L;
	}
}
