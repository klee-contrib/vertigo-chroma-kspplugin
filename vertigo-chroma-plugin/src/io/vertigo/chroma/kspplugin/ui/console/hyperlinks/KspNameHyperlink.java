package io.vertigo.chroma.kspplugin.ui.console.hyperlinks;

import io.vertigo.chroma.kspplugin.ui.commands.core.Navigator;

import org.eclipse.ui.console.IHyperlink;

/**
 * Lien de console permettant de naviguer sur la définition d'un KSP à partir de son nom.
 * 
 * @author sebez
 */
public class KspNameHyperlink implements IHyperlink {

	private String kspName;

	/**
	 * Créé une nouvelle instance de KspNameHyperlink.
	 * 
	 * @param kspName Nom KSP.
	 */
	public KspNameHyperlink(String kspName) {
		this.kspName = kspName;
	}

	@Override
	public void linkEntered() {
		// RAS.
	}

	@Override
	public void linkExited() {
		// RAS.
	}

	@Override
	public void linkActivated() {
		/* Navigue vers le KSP. */
		Navigator.goToKspDeclarationFromKspName(kspName);
	}
}
