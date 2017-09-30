package io.vertigo.chroma.kspplugin.ui.editors.ksp;

import io.vertigo.chroma.kspplugin.model.KspRegionType;
import io.vertigo.chroma.kspplugin.ui.editors.completion.KspDefaultContentAssistProcessor;
import io.vertigo.chroma.kspplugin.ui.editors.completion.KspStringContentAssistProcessor;
import io.vertigo.chroma.kspplugin.ui.editors.hyperlinks.CanonicalJavaNameHyperLinkDetector;
import io.vertigo.chroma.kspplugin.ui.editors.hyperlinks.DtoDefinitionPathHyperLinkDetector;
import io.vertigo.chroma.kspplugin.ui.editors.hyperlinks.KspNameHyperLinkDetector;
import io.vertigo.chroma.kspplugin.ui.editors.hyperlinks.SqlTableHyperLinkDetector;
import io.vertigo.chroma.kspplugin.ui.editors.ksp.scanners.KspCommentScanner;
import io.vertigo.chroma.kspplugin.ui.editors.ksp.scanners.KspMainScanner;
import io.vertigo.chroma.kspplugin.ui.editors.ksp.scanners.KspStringScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

/**
 * Configuration du SourceViewer de l'éditeur de KSP.
 */
public class KspSourceViewerConfiguration extends TextSourceViewerConfiguration {

	private ITokenScanner commentScanner = new KspCommentScanner();
	private ITokenScanner stringScanner = new KspStringScanner();
	private ITokenScanner defaultScanner = new KspMainScanner();
	private KspEditor kspTextEditor;

	public KspSourceViewerConfiguration(KspEditor kspTextEditor) {
		this.kspTextEditor = kspTextEditor;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		/* Créé un Reconsilier chargé de gérer les changements du document. */
		PresentationReconciler reconciler = new PresentationReconciler();

		/* Définition du nom du partitionnement effectué par KspDocumentSetupParticipant. */
		reconciler.setDocumentPartitioning(KspRegionType.PARTITIONING);

		/* Définition des scanners pour chacune des trois partitions. */
		setRepairer(reconciler, commentScanner, KspRegionType.COMMENT.getContentType());
		setRepairer(reconciler, stringScanner, KspRegionType.STRING.getContentType());
		setRepairer(reconciler, defaultScanner, KspRegionType.DEFAULT.getContentType());

		return reconciler;
	}

	private void setRepairer(PresentationReconciler reconciler, ITokenScanner scanner, String contentType) {
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, contentType);
		reconciler.setRepairer(dr, contentType);
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return new KspTextHover();
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] hyperlinkDetectors = super.getHyperlinkDetectors(sourceViewer);
		List<IHyperlinkDetector> list = new ArrayList<>(Arrays.asList(hyperlinkDetectors));
		list.add(new KspNameHyperLinkDetector());
		list.add(new SqlTableHyperLinkDetector());
		list.add(new CanonicalJavaNameHyperLinkDetector());
		list.add(new DtoDefinitionPathHyperLinkDetector());
		return list.toArray(new IHyperlinkDetector[] {});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map targets = super.getHyperlinkDetectorTargets(sourceViewer);
		targets.put("io.vertigo.chroma.kspPlugin.targets.kspFile", kspTextEditor);
		return targets;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		assistant.setDocumentPartitioning(KspRegionType.PARTITIONING);
		assistant.setContentAssistProcessor(new KspDefaultContentAssistProcessor(), KspRegionType.DEFAULT.getContentType());
		assistant.setContentAssistProcessor(new KspStringContentAssistProcessor(), KspRegionType.STRING.getContentType());
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		assistant.enableAutoActivation(true);
		assistant.enableAutoInsert(true);
		return assistant;
	}

	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return parent -> new DefaultInformationControl(parent, new KspInformationPresenter());
	}
}
