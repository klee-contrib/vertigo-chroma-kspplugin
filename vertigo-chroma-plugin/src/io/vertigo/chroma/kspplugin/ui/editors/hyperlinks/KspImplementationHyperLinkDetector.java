package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.DaoImplementation;
import io.vertigo.chroma.kspplugin.model.JavaClassFile;
import io.vertigo.chroma.kspplugin.model.KspDeclaration;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.resources.DaoManager;
import io.vertigo.chroma.kspplugin.resources.JavaClassManager;
import io.vertigo.chroma.kspplugin.resources.KspManager;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;

import java.util.ArrayList;
import java.util.List;

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

		List<IHyperlink> hyperLinks = new ArrayList<>();

		/* Construit le lien vers la déclaration KSP. */
		IRegion targetRegion = new Region(currentWordSelection.getOffset(), currentWordSelection.getLength());
		hyperLinks.add(new KspDeclarationHyperLink(targetRegion, kspDeclaration));

		/* Cherche si l'implémentation KSP est une méthode de DAO. */
		DaoImplementation daoImplementation = DaoManager.getInstance().findDaoImplementation(currentWord);
		if (daoImplementation != null) {
			/* Cherche si la méthode de DAO possède une classe de test unitaire. */
			JavaClassFile testClass = JavaClassManager.getInstance().findJavaClassTest(daoImplementation);
			if (testClass != null) {
				/* Ajoute en premier le lien pour que le lien principal vers le KSP soit en dernier. */
				hyperLinks.add(0, new JavaTestClassHyperLink(targetRegion, testClass));
			}
		}

		if (hyperLinks.isEmpty()) {
			return null; // NOSONAR
		}

		/* On retourne les liens de la Task. */
		return hyperLinks.toArray(new IHyperlink[0]);
	}
}
