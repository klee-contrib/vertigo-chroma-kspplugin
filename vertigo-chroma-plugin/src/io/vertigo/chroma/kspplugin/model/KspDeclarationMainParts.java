package io.vertigo.chroma.kspplugin.model;

public class KspDeclarationMainParts {

	private final String verb;
	private final String nature;
	private final String constantCaseName;

	public KspDeclarationMainParts(String verb, String nature, String constantCaseName) {
		this.verb = verb;
		this.nature = nature;
		this.constantCaseName = constantCaseName;
	}

	public KspDeclarationMainParts(String nature, String constantCaseName) {
		this("create", nature, constantCaseName);
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
}
