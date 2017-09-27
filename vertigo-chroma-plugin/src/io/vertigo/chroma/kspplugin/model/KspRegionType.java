package io.vertigo.chroma.kspplugin.model;

import org.eclipse.jface.text.IDocument;

/**
 * Décrit les régions de partitionnement du document.
 *
 */
public enum KspRegionType {

	/**
	 * Région standard (hors string et commentaire).
	 */
	DEFAULT(IDocument.DEFAULT_CONTENT_TYPE),

	/**
	 * Région de string (contenant du SQL).
	 */
	STRING("org.eclipse.editor.kspString"),

	/**
	 * Région de commentaire du KSP.
	 */
	COMMENT("org.eclipse.editor.kspComment");

	/**
	 * Nom du partitionnement.
	 */
	public static final String PARTITIONING = "org.eclipse.editor.ksp";

	private final String contentType;

	KspRegionType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}
}
