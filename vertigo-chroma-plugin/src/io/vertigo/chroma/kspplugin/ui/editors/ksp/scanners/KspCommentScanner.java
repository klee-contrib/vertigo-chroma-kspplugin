package io.vertigo.chroma.kspplugin.ui.editors.ksp.scanners;

import io.vertigo.chroma.kspplugin.ui.theme.ColorName;
import io.vertigo.chroma.kspplugin.ui.theme.ColorTheme;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Scanner de la partition des commentaires d'un fichier KSP.
 */
public class KspCommentScanner extends RuleBasedScanner {

	private static final char NO_ESCAPE_CHAR = (char) -1;
	private RGB commentColor;

	/**
	 * Créé une nouvelle instance de KspCommentScanner.
	 */
	public KspCommentScanner() {
		super();
		setColors();
		setRules(extractRules());
	}

	private void setColors() {
		ColorTheme theme = ColorTheme.getCurrent();
		commentColor = theme.getColor(ColorName.COMMENT);
	}

	private IRule[] extractRules() {
		IToken comment = new Token(new TextAttribute(new Color(Display.getCurrent(), commentColor), null, SWT.NORMAL));
		return new IRule[] {
		/* Commentaire multi-ligne */
		new PatternRule("/*", "*/", comment, NO_ESCAPE_CHAR, false),
		/* Commentaire à la fin d'une ligne */
		new EndOfLineRule("//", comment) };
	}
}
