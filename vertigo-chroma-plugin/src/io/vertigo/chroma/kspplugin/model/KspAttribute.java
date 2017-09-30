package io.vertigo.chroma.kspplugin.model;

import io.vertigo.chroma.kspplugin.utils.KspStringUtils;

import java.util.HashMap;

/**
 * Représente un attribute d'une déclaration KSP.
 */
public class KspAttribute {

	private static final String DTC_SUFFIX = "_DTC";
	private static final String DTO_SUFFIX = "_DTO";
	private static final String IN_PROPERTY_VALUE = "\"in\"";
	private static final String TRUE_PROPERTY_VALUE = "\"true\"";
	private final PropertyMap map = new PropertyMap();
	private final String constantCaseName;
	private String domain;
	private boolean notNull;
	private boolean in = true;
	private DomainType domainType = DomainType.UNKNOWN;
	private String dtName;

	/**
	 * Créé une nouvelle instance de KspAttribute.
	 * 
	 * @param constantCaseName Nom en constant case de l'attribute.
	 */
	public KspAttribute(String constantCaseName) {
		this.constantCaseName = constantCaseName;
	}

	public KspAttribute(String constantCaseName, String domain, boolean notNull, boolean in, DomainType domainType, String dtName) {
		this.constantCaseName = constantCaseName;
		this.domain = domain;
		this.notNull = notNull;
		this.in = in;
		this.domainType = domainType;
		this.dtName = dtName;
	}

	public boolean isIn() {
		return in;
	}

	public boolean isOut() {
		return !in;
	}

	public String getConstantCaseName() {
		return constantCaseName;
	}

	public String getDomain() {
		return domain;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public DomainType getDomainType() {
		return domainType;
	}

	public String getDtName() {
		return dtName;
	}

	/**
	 * Ajoute une propriété.
	 * 
	 * @param name Nom de la propriété.
	 * @param value Valeur de la propriété.
	 */
	public void addProperty(String name, String value) {
		this.map.put(name, value);
		switch (name) {
		case "domain":
			domain = value;
			setDomainProperties();
			break;
		case "notNull":
			notNull = TRUE_PROPERTY_VALUE.equals(value);
			break;
		case "inOut":
			in = IN_PROPERTY_VALUE.equals(value);
			break;
		default:
			break;
		}
	}

	private void setDomainProperties() {
		if (domain.endsWith(DTO_SUFFIX)) {
			domainType = DomainType.DTO;
			dtName = KspStringUtils.getDtDefinitionName(domain);
		} else if (domain.endsWith(DTC_SUFFIX)) {
			domainType = DomainType.DTC;
			dtName = KspStringUtils.getDtDefinitionName(domain);
		} else {
			domainType = DomainType.PRIMITIVE;
		}
	}

	/**
	 * Map nom/valeur des propriétés.
	 */
	private static class PropertyMap extends HashMap<String, String> {

		private static final long serialVersionUID = 1L;

	}
}
