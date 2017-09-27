package io.vertigo.chroma.kspplugin.legacy;

import io.vertigo.chroma.kspplugin.model.DtoField;
import io.vertigo.chroma.kspplugin.model.DtoReferencePattern;
import io.vertigo.chroma.kspplugin.model.KspAttribute;
import io.vertigo.chroma.kspplugin.model.KspDeclarationParts;
import io.vertigo.chroma.kspplugin.model.KspNature;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;

/**
 * Contrat des stratégies de gestion d'un framework Vertigo/Kasper donné.
 */
public interface LegacyStrategy {

	/**
	 * Analyse une ligne de document KSP et renvoie la description d'une déclaration KSP si elle en contient une.
	 * 
	 * @param lineContent Contenu de la ligne du document KSP.
	 * @return Description de la déclaration.
	 */
	KspDeclarationParts getKspDeclarationParts(String lineContent);

	/**
	 * Obtient le nom Java pour un nom en constant case d'une déclaration KSP d'une nature donnée.
	 * 
	 * @param constantCaseNameOnly Nom en constant case.
	 * @param nature Nature de la déclaration.
	 * @return Nom Java.
	 */
	String getKspDeclarationJavaName(String constantCaseNameOnly, String nature);

	/**
	 * Analyse une ligne de document KSP et renvoie un attribut KSP si elle en contient un.
	 * 
	 * @param lineContent Contenu de la ligne du document KSP.
	 * @return Attribut KSP.
	 */
	KspAttribute getKspAttribute(String lineContent);

	/**
	 * Indique si un type est Java est un DTO.
	 * 
	 * @param type Type Java.
	 * @return <code>true</code> si c'est un DTO.
	 */
	boolean isDtoType(IType type);

	/**
	 * Indique si un fichier est candidat pour être un DTO.
	 * 
	 * @param file Fichier.
	 * @return <code>true</code> si c'est un candidat.
	 */
	boolean isDtoCandidate(IFile file);

	/**
	 * Extrait la liste des champs d'un DTO.
	 * 
	 * @param type Type JDT.
	 * @return Liste des champs.
	 */
	List<DtoField> parseDtoFields(IType type);

	/**
	 * Indique si un fichier est candidat pour être une implémentation de service métier.
	 * 
	 * @param file Fichier.
	 * @return <code>true</code> si c'est un candidat.
	 */
	boolean isServiceCandidate(IFile file);

	/**
	 * Indique si un fichier est candidat pour être un DAO/PAO.
	 * 
	 * @param file Fichier.
	 * @return <code>true</code> si c'est un candidat.
	 */
	boolean isDaoCandidate(IFile file);

	/**
	 * Obtient le mot-clé KSP pour une nature de déclaration donnée.
	 * 
	 * @param kspNature Nature.
	 * @return Mot-clé.
	 */
	String getKspKeyword(KspNature kspNature);

	/**
	 * Obtient le pattern utilisé pour référencer un DTO dans un KSP.
	 * 
	 * @return Pattern.
	 */
	DtoReferencePattern getDtoReferenceSyntaxe();
}
