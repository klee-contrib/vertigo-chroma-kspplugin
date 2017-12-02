package io.vertigo.chroma.kspplugin.model;

/**
 * Représente un fichier de classe Java.
 */
public class JavaClassFile extends JavaNavigable {

	private final String packageName;

	/**
	 * Créé une nouvelle instance de JavaClassFile.
	 * 
	 * @param fileRegion Région pour la navigation.
	 * @param javaName Nom simple Java.
	 * @param packageName Nom du package.
	 */
	public JavaClassFile(FileRegion fileRegion, String javaName, String packageName) {
		super(fileRegion, javaName);
		this.packageName = packageName == null ? "" : packageName;
	}

	/**
	 * @return Nom du package.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return Nom qualifié complet.
	 */
	public String getFullyQualifiedName() {
		return packageName + "." + this.getJavaName();
	}
}
