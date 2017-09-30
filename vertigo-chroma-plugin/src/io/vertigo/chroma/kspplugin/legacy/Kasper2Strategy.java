package io.vertigo.chroma.kspplugin.legacy;

import io.vertigo.chroma.kspplugin.model.DtoField;
import io.vertigo.chroma.kspplugin.model.DtoReferencePattern;
import io.vertigo.chroma.kspplugin.model.KspAttribute;
import io.vertigo.chroma.kspplugin.model.KspDeclarationMainParts;
import io.vertigo.chroma.kspplugin.model.KspDeclarationNameParts;
import io.vertigo.chroma.kspplugin.model.KspDeclarationParts;
import io.vertigo.chroma.kspplugin.model.KspNature;
import io.vertigo.chroma.kspplugin.utils.KspStringUtils;
import io.vertigo.chroma.kspplugin.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;

/**
 * Stratégie pour les projets en Kasper 2.
 */
public final class Kasper2Strategy implements LegacyStrategy {

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
		/* Jamais appelé car en Kasper 2 les DTO ne sont pas générés. */
		return false;
	}

	@Override
	public List<DtoField> parseDtoFields(IType type) {
		/* Jamais appelé car en Kasper 2 les DTO ne sont pas générés. */
		return new ArrayList<>();
	}

	@Override
	public boolean isDtoCandidate(IFile file) {
		/* En Kasper 2, les DTO ne sont pas générés. */
		return false;
	}

	@Override
	public boolean isServiceCandidate(IFile file) {
		/* En Kasper 2, pas de couche de service métier. */
		return false;
	}

	@Override
	public boolean isDaoCandidate(IFile file) {
		/* En Kasper 2, les DAO/PAO ne sont pas générés. */
		return false;
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
