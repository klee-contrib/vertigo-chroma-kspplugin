package io.vertigo.chroma.kspplugin.model;

import io.vertigo.chroma.kspplugin.utils.ImageUtils;
import io.vertigo.chroma.kspplugin.utils.ResourceUtils;

import java.text.MessageFormat;

import org.eclipse.swt.graphics.Image;

/**
 * Représente une route de webservice.
 */
public class WsRoute extends JavaNavigable implements Openable {

	private String routePattern;
	private String verb;

	/**
	 * Créé une nouvelle instance de WsRoute.
	 * 
	 * @param fileRegion Région de fichier de la méthode du webservice.
	 * @param javaName Nom de la méthode du webservice.
	 * @param routePattern Pattern de la route.
	 * @param verb Verbe HTTP de la route.
	 */
	public WsRoute(FileRegion fileRegion, String javaName, String routePattern, String verb) {
		super(fileRegion, javaName);
		this.routePattern = routePattern;
		this.verb = verb;
	}

	public String getRoutePattern() {
		return routePattern;
	}

	public String getVerb() {
		return verb;
	}

	@Override
	public String getQualifier() {
		String projectName = ResourceUtils.getProjectName(this);
		return MessageFormat.format("[{0}] {1} - {2}", verb, routePattern, projectName);
	}

	@Override
	public String getText() {
		return this.getRoutePattern();
	}

	@Override
	public Image getImage() {
		return ImageUtils.getWsImage();
	}
}
