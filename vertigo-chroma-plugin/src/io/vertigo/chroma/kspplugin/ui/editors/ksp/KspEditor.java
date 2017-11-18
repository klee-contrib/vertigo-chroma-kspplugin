package io.vertigo.chroma.kspplugin.ui.editors.ksp;

import io.vertigo.chroma.kspplugin.ui.editors.analysis.KspCheckerJob;
import io.vertigo.chroma.kspplugin.ui.editors.ksp.outline.KspOutlinePage;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import java.util.ListResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Editeur des fichiers KSP.
 */
public class KspEditor extends TextEditor implements IResourceChangeListener { // NOSONAR

	/*
	 * La vérification des KSP ramène des faux positifs. Désactivé pour le lot 1.
	 */
	private static final boolean ENABLE_CHECKER = false;

	/**
	 * Créé une nouvelle instance de KspTextEditor.
	 */
	public KspEditor() {
		super();

		/* Définit un ID de contexte utilisé pour ajouter des items via plugin.xml */
		setEditorContextMenuId("#KspEditorContext");

		/* Définit une configuration de SourceViewer pour définir des scanner. */
		KspSourceViewerConfiguration configuration = new KspSourceViewerConfiguration(this);
		setSourceViewerConfiguration(configuration);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		/* Abonnement au changement du fichier. */
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

		/* Abonnement au changement de part pour mémoriser l'éditeur courant. */
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		window.getPartService().addPartListener(new PartListener());
	}

	@Override
	protected void createActions() {
		super.createActions();

		/* Configure l'autocomplétion. */
		ContentAssistAction action = new ContentAssistAction(new ContentAssistBundle(), "ContentAssistProposal.", this);
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssist", action);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			return new KspOutlinePage(this);
		}
		return super.getAdapter(adapter);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
			return;
		}
		IEditorInput editorInput = this.getEditorInput();
		if (!(editorInput instanceof FileEditorInput)) {
			return;
		}

		FileEditorInput fileInput = (FileEditorInput) editorInput;
		IFile file = fileInput.getFile();
		IResourceDelta candidate = event.getDelta().findMember(file.getFullPath());
		if (candidate == null) {
			return;
		}
		/* Changement de contenu. */
		if ((candidate.getFlags() & IResourceDelta.CONTENT) == 0) {
			return;
		}
		switch (candidate.getKind()) {
		case IResourceDelta.ADDED:
		case IResourceDelta.CHANGED:
			checkKsp(file);
			break;
		case IResourceDelta.REMOVED:
		default:
			break;
		}
	}

	private void checkKsp(IFile file) {
		if (ENABLE_CHECKER) {
			KspCheckerJob.start(file);
		}
	}

	/**
	 * Listener des changements de parties de vue du workbench.
	 */
	private class PartListener implements IPartListener {

		@Override
		public void partOpened(IWorkbenchPart part) {
			setCurrent(part);
		}

		@Override
		public void partActivated(IWorkbenchPart part) {
			setCurrent(part);
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			setCurrent(part);
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
			// RAS.
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			// RAS.
		}

		private void setCurrent(IWorkbenchPart part) {
			if (KspEditor.this.equals(part)) {
				/* Si la part affichée est celle de l'instance de l'éditeur, on le note comme étant l'éditeur courant. */
				UiUtils.setCurrentEditor(KspEditor.this);
			}
		}
	}

	private static final class ContentAssistBundle extends ListResourceBundle {
		private final Object[][] contents = { { "ContentAssistProposal.label", "Content assist" }, { "ContentAssistProposal.tooltip", "Content assist" },
				{ "ContentAssistProposal.description", "Provides Content Assistance" } };

		@Override
		protected Object[][] getContents() {
			return contents;
		}
	}
}
