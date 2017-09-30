package io.vertigo.chroma.kspplugin.ui.editors.ksp;

import io.vertigo.chroma.kspplugin.model.KspRegionType;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Définit le paramétrage d'un document KSP.
 */
public class KspDocumentSetupParticipant implements IDocumentSetupParticipant {

	private static final String[] TYPES = new String[] {
	/* Région par défaut. */
	KspRegionType.DEFAULT.getContentType(),
	/* Région commentaire */
	KspRegionType.COMMENT.getContentType(),
	/* Région string. */
	KspRegionType.STRING.getContentType() };

	@Override
	public void setup(IDocument document) {
		/* Définit un partitionnement du document. */
		IDocumentPartitioner p = new FastPartitioner(createKspPartitionScanner(), TYPES);
		IDocumentExtension3 de3 = (IDocumentExtension3) document;
		de3.setDocumentPartitioner(KspRegionType.PARTITIONING, p);
		p.connect(document);
	}

	private IPartitionTokenScanner createKspPartitionScanner() {
		RuleBasedPartitionScanner scanner = new RuleBasedPartitionScanner();
		scanner.setPredicateRules(new IPredicateRule[] {
		/* String entre double quote. */
		new PatternRule("\"", "\"", new Token(KspRegionType.STRING.getContentType()), '\\', false),
		/* Commentaire multi-lignes */
		new PatternRule("/*", "*/", new Token(KspRegionType.COMMENT.getContentType()), '\\', false),
		/* Commentaire fin de ligne */
		new EndOfLineRule("//", new Token(KspRegionType.COMMENT.getContentType())) });
		return scanner;
	}

}
