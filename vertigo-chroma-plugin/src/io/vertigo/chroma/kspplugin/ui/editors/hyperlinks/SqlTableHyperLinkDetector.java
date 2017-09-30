package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.DtoFile;
import io.vertigo.chroma.kspplugin.model.KspRegionType;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;
import io.vertigo.chroma.kspplugin.utils.StringUtils;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Détecteur de lien à partir de nom de table SQL dans un fichier KSP.
 */
public class SqlTableHyperLinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IDocument document = textViewer.getDocument();

		/* Vérifie qu'on est dans une String de KSP */
		boolean isSqlString = DocumentUtils.isContentType(document, region.getOffset(), KspRegionType.STRING);
		if (!isSqlString) {
			return null; // NOSONAR
		}

		/* Extrait le mot courant. */
		ITextSelection selection = new TextSelection(document, region.getOffset(), region.getLength());
		ITextSelection currentWordSelection = DocumentUtils.findCurrentWord(document, selection, WordSelectionType.SNAKE_CASE);
		if (currentWordSelection == null) {
			return null; // NOSONAR
		}
		String currentWord = currentWordSelection.getText();
		if (currentWord == null) {
			return null; // NOSONAR
		}

		/* Extrait un nom de DTO : Calcul le nom en PascalCase */
		String javaName = StringUtils.toPascalCase(currentWord);

		/* Cherche le fichier Java du DTO. */
		DtoFile dtoFile = DtoManager.getInstance().findDtoFile(javaName);

		/* Fichier Java trouvé : on ajoute un lien vers le fichier Java. */
		if (dtoFile != null) {
			IRegion targetRegion = new Region(currentWordSelection.getOffset(), currentWordSelection.getLength());
			return new IHyperlink[] { new JavaImplementationHyperLink(targetRegion, dtoFile) };
		}

		return null; // NOSONAR
	}
}
