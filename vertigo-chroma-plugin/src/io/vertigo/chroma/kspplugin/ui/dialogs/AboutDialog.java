package io.vertigo.chroma.kspplugin.ui.dialogs;

import io.vertigo.chroma.kspplugin.Activator;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

public class AboutDialog extends Dialog {

	private static final int LOGO_SIZE = 50;
	private static final int POPUP_WIDTH = 600;
	private static final int POPUP_HEIGHT = 230;
	private static final String GITHUB_URL = "https://github.com/sebez/vertigo-chroma-kspplugin";

	public AboutDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		/* Titre dialogue. */
		this.getShell().setText("About KSP Plugin");

		/* Layout */
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, false);
		layout.marginRight = 5;
		layout.marginLeft = 10;
		Color white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		container.setBackground(white);
		container.setLayout(layout);

		/* Logo */
		Label lblLogo = new Label(container, SWT.NONE);
		lblLogo.setBackgroundImage(getImage());
		lblLogo.setBackground(white);
		lblLogo.setText("      ");
		lblLogo.setSize(LOGO_SIZE, LOGO_SIZE);

		/* Produit */
		Label lblProduct = new Label(container, SWT.NONE);
		lblProduct.setText("Vertigo Chroma KSP Plugin");
		lblProduct.setBackground(white);

		/* Version */
		Version version = FrameworkUtil.getBundle(getClass()).getVersion();
		String fullVersion = version.toString();
		Label lblVersion = new Label(container, SWT.NONE);
		lblVersion.setText("Version : " + fullVersion);
		lblVersion.setBackground(white);

		/* Version */
		Label lblAuthor = new Label(container, SWT.NONE);
		lblAuthor.setText("Author : @sebez");
		lblAuthor.setBackground(white);

		/* Libellé documentation */
		Label lblDoc = new Label(container, SWT.NONE);
		lblDoc.setText("Documentation, sources, releases are published in the KSP plugin github repository : ");
		lblDoc.setBackground(white);

		/* Lien vers le github */
		Link link = new Link(container, SWT.NONE);
		String message = "<a href=\"" + GITHUB_URL + "\">" + GITHUB_URL + "</a>";
		link.setText(message);
		link.setBackground(white);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(GITHUB_URL);
			}
		});

		return container;
	}

	private Image getImage() {
		String path = "icons/ksp.gif";
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL url = FileLocator.find(bundle, new Path(path), null);
		ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
		return imageDesc.createImage();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		/* On affiche qu'un bouton OK. */
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(POPUP_WIDTH, POPUP_HEIGHT);
	}

}
