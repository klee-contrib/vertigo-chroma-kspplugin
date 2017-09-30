package io.vertigo.chroma.kspplugin.model;

import io.vertigo.chroma.kspplugin.utils.ImageUtils;
import io.vertigo.chroma.kspplugin.utils.ResourceUtils;
import io.vertigo.chroma.kspplugin.utils.StringUtils;

import java.text.MessageFormat;

import org.eclipse.swt.graphics.Image;

/**
 * Représente une implémentation de Task DAO.
 */
public class DaoImplementation extends JavaNavigable implements Openable {

	private DaoFile file;

	/**
	 * Créé une nouvelle instance de DaoImplementation.
	 * 
	 * @param fileRegion Région de la tâche.
	 * @param javaName Nom de la méthode.
	 */
	public DaoImplementation(FileRegion fileRegion, String javaName) {
		super(fileRegion, javaName);
	}

	public DaoFile getFile() {
		return file;
	}

	public void setFile(DaoFile file) {
		this.file = file;
	}

	@Override
	public String getText() {
		return getJavaName();
	}

	@Override
	public String getQualifier() {
		String projectName = ResourceUtils.getProjectName(this);
		String serviceFileName = StringUtils.removeExtension(getFileRegion().getFile().getName());
		return MessageFormat.format("{0}.{1} - {2}", serviceFileName, getJavaName(), projectName);
	}

	@Override
	public Image getImage() {
		return ImageUtils.getDaoImage();
	}
}
