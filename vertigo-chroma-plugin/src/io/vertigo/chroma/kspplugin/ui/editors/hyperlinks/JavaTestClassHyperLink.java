package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.Navigable;

import org.eclipse.jface.text.IRegion;

/**
 * Lien vers une classe de test Java.
 */
public class JavaTestClassHyperLink extends NavigableHyperLink {

	/**
	 * Créé une nouvelle instance de JavaTestClassHyperLink.
	 * 
	 * @param urlRegion Région du lien dans le document.
	 * @param navigable Navigable de la classe de test.
	 */
	public JavaTestClassHyperLink(IRegion urlRegion, Navigable navigable) {
		super(urlRegion, navigable, "Open Java Test Class");
	}
}
