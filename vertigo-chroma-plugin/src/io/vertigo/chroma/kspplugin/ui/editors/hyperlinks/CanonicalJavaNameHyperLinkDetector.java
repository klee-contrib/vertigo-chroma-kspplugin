package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.JavaClassFile;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.resources.JavaClassManager;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;
import io.vertigo.chroma.kspplugin.utils.JdtUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Détecteur de lien à partir de nom canonique entre double quote dans un fichier KSP.
 */
public class CanonicalJavaNameHyperLinkDetector extends AbstractHyperlinkDetector {

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

		/* Charge le type Java. */
		IType javaType = findJavaType(currentWord);
		if (javaType == null) {
			return null; // NOSONAR
		}

		/* Renvoie un lien pour ouvrir le type Java. */
		IRegion targetRegion = new Region(currentWordSelection.getOffset(), currentWordSelection.getLength());
		return new IHyperlink[] { new JavaTypeHyperLink(targetRegion, javaType) };
	}

	private IType findJavaType(String currentWord) {

		IProject currentEditorProject = UiUtils.getCurrentEditorProject();

		/* Cas d'un nom qualifié complet. */
		IType javaTypeFromFullyQualifiedName = JdtUtils.getJavaType(currentWord, currentEditorProject);
		if (javaTypeFromFullyQualifiedName != null) {
			return javaTypeFromFullyQualifiedName;
		}

		/* Cas d'un nom simple. */
		JavaClassFile javaClassFromSimpleName = JavaClassManager.getInstance().findJavaClassFile(currentWord);
		if (javaClassFromSimpleName != null) {
			IType javaTypeFromSimpleName = JdtUtils.getJavaType(javaClassFromSimpleName.getFullyQualifiedName(), currentEditorProject);
			if (javaTypeFromSimpleName != null) {
				return javaTypeFromSimpleName;
			}
		}

		return null;
	}
}
