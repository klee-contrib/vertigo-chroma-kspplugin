package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.KspDeclaration;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.resources.KspManager;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Détecteur de lien à partir d'implémentation de KSP dans un fichier Java.
 */
public class KspImplementationHyperLinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		/* Cas d'un fichier .class : on ne traite pas car le fichier n'est pas dans le projet. */
		boolean isClassFile = !textViewer.isEditable();
		if (isClassFile) {
			return null; // NOSONAR
		}

		IDocument document = textViewer.getDocument();

		/* Extrait le mot courant. */
		ITextSelection selection = new TextSelection(document, region.getOffset(), region.getLength());
		ITextSelection currentWordSelection = DocumentUtils.findCurrentWord(document, selection, WordSelectionType.CAMEL_CASE);
		if (currentWordSelection == null) {
			return null; // NOSONAR
		}
		String currentWord = currentWordSelection.getText();
		if (currentWord == null) {
			return null; // NOSONAR
		}

		/* Cherche une déclaration KSP correspondant au nom Java. */
		KspDeclaration kspDeclaration = KspManager.getInstance().findKspDeclaration(currentWord);
		if (kspDeclaration == null) {
			return null; // NOSONAR
		}

		/* Construit le lien vers la déclaration KSP. */
		IRegion targetRegion = new Region(currentWordSelection.getOffset(), currentWordSelection.getLength());
		return new IHyperlink[] { new KspDeclarationHyperLink(targetRegion, kspDeclaration) };
	}

}
