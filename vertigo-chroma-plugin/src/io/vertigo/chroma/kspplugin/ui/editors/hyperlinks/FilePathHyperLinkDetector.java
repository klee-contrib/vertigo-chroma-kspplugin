package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;
import io.vertigo.chroma.kspplugin.utils.FileUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Détecteur de liens à partir de chemins de fichers.
 */
public class FilePathHyperLinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		IDocument document = textViewer.getDocument();

		/* Extrait le mot courant. */
		ITextSelection selection = new TextSelection(document, region.getOffset(), region.getLength());
		ITextSelection currentWordSelection = DocumentUtils.findCurrentWord(document, selection, WordSelectionType.NOT_SPACE);
		if (currentWordSelection == null) {
			return null; // NOSONAR
		}
		String currentWord = currentWordSelection.getText();
		if (currentWord == null) {
			return null; // NOSONAR
		}

		/* Vérifie que c'est un chemin relatif valide. */
		String absolutePath = getAbsolutePath(currentWord);
		if (absolutePath == null) {
			return null; // NOSONAR
		}

		/* Vérifie que le fichier existe. */
		IFile file = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(absolutePath);
		if (file == null) {
			return null; // NOSONAR
		}

		/* Renvoin un lien vers le fichier dont on a trouvé le chemin. */
		IRegion targetRegion = new Region(currentWordSelection.getOffset(), currentWordSelection.getLength());
		return new IHyperlink[] { new FileHyperLink(targetRegion, file) };
	}

	/**
	 * Calcul le chemin absolu d'un chemin relatif par rapport au fichier courant.
	 * 
	 * @param relativePath Chemin relatif.
	 * @return Chemin absolu.
	 */
	private static String getAbsolutePath(String relativePath) {
		/* Obtient le chemin absolu du fichier courant. */
		String currentFilePath = UiUtils.getCurrentEditorFile().getFullPath().toString();
		/* Calcule le chemin relatif. */
		return FileUtils.getAbsolutePath(currentFilePath, relativePath);
	}

}
