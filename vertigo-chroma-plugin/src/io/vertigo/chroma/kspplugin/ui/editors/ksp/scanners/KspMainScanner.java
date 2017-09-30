package io.vertigo.chroma.kspplugin.ui.editors.ksp.scanners;

import io.vertigo.chroma.kspplugin.lexicon.LexiconManager;
import io.vertigo.chroma.kspplugin.lexicon.Lexicons;
import io.vertigo.chroma.kspplugin.ui.theme.ColorName;
import io.vertigo.chroma.kspplugin.ui.theme.ColorTheme;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Scanner de la partition principale des fichiers KSP.
 */
public class KspMainScanner extends RuleBasedScanner {

	private static final char NO_ESCAPE_CHAR = (char) -1;
	private static final String[] KSP_VERBS = LexiconManager.getInstance().getWords(Lexicons.KSP_VERBS);
	private static final String[] KSP_PREPOSITIONS = LexiconManager.getInstance().getWords(Lexicons.KSP_PREPOSITIONS);
	private static final String[] KSP_NATURES = LexiconManager.getInstance().getWords(Lexicons.KSP_NATURES);
	private static final String[] KSP_PROPERTIES = LexiconManager.getInstance().getWords(Lexicons.KSP_PROPERTIES);
	private static final String[] KSP_ATTRIBUTES = LexiconManager.getInstance().getWords(Lexicons.KSP_ATTRIBUTES);
	private RGB verbColor;
	private RGB natureColor;
	private RGB propertyColor;
	private RGB attributeColor;
	private RGB prepositionColor;
	private RGB tagColor;
	private RGB defaultColor;

	/**
	 * Créé une nouvelle instance de KspMainScanner.
	 */
	public KspMainScanner() {
		super();
		setColors();
		setRules(extractRules());
	}

	private void setColors() {
		ColorTheme theme = ColorTheme.getCurrent();
		verbColor = theme.getColor(ColorName.KSP_VERB);
		natureColor = theme.getColor(ColorName.KSP_OBJECT);
		propertyColor = theme.getColor(ColorName.KSP_PROPERTY);
		attributeColor = theme.getColor(ColorName.KSP_ATTRIBUTE);
		prepositionColor = theme.getColor(ColorName.KSP_PREPOSITION);
		tagColor = theme.getColor(ColorName.KSP_TAG);
		defaultColor = theme.getColor(ColorName.KSP_DEFAULT);
	}

	private IRule[] extractRules() {

		/* Tokens par type de mots. */
		IToken verb = new Token(new TextAttribute(new Color(Display.getCurrent(), verbColor), null, SWT.BOLD));
		IToken nature = new Token(new TextAttribute(new Color(Display.getCurrent(), natureColor), null, SWT.BOLD));
		IToken property = new Token(new TextAttribute(new Color(Display.getCurrent(), propertyColor), null, SWT.BOLD));
		IToken attribute = new Token(new TextAttribute(new Color(Display.getCurrent(), attributeColor), null, SWT.BOLD));
		IToken preposition = new Token(new TextAttribute(new Color(Display.getCurrent(), prepositionColor), null, SWT.BOLD));
		IToken tag = new Token(new TextAttribute(new Color(Display.getCurrent(), tagColor), null, SWT.BOLD));
		IToken defaut = new Token(new TextAttribute(new Color(Display.getCurrent(), defaultColor)));

		/* Règle de détection de mots avec un style par défaut. */
		WordRule wordRule = new WordRule(new IWordDetector() {
			@Override
			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}

			@Override
			public boolean isWordStart(char c) {
				return Character.isJavaIdentifierStart(c);
			}
		}, defaut);

		/* Ajoute les mots clés avec un style par type de mots-clés. */
		for (String k : KSP_VERBS) {
			wordRule.addWord(k, verb);
		}
		for (String k : KSP_NATURES) {
			wordRule.addWord(k, nature);
		}
		for (String k : KSP_ATTRIBUTES) {
			wordRule.addWord(k, attribute);
		}
		for (String k : KSP_PROPERTIES) {
			wordRule.addWord(k, property);
		}
		for (String k : KSP_PREPOSITIONS) {
			wordRule.addWord(k, preposition);
		}

		return new IRule[] {
		/* Mots-clés */
		wordRule,
		/* Tag Model pour Kasper 2 */
		new PatternRule("<Model", ">", tag, NO_ESCAPE_CHAR, false),
		/* Tag Model pour Kasper 2 */
		new PatternRule("</Model", ">", tag, NO_ESCAPE_CHAR, false),
		/* Tag Controller pour Kasper 2 */
		new PatternRule("<Controller", ">", tag, NO_ESCAPE_CHAR, false),
		/* Tag Controller pour Kasper 2 */
		new PatternRule("</Controller", ">", tag, NO_ESCAPE_CHAR, false),
		/* Tag Model pour Kasper 2 */
		new PatternRule("<MODEL", ">", tag, NO_ESCAPE_CHAR, false),
		/* Tag Model pour Kasper 2 */
		new PatternRule("</MODEL", ">", tag, NO_ESCAPE_CHAR, false),
		/* Tag Controller pour Kasper 2 */
		new PatternRule("<CONTROLLER", ">", tag, NO_ESCAPE_CHAR, false),
		/* Tag Controller pour Kasper 2 */
		new PatternRule("</CONTROLLER", ">", tag, NO_ESCAPE_CHAR, false) };
	}
}
