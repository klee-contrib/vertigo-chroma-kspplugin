package io.vertigo.chroma.kspplugin.ui.editors.kpr;

import org.eclipse.ui.editors.text.TextEditor;

/**
 * Editeur des fichiers KPR.
 */
public class KprEditor extends TextEditor {

	/**
	 * Créé une nouvelle instance de KspTextEditor.
	 */
	public KprEditor() {
		super();

		/* Définit une configuration de SourceViewer pour définir des scanner. */
		KprSourceViewerConfiguration configuration = new KprSourceViewerConfiguration(this);
		setSourceViewerConfiguration(configuration);
	}
}
