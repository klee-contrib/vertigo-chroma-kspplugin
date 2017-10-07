package io.vertigo.chroma.kspplugin.resources;

import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.Manager;
import io.vertigo.chroma.kspplugin.model.WsFile;
import io.vertigo.chroma.kspplugin.model.WsRoute;
import io.vertigo.chroma.kspplugin.model.WsRouteWorkspace;
import io.vertigo.chroma.kspplugin.resources.core.FileProvider;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStore;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStoreImplementor;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;
import io.vertigo.chroma.kspplugin.utils.JdtUtils;
import io.vertigo.chroma.kspplugin.utils.KspStringUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Manager des fichiers des routes de webservice.
 * <p>
 * <ul>
 * <li>index les fichiers de webservice et leurs routes.</li>
 * <li>maintient un cache à jour</li>
 * <li>publie une API de recherche</li>
 * </ul>
 * <p>
 */
public final class WsRouteManager implements Manager {

	private static final String[] HTTP_VERBS = { "GET", "POST", "DELETE", "PUT", "PATCH" };
	private static WsRouteManager instance;
	private final ResourceStore<WsRoute> store;

	/**
	 * Créé une nouvelle instance de WsRouteManager.
	 */
	private WsRouteManager() {
		store = new ResourceStore<>(new Implementor());
	}

	/**
	 * @return Instance du singleton.
	 */
	public static synchronized WsRouteManager getInstance() {
		if (instance == null) {
			instance = new WsRouteManager();
		}
		return instance;
	}

	@Override
	public void init() {
		/* Démarre le magasin qui index tous les fichiers concernés. */
		instance.store.start();
	}

	/**
	 * @return Workspace des routes.
	 */
	public WsRouteWorkspace getWorkspace() {
		return new WsRouteWorkspace(store.getAllItems());
	}

	private class Implementor implements ResourceStoreImplementor<WsRoute> {

		@Override
		public List<WsRoute> getItems(FileProvider fileProvider) {
			IFile file = fileProvider.getFile();
			IJavaProject javaProject = fileProvider.getJavaProject();

			/* Parse le fichier de webservice et ses routes. */
			WsFile wsFile = createWsFile(file, javaProject);
			if (wsFile == null) {
				return null; // NOSONAR
			}

			/* Récupère toutes les implémentations. */
			return wsFile.getWsRoutes();
		}

		@Override
		public boolean isCandidate(IFile file) {
			/* Filtre avec une convention de nommage. */
			return KspStringUtils.getWsFileName(file.getName()) != null;
		}

		/**
		 * Parse un fichier candidat de webservice.
		 * 
		 * @param file Fichier du webservice.
		 * @param javaProject Projet Java du fichier.
		 * @return Le webservice, <code>null</code> sinon.
		 */
		private WsFile createWsFile(IFile file, IJavaProject javaProject) {
			/* Charge l'AST du fichier Java. */
			ICompilationUnit compilationUnit = JdtUtils.getCompilationUnit(file, javaProject);
			if (compilationUnit == null) {
				return null;
			}
			List<WsRoute> wsRoutes = new ArrayList<>();
			try {
				/* Parcourt les types du fichier Java. */
				for (IType type : compilationUnit.getAllTypes()) {
					handleType(type, file, wsRoutes);
				}
			} catch (JavaModelException e) {
				ErrorUtils.handle(e);
			}

			/* Créé le fichier de webservice. */
			return new WsFile(file, wsRoutes);
		}

		private void handleType(IType type, IFile file, List<WsRoute> wsRoutes) {
			try {
				String pathPrefix = getPathPrefix(type);

				/* Parcourt les méthodes du contrôleur de web services. */
				for (IMethod method : type.getMethods()) {
					handleMethod(method, pathPrefix, file, wsRoutes);
				}
			} catch (JavaModelException e) {
				ErrorUtils.handle(e);
			}
		}

		private void handleMethod(IMethod method, String pathPrefix, IFile file, List<WsRoute> wsRoutes) throws JavaModelException {
			/* Filtre pour ne garder que les méthodes publiques d'instance */
			if (method.isConstructor() || Flags.isStatic(method.getFlags()) || Flags.isPrivate(method.getFlags())) {
				return;
			}

			/* Parcourt les verbes HTTP */
			for (String verb : HTTP_VERBS) {

				/* Extrait l'annotation du verbe. */
				IAnnotation verbAnnotation = JdtUtils.getAnnotation(method, verb);
				if (verbAnnotation == null) {
					continue;
				}

				/* Extrait la route partielle. */
				String routePatternSuffix = JdtUtils.getMemberValue(verbAnnotation);

				/* Calcule la route complète. */
				String routePattern = pathPrefix + routePatternSuffix;

				/* Créé la WsRoute. */
				String javaName = method.getElementName();
				ISourceRange nameRange = method.getNameRange();
				FileRegion fileRegion = new FileRegion(file, nameRange.getOffset(), nameRange.getLength());
				WsRoute wsRoute = new WsRoute(fileRegion, javaName, routePattern, verb);
				wsRoutes.add(wsRoute);
			}
		}

		private String getPathPrefix(IType type) {
			/* Extrait un éventuel PathPrefix de la classe. */
			IAnnotation pathPrefixAnnotation = JdtUtils.getAnnotation(type, "PathPrefix");
			if (pathPrefixAnnotation == null) {
				return "";
			}
			return JdtUtils.getMemberValue(pathPrefixAnnotation);
		}
	}

}
