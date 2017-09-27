package io.vertigo.chroma.kspplugin.ui.editors.ksp;

import io.vertigo.chroma.kspplugin.model.DtoFile;
import io.vertigo.chroma.kspplugin.model.KspRegionType;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;
import io.vertigo.chroma.kspplugin.utils.StringUtils;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;

/**
 * Gère le tooltip dans le fichier KSP.
 */
public class KspTextHover implements ITextHover {

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {

		IDocument document = textViewer.getDocument();

		/* Vérifie qu'on est dans une String de KSP */
		boolean isSqlString = DocumentUtils.isContentType(document, offset, KspRegionType.STRING);
		if (!isSqlString) {
			return null;
		}

		/* Extrait le mot courant. */
		ITextSelection selection = new TextSelection(document, offset, 0);
		ITextSelection currentWordSelection = DocumentUtils.findCurrentWord(document, selection, WordSelectionType.SNAKE_CASE);
		if (currentWordSelection == null) {
			return null;
		}
		String currentWord = currentWordSelection.getText();
		if (currentWord == null) {
			return null;
		}

		/* Renvoie la région du mot. */
		return new Region(currentWordSelection.getOffset(), currentWordSelection.getLength());
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {

		/* Extrait le mot de la région. */
		String currentWord = getSelectedWord(textViewer, hoverRegion);

		/* Extrait un nom de DTO : Calcul le nom en PascalCase */
		String javaName = StringUtils.toPascalCase(currentWord);

		/* Cherche le fichier Java du DTO. */
		DtoFile dtoFile = DtoManager.getInstance().findDtoFile(javaName);
		if (dtoFile == null) {
			return null;
		}

		/* Renvoie le nom Java. Le texte complet sera généré par KspInformationPresenter. */
		return javaName;
	}

	private String getSelectedWord(ITextViewer textViewer, IRegion hoverRegion) {
		try {
			IDocument document = textViewer.getDocument();
			return document.get(hoverRegion.getOffset(), hoverRegion.getLength());
		} catch (BadLocationException e) {
			ErrorUtils.handle(e);
		}
		return null;
	}
}
