package io.vertigo.chroma.kspplugin.model;

import io.vertigo.chroma.kspplugin.utils.ImageUtils;
import io.vertigo.chroma.kspplugin.utils.ResourceUtils;
import io.vertigo.chroma.kspplugin.utils.StringUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.graphics.Image;

/**
 * Représente un fichier de DTO.
 */
public class DtoFile extends JavaNavigable implements Openable {

	private final String packageName;
	private final String lastPackagePart;
	private final List<DtoField> fields;

	/**
	 * Créé une nouvelle instance de DtoFile.
	 * 
	 * @param fileRegion Région du nom de la classe.
	 * @param javaName Nom Java.
	 * @param fields Listes des champs.
	 */
	public DtoFile(FileRegion fileRegion, String javaName, String packageName, List<DtoField> fields) {
		super(fileRegion, javaName);
		this.packageName = packageName;
		this.fields = fields;
		this.lastPackagePart = StringUtils.getLastNameFragment(packageName);
	}

	@Override
	public String getQualifier() {
		String projectName = ResourceUtils.getProjectName(this);
		return MessageFormat.format("{0} - {1}", getJavaName(), projectName);
	}

	@Override
	public String getText() {
		return getJavaName();
	}

	@Override
	public Image getImage() {
		return ImageUtils.getDtoImage();
	}

	public String getPackageName() {
		return packageName;
	}

	public String getLastPackagePart() {
		return lastPackagePart;
	}

	public List<DtoField> getFields() {
		return fields;
	}

	public List<DtoField> getPersistentFields() {
		return fields.stream().filter(DtoField::isPersistent).collect(Collectors.toList());
	}
}
