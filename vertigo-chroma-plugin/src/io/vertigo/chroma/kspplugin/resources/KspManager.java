package io.vertigo.chroma.kspplugin.resources;

import io.vertigo.chroma.kspplugin.legacy.LegacyManager;
import io.vertigo.chroma.kspplugin.legacy.LegacyStrategy;
import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.KspAttribute;
import io.vertigo.chroma.kspplugin.model.KspDeclaration;
import io.vertigo.chroma.kspplugin.model.KspDeclarationParts;
import io.vertigo.chroma.kspplugin.model.KspFile;
import io.vertigo.chroma.kspplugin.model.KspNature;
import io.vertigo.chroma.kspplugin.model.KspRegionType;
import io.vertigo.chroma.kspplugin.model.KspWorkspace;
import io.vertigo.chroma.kspplugin.model.Manager;
import io.vertigo.chroma.kspplugin.resources.core.FileProvider;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStore;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStoreImplementor;
import io.vertigo.chroma.kspplugin.utils.DocumentUtils;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;
import io.vertigo.chroma.kspplugin.utils.KspStringUtils;
import io.vertigo.chroma.kspplugin.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Manager des fichiers KSP.
 * <p>
 * <ul>
 * <li>index les fichiers KSP et leurs déclarations.</li>
 * <li>maintient un cache à jour</li>
 * <li>publie une API de recherche</li>
 * </ul>
 * <p>
 */
public final class KspManager implements Manager {

	private final ResourceStore<KspDeclaration> store;
	private static KspManager instance;

	/**
	 * Créé une nouvelle instance de KspManager.
	 */
	private KspManager() {
		store = new ResourceStore<>(new Implementor());
	}

	/**
	 * @return Instance du singleton.
	 */
	public static synchronized KspManager getInstance() {
		if (instance == null) {
			instance = new KspManager();
		}
		return instance;
	}

	@Override
	public void init() {
		/* Démarre le magasin qui index tous les fichiers concernés. */
		instance.store.start();
	}

	/**
	 * @return Workspace des KSP.
	 */
	public KspWorkspace getWorkspace() {
		return new KspWorkspace(store.getAllItems());
	}

	/**
	 * Renvoie la première déclaration correspondant au nom en constant case (avec le préfixe).
	 * 
	 * @param constantCaseName Nom en constant case avec le préfixe.
	 * @return Déclaration KSP, <code>null</code> sinon.
	 */
	public KspDeclaration findKspDeclarationByConstantCaseName(String constantCaseName) {
		return store.findFirstItem(kspDeclaration -> constantCaseName.equals(kspDeclaration.getConstantCaseName()));
	}

	/**
	 * Renvoie la première déclaration correspondant au nom Java et à l'objet (Task, DtDefinition, ...).
	 * 
	 * @param javaName Nom en java.
	 * @param kspNature Nature (Task, DtDefinition, ...).
	 * @return Déclaration KSP, <code>null</code> sinon.
	 */
	public KspDeclaration findKspDeclaration(String javaName, KspNature kspNature) {
		String kspKeyword = LegacyManager.getInstance().getCurrentStrategy().getKspKeyword(kspNature);
		return store.findFirstItem(kspDeclaration -> kspKeyword.equals(kspDeclaration.getNature()) && javaName.equals(kspDeclaration.getJavaName()));
	}

	/**
	 * Renvoie la première déclaration correspondant au nom Java.
	 * 
	 * @param javaName Nom en java.
	 * @return Déclaration KSP, <code>null</code> sinon.
	 */
	public KspDeclaration findKspDeclaration(String javaName) {
		return store.findFirstItem(kspDeclaration -> javaName.equals(kspDeclaration.getJavaName()));
	}

	/**
	 * @return Les déclarations de domaines.
	 */
	public List<KspDeclaration> findDomains() {
		return store.findAllItems(kspDeclaration -> "Domain".equals(kspDeclaration.getNature()));
	}

	/**
	 * Trouve la déclaration contenant la région de fichier.
	 * 
	 * @param fileRegion Région de fichier.
	 * @return La déclaration, <code>null</code> sinon.
	 */
	public KspDeclaration findDeclarationAt(FileRegion fileRegion) {
		return store.findItem(fileRegion);
	}

	/**
	 * Parse un fichier candidat de KSP.
	 * 
	 * @param document Document du KSP.
	 * @param file Fichier du KSP.
	 * @return Le KSP, <code>null</code> sinon.
	 */
	public KspFile createKspFile(IDocument document, IFile file) {
		return new KspParser(document, file).parse();
	}

	/**
	 * Parseur de fichier KSP.
	 */
	private class KspParser {

		private final IDocument document;
		private final IFile file;

		/* Etat du parseur. */
		private final List<KspDeclaration> kspDeclarations = new ArrayList<>();
		private String packageName;
		private KspDeclaration kspDeclaration;
		private IRegion lineInformation;
		private String lineContent;

		/**
		 * Créé une nouvelle instance de KspParser.
		 * 
		 * @param document Document.
		 * @param file Fichier.
		 */
		public KspParser(IDocument document, IFile file) {
			this.document = document;
			this.file = file;
		}

		/**
		 * Parse le document.
		 * 
		 * @return Fichier KSP.
		 */
		public KspFile parse() {

			/* Parcourt les lignes du document. */
			for (int lineIdx = 0; lineIdx < document.getNumberOfLines(); lineIdx++) {
				try {
					/* Lit la ligne courante. */
					readLine(lineIdx);

					/* Extrait le package. */
					parsePackage();

					/* Extrait une déclaration KSP */
					parseKspDeclaration();

					/* Extrait un attribut de déclaration KSP. */
					parseKspDeclarationAttribute();
				} catch (BadLocationException e) {
					ErrorUtils.handle(e);
				}
			}

			/* Créé le fichier KSP. */
			return new KspFile(file, packageName, kspDeclarations);
		}

		private void readLine(int lineIdx) throws BadLocationException {
			/* Charge la région de la ligne. */
			lineInformation = document.getLineInformation(lineIdx);
			lineContent = document.get(lineInformation.getOffset(), lineInformation.getLength());
		}

		private void parsePackage() {
			/* Extrait le package si pas encore extrait */
			if (packageName == null) {
				packageName = KspStringUtils.getPackageName(lineContent);
			}
		}

		private void parseKspDeclaration() {
			LegacyStrategy strategy = LegacyManager.getInstance().getStrategy(file);

			/* Extrait une déclaration KSP */
			KspDeclarationParts parts = strategy.getKspDeclarationParts(lineContent);
			if (parts == null) {
				return;
			}

			/* Calcule la région du nom de la déclaration KSP */
			int fullNameLineOffSet = lineContent.indexOf(parts.getConstantCaseName());
			int taskNameOffSet = lineInformation.getOffset() + fullNameLineOffSet;

			/* Vérifie qu'on est dans une région standard */
			/* Permet d'ignorer le contenu des string et des commentaires KSP. */
			if (!DocumentUtils.isContentType(document, taskNameOffSet, KspRegionType.DEFAULT)) {
				return;
			}

			/* Créé la déclaration. */
			FileRegion taskRegion = new FileRegion(file, taskNameOffSet, parts.getConstantCaseName().length());
			String javaName = strategy.getKspDeclarationJavaName(parts.getConstantCaseNameOnly(), parts.getNature());
			kspDeclaration = new KspDeclaration(taskRegion, packageName, parts.getVerb(), parts.getNature(), parts.getConstantCaseName(), parts.getPrefix(),
					javaName);

			kspDeclarations.add(kspDeclaration);
		}

		private void parseKspDeclarationAttribute() {
			/* Vérification qu'une déclaration courante existe. */
			if (kspDeclaration == null) {
				return;
			}

			/* Extrait un attribut de la déclaration courante. */
			KspAttribute attribute = LegacyManager.getInstance().getStrategy(file).getKspAttribute(lineContent);
			if (attribute == null) {
				return;
			}

			/* Ajoute l'attribut à la déclaration courante. */
			kspDeclaration.addAttribute(attribute);
		}
	}

	private class Implementor implements ResourceStoreImplementor<KspDeclaration> {

		@Override
		public List<KspDeclaration> getItems(FileProvider fileProvider) {
			IFile file = fileProvider.getFile();
			IDocument document = fileProvider.getDocument();

			/* Parse le document KSP. */
			KspFile kspFile = createKspFile(document, file);

			/* Récupère toutes les déclarations KSP. */
			return kspFile.getKspDeclarations();
		}

		@Override
		public boolean isCandidate(IFile file) {
			/* Vérifie que le fichier a l'extension KSP. */
			return ResourceUtils.isKspFile(file);
		}
	}

}
