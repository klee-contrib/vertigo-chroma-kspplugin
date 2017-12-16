package io.vertigo.chroma.kspplugin.utils;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.internal.core.JavaProject;

/**
 * Méthodes utilitaires pour manipuler l'AST Java avec le JDT.
 */
@SuppressWarnings("restriction")
public final class JdtUtils {

	private JdtUtils() {
		// RAS.
	}

	/**
	 * Obtient l'unité de compilation d'un fichier dans un projet donné.
	 * 
	 * @param file Ressource fichier.
	 * @param javaProject Projet Java.
	 * @return Unité de compilation du fichier.
	 */
	public static ICompilationUnit getCompilationUnit(IFile file, IJavaProject javaProject) {
		try {
			ICompilationUnit unit = (ICompilationUnit) JavaCore.create(file, javaProject);
			if (isUnitInProjectBuildPath(unit)) {
				return unit;
			}
		} catch (Exception e) {
			ErrorUtils.handle(e);
		}
		return null;
	}

	/**
	 * Obtient une annotation d'un nom donné sur un objet donné.
	 * 
	 * @param annotable Objet à inspecter.
	 * @param name Nom de l'annotation.
	 * @return Annotation, <code>null</code> sinon.
	 */
	public static IAnnotation getAnnotation(IAnnotatable annotable, String name) {
		try {
			for (IAnnotation annotation : annotable.getAnnotations()) {
				String annotationName = StringUtils.getLastNameFragment(annotation.getElementName());
				if (name.equals(annotationName)) {
					return annotation;
				}
			}
		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return null;
	}

	/**
	 * Retourne la valeur du membre d'une annotation donnée.
	 * 
	 * @param annotation Annotation.
	 * @param memberName Nom du membre.
	 * @return Valeur du membre.
	 */
	public static Object getMemberValue(IAnnotation annotation, String memberName) {
		try {
			for (IMemberValuePair pair : annotation.getMemberValuePairs()) {
				if (memberName.equals(pair.getMemberName())) {
					return pair.getValue();
				}
			}
		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return null;
	}

	/**
	 * Retourne la valeur du membre par défaut d'une annotation donnée.
	 * 
	 * @param annotation Annotation.
	 * @return Valeur du membre.
	 */
	public static String getMemberValue(IAnnotation annotation) {
		try {
			IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
			if (memberValuePairs.length == 0) {
				return null;
			}
			return (String) memberValuePairs[0].getValue();
		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return null;
	}

	/**
	 * Indique si le type donné est un DtObject Vertigo.
	 * 
	 * @param type Type JDT.
	 * @return <code>true</code> si le type est un DtObject.
	 */
	public static boolean isVertigoDtoType(IType type) {
		try {
			/* Vérifie que c'est une classe publique final. */
			if (!type.isClass() || !Flags.isPublic(type.getFlags()) || !Flags.isFinal(type.getFlags())) {
				return false;
			}
			/* Vérifie les interfaces. */
			return hasVertigoDtoTypeInterface(type);
		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return false;
	}

	private static boolean hasVertigoDtoTypeInterface(IType type) throws JavaModelException {
		for (String interfaceName : type.getSuperInterfaceNames()) {
			switch (interfaceName) {
			case "DtObject":
			case "Entity":
			case "KeyConcept":
				return true;
			default:
				return false;
			}
		}
		return false;
	}

	/**
	 * Indique si le type donné est un DtObject Kasper 3.
	 * 
	 * @param type Type JDT.
	 * @return <code>true</code> si le type est un DtObject.
	 */
	public static boolean isKasper3DtoType(IType type) {
		try {
			/* Vérifie que c'est une classe publique. */
			if (!type.isClass() || !Flags.isPublic(type.getFlags())) {
				return false;
			}
			/* Vérifie que la classe hérite de SuperDtObject */
			if (type.getSuperclassName() == null) {
				return false;
			}
			return "SuperDtObject".equals(type.getSuperclassName()) || "kasper.model.SuperDtObject".equals(type.getSuperclassName());

		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return false;
	}

	/**
	 * Indique si le type donné est un DtObject Kasper 4 ou 5.
	 * 
	 * @param type Type JDT.
	 * @return <code>true</code> si le type est un DtObject.
	 */
	public static boolean isKasper345DtoType(IType type) {
		try {
			/* Vérifie que c'est une classe publique. */
			if (!type.isClass() || !Flags.isPublic(type.getFlags())) {
				return false;
			}
			/* Vérifie que la classe hérite d'un abstract de même nom préfixé ou suffixé par Abstract */
			String superclassName = type.getSuperclassName();
			if (superclassName == null) {
				return false;
			}
			String prefixedName = "Abstract" + type.getElementName();
			String suffixedName = type.getElementName() + "Abstract";

			return superclassName.equals(prefixedName) || superclassName.equals(suffixedName);

		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return false;
	}

	/**
	 * Indique si le type donné est une sous-classe direct d'un type parmi une liste.
	 * 
	 * @param type Type JDT.
	 * @param parentClasses Liste des classes parentes candidates.
	 * @return <code>true</code> si le type est une sous-classe.
	 */
	public static boolean isSubclass(IType type, List<String> parentClasses) {
		if (parentClasses == null || parentClasses.isEmpty()) {
			return false;
		}
		try {
			/* Vérifie que c'est une classe publique. */
			if (!type.isClass() || !Flags.isPublic(type.getFlags())) {
				return false;
			}
			/* Vérifie que la classe hérite d'une classe (autre que Object) */
			String superclassName = type.getSuperclassName();
			if (superclassName == null) {
				return false;
			}

			/* Vérifie que la classe parente est parmi les candidates. */
			return parentClasses.contains(superclassName);

		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return false;
	}

	/**
	 * Obtient le type JDT pour un nom complet qualifié dans un projet donné.
	 * 
	 * @param fullyQualifiedName Nom complet qualifié.
	 * @param project Projet.
	 * @return Le type JDT, <code>null</code> sinon.
	 */
	public static IType getJavaType(String fullyQualifiedName, IProject project) {
		IJavaProject javaProject = JavaCore.create(project);
		try {
			return javaProject.findType(fullyQualifiedName);
		} catch (Exception e) {
			ErrorUtils.handle(e);
		}
		return null;
	}

	public static CompilationUnit parseAST(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */);
	}

	public static String getDomString(Object arg) {
		if (arg instanceof SimpleName) {
			return ((SimpleName) arg).getIdentifier();
		}
		if (arg instanceof StringLiteral) {
			return ((StringLiteral) arg).getLiteralValue();
		}
		return null;
	}

	public static boolean isJavaProject(IProject project) {
		return JavaProject.hasJavaNature(project);
	}

	/**
	 * Indique si une unité de compilation Java se trouve dans le build path de son projet.
	 * 
	 * @param unit Unité de compilation.
	 * @return <ocde>true</code> si dans le build path de son projet.
	 */
	private static boolean isUnitInProjectBuildPath(ICompilationUnit unit) {
		try {
			/* Si le fichier Java n'est pas dans le buildpath de son projet, lancera une JavaModelException */
			unit.getAllTypes();
			/* Pas d'exception : le fichier est dans le build path. */
			return true;
		} catch (JavaModelException jme) { // NOSONAR
			/* Exception : le fichier n'est pas dans le build path. */
			return false;
		}
	}
}
