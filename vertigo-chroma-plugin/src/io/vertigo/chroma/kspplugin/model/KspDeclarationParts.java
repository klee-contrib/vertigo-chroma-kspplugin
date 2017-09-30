package io.vertigo.chroma.kspplugin.model;

public class KspDeclarationParts {

	private final String verb;
	private final String nature;
	private final String constantCaseName;
	private final String prefix;
	private final String constantCaseNameOnly;

	public KspDeclarationParts(KspDeclarationMainParts mainParts, KspDeclarationNameParts nameParts) {
		this.verb = mainParts.getVerb();
		this.nature = mainParts.getNature();
		this.constantCaseName = mainParts.getConstantCaseName();
		this.prefix = nameParts.getPrefix();
		this.constantCaseNameOnly = nameParts.getConstantCaseNameOnly();
	}

	public String getVerb() {
		return verb;
	}

	public String getNature() {
		return nature;
	}

	public String getConstantCaseName() {
		return constantCaseName;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getConstantCaseNameOnly() {
		return constantCaseNameOnly;
	}
}
