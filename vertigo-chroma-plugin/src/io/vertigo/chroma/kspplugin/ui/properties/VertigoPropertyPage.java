package io.vertigo.chroma.kspplugin.ui.properties;

import io.vertigo.chroma.kspplugin.legacy.LegacyManager;
import io.vertigo.chroma.kspplugin.legacy.LegacyVersion;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Page de propriété d'un projet permettant de consulter et d'éditer la version du framework du projet.
 */
public class VertigoPropertyPage extends PropertyPage {

	private static final String PATH_TITLE = "Path:";
	private static final String LEGACY_VERSION_TITLE = "&Legacy version:";

	private static final int TEXT_FIELD_WIDTH = 50;

	private Combo legacyVersionCombo;

	/**
	 * Constructor for VertigoPropertyPage.
	 */
	public VertigoPropertyPage() {
		super();
	}

	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(PATH_TITLE);

		// Path text field
		Text pathValueText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
		pathValueText.setText(getProject().getFullPath().toString());
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void addSecondSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for owner field
		Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText(LEGACY_VERSION_TITLE);

		// Owner text field
		legacyVersionCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		legacyVersionCombo.setLayoutData(gd);

		// Populate owner text field
		LegacyVersion legacyVersion = LegacyManager.getInstance().getVersion(getProject());
		legacyVersionCombo.setItems(LegacyVersion.names());
		legacyVersionCombo.setText(legacyVersion.name());
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		LegacyVersion legacyVersion = LegacyManager.getInstance().getDefaultVersion(getProject());
		legacyVersionCombo.setText(legacyVersion.name());
	}

	public boolean performOk() {
		LegacyVersion legacyVersion = LegacyVersion.valueOf(legacyVersionCombo.getText());
		LegacyManager.getInstance().setVersion(getProject(), legacyVersion);
		return true;
	}

	private IProject getProject() {
		if (getElement() instanceof IResource) {
			return ((IResource) getElement()).getProject();
		}

		return ((IJavaProject) getElement()).getProject();
	}
}