package io.vertigo.chroma.kspplugin.ui.editors.completion;

import io.vertigo.chroma.kspplugin.legacy.LegacyManager;
import io.vertigo.chroma.kspplugin.model.CompletionCandidate;
import io.vertigo.chroma.kspplugin.model.DtoFile;
import io.vertigo.chroma.kspplugin.model.DtoReferencePattern;
import io.vertigo.chroma.kspplugin.model.KspDeclaration;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.resources.KspManager;
import io.vertigo.chroma.kspplugin.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextSelection;

/**
 * Processeur gérant l'autocomplétion pour la région principale d'un KSP.
 * <p>
 * Gère l'autocomplétion sur les noms de domaines.
 * </p>
 */
public class KspDefaultContentAssistProcessor extends BaseContentAssistProcessor {

	protected WordSelectionType getWordSelectionType() {
		return WordSelectionType.SNAKE_CASE;
	}

	protected List<CompletionCandidate> getCandidates(ITextSelection currentWordSelection) {
		List<CompletionCandidate> list = new ArrayList<>();

		/* Ajoute les domaines primitifs. */
		List<KspDeclaration> domains = KspManager.getInstance().findDomains();
		for (KspDeclaration domain : domains) {
			list.add(new CompletionCandidate(domain.getConstantCaseName(), "Domain of primitive " + domain.getJavaName() + "."));
		}

		/* Obtient le pattern à utiliser pour les références de DTO. */
		DtoReferencePattern pattern = LegacyManager.getInstance().getCurrentStrategy().getDtoReferenceSyntaxe();

		/* Ajoute les domaines des DTO et DTC. */
		List<DtoFile> dtoFiles = DtoManager.getInstance().getWorkspace().getDtoFiles();
		for (DtoFile dtoFile : dtoFiles) {
			String javaName = dtoFile.getJavaName();
			String constantCaseName = StringUtils.toConstantCase(javaName);
			switch (pattern) {
			case DOMAIN:
				list.add(new CompletionCandidate(String.format("DO_DT_%s_DTO", constantCaseName), String.format("Domain of DTO %s.", javaName)));
				list.add(new CompletionCandidate(String.format("DO_DT_%s_DTC", constantCaseName), String.format("Domain of list of DTO %s.", javaName)));
				break;
			case SIMPLE_NAME:
				list.add(new CompletionCandidate(String.format("DT_%s", constantCaseName), String.format("Domain of DTO %s.", javaName)));
				break;
			default:
				break;
			}
		}

		/* Tri par libellé. */
		list.sort((o1, o2) -> o1.getDisplayString().compareTo(o2.getDisplayString()));
		return list;
	}

}
