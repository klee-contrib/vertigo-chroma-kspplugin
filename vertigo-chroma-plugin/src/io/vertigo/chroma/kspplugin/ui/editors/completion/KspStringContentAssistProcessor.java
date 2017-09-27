package io.vertigo.chroma.kspplugin.ui.editors.completion;

import io.vertigo.chroma.kspplugin.legacy.LegacyManager;
import io.vertigo.chroma.kspplugin.legacy.LegacyStrategy;
import io.vertigo.chroma.kspplugin.model.CompletionCandidate;
import io.vertigo.chroma.kspplugin.model.DtoField;
import io.vertigo.chroma.kspplugin.model.DtoFile;
import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.KspAttribute;
import io.vertigo.chroma.kspplugin.model.KspDeclaration;
import io.vertigo.chroma.kspplugin.model.KspNature;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.resources.KspManager;
import io.vertigo.chroma.kspplugin.utils.StringUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.ITextSelection;

/**
 * Processeur gérant l'autocomplétion pour la région des strings d'un KSP.
 * <p>
 * Gère l'autocomplétion sur les noms de paramètre (#OBJET_ID#, #CRITERE.OBJET_ID#).
 * </p>
 */
public class KspStringContentAssistProcessor extends BaseContentAssistProcessor {

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '#' };
	}

	@Override
	protected boolean isCurrentWordValid(String currentWord) {
		return currentWord.startsWith("#");
	}

	@Override
	protected List<CompletionCandidate> getCandidates(ITextSelection currentWordSelection) {
		List<CompletionCandidate> list = new ArrayList<>();

		/* Trouve la déclaration contenant le nom de paramètre. */
		IFile file = UiUtils.getCurrentEditorFile();
		FileRegion fileRegion = new FileRegion(file, currentWordSelection.getOffset(), currentWordSelection.getLength());
		KspDeclaration declaration = KspManager.getInstance().findDeclarationAt(fileRegion);
		if (declaration == null) {
			return list;
		}

		/* Construit les candidats */
		for (KspAttribute attribute : declaration.getAttributes()) {
			handleAttribute(attribute, list);
		}

		/* Tri par libellé. */
		list.sort((o1, o2) -> o1.getDisplayString().compareTo(o2.getDisplayString()));

		return list;
	}

	@Override
	protected WordSelectionType getWordSelectionType() {
		return WordSelectionType.SQL_PARAMETER_NAME;
	}

	private void handleAttribute(KspAttribute attribute, List<CompletionCandidate> candidates) {
		/* Les paramètres out ne sont pas traités. */
		if (attribute.isOut()) {
			return;
		}
		switch (attribute.getDomainType()) {
		case UNKNOWN:
			candidates.add(new CompletionCandidate(String.format("#%s#", attribute.getConstantCaseName()), "Parameter without domain."));
			break;
		case PRIMITIVE:
			candidates
					.add(new CompletionCandidate(String.format("#%s#", attribute.getConstantCaseName()), "Parameter of domain " + attribute.getDomain() + "."));
			break;
		case DTO:
			handleDtoAttribute(attribute, candidates);
			break;
		case DTC:
			handleDtcAttribute(attribute, candidates);
			break;
		default:
			break;
		}
	}

	private static void handleDtoAttribute(KspAttribute attribute, List<CompletionCandidate> candidates) {
		if (attribute.getDtName() == null) {
			return;
		}

		DtoFile dtoFile = findDtoFile(attribute);
		if (dtoFile == null) {
			return;
		}
		for (DtoField field : dtoFile.getFields()) {
			String paramName = String.format("#%s.%s#", attribute.getConstantCaseName(), field.getConstantCaseName());
			String additionalProposalInfo = String.format("DTO parameter field of domain %s.", field.getDomain());
			candidates.add(new CompletionCandidate(paramName, additionalProposalInfo));
		}
	}

	private static void handleDtcAttribute(KspAttribute attribute, List<CompletionCandidate> candidates) {
		if (attribute.getDtName() == null) {
			return;
		}
		DtoFile dtoFile = findDtoFile(attribute);
		if (dtoFile == null) {
			return;
		}
		for (DtoField field : dtoFile.getFields()) {
			String paramName = String.format("#%s.ROWNUM.%s#", attribute.getConstantCaseName(), field.getConstantCaseName());
			String additionalProposalInfo = String.format("DTO list parameter field of domain %s.", field.getDomain());
			candidates.add(new CompletionCandidate(paramName, additionalProposalInfo));
		}
	}

	private static DtoFile findDtoFile(KspAttribute attribute) {
		LegacyStrategy strategy = LegacyManager.getInstance().getCurrentStrategy();
		String kspKeyword = strategy.getKspKeyword(KspNature.DT_DEFINITION);
		String dtName = attribute.getDtName();

		/* Cas d'un bean non persistant (sert pour Kasper 3). */
		String beanJavaName = strategy.getKspDeclarationJavaName(dtName, kspKeyword);
		DtoFile dtoFile = DtoManager.getInstance().findDtoFile(beanJavaName);
		if (dtoFile != null) {
			return dtoFile;
		}

		/* Cas d'un bean persistant */
		String dtoJavaName = StringUtils.toPascalCase(dtName);
		return DtoManager.getInstance().findDtoFile(dtoJavaName);
	}
}
