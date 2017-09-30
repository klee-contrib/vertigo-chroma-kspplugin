package io.vertigo.chroma.kspplugin.legacy;

import io.vertigo.chroma.kspplugin.model.DtoField;
import io.vertigo.chroma.kspplugin.model.DtoReferencePattern;
import io.vertigo.chroma.kspplugin.model.KspAttribute;
import io.vertigo.chroma.kspplugin.model.KspDeclarationParts;
import io.vertigo.chroma.kspplugin.model.KspNature;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;

/**
 * Stratégie pour les projets n'utilisant pas le framework Kasper/Vertigo.
 */
public final class NoFrameworkStrategy implements LegacyStrategy {

	@Override
	public KspDeclarationParts getKspDeclarationParts(String lineContent) {
		return null;
	}

	@Override
	public String getKspDeclarationJavaName(String constantCaseNameOnly, String nature) {
		return null;
	}

	@Override
	public KspAttribute getKspAttribute(String lineContent) {
		return null;
	}

	@Override
	public boolean isDtoType(IType type) {
		return false;
	}

	@Override
	public List<DtoField> parseDtoFields(IType type) {
		return new ArrayList<>();
	}

	@Override
	public boolean isDtoCandidate(IFile file) {
		return false;
	}

	@Override
	public boolean isServiceCandidate(IFile file) {
		return false;
	}

	@Override
	public boolean isDaoCandidate(IFile file) {
		return false;
	}

	@Override
	public String getKspKeyword(KspNature kspNature) {
		return null;
	}

	@Override
	public DtoReferencePattern getDtoReferenceSyntaxe() {
		return null;
	}
}
