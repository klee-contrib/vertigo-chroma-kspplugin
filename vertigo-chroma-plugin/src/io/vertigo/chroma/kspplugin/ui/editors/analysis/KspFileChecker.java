package io.vertigo.chroma.kspplugin.ui.editors.analysis;

import io.vertigo.chroma.kspplugin.utils.ErrorUtils;
import io.vertigo.chroma.kspplugin.utils.MarkerUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

/**
 * Vérificateur de fichier KSP.
 */
public class KspFileChecker {

	private final IFile file;
	private IDocument document;

	/**
	 * Créé une nouvelle instance de KspFileChecker.
	 * 
	 * @param file Fichier KSP.
	 */
	public KspFileChecker(IFile file) {
		this.file = file;
	}

	/**
	 * Exécute la vérification du fichier et met à jour les marqueurs.
	 */
	public void check() {
		deleteMarkers();
		createMarkers();
	}

	/**
	 * Supprime les marqueurs du fichier courant.
	 */
	private void deleteMarkers() {
		MarkerUtils.deleteKspMarkers(file);
	}

	/**
	 * Créé les marqueurs du fichier courant.
	 */
	private void createMarkers() {
		try {
			/* Obtient le document. */
			TextFileDocumentProvider documentProvider = new TextFileDocumentProvider();
			documentProvider.connect(file);
			document = documentProvider.getDocument(file);

			/* Analyse le document. */
			checkKspDocument();

			/* Libère le document. */
			documentProvider.disconnect(file);
		} catch (CoreException e) {
			ErrorUtils.handle(e);
		}
	}

	/**
	 * Analyse le document KSP.
	 */
	private void checkKspDocument() {

		KspDeclarationChecker checker = null;

		/* Parcourt les lignes du document. */
		for (int lineIdx = 0; lineIdx < document.getNumberOfLines(); lineIdx++) {
			/* Extrait un candidat de ligne de déclaration. */
			KspDeclarationChecker candidate = KspDeclarationChecker.extractChecker(file, document, lineIdx);

			/* Cas où la ligne contient une déclaration KSP */
			if (candidate != null) {
				if (checker != null) {
					/* Cas d'une nouvelle déclaration : on génère les marqueurs pour la déclaration précédente. */
					checker.generateMarkers();
				}

				/* Mise à jour de la déclaration inspectée courante. */
				checker = candidate;
			}

			/* Il existe une déclaration courante */
			if (checker != null) {
				/* On inspecte la ligne. */
				checker.inspectLine(lineIdx);
			}
		}

		/* Dernière déclaration du fichier */
		if (checker != null) {
			checker.generateMarkers();
		}
	}
}
