package io.vertigo.chroma.kspplugin.ui.theme;

import io.vertigo.chroma.kspplugin.utils.UiUtils;

import org.eclipse.swt.graphics.RGB;

/**
 * Thème de couleur d'Eclipse.
 */
public enum ColorTheme {

	/**
	 * Thème par défaut (fond blanc).
	 */
	DEFAULT_THEME,

	/**
	 * Thème Dark (fond noir).
	 */
	DARK_THEME;

	/**
	 * Retourne le thème courant.
	 * 
	 * @return Thème courant.
	 */
	public static ColorTheme getCurrent() {
		return UiUtils.isDarkBackground() ? ColorTheme.DARK_THEME : ColorTheme.DEFAULT_THEME;
	}

	/**
	 * Obtient la couleur RGB pour un nom de couleur donné, pour ce thème.
	 * 
	 * @param colorName Nom de la couleur.
	 * @return Couleur RGB.
	 */
	public RGB getColor(ColorName colorName) {
		if (DARK_THEME.equals(this)) {
			return toDarkColor(colorName.getRgb());
		}
		return colorName.getRgb();
	}

	private RGB toDarkColor(RGB rgb) {
		/* On utilise la couleur complémentaire au blanc. */
		return new RGB(255 - rgb.red, 255 - rgb.green, 255 - rgb.blue);
	}
}
