package io.vertigo.chroma.kspplugin.ui.theme;

import org.eclipse.swt.graphics.RGB;

/**
 * Enumération des couleurs associées à une fonction grammaticale dans le KSP.
 */
public enum ColorName {

	/**
	 * Verbe de déclaration KSP.
	 */
	KSP_VERB(new RGB(140, 10, 210)),

	/**
	 * Objet de déclaration KSP.
	 */
	KSP_OBJECT(new RGB(0, 0, 192)),

	/**
	 * Propriété de déclaration KSP.
	 */
	KSP_PROPERTY(new RGB(0, 100, 0)),

	/**
	 * Attribut de déclaration KSP.
	 */
	KSP_ATTRIBUTE(new RGB(0, 100, 50)),

	/**
	 * Préposition (Kasper <= 3).
	 */
	KSP_PREPOSITION(new RGB(140, 10, 210)),

	/**
	 * Tag de KSP (Kasper 2).
	 */
	KSP_TAG(new RGB(140, 10, 210)),

	/**
	 * Couleur par défaut du KSP.
	 */
	KSP_DEFAULT(new RGB(0, 0, 0)),

	/**
	 * Verbe SQL.
	 */
	STRING_VERB(new RGB(0, 51, 187)),

	/**
	 * Java en ligne dans le SQL.
	 */
	STRING_INLINE_JAVA(new RGB(128, 128, 128)),

	/**
	 * Paramètre d'une tâche SQL.
	 */
	STRING_PARAMETER(new RGB(84, 11, 206)),

	/**
	 * Double quote d'une string de KSP.
	 */
	STRING_DOUBLE_QUOTE(new RGB(0, 0, 200)),

	/**
	 * Color par défaut d'une string de KSP.
	 */
	STRING_DEFAULT(new RGB(0, 80, 80)),

	/**
	 * Commentaire.
	 */
	COMMENT(new RGB(128, 128, 128));

	private final RGB rgb;

	/**
	 * Créé une nouvelle instance de ColorName.
	 * 
	 * @param rgb Couleur par défaut.
	 */
	ColorName(RGB rgb) {
		this.rgb = rgb;
	}

	public RGB getRgb() {
		return rgb;
	}
}
