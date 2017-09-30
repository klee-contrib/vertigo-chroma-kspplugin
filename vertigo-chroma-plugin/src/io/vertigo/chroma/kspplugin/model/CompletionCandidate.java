package io.vertigo.chroma.kspplugin.model;

/**
 * Candidat pour une autocomplétion.
 *
 */
public class CompletionCandidate {

	private final String displayString;
	private final String additionalProposalInfo;

	/**
	 * Créé une nouvelle instance de CompletionCandidate.
	 * 
	 * @param displayString String à afficher dans la liste de choix.
	 * @param additionalProposalInfo Information supplémentaire affiché à la sélection.
	 */
	public CompletionCandidate(String displayString, String additionalProposalInfo) {
		this.displayString = displayString;
		this.additionalProposalInfo = additionalProposalInfo;
	}

	public String getDisplayString() {
		return displayString;
	}

	public String getAdditionalProposalInfo() {
		return additionalProposalInfo;
	}
}
