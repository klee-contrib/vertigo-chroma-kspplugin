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
 * Stratégie pour les projets en Kasper 4.
 */
public final class Kasper4Strategy implements LegacyStrategy {

	@Override
	public KspDeclarationParts getKspDeclarationParts(String lineContent) {
		KspDeclarationMainParts mainParts = KspStringUtils.getKasper5KspDeclarationParts(lineContent);
		if (mainParts == null) {
			mainParts = KspStringUtils.getKasper4KspDeclarationParts(lineContent);
		}
		if (mainParts == null) {
			return null;
		}

		/* Filtre sur la nature. */
		if (!isNatureConcerned(mainParts.getNature())) {
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
		default:
			return StringUtils.toPascalCase(constantCaseNameOnly);
		}
	}

	@Override
	public KspAttribute getKspAttribute(String lineContent) {
		return KspStringUtils.getKasper5KspAttribute(lineContent);
	}

	@Override
	public boolean isDtoType(IType type) {
		return JdtUtils.isKasper345DtoType(type);
	}

	@Override
	public boolean isDtoCandidate(IFile file) {
		/* Pas de convention de nommage sur les DTO. */
		/* On vérifie que c'est un fichier Java. */
		/* En Kasper 4, les DTO ne sont pas générés dans un dossier spécifique. */
		return ResourceUtils.isJavaFile(file);
	}

	@Override
	public List<DtoField> parseDtoFields(IType type) {
		return DtoUtils.parseKasper5DtoFields(type);
	}

	@Override
	public boolean isServiceCandidate(IFile file) {
		/* Vérifie la convention de nommage. */
		return KspStringUtils.getKasper4ServiceFileName(file.getName()) != null;
	}

	@Override
	public boolean isDaoCandidate(IFile file) {
		/* Filtre avec la convention de nommage sur le nom du fichier. */
		return KspStringUtils.getKasper4DaoFileName(file.getName()) != null;
	}

	@Override
	public String getKspKeyword(KspNature kspNature) {
		return kspNature.getKspKeyword();
	}

	@Override
	public DtoReferencePattern getDtoReferenceSyntaxe() {
		return DtoReferencePattern.DOMAIN;
	}

	private static boolean isNatureConcerned(String nature) {
		switch (nature) {
		/* Attribute ou field d'un Service : on ne le prend pas en compte. */
		case "Attribute":
		case "attribute":
		case "Field":
		case "field":
			return false;
		default:
			return true;
		}
	}
}
