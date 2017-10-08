package io.vertigo.chroma.kspplugin.resources;

import io.vertigo.chroma.kspplugin.legacy.LegacyManager;
import io.vertigo.chroma.kspplugin.legacy.LegacyStrategy;
import io.vertigo.chroma.kspplugin.model.DtoDefinitionPath;
import io.vertigo.chroma.kspplugin.model.DtoField;
import io.vertigo.chroma.kspplugin.model.DtoFile;
import io.vertigo.chroma.kspplugin.model.DtoWorkspace;
import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.Manager;
import io.vertigo.chroma.kspplugin.resources.core.FileProvider;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStore;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStoreImplementor;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;
import io.vertigo.chroma.kspplugin.utils.JdtUtils;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Manager des fichiers DTO.
 * <p>
 * <ul>
 * <li>index les fichiers DTO.</li>
 * <li>maintient un cache à jour</li>
 * <li>publie une API de recherche</li>
 * </ul>
 * <p>
 */
public final class DtoManager implements Manager {

	private static DtoManager instance;
	private final ResourceStore<DtoFile> store;

	/**
	 * Créé une nouvelle instance de DtoManager.
	 */
	private DtoManager() {
		store = new ResourceStore<>(new Implementor());
	}

	/**
	 * @return Instance du singleton.
	 */
	public static synchronized DtoManager getInstance() {
		if (instance == null) {
			instance = new DtoManager();
		}
		return instance;
	}

	@Override
	public void init() {
		/* Démarre le magasin qui index tous les fichiers concernés. */
		instance.store.start();
	}

	/**
	 * @return Workspace des DTO.
	 */
	public DtoWorkspace getWorkspace() {
		return new DtoWorkspace(store.getAllItems());
	}

	/**
	 * Recherche un DTO par son nom java.
	 * 
	 * @param javaName Nom java.
	 * @return DTO.
	 */
	public DtoFile findDtoFile(String javaName) {
		return store.findFirstItem(dtoFile -> javaName.equals(dtoFile.getJavaName()));
	}

	/**
	 * Recherche un DTO par son chemin de définition.
	 * 
	 * @param path Chemin de définition.
	 * @return DTO.
	 */
	public DtoFile findDtoFile(DtoDefinitionPath path) {
		return store.findFirstItem(dtoFile -> path.getDtoName().equals(dtoFile.getJavaName()) && path.getPackageName().equals(dtoFile.getLastPackagePart()));
	}

	private class Implementor implements ResourceStoreImplementor<DtoFile> {

		@Override
		public List<DtoFile> getItems(FileProvider fileProvider) {
			IFile file = fileProvider.getFile();
			IJavaProject javaProject = fileProvider.getJavaProject();

			/* Parse le fichier DTO. */
			DtoFile dtoFile = createDtoFile(file, javaProject);
			if (dtoFile == null) {
				return null; // NOSONAR
			}

			/* Fichier trouvé. */
			return Arrays.asList(dtoFile);
		}

		@Override
		public boolean isCandidate(IFile file) {
			return LegacyManager.getInstance().getStrategy(file).isDtoCandidate(file);
		}

		/**
		 * Parse un fichier candidat de DTO.
		 * 
		 * @param file Fichier.
		 * @param javaProject Projet Java du fichier.
		 * @return Le DTO, <code>null</code> sinon.
		 */
		private DtoFile createDtoFile(IFile file, IJavaProject javaProject) {

			/* Charge l'AST du fichier Java. */
			ICompilationUnit compilationUnit = JdtUtils.getCompilationUnit(file, javaProject);
			if (compilationUnit == null) {
				return null;
			}
			try {
				LegacyStrategy strategy = LegacyManager.getInstance().getStrategy(file);
				/* Parcourt les types du fichier Java. */
				for (IType type : compilationUnit.getAllTypes()) {

					/* Vérifie que c'est un Dto */
					boolean isDtoType = strategy.isDtoType(type);

					if (!isDtoType) {
						continue;
					}

					/* Parse les champs. */
					List<DtoField> fields = strategy.parseDtoFields(type);

					/* Créé le DtoFile. */
					String javaName = type.getElementName();
					String packageName = type.getPackageFragment().getElementName();
					ISourceRange nameRange = type.getNameRange();
					FileRegion fileRegion = new FileRegion(file, nameRange.getOffset(), nameRange.getLength());
					return new DtoFile(fileRegion, javaName, packageName, fields);
				}
			} catch (JavaModelException e) {
				ErrorUtils.handle(e);
			}

			return null;
		}
	}
}
