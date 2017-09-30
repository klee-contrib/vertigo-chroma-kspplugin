package io.vertigo.chroma.kspplugin.ui.editors.analysis;

import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.KspDeclarationMainParts;
import io.vertigo.chroma.kspplugin.model.KspRegionType;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;
import io.vertigo.chroma.kspplugin.utils.KspStringUtils;
import io.vertigo.chroma.kspplugin.utils.MarkerUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Inspecteur de déclaration KSP.
 * <p>
 * Vérifie les règles de grammaire et génères des marqueurs de problèmes.
 * </p>
 */
public class KspDeclarationChecker {

	private IFile file;
	private final IDocument document;
	private static final OpenCloseCouple CURLY_BRACE_COUPLE = new OpenCloseCurlyBrace();
	private static final OpenCloseCouple PARENTHESIS_COUPLE = new OpenCloseParenthesis();
	private static final OpenCloseCouple JAVA_TAG_COUPLE = new OpenCloseJavaTag();
	private final SortedMap<Integer, OpenCloseCharacterOccurence> occurences = new TreeMap<>();

	/**
	 * Créé une nouvelle instance de KspDeclarationChecker.
	 * 
	 * @param document Document du KSP.
	 * @param file Fichier du KSP.
	 */
	public KspDeclarationChecker(IDocument document, IFile file) {
		this.file = file;
		this.document = document;
	}

	/**
	 * Tente de créer un inspecteur de déclaration KSP si la ligne en contient une.
	 * 
	 * @param file Fichier KSP.
	 * @param document Document du fichier.
	 * @param lineIdx Index en base zéro de la ligne du document.
	 * @return Inspecteur si présence de déclaration KSP, <code>null</code> sinon.
	 */
	public static KspDeclarationChecker extractChecker(IFile file, IDocument document, int lineIdx) {
		try {
			/* Extrait la ligne du document. */
			IRegion lineInformation = document.getLineInformation(lineIdx);
			String lineContent = document.get(lineInformation.getOffset(), lineInformation.getLength());

			/* Extrait une déclaration KSP. */
			KspDeclarationMainParts declarationParts = KspStringUtils.getVertigoKspDeclarationParts(lineContent);
			if (declarationParts != null) {
				/* Calcule la région du nom de la déclaration KSP */
				String name = declarationParts.getConstantCaseName();
				int fullNameLineOffSet = lineContent.indexOf(name);
				int taskNameOffSet = lineInformation.getOffset() + fullNameLineOffSet;

				/* Vérifie qu'on est dans une région standard */
				/* Permet d'ignorer le contenu des string et des commentaires KSP. */
				if (!DocumentUtils.isContentType(document, taskNameOffSet, KspRegionType.DEFAULT)) {
					return null;
				}

				/* Retourne un inspecteur */
				return new KspDeclarationChecker(document, file);
			}
		} catch (BadLocationException e) {
			ErrorUtils.handle(e);
		}

		/* Pas de déclaration KSP : on retourne null */
		return null;
	}

	/**
	 * Inspecte la ligne du document.
	 * 
	 * @param lineIdx Index en base zéro de la ligne.
	 */
	public void inspectLine(int lineIdx) {
		new LineInspector(lineIdx).inspectLine();
	}

	/**
	 * Génère les marqueurs pour la déclaration courante.
	 */
	public void generateMarkers() {
		generateMarkers(CURLY_BRACE_COUPLE);
		generateMarkers(PARENTHESIS_COUPLE);
		generateMarkers(JAVA_TAG_COUPLE);
	}

	/**
	 * Génère les marqueurs pour les problèmes liés à un couple de caractères ouvrants fermants donné.
	 * 
	 * @param couple Couple de caractères.
	 */
	private void generateMarkers(OpenCloseCouple couple) {
		/* Pile des caractères ouvrants */
		Deque<OpenCloseCharacterOccurence> openStack = new ArrayDeque<>();

		/* Parcourt les occurences des caractères. */
		for (OpenCloseCharacterOccurence occurence : occurences.values()) {
			if (occurence.getOpenCloseCharacter() == couple.getOpenCharacter()) {
				/* Ajoute à la stack des caractères ouvrants. */
				openStack.add(occurence);
			} else if (occurence.getOpenCloseCharacter() == couple.getCloseCharacter()) {
				if (openStack.isEmpty()) {
					/* Aucun caractère ouvrant à fermer : erreur */
					addMarker(occurence, couple.getMissingOpeningMessage());
				} else {
					/* Enlève le dernier caractère ouvrant. */
					openStack.pop();
				}
			}
		}

		if (!openStack.isEmpty()) {
			/* Il reste des caractères ouvrants non fermés : erreur */
			for (OpenCloseCharacterOccurence occurence : openStack) {
				addMarker(occurence, couple.getMissingClosingMessage());
			}
		}
	}

	/**
	 * Ajoute un marqueur sur une occurence de aractère avec un message donné.
	 * 
	 * @param occurence Occurence de caractère.
	 * @param message Message.
	 */
	private void addMarker(OpenCloseCharacterOccurence occurence, String message) {
		MarkerUtils.addKspMarker(occurence.getFileRegion(), message, IMarker.SEVERITY_ERROR);
	}

	/**
	 * Inspecteur d'une ligne de déclaration.
	 */
	private class LineInspector {

		private final int lineIdx;
		private IRegion lineInformation;
		private String lineContent;

		/**
		 * Créé une nouvelle instance de LineInspector.
		 * 
		 * @param lineIdx Index en base zéro de la ligne dans le document.
		 */
		public LineInspector(int lineIdx) {
			this.lineIdx = lineIdx;
		}

		/**
		 * Inspecte la ligne.
		 * <p>
		 * Note la présence des caractères ouvrants et fermants.
		 * </p>
		 */
		public void inspectLine() {
			try {
				/* Obtient la ligne. */
				lineInformation = document.getLineInformation(lineIdx);
				lineContent = document.get(lineInformation.getOffset(), lineInformation.getLength());
				/* Région standard : accolades et parenthèses. */
				checkCharacterCouple(CURLY_BRACE_COUPLE, KspRegionType.DEFAULT);
				checkCharacterCouple(PARENTHESIS_COUPLE, KspRegionType.DEFAULT);
				/* Région string SQL : tags Java. */
				checkCharacterCouple(JAVA_TAG_COUPLE, KspRegionType.STRING);
			} catch (BadLocationException e) {
				ErrorUtils.handle(e);
			}
		}

		/**
		 * Vérifie la présence d'un couple de caractère dans un type de région donné.
		 * 
		 * @param couple Couple de caractères.
		 * @param regionType Type de la région.
		 */
		private void checkCharacterCouple(OpenCloseCouple couple, KspRegionType regionType) {
			checkCharacter(couple.getOpenCharacter(), regionType);
			checkCharacter(couple.getCloseCharacter(), regionType);
		}

		/**
		 * Vérifie la présence des caractères dans un type de région donné.
		 * 
		 * @param openCloseCharacter Caractère à chercher.
		 * @param regionType Type de région ciblé.
		 */
		private void checkCharacter(OpenCloseCharacter openCloseCharacter, KspRegionType regionType) {
			/* Recherche le caractère à partir de son pattern. */
			Pattern pattern = openCloseCharacter.getPattern();
			Matcher matcher = pattern.matcher(lineContent);

			/* Parcourt les résultats. */
			while (matcher.find()) {
				for (int group = 1; group <= matcher.groupCount(); group++) {
					/* Construit la région de fichier du caractère trouvé */
					String characterValue = matcher.group(group);
					int characterLineOffSet = matcher.start(group);
					int characterDocumentOffSet = lineInformation.getOffset() + characterLineOffSet;

					/* Vérifie la région du caractère trouvé. */
					if (!DocumentUtils.isContentType(document, characterDocumentOffSet, regionType)) {
						return;
					}

					/* Note le caractère. */
					FileRegion characterRegion = new FileRegion(file, characterDocumentOffSet, characterValue.length(), lineIdx);
					OpenCloseCharacterOccurence occurence = new OpenCloseCharacterOccurence(openCloseCharacter, characterRegion);
					occurences.put(characterDocumentOffSet, occurence);
				}
			}
		}
	}

	/**
	 * Occurence d'un caractère à une région de fichier donné.
	 */
	private static class OpenCloseCharacterOccurence {

		private final OpenCloseCharacter openCloseCharacter;
		private final FileRegion fileRegion;

		/**
		 * Créé une nouvelle instance de OpenCloseCharacterOccurence.
		 * 
		 * @param openCloseCharacter Caractère.
		 * @param fileRegion Région de fichier du caractère.
		 */
		public OpenCloseCharacterOccurence(OpenCloseCharacter openCloseCharacter, FileRegion fileRegion) {
			this.openCloseCharacter = openCloseCharacter;
			this.fileRegion = fileRegion;
		}

		/**
		 * @return Le caractère.
		 */
		public OpenCloseCharacter getOpenCloseCharacter() {
			return openCloseCharacter;
		}

		/**
		 * @return La région de fichier.
		 */
		public FileRegion getFileRegion() {
			return fileRegion;
		}
	}
}
