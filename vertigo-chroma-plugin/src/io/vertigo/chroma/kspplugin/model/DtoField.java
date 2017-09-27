package io.vertigo.chroma.kspplugin.model;

/**
 * Champ d'un Dto.
 */
public class DtoField {

	private final String constantCaseName;
	private final String label;
	private final String domain;
	private final boolean persistent;

	public DtoField(String constantCaseName, String label, String domain, Boolean persistent) {
		this.constantCaseName = constantCaseName;
		this.label = label;
		this.domain = domain;
		this.persistent = persistent == null ? true : persistent;
	}

	public String getConstantCaseName() {
		return constantCaseName;
	}

	public String getLabel() {
		return label;
	}

	public String getDomain() {
		return domain;
	}

	public boolean isPersistent() {
		return persistent;
	}

}
