package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.DtoDefinitionPath;
import io.vertigo.chroma.kspplugin.model.DtoFile;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;
import io.vertigo.chroma.kspplugin.utils.KspStringUtils;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Détecteur de chemin de définition de DTO ("package.NomDto") dans un fichier KSP.
 * <p>
 * Sert uniquement pour Kasper 3.
 * </p>
 */
public class DtoDefinitionPathHyperLinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IDocument document = textViewer.getDocument();

		/* Extrait le mot courant. */
		ITextSelection selection = new TextSelection(document, region.getOffset(), region.getLength());
		ITextSelection currentWordSelection = DocumentUtils.findCurrentWord(document, selection, WordSelectionType.CANONICAL_JAVA_NAME);
		if (currentWordSelection == null) {
			return null; // NOSONAR
		}
		String currentWord = currentWordSelection.getText();
		if (currentWord == null) {
			return null; // NOSONAR
		}

		/* Vérifie qu'on est dans une région entière KspString */
		if (!DocumentUtils.isExactKspString(document, currentWordSelection)) {
			return null; // NOSONAR
		}

		/* Extrait un chemin de définition de DTO. */
		DtoDefinitionPath definitionPath = KspStringUtils.getKasper3DefinitionPath(currentWord);
		if (definitionPath == null) {
			return null; // NOSONAR
		}

		/* Cherche le fichier Java du DTO. */
		DtoFile dtoFile = DtoManager.getInstance().findDtoFile(definitionPath);
		if (dtoFile == null) {
			return null; // NOSONAR
		}

		/* Fichier Java trouvé : on ajoute un lien vers le fichier Java. */
		IRegion targetRegion = new Region(currentWordSelection.getOffset(), currentWordSelection.getLength());
		return new IHyperlink[] { new JavaImplementationHyperLink(targetRegion, dtoFile) };
	}
}
