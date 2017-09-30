package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.model.JavaProjectMap;
import io.vertigo.chroma.kspplugin.model.Navigable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Méthodes utilitaires pour gérer les resources du workspace.
 *
 */
public final class ResourceUtils {

	private static final String KSP_EXTENSION = "ksp";
	private static final String JAVA_EXTENSION = "java";

	private ResourceUtils() {
		// RAS.
	}

	/**
	 * Obtient le nom du projet d'un navigable.
	 * 
	 * @param navigable Navigable.
	 * @return Nom du projet.
	 */
	public static String getProjectName(Navigable navigable) {
		return getProject(navigable).getName();
	}

	/**
	 * Obtient le projet d'un navigable.
	 * 
	 * @param navigable Navigable.
	 * @return Projet.
	 */
	public static IProject getProject(Navigable navigable) {
		return navigable.getFileRegion().getProject();
	}

	/**
	 * Obtient la map Project vers Projet Java du workspace courant.
	 * 
	 * @return Map des projets.
	 */
	public static JavaProjectMap getProjectMap() {
		JavaProjectMap projects = new JavaProjectMap();

		/* Racine du workspace courant. */
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();

		/* Parcourt les projets de la racine. */
		for (IProject project : wsRoot.getProjects()) {

			/* Vérication que le projet est accessible. */
			if (!project.isAccessible()) {
				continue;
			}

			/* Vérifie que le projet est un projet Java. */
			if (!JdtUtils.isJavaProject(project)) {
				continue;
			}

			/* Obtient l'AST du projet. */
			IJavaProject javaProject = JavaCore.create(project);
			projects.put(project, javaProject);
		}
		return projects;
	}

	/**
	 * Indique si une ressource est dans le dossier target.
	 * 
	 * @param resource Resource.
	 * @return <code>true</code> si la ressource est dans le dossier target.
	 */
	public static boolean isTargetFolder(IResource resource) {
		String fullPath = resource.getFullPath().toString();
		return fullPath.contains("target/classes") || fullPath.contains("WEB-INF/classes");
	}

	/**
	 * Indique si une ressource est dans le dossier javagen.
	 * 
	 * @param resource Resource.
	 * @return <code>true</code> si la ressource est dans le dossier javagen.
	 */
	public static boolean isSrcJavagen(IResource resource) {
		return resource.getFullPath().toString().contains("src/main/javagen");
	}

	/**
	 * Indique si une ressource est dans le dossier java.
	 * 
	 * @param resource Resource.
	 * @return <code>true</code> si la ressource est dans le dossier java.
	 */
	public static boolean isSrcJava(IResource resource) {
		return resource.getFullPath().toString().contains("src/main/java/");
	}

	/**
	 * Indique si une ressource est un fichier Java.
	 * 
	 * @param resource Resource.
	 * @return <code>true</code> si la ressource est un fichier Java.
	 */
	public static boolean isJavaFile(IResource resource) {
		return JAVA_EXTENSION.equals(resource.getFileExtension());
	}

	/**
	 * Indique si une ressource est un fichier KSP.
	 * 
	 * @param resource Resource.
	 * @return <code>true</code> si la ressource est un fichier KSP.
	 */
	public static boolean isKspFile(IResource resource) {
		return KSP_EXTENSION.equals(resource.getFileExtension());
	}
}
