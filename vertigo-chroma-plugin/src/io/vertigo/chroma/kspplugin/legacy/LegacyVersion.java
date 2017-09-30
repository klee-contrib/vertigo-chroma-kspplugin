package io.vertigo.chroma.kspplugin.legacy;

/**
 * Version du framework Kasper / Vertigo.
 */
public enum LegacyVersion {

	/**
	 * Vertigo.
	 */
	VERTIGO(new VertigoStrategy()),

	/**
	 * Kasper 6.
	 */
	KASPER_6(new Kasper6Strategy()),

	/**
	 * Kasper 5.
	 */
	KASPER_5(new Kasper5Strategy()),

	/**
	 * Kasper 4.
	 */
	KASPER_4(new Kasper4Strategy()),

	/**
	 * Kasper 3 orienté objet.
	 */
	KASPER_3_OO(new Kasper3OOStrategy()),

	/**
	 * Kasper 3.
	 */
	KASPER_3(new Kasper3Strategy()),

	/**
	 * Kasper 2.
	 */
	KASPER_2(new Kasper2Strategy()),

	/**
	 * Aucun framework détecté.
	 */
	NO_FRAMEWORK(new NoFrameworkStrategy());

	private final LegacyStrategy strategy;

	LegacyVersion(LegacyStrategy strategy) {
		this.strategy = strategy;
	}

	public LegacyStrategy getStrategy() {
		return strategy;
	}
}
