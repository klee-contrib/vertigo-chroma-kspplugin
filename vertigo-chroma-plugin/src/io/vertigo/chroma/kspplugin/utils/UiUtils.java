package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.Navigable;
import io.vertigo.chroma.kspplugin.model.WordSelectionType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public final class UiUtils {

	private static IEditorPart currentEditor;

	private UiUtils() {
		// RAS.
	}

	/**
	 * Définit l'éditeur courant.
	 * 
	 * @param part Editeur.
	 */
	public static synchronized void setCurrentEditor(IEditorPart part) {
		currentEditor = part;
	}

	/**
	 * Affiche un message d'information dans une popup.
	 * 
	 * @param message Message à afficher.
	 */
	public static void showMessage(String message) {
		MessageDialog.openInformation(getShell(), "Vertigo Plugin Information", message);
	}

	/**
	 * Navigue vers un fichier (ouvre un éditeur).
	 * 
	 * @param file Fichier.
	 */
	public static void navigateTo(IFile file) {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();

		try {
			IDE.openEditor(activePage, file);
		} catch (PartInitException e) {
			ErrorUtils.handle(e);
		}
	}

	/**
	 * Navigue vers l'objet (ouvre un éditeur et met le focus sur la région).
	 * 
	 * @param navigable Objet navigable.
	 */
	public static void navigateTo(Navigable navigable) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		FileRegion fileRegion = navigable.getFileRegion();
		try {
			IEditorPart openEditor = IDE.openEditor(activePage, fileRegion.getFile());
			AbstractTextEditor editor = (AbstractTextEditor) openEditor;
			editor.selectAndReveal(fileRegion.getOffset(), fileRegion.getLength());
		} catch (PartInitException e) {
			ErrorUtils.handle(e);
		}
	}

	/**
	 * Navigue vers l'objet (met le focus sur la région).
	 * 
	 * @param navigable Objet navigable.
	 * @param editor Editeur courant.
	 */
	public static void navigateTo(Navigable navigable, AbstractTextEditor editor) {
		FileRegion fileRegion = navigable.getFileRegion();
		editor.selectAndReveal(fileRegion.getOffset(), fileRegion.getLength());
	}

	public static Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	private static IEditorPart getCurrentEditor() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		/* Cas d'un thread non UI sans workbench actif. */
		if (activeWorkbenchWindow == null) {
			/* On renvoie l'éditeur courant déclaré par le dernier éditeur ouvert. */
			return currentEditor;
		}
		return activeWorkbenchWindow.getActivePage().getActiveEditor();
	}

	public static IFile getCurrentEditorFile() {
		IEditorPart activeEditor = getCurrentEditor();
		if (activeEditor == null) {
			return null;
		}
		return getEditorFile(activeEditor);
	}

	public static IProject getCurrentEditorProject() {
		IFile file = getCurrentEditorFile();
		if (file == null) {
			return null;
		}
		return file.getProject();
	}

	public static IFile getEditorFile(IEditorPart editor) {
		if (editor == null) {
			return null;
		}
		FileEditorInput fileInput = (FileEditorInput) editor.getEditorInput();
		if (fileInput == null) {
			return null;
		}
		return fileInput.getFile();
	}

	public static IDocument getEditorDocument(AbstractTextEditor editor) {
		IEditorInput editorInput = editor.getEditorInput();
		IDocumentProvider documentProvider = editor.getDocumentProvider();
		return documentProvider.getDocument(editorInput);
	}

	public static void refreshViewer(Viewer viewer) {
		Control widget = viewer.getControl();
		if (widget.isDisposed()) {
			return;
		}

		widget.getDisplay().asyncExec(() -> {
			Control ctrl = widget;
			if (ctrl == null || ctrl.isDisposed()) {
				return;
			}

			viewer.refresh();
		});
	}

	public static String getCurrentEditorCurrentWord(WordSelectionType wordSelectionType) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		/* Extrait l'éditeur courant. */
		ITextEditor editor = (ITextEditor) activePage.getActiveEditor();
		if (editor == null) {
			return null;
		}

		/* Extrait la sélection courante. */
		ITextSelection selection = (ITextSelection) activePage.getSelection();
		if (selection == null) {
			return null;
		}

		/* Extrait le document courant. */
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());

		/* Extrait le mot sélectionné. */
		ITextSelection currentWordSelection = DocumentUtils.findCurrentWord(document, selection, wordSelectionType);
		if (currentWordSelection == null) {
			return null;
		}

		return currentWordSelection.getText();
	}

	public static boolean isDarkBackground() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.e4.ui.css.swt.theme");
		String themeId = preferences.get("themeid", "org.eclipse.e4.ui.css.theme.e4_classic");
		return themeId.contains("dark");
	}
}
