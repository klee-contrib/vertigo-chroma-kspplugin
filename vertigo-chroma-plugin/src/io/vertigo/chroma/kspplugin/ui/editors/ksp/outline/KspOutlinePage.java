package io.vertigo.chroma.kspplugin.ui.editors.ksp.outline;

import io.vertigo.chroma.kspplugin.model.KspDeclaration;
import io.vertigo.chroma.kspplugin.model.KspFile;
import io.vertigo.chroma.kspplugin.resources.KspManager;
import io.vertigo.chroma.kspplugin.ui.editors.ksp.KspEditor;
import io.vertigo.chroma.kspplugin.utils.ImageUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * Page affichant le outline d'un fichier KSP.
 */
public class KspOutlinePage extends ContentOutlinePage {

	private final KspEditor editor;
	private Action sortAction;
	private boolean isAlphabeticOrder = false;

	/**
	 * Créé une nouvelle instance de KspOutlinePage.
	 * 
	 * @param editor Editeur KSP.
	 */
	public KspOutlinePage(KspEditor editor) {
		this.editor = editor;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new KspContentProvider());
		viewer.setLabelProvider(new KspLabelProvider());
		viewer.addSelectionChangedListener(this);
		viewer.setInput((Object) UiUtils.getEditorFile(editor));

		createActions();
		createToolbar();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);

		/* Récupère l'objet de déclaration KSP */
		TreeKspDeclaration treeKspDeclaration = getTreeKspDeclaration(event);
		if (treeKspDeclaration == null) {
			return;
		}

		/* Navigue vers l'objet. */
		UiUtils.navigateTo(treeKspDeclaration.getKspDeclaration(), editor);
	}

	/**
	 * Créé les actions de la page.
	 */
	private void createActions() {
		/* Créé l'action de tri de l'arborescence */
		sortAction = new Action("Sort", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				sortDeclarations();
			}
		};
		sortAction.setImageDescriptor(ImageUtils.getSortImage());
	}

	/**
	 * Créé la barre d'outils de la page.
	 */
	private void createToolbar() {
		/* Ajoute le bouton de tri. */
		IToolBarManager mgr = getSite().getActionBars().getToolBarManager();
		mgr.add(sortAction);
	}

	/**
	 * Extrait l'objet de déclaration KSP d'un événement de sélection de l'arboresence.
	 * 
	 * @param event Evénement de sélection.
	 * @return Déclaration KSP.
	 */
	private TreeKspDeclaration getTreeKspDeclaration(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection == null) {
			return null;
		}
		if (!(selection instanceof ITreeSelection)) {
			return null;
		}
		ITreeSelection treeSelection = (ITreeSelection) selection;
		Object firstElement = treeSelection.getFirstElement();
		if (!(firstElement instanceof TreeKspDeclaration)) {
			return null;
		}
		return (TreeKspDeclaration) firstElement;
	}

	/**
	 * Tri les déclarations.
	 */
	private void sortDeclarations() {
		isAlphabeticOrder = sortAction.isChecked();

		/* Rafraîchit le viewer. */
		refreshViewer();
	}

	/**
	 * Rafraîchit la vue.
	 */
	private void refreshViewer() {
		TreeViewer viewer = getTreeViewer();

		viewer.getControl().getDisplay().asyncExec(() -> {
			Control ctrl = viewer.getControl();
			if (ctrl == null || ctrl.isDisposed()) {
				return;
			}

			viewer.refresh();
		});
	}

	/**
	 * Fournisseur de contenu de l'arboresence.
	 */
	private class KspContentProvider implements IStructuredContentProvider, ITreeContentProvider, IResourceChangeListener {

		private Viewer viewer;
		private IResource input;
		private TreeParent invisibleRoot;

		@Override
		public void inputChanged(Viewer newViewer, Object oldInput, Object newInput) {
			if (oldInput == null) {
				IResource resource = (IResource) newInput;
				/* Abonnement aux changement de ressources. */
				if (newInput != null) {
					resource.getWorkspace().addResourceChangeListener(this);
				}
			}
			this.viewer = newViewer;
			this.input = (IResource) newInput;
		}

		@Override
		public void dispose() {
			// RAS.
		}

		@Override
		public Object[] getElements(Object parent) {
			if (input == null) {
				return new Object[0];
			}
			if (parent.equals(input)) {
				initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		@Override
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		@Override
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).hasChildren();
			}
			return false;
		}

		/**
		 * Initialise l'arborescence.
		 */
		private void initialize() {

			/* Obtient le document à partir de l'éditeur. */
			IDocument document = UiUtils.getEditorDocument(editor);
			IFile file = UiUtils.getEditorFile(editor);

			/* Parse le contenu du document. */
			KspFile kspFile = KspManager.getInstance().createKspFile(document, file);

			/* Construit l'arbre. */
			invisibleRoot = new TreeParent("");

			/* Noeud de package. */
			String packageName = kspFile.getPackageName();
			if (packageName != null && !packageName.isEmpty()) {
				TreeParent kspPackage = new TreeParent(packageName);
				invisibleRoot.addChild(kspPackage);
			}

			/* Noeuds des déclarations. */
			KspDeclaration[] kspDeclarations = getSortedDeclarations(kspFile);
			for (KspDeclaration kspDeclaration : kspDeclarations) {
				TreeObject to = new TreeKspDeclaration(kspDeclaration);
				invisibleRoot.addChild(to);
			}
		}

		/**
		 * Obtient les déclarations triés du KSP.
		 * 
		 * @param kspFile Fichier KSP.
		 * @return Déclarations triées.
		 */
		private KspDeclaration[] getSortedDeclarations(KspFile kspFile) {
			KspDeclaration[] kspDeclarations = kspFile.getKspDeclarations().toArray(new KspDeclaration[] {});

			/* Gestion du tri par ordre alphabétique. */
			if (isAlphabeticOrder) {
				Arrays.sort(kspDeclarations, (arg0, arg1) -> arg0.toString().compareTo(arg1.toString()));
			}
			return kspDeclarations;
		}

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			/* Rafraîchissement de toute la vue. */
			UiUtils.refreshViewer(viewer);
		}
	}

	class KspLabelProvider extends StyledCellLabelProvider { // NOSONAR

		@Override
		public void update(ViewerCell cell) {
			TreeObject obj = (TreeObject) cell.getElement();

			setStyledText(cell, obj);

			/* Image */
			cell.setImage(getImage(obj));

			super.update(cell);
		}

		private void setStyledText(ViewerCell cell, TreeObject obj) {
			/* Calcul du texte. */
			String mainText = obj.getMainText();
			if (mainText == null) {
				return;
			}
			String subText = obj.getSubText();
			String subTextFinal = subText == null ? "" : (" : " + subText);
			String fullText = mainText + subTextFinal;
			cell.setText(fullText);

			/* Calcul du style. */
			List<StyleRange> styles = new ArrayList<>();
			StyleRange styleMainText = new StyleRange(0, mainText.length(), null, null);
			styles.add(styleMainText);
			if (!subTextFinal.isEmpty()) {
				Display display = Display.getCurrent();
				Color blue = display.getSystemColor(SWT.COLOR_DARK_YELLOW);
				StyleRange styleSubText = new StyleRange(mainText.length(), subTextFinal.length(), blue, null);
				styles.add(styleSubText);
			}
			cell.setStyleRanges(styles.toArray(new StyleRange[0]));
		}

		public Image getImage(TreeObject to) {
			if (to instanceof TreeKspDeclaration) {
				TreeKspDeclaration tok = (TreeKspDeclaration) to;
				return ImageUtils.getDeclarationImage(tok.getKspDeclaration());
			}
			return ImageUtils.getPackageImage();
		}
	}
}
