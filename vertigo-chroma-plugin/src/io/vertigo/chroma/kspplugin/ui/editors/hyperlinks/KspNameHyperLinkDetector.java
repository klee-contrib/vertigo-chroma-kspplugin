package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.legacy.LegacyManager;
import io.vertigo.chroma.kspplugin.legacy.LegacyStrategy;
import io.vertigo.chroma.kspplugin.model.DaoImplementation;
import io.vertigo.chroma.kspplugin.model.DtoFile;
import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.KspDeclaration;
import io.vertigo.chroma.kspplugin.model.KspNature;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.resources.DaoManager;
import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.resources.KspManager;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;
import io.vertigo.chroma.kspplugin.utils.KspStringUtils;
import io.vertigo.chroma.kspplugin.utils.StringUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

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
 * Détecteur de lien à partir de nom KSP dans un fichier KSP.
 */
public class KspNameHyperLinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IDocument document = textViewer.getDocument();

		/* Extrait le mot courant. */
		ITextSelection selection = new TextSelection(document, region.getOffset(), region.getLength());
		ITextSelection currentWordSelection = DocumentUtils.findCurrentWord(document, selection, WordSelectionType.CONSTANT_CASE);
		if (currentWordSelection == null) {
			return null; // NOSONAR
		}
		String currentWord = currentWordSelection.getText();
		if (currentWord == null) {
			return null; // NOSONAR
		}

		IRegion targetRegion = new Region(currentWordSelection.getOffset(), currentWordSelection.getLength());
		FileRegion fileRegion = new FileRegion(UiUtils.getCurrentEditorFile(), targetRegion.getOffset(), targetRegion.getLength());

		/* Cherche un nom de DTO. */
		IHyperlink[] hyperlinks = detectDtDefinitionName(currentWord, targetRegion, fileRegion);
		if (hyperlinks != null) {
			return hyperlinks;
		}

		/* Cherche un nom de Task. */
		hyperlinks = detectTaskName(currentWord, targetRegion);
		if (hyperlinks != null) {
			return hyperlinks;
		}

		/* Cherche une déclaration KSP autre. */
		return detectKspName(currentWord, targetRegion, fileRegion);
	}

	private IHyperlink[] detectDtDefinitionName(String currentWord, IRegion targetRegion, FileRegion fileRegion) {
		List<IHyperlink> hyperLinks = new ArrayList<>();

		/* Extrait le nom du DTO. */
		String dtName = KspStringUtils.getDtDefinitionName(currentWord);
		if (dtName == null) {
			return null; // NOSONAR
		}

		/* Calcul le nom Java */
		LegacyStrategy strategy = LegacyManager.getInstance().getCurrentStrategy();
		String kspKeyword = strategy.getKspKeyword(KspNature.DT_DEFINITION);
		String javaName = strategy.getKspDeclarationJavaName(dtName, kspKeyword);

		/* Cherche la déclaration KSP de DtDefinition */
		KspDeclaration kspDeclaration = KspManager.getInstance().findKspDeclaration(javaName, KspNature.DT_DEFINITION);

		/* Déclaration KSP trouvé : on ajoute un lien si on n'est pas déjà sur la déclaration. */
		if (kspDeclaration != null && !fileRegion.equals(kspDeclaration.getFileRegion())) {
			hyperLinks.add(new KspDeclarationHyperLink(targetRegion, kspDeclaration));
		}

		/* Cherche le fichier Java du DTO. */
		DtoFile dtoFile = DtoManager.getInstance().findDtoFile(javaName);

		/* Fichier Java trouvé : on ajoute un lien vers le fichier Java. */
		if (dtoFile != null) {
			hyperLinks.add(new JavaImplementationHyperLink(targetRegion, dtoFile));
		}

		if (hyperLinks.isEmpty()) {
			return null; // NOSONAR
		}

		/* On retourne les liens du DTO. */
		return hyperLinks.toArray(new IHyperlink[0]);
	}

	private IHyperlink[] detectTaskName(String currentWord, IRegion targetRegion) {
		/* Extrait un nom de tâche DAO / PAO. */
		String taskName = KspStringUtils.getTaskName(currentWord);
		if (taskName == null) {
			return null; // NOSONAR
		}
		/* Calcul le nom en PascalCase */
		String javaName = StringUtils.toCamelCase(taskName);

		/* Cherche la tâche. */
		DaoImplementation daoImplementation = DaoManager.getInstance().findDaoImplementation(javaName);
		if (daoImplementation == null) {
			return null; // NOSONAR
		}

		/* On retourne le lien vers la tâche. */
		return new IHyperlink[] { new JavaImplementationHyperLink(targetRegion, daoImplementation) };
	}

	private IHyperlink[] detectKspName(String currentWord, IRegion targetRegion, FileRegion fileRegion) {

		/* Cherche la déclaration. */
		KspDeclaration kspDeclaration = KspManager.getInstance().findKspDeclarationByConstantCaseName(currentWord);
		if (kspDeclaration == null) {
			return null; // NOSONAR
		}

		/* Vérifie que le focus n'est pas déjà sur la déclaration. */
		if (fileRegion.equals(kspDeclaration.getFileRegion())) {
			return null; // NOSONAR
		}

		/* Renvoie un lien vers la déclaration. */
		return new IHyperlink[] { new KspDeclarationHyperLink(targetRegion, kspDeclaration) };
	}
}
