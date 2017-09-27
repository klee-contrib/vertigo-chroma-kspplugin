package io.vertigo.chroma.kspplugin.ui.editors.completion;

import io.vertigo.chroma.kspplugin.model.CompletionCandidate;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * Classe de base pour les processeurs d'autocomplétion.
 */
public abstract class BaseContentAssistProcessor implements IContentAssistProcessor {

	private String lastError;

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer textViewer, int documentOffset) {
		IDocument document = textViewer.getDocument();

		/* Extrait le mot courant en snake-case pour tolérer les minuscules. */
		ITextSelection selection = new TextSelection(document, documentOffset - 1, 1);
		ITextSelection currentWordSelection = DocumentUtils.findCurrentWord(document, selection, getWordSelectionType());
		if (currentWordSelection == null) {
			return null; // NOSONAR
		}
		String currentWord = currentWordSelection.getText();
		if (currentWord == null || !isCurrentWordValid(currentWord)) {
			return null; // NOSONAR
		}

		/* Charge tous les domaines candidats. */
		List<CompletionCandidate> candidates = getCandidates(currentWordSelection);
		/* Filtre la liste des candidats avec le mot courant. */
		List<CompletionCandidate> suggestions = filterSuggestions(candidates, currentWord);

		/* Cas sans suggestions. */
		if (suggestions.isEmpty()) {
			return null; // NOSONAR
		}

		/* Construit le résultat. */
		try {
			return buildProposals(suggestions, currentWord, documentOffset);
		} catch (Exception e) {
			ErrorUtils.handle(e);
		}

		return null; // NOSONAR
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		lastError = "No Context Information available";
		return new IContextInformation[] {};
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null; // NOSONAR
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null; // NOSONAR
	}

	@Override
	public String getErrorMessage() {
		return lastError;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return new ContextInformationValidator(this);
	}

	/**
	 * @return Type de sélection de mot.
	 */
	protected abstract WordSelectionType getWordSelectionType();

	/**
	 * Indique si le mot courant est valide.
	 * 
	 * @param currentWord Mot courant.
	 * @return <code>true</code> si le mot est valide.
	 */
	protected boolean isCurrentWordValid(String currentWord) {
		return true;
	}

	/**
	 * Construit la liste des candidats.
	 * 
	 * @param currentWordSelection Mot courant sélectionné.
	 * @return Liste des domaines candidats.
	 */
	protected abstract List<CompletionCandidate> getCandidates(ITextSelection currentWordSelection);

	/**
	 * Filtre les candidats en recherchant les mots qui commencent par le mot courant.
	 * 
	 * @param candidates Candidats à l'autocomplétion.
	 * @param currentWord Mot courant.
	 * @return Liste des candidats filtrés.
	 */
	private static List<CompletionCandidate> filterSuggestions(List<CompletionCandidate> candidates, String currentWord) {
		String currentWordUpperCase = currentWord.toUpperCase(Locale.ENGLISH);
		return candidates.stream().filter(candidate -> candidate.getDisplayString().startsWith(currentWordUpperCase)).collect(Collectors.toList());
	}

	/**
	 * Constuit les propositions d'aucomplétion.
	 * 
	 * @param suggestions Suggestionsà proposer.
	 * @param replacedWord Mot courant à remplacer dans le document.
	 * @param documentOffset Offset dans le document du curseur. Le mot est placé avant ce curseur.
	 * @return Propositions d'autocomplétion.
	 */
	private ICompletionProposal[] buildProposals(List<CompletionCandidate> suggestions, String replacedWord, int documentOffset) {
		/* Calcul l'offset et la longueur du mot à remplacer dans le document. */
		int replacementLength = replacedWord.length();
		int replacementOffset = documentOffset - replacementLength;

		/* Construit les propositions en parcourant les suggestions. */
		List<ICompletionProposal> proposals = new ArrayList<>();
		for (CompletionCandidate suggestion : suggestions) {
			/* String qui remplacera le mot courant. */
			String replacementString = suggestion.getDisplayString();

			/* String affiché comme libellé de la proposition. */
			String displayString = replacementString;

			/* String affiché comme description de la proposition (dans la boîte jaune). */
			String additionalProposalInfo = suggestion.getAdditionalProposalInfo();
			CompletionProposal proposal = new CompletionProposal(replacementString, replacementOffset, replacementLength, replacementString.length(), null,
					displayString, null, additionalProposalInfo);
			proposals.add(proposal);
		}
		return proposals.toArray(new ICompletionProposal[0]);
	}

}
