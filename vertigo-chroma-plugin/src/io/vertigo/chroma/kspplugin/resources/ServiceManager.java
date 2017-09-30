package io.vertigo.chroma.kspplugin.resources;

import io.vertigo.chroma.kspplugin.legacy.LegacyManager;
import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.Manager;
import io.vertigo.chroma.kspplugin.model.ServiceFile;
import io.vertigo.chroma.kspplugin.model.ServiceImplementation;
import io.vertigo.chroma.kspplugin.model.ServiceWorkspace;
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
 * Manager des fichiers des services métier.
 * <p>
 * <ul>
 * <li>index les fichiers d'implémentation de services métier et leurs méthodes.</li>
 * <li>maintient un cache à jour</li>
 * <li>publie une API de recherche</li>
 * </ul>
 * <p>
 */
public final class ServiceManager implements Manager {

	private static ServiceManager instance;
	private final ResourceStore<ServiceImplementation> store;

	/**
	 * Créé une nouvelle instance de ServiceManager.
	 */
	private ServiceManager() {
		store = new ResourceStore<>(new Implementor());
	}

	/**
	 * @return Instance du singleton.
	 */
	public static synchronized ServiceManager getInstance() {
		if (instance == null) {
			instance = new ServiceManager();
		}
		return instance;
	}

	@Override
	public void init() {
		/* Démarre le magasin qui index tous les fichiers concernés. */
		instance.store.start();
	}

	/**
	 * @return Workspace des services métier.
	 */
	public ServiceWorkspace getWorkspace() {
		return new ServiceWorkspace(store.getAllItems());
	}

	private class Implementor implements ResourceStoreImplementor<ServiceImplementation> {

		@Override
		public List<ServiceImplementation> getItems(FileProvider fileProvider) {
			IFile file = fileProvider.getFile();
			IJavaProject javaProject = fileProvider.getJavaProject();

			/* Parse le fichier d'implémentation de service. */
			ServiceFile serviceFile = createServiceFile(file, javaProject);
			if (serviceFile == null) {
				return null; // NOSONAR
			}

			/* Récupère toutes les implémentations. */
			return serviceFile.getServiceImplementations();
		}

		@Override
		public boolean isCandidate(IFile file) {
			return LegacyManager.getInstance().getStrategy(file).isServiceCandidate(file);
		}

		/**
		 * Parse un fichier candidat de service métier.
		 * 
		 * @param document Document du service métier.
		 * @param javaProject Projet Java du service.
		 * @return Le service, <code>null</code> sinon.
		 */
		private ServiceFile createServiceFile(IFile file, IJavaProject javaProject) {
			/* Charge l'AST du fichier Java. */
			ICompilationUnit compilationUnit = JdtUtils.getCompilationUnit(file, javaProject);
			if (compilationUnit == null) {
				return null;
			}
			List<ServiceImplementation> serviceImplementations = new ArrayList<>();
			try {
				/* Parcourt les types du fichier Java. */
				for (IType type : compilationUnit.getAllTypes()) {
					handleType(type, file, serviceImplementations);
				}
			} catch (JavaModelException e) {
				ErrorUtils.handle(e);
			}

			/* Créé le fichier de service. */
			return new ServiceFile(serviceImplementations);
		}

		private void handleType(IType type, IFile file, List<ServiceImplementation> serviceImplementations) throws JavaModelException {
			/* Parcourt les méthodes. */
			for (IMethod method : type.getMethods()) {
				/* Filtre pour ne garder que les méthodes publiques d'instance */
				if (method.isConstructor() || Flags.isStatic(method.getFlags()) || Flags.isPrivate(method.getFlags())) {
					continue;
				}

				/* Créé le ServiceImplementation. */
				String javaName = method.getElementName();
				ISourceRange nameRange = method.getNameRange();
				FileRegion fileRegion = new FileRegion(file, nameRange.getOffset(), nameRange.getLength());
				ServiceImplementation serviceImplementation = new ServiceImplementation(fileRegion, javaName);
				serviceImplementations.add(serviceImplementation);
			}
		}
	}
}
