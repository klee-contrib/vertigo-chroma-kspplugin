package io.vertigo.chroma.kspplugin.legacy;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;

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

/**
 * Strat??gie pour les projets en Kasper 6.
 */
public final class SparkKasper6Strategy implements LegacyStrategy {

	@Override
	public KspDeclarationParts getKspDeclarationParts(String lineContent) {
		KspDeclarationMainParts mainParts = KspStringUtils.getKasper6KspDeclarationParts(lineContent);
		if (mainParts == null) {
			return null;
		}

		/* Extrait le pr??fixe et le nom simple de la d??claration */
		KspDeclarationNameParts declarationNameParts = KspStringUtils.getKspDeclarationNameParts(mainParts.getConstantCaseName());
		if (declarationNameParts == null) {
			return null;
		}

		return new KspDeclarationParts(mainParts, declarationNameParts);
	}

	@Override
	public String getKspDeclarationJavaName(String constantCaseNameOnly, String nature) {
		if ("Task".equals(nature)) {
			return StringUtils.toCamelCase(constantCaseNameOnly);
		}
		return StringUtils.toPascalCase(constantCaseNameOnly);
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
		/* On v??rifie que c'est un fichier Java dans le dossier Javagen. */
		return ResourceUtils.isSrcSparkGenerated(file) && ResourceUtils.isJavaFile(file);
	}

	@Override
	public List<DtoField> parseDtoFields(IType type) {
		return DtoUtils.parseVertigoDtoFields(type);
	}

	@Override
	public boolean isServiceCandidate(IFile file) {
		/* V??rifie la convention de nommage. */
		return KspStringUtils.getSparkServiceFileName(file.getName()) != null;
	}

	@Override
	public boolean isDaoCandidate(IFile file) {
		/* V??rifie que le fichier est dans Javagen */
		/* Filtre avec la convention de nommage sur le nom du fichier. */
		return ResourceUtils.isSrcSparkGenerated(file) && KspStringUtils.getDaoFileName(file.getName()) != null;
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
