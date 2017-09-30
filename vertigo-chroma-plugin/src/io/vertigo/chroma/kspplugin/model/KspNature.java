package io.vertigo.chroma.kspplugin.model;

/**
 * Nature de déclaration KSP.
 */
public enum KspNature {

	DT_DEFINITION("DtDefinition", "DT");

	private final String kspKeyword;
	private final String kspKeywordKasper3;

	/**
	 * Créé une nouvelle instance de KspNature.
	 * 
	 * @param kspKeyWord Mot-clé.
	 * @param kspKeyWordKasper3 Mot-clé pour Kasper 3.
	 */
	KspNature(String kspKeyWord, String kspKeyWordKasper3) {
		this.kspKeyword = kspKeyWord;
		this.kspKeywordKasper3 = kspKeyWordKasper3;
	}

	public String getKspKeyword() {
		return kspKeyword;
	}

	public String getKspKeyWordKasper3() {
		return kspKeywordKasper3;
	}
}
