package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.Navigable;

import org.eclipse.jface.text.IRegion;

/**
 * Lien vers une déclaration KSP.
 */
public class KspDeclarationHyperLink extends NavigableHyperLink {

	/**
	 * Créé une nouvelle instance de KspDeclarationHyperLink.
	 * 
	 * @param urlRegion Région du lien dans le document.
	 * @param navigable Navigable de la déclaration KSP.
	 */
	public KspDeclarationHyperLink(IRegion urlRegion, Navigable navigable) {
		super(urlRegion, navigable, "Open KSP declaration");
	}
}
