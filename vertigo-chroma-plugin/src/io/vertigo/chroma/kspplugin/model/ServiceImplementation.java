package io.vertigo.chroma.kspplugin.model;

import io.vertigo.chroma.kspplugin.utils.ImageUtils;
import io.vertigo.chroma.kspplugin.utils.ResourceUtils;
import io.vertigo.chroma.kspplugin.utils.StringUtils;

import java.text.MessageFormat;

import org.eclipse.swt.graphics.Image;

/**
 * Représente une implémentation de méthode de service métier.
 */
public class ServiceImplementation extends JavaNavigable implements Openable {

	public ServiceImplementation(FileRegion fileRegion, String javaName) {
		super(fileRegion, javaName);
	}

	@Override
	public String getQualifier() {
		String projectName = ResourceUtils.getProjectName(this);
		String serviceFileName = StringUtils.removeExtension(getFileRegion().getFile().getName());
		return MessageFormat.format("{0}.{1} - {2}", serviceFileName, getJavaName(), projectName);
	}

	@Override
	public String getText() {
		return this.getJavaName();
	}

	@Override
	public Image getImage() {
		return ImageUtils.getServiceImage();
	}
}
