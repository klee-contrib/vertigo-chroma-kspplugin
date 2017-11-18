package io.vertigo.chroma.kspplugin.resources;

import io.vertigo.chroma.kspplugin.model.FileRegion;
import io.vertigo.chroma.kspplugin.model.JavaClassFile;
import io.vertigo.chroma.kspplugin.model.Manager;
import io.vertigo.chroma.kspplugin.resources.core.FileProvider;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStore;
import io.vertigo.chroma.kspplugin.resources.core.ResourceStoreImplementor;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;
import io.vertigo.chroma.kspplugin.utils.JdtUtils;
import io.vertigo.chroma.kspplugin.utils.ResourceUtils;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Manager des fichiers de classe Java.
 * <p>
 * Nécessaire pour la recherche de classes Java référencées par leur nom simple dans les KSP.
 * <ul>
 * <li>index les fichiers de classes Java.</li>
 * <li>maintient un cache à jour</li>
 * <li>publie une API de recherche</li>
 * </ul>
 * <p>
 */
public final class JavaClassManager implements Manager {

	private static JavaClassManager instance;
	private final ResourceStore<JavaClassFile> store;

	/**
	 * Créé une nouvelle instance de JavaClassManager.
	 */
	private JavaClassManager() {
		store = new ResourceStore<>(new Implementor());
	}

	/**
	 * @return Instance du singleton.
	 */
	public static synchronized JavaClassManager getInstance() {
		if (instance == null) {
			instance = new JavaClassManager();
		}
		return instance;
	}

	@Override
	public void init() {
		/* Démarre le magasin qui index tous les fichiers concernés. */
		instance.store.start();
	}

	/**
	 * Cherche un fichier Java à partir de son nom simple.
	 * 
	 * @param javaName Nom simple Java de la classe.
	 * @return Le premier JavaClassFile qui correspond.
	 */
	public JavaClassFile findJavaClassFile(String javaName) {
		return store.findFirstItem(javaClassFile -> javaName.equals(javaClassFile.getJavaName()));
	}

	private class Implementor implements ResourceStoreImplementor<JavaClassFile> {

		@Override
		public List<JavaClassFile> getItems(FileProvider fileProvider) {
			IFile file = fileProvider.getFile();
			IJavaProject javaProject = fileProvider.getJavaProject();

			/* Parse le fichier Java. */
			JavaClassFile javaClassFile = createJavaClassFile(file, javaProject);
			if (javaClassFile == null) {
				return null; // NOSONAR
			}

			/* Fichier trouvé. */
			return Arrays.asList(javaClassFile);
		}

		@Override
		public boolean isCandidate(IFile file) {
			return ResourceUtils.isJavaFile(file);
		}

		/**
		 * Parse un fichier de classe Java.
		 * 
		 * @param file Fichier.
		 * @param javaProject Projet Java du fichier.
		 * @return Le fichier de classe Java, <code>null</code> sinon.
		 */
		private JavaClassFile createJavaClassFile(IFile file, IJavaProject javaProject) {

			/* Charge l'AST du fichier Java. */
			ICompilationUnit compilationUnit = JdtUtils.getCompilationUnit(file, javaProject);
			if (compilationUnit == null) {
				return null;
			}
			try {
				/* Parcourt les types du fichier Java. */
				for (IType type : compilationUnit.getAllTypes()) {

					if (!type.isClass()) {
						continue;
					}

					/* Créé le JavaClassFile. */
					String javaName = type.getElementName();
					String packageName = type.getPackageFragment().getElementName();
					ISourceRange nameRange = type.getNameRange();
					FileRegion fileRegion = new FileRegion(file, nameRange.getOffset(), nameRange.getLength());
					return new JavaClassFile(fileRegion, javaName, packageName);
				}
			} catch (JavaModelException e) {
				ErrorUtils.handle(e);
			}

			return null;
		}
	}
}
