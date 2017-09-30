package io.vertigo.chroma.kspplugin.model;

import java.util.List;

/**
 * Stocke l'ensemble des déclarations KSP de tout un workspace.
 */
public class KspWorkspace {

	private final List<KspDeclaration> kspDeclarations;

	public KspWorkspace(List<KspDeclaration> kspDeclarations) {
		this.kspDeclarations = kspDeclarations;
	}

	public List<KspDeclaration> getKspDeclarations() {
		return kspDeclarations;
	}
}
