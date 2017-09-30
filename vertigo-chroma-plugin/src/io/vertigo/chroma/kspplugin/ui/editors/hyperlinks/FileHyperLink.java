package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.Navigable;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;

/**
 * Lien vers un fichier.
 */
public class FileHyperLink extends NavigableHyperLink {

	/**
	 * Créé une nouvelle instance de FileHyperLink.
	 * 
	 * @param urlRegion Région du lien dans le document.
	 * @param file Fichier ciblé.
	 */
	public FileHyperLink(IRegion urlRegion, IFile file) {
		super(urlRegion, createNavigable(file), "Open file");
	}

	/**
	 * Créé un navigable à partir du fichier.
	 * 
	 * @param file Fichier.
	 * @return Navigable vers le premier caractère du fichier.
	 */
	private static Navigable createNavigable(IFile file) {
		return () -> new FileRegion(file, 0, 0);
	}
}
