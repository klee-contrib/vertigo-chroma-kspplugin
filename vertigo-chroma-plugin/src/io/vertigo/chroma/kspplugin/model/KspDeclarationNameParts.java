package io.vertigo.chroma.kspplugin.model;

public class KspDeclarationNameParts {

	private final String prefix;
	private final String constantCaseNameOnly;

	public KspDeclarationNameParts(String prefix, String constantCaseNameOnly) {
		this.prefix = prefix;
		this.constantCaseNameOnly = constantCaseNameOnly;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getConstantCaseNameOnly() {
		return constantCaseNameOnly;
	}
}
