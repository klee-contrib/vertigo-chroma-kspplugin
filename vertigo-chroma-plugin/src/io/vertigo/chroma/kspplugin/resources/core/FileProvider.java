package io.vertigo.chroma.kspplugin.resources.core;

import io.vertigo.chroma.kspplugin.utils.ErrorUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

/**
 * Fournisseur de fichier pour les managers de ressources.
 */
public class FileProvider {

	private final TextFileDocumentProvider documentProvider;
	private final IFile file;
	private final IProject project;
	private final IJavaProject javaProject;

	/**
	 * Créé une nouvelle instance de FileProvider.
	 * 
	 * @param file Fichier.
	 * @param project Projet.
	 * @param javaProject Projet Java.
	 * @param documentProvider Fournisseur de document.
	 */
	public FileProvider(IFile file, IProject project, IJavaProject javaProject, TextFileDocumentProvider documentProvider) {
		this.file = file;
		this.project = project;
		this.javaProject = javaProject;
		this.documentProvider = documentProvider;
	}

	public IFile getFile() {
		return file;
	}

	public IProject getProject() {
		return project;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public IDocument getDocument() {
		try {
			documentProvider.connect(file);
			IDocument document = documentProvider.getDocument(file);
			documentProvider.disconnect(file);
			return document;
		} catch (CoreException e) {
			ErrorUtils.handle(e);
			return null;
		}
	}
}
