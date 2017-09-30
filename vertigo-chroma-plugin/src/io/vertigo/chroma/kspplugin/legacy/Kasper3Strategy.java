package io.vertigo.chroma.kspplugin.legacy;

import io.vertigo.chroma.kspplugin.model.DtoField;
import io.vertigo.chroma.kspplugin.model.DtoReferencePattern;
import io.vertigo.chroma.kspplugin.model.KspAttribute;
import io.vertigo.chroma.kspplugin.model.KspDeclarationMainParts;
import io.vertigo.chroma.kspplugin.model.KspDeclarationNameParts;
import io.vertigo.chroma.kspplugin.model.KspDeclarationParts;
import io.vertigo.chroma.kspplugin.model.KspNature;
import io.vertigo.chroma.kspplugin.utils.DtoUtils;
import io.vertigo.chroma.kspplugin.utils.JdtUtils;
import io.vertigo.chroma.kspplugin.utils.KspStringUtils;
import io.vertigo.chroma.kspplugin.utils.ResourceUtils;
import io.vertigo.chroma.kspplugin.utils.StringUtils;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;

/**
 * Stratégie pour les projets en Kasper 3.
 */
public final class Kasper3Strategy implements LegacyStrategy {

	@Override
	public KspDeclarationParts getKspDeclarationParts(String lineContent) {
		KspDeclarationMainParts mainParts = KspStringUtils.getKasper3KspDeclarationParts(lineContent);
		if (mainParts == null) {
			return null;
		}

		/* Extrait le préfixe et le nom simple de la déclaration */
		KspDeclarationNameParts nameParts = KspStringUtils.getKspDeclarationNameParts(mainParts.getConstantCaseName());
		if (nameParts == null) {
			return null;
		}
		return new KspDeclarationParts(mainParts, nameParts);
	}

	@Override
	public String getKspDeclarationJavaName(String constantCaseNameOnly, String nature) {
		switch (nature) {
		case "Service":
			return StringUtils.toCamelCase(constantCaseNameOnly);
		case "DT":
			return StringUtils.toPascalCase(constantCaseNameOnly) + "Dt";
		default:
			return StringUtils.toPascalCase(constantCaseNameOnly);
		}
	}

	@Override
	public KspAttribute getKspAttribute(String lineContent) {
		return KspStringUtils.getKasper3KspAttribute(lineContent);
	}

	@Override
	public boolean isDtoType(IType type) {
		return JdtUtils.isKasper345DtoType(type) || JdtUtils.isKasper3DtoType(type);
	}

	@Override
	public List<DtoField> parseDtoFields(IType type) {
		return DtoUtils.parseKasper3DtoFields(type);
	}

	@Override
	public boolean isDtoCandidate(IFile file) {
		/* Pas de convention de nommage sur les DTO. */
		/* On vérifie que c'est un fichier Java. */
		return ResourceUtils.isJavaFile(file);
	}

	@Override
	public boolean isServiceCandidate(IFile file) {
		/* Vérifie la convention de nommage. */
		return KspStringUtils.getKasper3ServiceFileName(file.getName()) != null || KspStringUtils.getKasper4ServiceFileName(file.getName()) != null;
	}

	@Override
	public boolean isDaoCandidate(IFile file) {
		/* Filtre avec la convention de nommage sur le nom du fichier. */
		return KspStringUtils.getKasper3DaoFileName(file.getName()) != null || KspStringUtils.getKasper4DaoFileName(file.getName()) != null;
	}

	@Override
	public String getKspKeyword(KspNature kspNature) {
		return kspNature.getKspKeyWordKasper3();
	}

	@Override
	public DtoReferencePattern getDtoReferenceSyntaxe() {
		return DtoReferencePattern.SIMPLE_NAME;
	}
}
