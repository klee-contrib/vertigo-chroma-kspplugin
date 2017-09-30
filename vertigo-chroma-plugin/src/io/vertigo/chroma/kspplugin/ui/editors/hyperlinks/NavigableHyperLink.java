package io.vertigo.chroma.kspplugin.ui.editors.hyperlinks;

import io.vertigo.chroma.kspplugin.model.Navigable;
import io.vertigo.chroma.kspplugin.utils.UiUtils;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Lien vers un navigable.
 */
public class NavigableHyperLink implements IHyperlink {

	private final IRegion urlRegion;
	private final Navigable navigable;
	private final String hyperlinkText;

	/**
	 * Créé une nouvelle instance de NavigableHyperLink.
	 * 
	 * @param urlRegion Région du lien dans le document.
	 * @param navigable Navigable ciblé.
	 * @param hyperlinkText Libellé du lien.
	 */
	public NavigableHyperLink(IRegion urlRegion, Navigable navigable, String hyperlinkText) {
		this.urlRegion = urlRegion;
		this.navigable = navigable;
		this.hyperlinkText = hyperlinkText;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return urlRegion;
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public String getHyperlinkText() {
		return hyperlinkText;
	}

	@Override
	public void open() {
		UiUtils.navigateTo(navigable);
	}
}
