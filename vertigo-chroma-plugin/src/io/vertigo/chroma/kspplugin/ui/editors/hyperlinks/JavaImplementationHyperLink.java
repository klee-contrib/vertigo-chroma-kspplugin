package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.Navigable;

import org.eclipse.jface.text.IRegion;

/**
 * Lien vers une implémentation Java.
 */
public class JavaImplementationHyperLink extends NavigableHyperLink {

	/**
	 * Créé une nouvelle instance de JavaImplementationHyperLink.
	 * 
	 * @param urlRegion Région du lien dans le document.
	 * @param navigable Navigable de l'implémentation Java.
	 */
	public JavaImplementationHyperLink(IRegion urlRegion, Navigable navigable) {
		super(urlRegion, navigable, "Open Java Implementation");
	}
}
