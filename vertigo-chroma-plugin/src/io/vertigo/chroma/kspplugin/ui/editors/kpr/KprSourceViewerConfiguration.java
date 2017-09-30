package io.vertigo.chroma.kspplugin.ui.editors.kpr;

import io.vertigo.chroma.kspplugin.ui.editors.hyperlinks.FilePathHyperLinkDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

/**
 * Coniguration du SourceViewer de l'éditeur de KPR.
 */
public class KprSourceViewerConfiguration extends TextSourceViewerConfiguration {

	private KprEditor kprEditor;

	public KprSourceViewerConfiguration(KprEditor kprEditor) {
		this.kprEditor = kprEditor;
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] hyperlinkDetectors = super.getHyperlinkDetectors(sourceViewer);
		List<IHyperlinkDetector> list = new ArrayList<>(Arrays.asList(hyperlinkDetectors));
		list.add(new FilePathHyperLinkDetector());
		return list.toArray(new IHyperlinkDetector[] {});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map targets = super.getHyperlinkDetectorTargets(sourceViewer);
		targets.put("io.vertigo.chroma.kspPlugin.targets.kprFile", kprEditor);
		return targets;
	}
}
