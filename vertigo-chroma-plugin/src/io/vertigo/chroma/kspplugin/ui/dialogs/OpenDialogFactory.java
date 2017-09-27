package io.vertigo.chroma.kspplugin.ui.dialogs;

import io.vertigo.chroma.kspplugin.model.Openable;
import io.vertigo.chroma.kspplugin.utils.MessageUtils;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

/**
 * Usine à dialogue de recherche.
 */
public final class OpenDialogFactory {

	private static final int LIST_WIDTH = 100;
	private static final int LIST_HEIGHT = 15;

	private OpenDialogFactory() {
		// RAS.
	}

	/**
	 * Ouvre un dialogue de recherche à partir d'un template.
	 * 
	 * @param template Template.
	 */
	public static void openDialog(OpenDialogTemplate template) {

		String nature = template.getNature();

		/* Charge les éléments à rechercher. */
		Object[] items = template.getElements();

		/* Cas sans élément à afficher. */
		if (items.length == 0) {
			/* On affiche un message et on sort. */
			MessageUtils.showNoElementMessage(nature);
			return;
		}

		/* Construit le dialogue. */
		TwoPaneElementSelector dialog = createDialog(nature, items);

		/* Ouvre le dialogue. */
		openDialog(dialog);
	}

	private static <T extends Openable> TwoPaneElementSelector createDialog(String nature, Object[] items) {
		TwoPaneElementSelector dialog = new TwoPaneElementSelector(UiUtils.getShell(), new ElementLabelProvider<T>(), new QualifierLabelProvider<T>());
		dialog.setSize(LIST_WIDTH, LIST_HEIGHT);
		dialog.setLowerListLabel("Selection");
		dialog.setLowerListComparator((o1, o2) -> 0);
		dialog.setTitle(MessageFormat.format("Open {0}", nature));
		dialog.setMessage(MessageFormat.format("Enter {0} prefix or pattern :", nature));
		dialog.setElements(items);
		return dialog;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Openable> void openDialog(TwoPaneElementSelector dialog) {
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result != null && result.length == 1) {
				/* Un objet est sélectionné : on navigue dessus. */
				Object object = result[0];
				T item = (T) object;
				UiUtils.navigateTo(item);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static class ElementLabelProvider<T extends Openable> extends LabelProvider {
		@Override
		public String getText(Object element) {
			T item = (T) element;
			return item.getText();
		}

		@Override
		public Image getImage(Object element) {
			T item = (T) element;
			return item.getImage();
		}
	}

	@SuppressWarnings("unchecked")
	private static class QualifierLabelProvider<T extends Openable> extends LabelProvider {
		@Override
		public String getText(Object element) {
			T item = (T) element;
			return item.getQualifier();
		}
	}
}
