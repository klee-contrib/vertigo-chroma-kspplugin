package io.vertigo.chroma.kspplugin.model;

/**
 * Classe de base pour un navigable vers un élément ayant un nom Java.
 */
public class JavaNavigable implements Navigable {

	private final String javaName;
	private final FileRegion fileRegion;

	/**
	 * Créé une nouvelle instance de JavaNavigable.
	 * 
	 * @param fileRegion Région pour la navigation.
	 * @param javaName Nom Java.
	 */
	public JavaNavigable(FileRegion fileRegion, String javaName) {
		this.fileRegion = fileRegion;
		this.javaName = javaName;
	}

	@Override
	public FileRegion getFileRegion() {
		return fileRegion;
	}

	public String getJavaName() {
		return javaName;
	}

	@Override
	public String toString() {
		return getJavaName();
	}
}
