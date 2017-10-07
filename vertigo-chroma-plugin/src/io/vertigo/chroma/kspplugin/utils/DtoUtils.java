package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.model.DtoField;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 * Méthodes utilitaires pour parser les DTO.
 */
public final class DtoUtils {

	private static final String DTO_DOMAIN_PREFIX = "DO_DT";
	private static final String FIELD_ANNOTATION_NAME = "Field";
	private static final String COLUMN_ANNOTATION_NAME = "Column";
	private static final String DOMAIN_FIELD_NAME = "domain";
	private static final String LABEL_FIELD_NAME = "label";
	private static final String NAME_FIELD_NAME = "name";
	private static final String PERSISTENT_FIELD_NAME = "persistent";

	private DtoUtils() {
		// RAS
	}

	public static List<DtoField> parseVertigoDtoFields(IType type) {
		List<DtoField> fields = new ArrayList<>();
		try {
			for (IMethod method : type.getMethods()) {
				parseVertigoDtoField(method, fields);
			}
		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return fields;
	}

	private static void parseVertigoDtoField(IMethod method, List<DtoField> fields) {
		try {
			if (method.isConstructor() || !Flags.isPublic(method.getFlags())) {
				return;
			}
			IAnnotation fieldAnnotation = JdtUtils.getAnnotation(method, FIELD_ANNOTATION_NAME);
			if (fieldAnnotation == null) {
				return;
			}
			String domain = (String) JdtUtils.getMemberValue(fieldAnnotation, DOMAIN_FIELD_NAME);

			/* Cas d'un champ de composition DTO/DTC : on filtre. */
			if (domain == null || domain.startsWith(DTO_DOMAIN_PREFIX)) {
				return;
			}

			String constantCaseName = StringUtils.toConstantCase(KspStringUtils.getFieldNameFromGetter(method.getElementName()));
			String label = (String) JdtUtils.getMemberValue(fieldAnnotation, LABEL_FIELD_NAME);
			Boolean persistent = (Boolean) JdtUtils.getMemberValue(fieldAnnotation, PERSISTENT_FIELD_NAME);

			DtoField field = new DtoField(constantCaseName, label, domain, persistent);
			fields.add(field);
		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
	}

	public static List<DtoField> parseKasper5DtoFields(IType type) {
		List<DtoField> fields = new ArrayList<>();
		try {
			/* Extraire le type parent */
			ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
			IType superclass = hierarchy.getSuperclass(type);
			for (IMethod method : superclass.getMethods()) {
				parseKasper5DtoField(method, fields);
			}
		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return fields;
	}

	private static void parseKasper5DtoField(IMethod method, List<DtoField> fields) {
		try {
			if (method.isConstructor() || !Flags.isPublic(method.getFlags())) {
				return;
			}
			IAnnotation fieldAnnotation = JdtUtils.getAnnotation(method, FIELD_ANNOTATION_NAME);
			if (fieldAnnotation == null) {
				return;
			}
			String domain = (String) JdtUtils.getMemberValue(fieldAnnotation, DOMAIN_FIELD_NAME);

			/* Cas d'un champ de composition DTO/DTC : on filtre. */
			if (domain == null || domain.startsWith(DTO_DOMAIN_PREFIX)) {
				return;
			}

			IAnnotation columnAnnotation = JdtUtils.getAnnotation(method, COLUMN_ANNOTATION_NAME);
			if (columnAnnotation == null) {
				return;
			}

			String constantCaseName = (String) JdtUtils.getMemberValue(columnAnnotation, NAME_FIELD_NAME);
			String label = (String) JdtUtils.getMemberValue(fieldAnnotation, LABEL_FIELD_NAME);
			Boolean persistent = (Boolean) JdtUtils.getMemberValue(fieldAnnotation, PERSISTENT_FIELD_NAME);

			DtoField field = new DtoField(constantCaseName, label, domain, persistent);
			fields.add(field);
		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
	}

	public static List<DtoField> parseKasper3DtoFields(IType type) {
		List<DtoField> fields = new ArrayList<>();
		try {
			if (type.getElementName().endsWith("Dt")) {
				/* Cas d'un bean métier. */
				parseKasper3BeanFields(type, fields);
			} else {
				/* Cas d'un objet persisté. */
				parseKasper3PersistedDtoFields(type, fields);
			}
		} catch (JavaModelException e) {
			ErrorUtils.handle(e);
		}
		return fields;
	}

	private static void parseKasper3PersistedDtoFields(IType type, List<DtoField> fields) throws JavaModelException {
		/* Extraire le type parent Abstract. */
		ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
		IType superclass = hierarchy.getSuperclass(type);

		/* Parse l'AST du type Abstract. */
		CompilationUnit ast = JdtUtils.parseAST(superclass.getCompilationUnit());

		/* Visite l'AST. */
		ast.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodDeclaration node) {
				return "initDefinition".equals(node.getName().getIdentifier());
			}

			@Override
			public boolean visit(MethodInvocation node) {
				if ("createField".equals(node.getName().getIdentifier())) {
					List<?> arguments = node.arguments();
					String columnName = JdtUtils.getSimpleNameIdentifier(arguments.get(1));
					String label = JdtUtils.getStringLiteralValue(arguments.get(2));
					String domainName = JdtUtils.getSimpleNameIdentifier(arguments.get(8));
					fields.add(new DtoField(columnName, label, domainName, true));
				}
				return false;
			}

		});
	}

	private static void parseKasper3BeanFields(IType type, List<DtoField> fields) throws JavaModelException {
		/* Extraire le type parent Abstract. */
		ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
		IType superclass = hierarchy.getSuperclass(type);

		for (IMethod method : superclass.getMethods()) {
			DtoField field = parseKasper3BeanFieldGetter(method);
			if (field != null) {
				fields.add(field);
			}
		}
	}

	private static DtoField parseKasper3BeanFieldGetter(IMethod method) throws JavaModelException {
		if (method.isConstructor() || !Flags.isPublic(method.getFlags()) || !Flags.isFinal(method.getFlags())) {
			return null;
		}
		String methodName = method.getElementName();
		if (!methodName.startsWith("get")) {
			return null;
		}

		String constantCaseName = StringUtils.toConstantCase(KspStringUtils.getFieldNameFromGetter(method.getElementName()));
		String label = "Unknown";
		Boolean persistent = false;

		return new DtoField(constantCaseName, label, "Unknown", persistent);
	}
}
