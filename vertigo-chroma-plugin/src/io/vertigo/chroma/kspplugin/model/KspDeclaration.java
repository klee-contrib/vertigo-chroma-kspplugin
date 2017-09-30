package io.vertigo.chroma.kspplugin.model;

import io.vertigo.chroma.kspplugin.utils.ImageUtils;
import io.vertigo.chroma.kspplugin.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.graphics.Image;

/**
 * Représente une déclaration du fichier Ksp (DtDefinition, Task, ...).
 */
public class KspDeclaration extends JavaNavigable implements Openable {

	private final String packageName;
	private final String prefix;
	private final String nature;
	private final String verb;
	private final String constantCaseName;
	private final AttributeMap map = new AttributeMap();
	private KspFile file;

	public KspDeclaration(FileRegion fileRegion, String packageName, String verb, String nature, String constantCaseName, String prefix, String javaName) {
		super(fileRegion, javaName);
		this.packageName = packageName;
		this.verb = verb;
		this.nature = nature;
		this.constantCaseName = constantCaseName;
		this.prefix = prefix;
	}

	public String getConstantCaseName() {
		return constantCaseName;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getNature() {
		return nature;
	}

	public String getVerb() {
		return verb;
	}

	@Override
	public String getQualifier() {
		String projectName = ResourceUtils.getProjectName(this);
		return String.format("[%s] %s.%s - %s", nature, packageName, getJavaName(), projectName);
	}

	public KspFile getFile() {
		return file;
	}

	public void setFile(KspFile file) {
		this.file = file;
	}

	@Override
	public String getText() {
		return this.getJavaName();
	}

	@Override
	public Image getImage() {
		return ImageUtils.getDeclarationImage(this);
	}

	/**
	 * Ajoute un attribut à la déclaration.
	 * 
	 * @param attribute Attribute.
	 */
	public void addAttribute(KspAttribute attribute) {
		this.map.put(attribute.getConstantCaseName(), attribute);
	}

	/**
	 * Retourne la liste des attributs de la déclaration.
	 * 
	 * @return Liste des attributs.
	 */
	public List<KspAttribute> getAttributes() {
		return new ArrayList<>(this.map.values());
	}

	/**
	 * Attributs indexés par leur nom en constant case.
	 */
	private static class AttributeMap extends HashMap<String, KspAttribute> {

		private static final long serialVersionUID = 1L;
	}
}
