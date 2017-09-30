package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.model.KspRegionType;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;

import java.util.function.Predicate;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextSelection;

/**
 * Méthodes utilitaires pour manipuler des documents.
 */
public final class DocumentUtils {

	private DocumentUtils() {
		// RAS.
	}

	/**
	 * Renvoie le mot courant à partir d'une sélection dans un document.
	 * 
	 * @param document Document.
	 * @param selection Sélection. Peut être vide.
	 * @param wordSelectionType Type de sélection.
	 * @return Mot sélectionné, <code>null</code> si aucun mot trouvé.
	 */
	public static ITextSelection findCurrentWord(IDocument document, ITextSelection selection, WordSelectionType wordSelectionType) {
		return new CurrentWordFinder(document, selection, wordSelectionType).find();
	}

	/**
	 * Indique si l'offset d'un document donné se trouve dans une région donnée.
	 * 
	 * @param document Document.
	 * @param offset Offset.
	 * @param contentType Content type de la région.
	 * @return
	 */
	public static boolean isContentType(IDocument document, int offset, KspRegionType regionType) {
		try {
			/* Extrait le type de la partition. */
			IDocumentExtension3 de3 = (IDocumentExtension3) document;
			String regionContentType = de3.getContentType(KspRegionType.PARTITIONING, offset, true);

			/* Vérifie que la région correspond. */
			return regionType.getContentType().equals(regionContentType);
		} catch (BadLocationException | BadPartitioningException e) {
			ErrorUtils.handle(e);
		}

		return false;
	}

	/**
	 * Indique si une sélection d'un document représente exactement une région de string (aux double quote près).
	 * 
	 * @param document Document.
	 * @param selection Sélection de texte.
	 * @return <code>true</code> si c'est exactement une région de string.
	 */
	public static boolean isExactKspString(IDocument document, ITextSelection selection) {
		IDocumentExtension3 extension = (IDocumentExtension3) document;
		try {
			/* Charge les régions du document couverte par la sélecion. */
			ITypedRegion[] regions = extension.computePartitioning(KspRegionType.PARTITIONING, selection.getOffset(), selection.getLength(), false);

			/* Vérifie qu'on a une seule région. */
			if (regions.length != 1) {
				return false;
			}

			/* Charge la région entière */
			ITypedRegion region = extension.getPartition(KspRegionType.PARTITIONING, selection.getOffset(), false);

			/* Vérifie que c'est une région de string KSP. */
			if (!region.getType().equals(KspRegionType.STRING.getContentType())) {
				return false;
			}

			/* Vérifie que la région couvre exactement la sélection */
			int selectionWithQuoteOffset = selection.getOffset() - 1; // Prend en compte la double quote ouvrante.
			int selectionWithQuoteLength = selection.getLength() + 2; // Prend en compte les deux double quote.
			if (region.getOffset() == selectionWithQuoteOffset && region.getLength() == selectionWithQuoteLength) {
				return true;
			}
		} catch (BadLocationException | BadPartitioningException e) {
			ErrorUtils.handle(e);
		}

		return false;
	}

	/**
	 * Classe pour trouver le mot courant dans un document.
	 */
	private static class CurrentWordFinder {

		private final IDocument document;
		private final ITextSelection selection;
		private final Predicate<String> wordTest;
		private final StringBuilder builder = new StringBuilder();
		private int currentOffset;
		private int currentLength;

		/**
		 * Créé une nouvelle instance de CurrentWordFinder.
		 * 
		 * @param document Document.
		 * @param selection Sélection dans le document.
		 * @param wordSelectionType Type de sélection.
		 */
		public CurrentWordFinder(IDocument document, ITextSelection selection, WordSelectionType wordSelectionType) {
			this.document = document;
			this.selection = selection;
			this.wordTest = wordSelectionType.getTester();
		}

		/**
		 * Trouve le mot courant.
		 * 
		 * @return
		 */
		public ITextSelection find() {

			/* Initialise un buffer avec la sélection. */
			String initialSelection = selection.getText();
			if (!initialSelection.isEmpty() && !wordTest.test(initialSelection)) {
				return null;
			}
			builder.append(initialSelection);

			/* Initialise le parcourt du document. */
			currentOffset = selection.getOffset();
			currentLength = selection.getLength();

			/* Trouve le début du mot. */
			findWordStart();

			/* Trouve la fin du mot. */
			findWordEnd();

			/* Aucun mot sélectionné : on renvoie null. */
			if (builder.length() == 0) {
				return null;
			}

			/* Renvoie de la sélection. */
			return new TextSelection(document, currentOffset, currentLength);
		}

		private void findWordStart() {
			/* On se place au début de la sélection initiale. */
			int offset = selection.getOffset();
			/* On parcourt les caractères vers la gauche. */
			while (true) { // NOSONAR
				offset--;
				try {
					/* Obtention du caractère. */
					String currentChar = document.get(offset, 1);
					if (wordTest.test(currentChar)) {
						/* Test ok : on l'insert au début du mot courant. */
						builder.insert(0, currentChar);
						/* On met à jour les coordonnées du mot courant. */
						currentOffset--;
						currentLength++;
					} else {
						/* Test ko : le début du mot est atteint. */
						break;
					}
				} catch (BadLocationException e) { // NOSONAR
					break;
				}
			}
		}

		private void findWordEnd() {
			/* On se place à la fin de la sélection initiale. */
			int offset = selection.getOffset() + selection.getLength() - 1;
			/* On parcourt les caractères vers la droite. */
			while (true) { // NOSONAR
				offset++;
				try {
					/* Obtention du caractère. */
					String currentChar = document.get(offset, 1);
					if (wordTest.test(currentChar)) {
						/* Test ok : on l'insert à la fin du mot courant. */
						builder.append(currentChar);
						/* On met à jour les coordonnées du mot courant. */
						currentLength++;
					} else {
						/* Test ko : la fin du mot est atteint. */
						break;
					}
				} catch (BadLocationException e) { // NOSONAR
					break;
				}
			}
		}
	}
}
