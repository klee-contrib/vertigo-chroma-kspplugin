package io.vertigo.chroma.kspplugin.resources;

import io.vertigo.chroma.kspplugin.legacy.LegacyManager;
import io.vertigo.chroma.kspplugin.model.DaoFile;
import io.vertigo.chroma.kspplugin.model.DaoImplementation;
import io.vertigo.chroma.kspplugin.model.DaoWorkspace;
import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.Manager;
import io.vertigo.chroma.kspplugin.resources.core.FileProvider;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStore;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStoreImplementor;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;
import io.vertigo.chroma.kspplugin.utils.JdtUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Manager des fichiers DAO / PAO.
 * <p>
 * <ul>
 * <li>index les fichiers DAO / PAO et leurs méthodes de Task SQL.</li>
 * <li>maintient un cache à jour</li>
 * <li>publie une API de recherche</li>
 * </ul>
 * <p>
 */
public final class DaoManager implements Manager {

	private static DaoManager instance;
	private final ResourceStore<DaoImplementation> store;

	/**
	 * Créé une nouvelle instance de DaoManager.
	 */
	private DaoManager() {
		store = new ResourceStore<>(new Implementor());
	}

	/**
	 * @return Instance du singleton.
	 */
	public static synchronized DaoManager getInstance() {
		if (instance == null) {
			instance = new DaoManager();
		}
		return instance;
	}

	@Override
	public void init() {
		/* Démarre le magasin qui index tous les fichiers concernés. */
		instance.store.start();
	}

	/**
	 * @return Workspace des DAO/PAO.
	 */
	public DaoWorkspace getWorkspace() {
		return new DaoWorkspace(store.getAllItems());
	}

	/**
	 * Cherche une méthode de Task à partir de son nom Java.
	 * 
	 * @param javaName Nom java de la méthode de Task.
	 * @return Le premier DaoImplementation qui correspond.
	 */
	public DaoImplementation findDaoImplementation(String javaName) {
		return store.findFirstItem(daoImplementation -> javaName.equals(daoImplementation.getJavaName()));
	}

	private class Implementor implements ResourceStoreImplementor<DaoImplementation> {

		@Override
		public List<DaoImplementation> getItems(FileProvider fileProvider) {
			IFile file = fileProvider.getFile();
			IJavaProject javaProject = fileProvider.getJavaProject();

			/* Parse le fichier DAO et ses implémentations. */
			DaoFile daoFile = createDaoFile(file, javaProject);
			if (daoFile == null) {
				return null; // NOSONAR
			}

			/* Récupère toutes les implémentations. */
			return daoFile.getDaoImplementations();
		}

		@Override
		public boolean isCandidate(IFile file) {
			return LegacyManager.getInstance().getStrategy(file).isDaoCandidate(file);
		}

		/**
		 * Parse un fichier candidat de DAO/PAO.
		 * 
		 * @param file Fichier.
		 * @param javaProject Projet Java du fichier.
		 * @return Le DAO, <code>null</code> sinon.
		 */
		private DaoFile createDaoFile(IFile file, IJavaProject javaProject) {
			/* Charge l'AST du fichier Java. */
			ICompilationUnit compilationUnit = JdtUtils.getCompilationUnit(file, javaProject);
			if (compilationUnit == null) {
				return null;
			}
			List<DaoImplementation> daoImplementations = new ArrayList<>();
			try {
				/* Parcourt les types du fichier Java. */
				for (IType type : compilationUnit.getAllTypes()) {
					handleType(type, file, daoImplementations);
				}
			} catch (JavaModelException e) {
				ErrorUtils.handle(e);
			}

			/* Créé le fichier de DAO/PAO. */
			return new DaoFile(file, daoImplementations);
		}

		private void handleType(IType type, IFile file, List<DaoImplementation> daoImplementations) throws JavaModelException {
			/* Parcourt les méthodes. */
			for (IMethod method : type.getMethods()) {
				/* Filtre pour ne garder que les méthodes publiques d'instance */
				if (method.isConstructor() || Flags.isStatic(method.getFlags()) || Flags.isPrivate(method.getFlags())) {
					continue;
				}

				/* Créé le DaoImplementation. */
				String javaName = method.getElementName();
				ISourceRange nameRange = method.getNameRange();
				FileRegion fileRegion = new FileRegion(file, nameRange.getOffset(), nameRange.getLength());
				DaoImplementation daoImplementation = new DaoImplementation(fileRegion, javaName);
				daoImplementations.add(daoImplementation);
			}
		}
	}
}
