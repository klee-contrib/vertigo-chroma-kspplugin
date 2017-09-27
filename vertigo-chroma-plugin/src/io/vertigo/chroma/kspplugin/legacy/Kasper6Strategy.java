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
 * Stratégie pour les projets en Kasper 6.
 */
public final class Kasper6Strategy implements LegacyStrategy {

	@Override
	public KspDeclarationParts getKspDeclarationParts(String lineContent) {
		KspDeclarationMainParts mainParts = KspStringUtils.getKasper6KspDeclarationParts(lineContent);
		if (mainParts == null) {
			return null;
		}

		/* Extrait le préfixe et le nom simple de la déclaration */
		KspDeclarationNameParts declarationNameParts = KspStringUtils.getKspDeclarationNameParts(mainParts.getConstantCaseName());
		if (declarationNameParts == null) {
			return null;
		}

		return new KspDeclarationParts(mainParts, declarationNameParts);
	}

	@Override
	public String getKspDeclarationJavaName(String constantCaseNameOnly, String nature) {
		switch (nature) {
		case "Task":
			return StringUtils.toCamelCase(constantCaseNameOnly);
		default:
			return StringUtils.toPascalCase(constantCaseNameOnly);
		}
	}

	@Override
	public KspAttribute getKspAttribute(String lineContent) {
		return KspStringUtils.getKasper6KspAttribute(lineContent);
	}

	@Override
	public boolean isDtoType(IType type) {
		return JdtUtils.isVertigoDtoType(type);
	}

	@Override
	public boolean isDtoCandidate(IFile file) {
		/* Pas de convention de nommage sur les DTO. */
		/* On vérifie que c'est un fichier Java dans le dossier Javagen. */
		return ResourceUtils.isSrcJavagen(file) && ResourceUtils.isJavaFile(file);
	}

	@Override
	public List<DtoField> parseDtoFields(IType type) {
		return DtoUtils.parseVertigoDtoFields(type);
	}

	@Override
	public boolean isServiceCandidate(IFile file) {
		/* Vérifie la convention de nommage. */
		return KspStringUtils.getVertigoServiceFileName(file.getName()) != null;
	}

	@Override
	public boolean isDaoCandidate(IFile file) {
		/* Vérifie que le fichier est dans Javagen */
		/* Filtre avec la convention de nommage sur le nom du fichier. */
		return ResourceUtils.isSrcJavagen(file) && KspStringUtils.getDaoFileName(file.getName()) != null;
	}

	@Override
	public String getKspKeyword(KspNature kspNature) {
		return kspNature.getKspKeyword();
	}

	@Override
	public DtoReferencePattern getDtoReferenceSyntaxe() {
		return DtoReferencePattern.DOMAIN;
	}
}
