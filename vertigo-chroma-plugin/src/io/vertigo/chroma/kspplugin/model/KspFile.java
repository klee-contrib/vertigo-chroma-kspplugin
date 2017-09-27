package io.vertigo.chroma.kspplugin.model;

import io.vertigo.chroma.kspplugin.model.KspDeclaration;

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * Représente un fichier KSP et l'ensemble de ses déclarations.
 */
public class KspFile {

	private IFile file;
	private String packageName;
	private List<KspDeclaration> kspDeclarations;

	/**
	 * Créé une nouvelle instance de KspFile.
	 * 
	 * @param file Ressource fichier du KSP dans le workspace.
	 * @param packageName Nom du package du KSP.
	 * @param kspDeclarations Liste des déclarations.
	 */
	public KspFile(IFile file, String packageName, List<KspDeclaration> kspDeclarations) {
		super();
		this.file = file;
		this.packageName = packageName;
		this.kspDeclarations = kspDeclarations;
		for (KspDeclaration kspDeclaration : kspDeclarations) {
			kspDeclaration.setFile(this);
		}
	}

	/**
	 * @return Renvoie la ressource fichier du KSP dans le workspace.
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * @return Renvoie le nom du package du KSP.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return Renvoie la liste des déclarations KSP.
	 */
	public List<KspDeclaration> getKspDeclarations() {
		return kspDeclarations;
	}
}
